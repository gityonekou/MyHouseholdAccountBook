/**
 * 支払方法情報管理ユースケースです。
 * ・情報管理(支払方法)画面の表示情報取得(初期表示)
 * ・情報管理(支払方法)画面の表示情報取得(対象選択時)
 * ・支払方法情報の追加・更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   紙芝居準拠の有効/無効切替対応(execToggleEnableFlg追加)、from()型安全性対応(06-§8/§8.4)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.paymentmethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent;
import com.yonetani.webapp.accountbook.domain.service.account.paymentmethod.PaymentMethodNameResolver;
import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ClosingDay;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodMemo;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.EnableUpdateFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.PaymentMethodInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse.PaymentMethodKubunOptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse.PaymentMethodListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支払方法情報管理ユースケースです。
 * ・情報管理(支払方法)画面の表示情報取得(初期表示)
 * ・情報管理(支払方法)画面の表示情報取得(対象選択時)
 * ・支払方法情報の追加・更新
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class PaymentMethodInfoManageUseCase {

	// 新規登録可能な支払方法表示順の上限値(予約帯990～999は対象外)
	private static final int MAX_PAYMENT_METHOD_SORT = 989;

	// 支払方法情報取得リポジトリー
	private final PaymentMethodTableRepository paymentMethodRepository;
	// 支払方法・銀行口座の名称解決、選択肢生成コンポーネント
	private final PaymentMethodInfoComponent paymentMethodInfoComponent;
	// コードテーブル
	private final CodeTableItemComponent codeTableItem;

	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(支払方法)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(支払方法)画面の表示情報
	 *
	 */
	public PaymentMethodInfoManageResponse readPaymentMethodInfo(LoginUserInfo user) {
		log.debug("readPaymentMethodInfo:userid=" + user.getUserId());

		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		return createPaymentMethodInfoManageResponse(UserId.from(user.getUserId()), form);
	}

	/**
	 *<pre>
	 * 指定したユーザIDと支払方法に応じた情報管理(支払方法)画面の表示情報を取得します。
	 * 対象がシステム予約行(ENABLE_UPDATE_FLG=false)の場合、更新を許可せずエラーメッセージを設定して一覧表示に戻します
	 * (一覧に出ないため通常操作では起こらないが、URL直叩き等の不正操作に備えた防御)。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param paymentMethodCodeStr 表示対象の支払方法コード
	 * @return 情報管理(支払方法)画面の表示情報
	 *
	 */
	public PaymentMethodInfoManageResponse readPaymentMethodInfo(LoginUserInfo user, String paymentMethodCodeStr) {
		log.debug("readPaymentMethodInfo:userid=" + user.getUserId() + ",paymentMethodCode=" + paymentMethodCodeStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支払方法コード
		PaymentMethodCode paymentMethodCode = PaymentMethodCode.from(paymentMethodCodeStr);

		// 支払方法コードに対応する支払方法情報を取得
		PaymentMethod paymentMethod = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodCode.from(userId, paymentMethodCode));
		if(paymentMethod == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の支払方法情報が存在しません。管理者に問い合わせてください。paymentMethodCode:" + paymentMethodCode);
		}

		// システム予約行(更新不可)の場合は初期表示に戻し、エラーメッセージを設定する
		if(!paymentMethod.getEnableUpdateFlg().getValue()) {
			PaymentMethodInfoForm addForm = new PaymentMethodInfoForm();
			addForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
			PaymentMethodInfoManageResponse errorResponse = createPaymentMethodInfoManageResponse(userId, addForm);
			errorResponse.addErrorMessage("指定の支払方法は更新できません。管理者に問い合わせてください。paymentMethodCode:" + paymentMethodCode);
			return errorResponse;
		}

		/* 取得した支払方法情報を画面入力フォームに設定 */
		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		// アクション：更新
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 支払方法コード
		form.setPaymentMethodCode(paymentMethod.getPaymentMethodCode().getValue());
		// 支払方法名
		form.setPaymentMethodName(paymentMethod.getPaymentMethodName().getValue());
		// 支払方法メモ
		form.setPaymentMethodMemo(paymentMethod.getPaymentMethodMemo().getValue());
		// 支払方法種別
		form.setPaymentMethodKubun(paymentMethod.getPaymentMethodKubun().getValue());
		// 銀行口座コード(銀行口座不要な種別の場合はnull)
		form.setBankAccountCode(paymentMethod.getBankAccountCode() == null ? null : paymentMethod.getBankAccountCode().getValue());
		// 集計開始日
		form.setClosingDay(paymentMethod.getClosingDay().getValue());
		// 表示順
		form.setPaymentMethodSort(Integer.parseInt(paymentMethod.getPaymentMethodSort().getValue()));
		// 更新前の表示順(表示順調整判定用の非表示項目)
		form.setPaymentMethodSortBefore(paymentMethod.getPaymentMethodSort().getValue());
		// 有効/無効フラグ(メイン更新フォームでは編集不可の非表示項目)
		form.setEnableFlg(paymentMethod.getEnableFlg().getValue());

		// 画面表示情報を取得し、更新対象であることのメッセージを設定
		PaymentMethodInfoManageResponse response = createPaymentMethodInfoManageResponse(userId, form);
		response.addMessage("支払方法名「" + paymentMethod.getPaymentMethodName().getValue() + "」の支払方法を更新します。");

		return response;
	}

	/**
	 *<pre>
	 * 支払方法情報登録・更新時のバリデーションチェックエラー時処理
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 支払方法情報入力フォームの入力値
	 * @return 情報管理(支払方法)更新画面の表示情報
	 *
	 */
	public PaymentMethodInfoManageResponse readUpdateBindingErrorSetInfo(LoginUserInfo user, PaymentMethodInfoForm inputForm) {
		log.debug("readUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		return createPaymentMethodInfoManageResponse(UserId.from(user.getUserId()), inputForm);
	}

	/**
	 *<pre>
	 * 支払方法情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param paymentMethodForm 支払方法情報入力フォームの入力値
	 * @return 情報管理(支払方法)画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public PaymentMethodInfoManageResponse execAction(LoginUserInfo user, PaymentMethodInfoForm paymentMethodForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",paymentMethodForm=" + paymentMethodForm);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());

		// 正常時は初期表示にリダイレクトされるので、ここでは空の一覧で画面表示情報を作成する
		// (以降でエラー発生時は一覧が空のまま画面表示し、再登録の形とする。ShopInfoManageUseCase.execAction()と同じ方針)
		PaymentMethodInfoManageResponse response = PaymentMethodInfoManageResponse.getInstance(
				paymentMethodForm, createPaymentMethodKubunOptions(), paymentMethodInfoComponent.getBankAccountOptions(userId), createClosingDayOptions());

		// ドメインタイプ:支払方法種別
		PaymentMethodKubun kubun = PaymentMethodKubun.from(paymentMethodForm.getPaymentMethodKubun());
		// 種別ごとに不要な項目は、フォームに何が送信されていてもnullに正規化する(突合レビュー指摘F)
		BankAccountCode normalizedBankAccountCode = (kubun.requiresAccount() && StringUtils.hasLength(paymentMethodForm.getBankAccountCode()))
				? BankAccountCode.from(paymentMethodForm.getBankAccountCode()) : null;
		ClosingDay normalizedClosingDay = ClosingDay.from(kubun.requiresClosingDay() ? paymentMethodForm.getClosingDay() : null);

		// 未入力の場合は有効として扱う(デフォルトtrue)
		boolean enableFlg = paymentMethodForm.getEnableFlg() == null ? true : paymentMethodForm.getEnableFlg();

		// 現在の登録件数を取得(予約帯990～999を除いた件数)
		int count = paymentMethodRepository.countByIdAndLessThanReserved(SearchQueryUserId.from(userId));

		// 既存データの表示順更新データ
		List<PaymentMethod> sortValueUpdateList = new ArrayList<>();

		if(Objects.equals(paymentMethodForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			/* 新規支払方法情報を追加 */
			// 追加後の件数を算出
			count++;
			// 上限件数チェック(予約帯990～999を除いた件数)
			if(count > MAX_PAYMENT_METHOD_SORT) {
				response.addErrorMessage("支払方法は" + MAX_PAYMENT_METHOD_SORT + "件より多く登録できません。管理者に問い合わせてください。");
				return response;
			}

			// 表示順の決定、及び既存データの表示順調整
			if(paymentMethodForm.getPaymentMethodSort() == null || paymentMethodForm.getPaymentMethodSort() > count) {
				// 未入力、または末尾より後ろの指定の場合、末尾(count)に設定(既存データの調整不要)
				paymentMethodForm.setPaymentMethodSort(count);

			} else if(paymentMethodForm.getPaymentMethodSort() < count) {
				// 既存データの間に割り込む場合、割り込み先以降の既存データを表示順+1で後ろにずらす対象として取得(システム予約行は対象外)
				PaymentMethodInquiryList sortList = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodSort.from(
						userId, PaymentMethodSort.from(paymentMethodForm.getPaymentMethodSort())));
				if(!sortList.isEmpty()) {
					sortList.getValues().stream()
							.filter(data -> !data.getPaymentMethodCode().isSystemReserved())
							.forEach(data -> sortValueUpdateList.add(createShiftedData(data, 1)));
				}
			}

			// 新規支払方法情報(ドメイン)を生成
			PaymentMethod paymentMethod = PaymentMethod.from(
					// ユーザID
					userId,
					// 支払方法コード(追加後の件数をそのままコードとして使用)
					PaymentMethodCode.from(count),
					// 支払方法名
					PaymentMethodName.from(paymentMethodForm.getPaymentMethodName()),
					// 支払方法メモ
					PaymentMethodMemo.from(paymentMethodForm.getPaymentMethodMemo()),
					// 支払方法種別
					kubun,
					// 銀行口座コード(種別に応じて正規化済み)
					normalizedBankAccountCode,
					// 集計開始日(種別に応じて正規化済み)
					normalizedClosingDay,
					// 表示順
					PaymentMethodSort.from(String.format("%03d", paymentMethodForm.getPaymentMethodSort())),
					// 有効/無効フラグ
					EnableFlg.from(enableFlg),
					// 更新可否フラグ(新規登録行は常に更新可)
					EnableUpdateFlg.from(true));

			// 支払方法テーブルに追加
			int addCount = paymentMethodRepository.add(paymentMethod);
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支払方法テーブルへの追加件数が不正でした。[件数=" + addCount + "][add data:" + paymentMethod + "]");
			}

			response.addMessage("新規支払方法を追加しました。[code:" + paymentMethod.getPaymentMethodCode() + "]" + paymentMethod.getPaymentMethodName());

		} else if(Objects.equals(paymentMethodForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			/* 既存支払方法情報を更新 */
			// 更新前の表示順(画面の非表示項目から引き継いだ値)が未設定の場合、不正データとしてエラー
			if(!StringUtils.hasLength(paymentMethodForm.getPaymentMethodSortBefore())) {
				throw new MyHouseholdAccountBookRuntimeException(
						"旧表示順の値が不正です。管理者に問い合わせてください。paymentMethodSortBefore=" + paymentMethodForm.getPaymentMethodSortBefore());
			}
			// 上限件数チェック(予約帯990～999を除いた件数)
			if(count > MAX_PAYMENT_METHOD_SORT) {
				response.addErrorMessage("支払方法が" + MAX_PAYMENT_METHOD_SORT + "件より多く登録されているため支払方法情報を更新できません。管理者に問い合わせてください。");
				return response;
			}

			// 表示順の決定(末尾より後ろの指定の場合、末尾(count)に補正)
			if(paymentMethodForm.getPaymentMethodSort() == null || paymentMethodForm.getPaymentMethodSort() > count) {
				paymentMethodForm.setPaymentMethodSort(count);
			}
			PaymentMethodSort newPaymentMethodSort = PaymentMethodSort.from(paymentMethodForm.getPaymentMethodSort());
			// 表示順が変更された場合、変更前後の間にある既存データの表示順をずらして調整
			if(!newPaymentMethodSort.getValue().equals(paymentMethodForm.getPaymentMethodSortBefore())) {
				log.debug(" 既存データの表示順調整ありpaymentMethodSort Before:" + paymentMethodForm.getPaymentMethodSortBefore() + ",New=" + newPaymentMethodSort);

				if(paymentMethodForm.getPaymentMethodSortBefore().compareTo(newPaymentMethodSort.getValue()) > 0) {
					// 表示順を前方(若い番号)に変更する場合、変更後～変更前の間の既存データを表示順+1で後ろにずらす対象として取得(システム予約行は対象外)
					int searchBIntVal = Integer.parseInt(paymentMethodForm.getPaymentMethodSortBefore()) - 1;
					PaymentMethodInquiryList sortList = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB.from(
							userId, newPaymentMethodSort, PaymentMethodSort.from(searchBIntVal)));
					if(!sortList.isEmpty()) {
						sortList.getValues().stream()
								.filter(data -> !data.getPaymentMethodCode().isSystemReserved())
								.forEach(data -> sortValueUpdateList.add(createShiftedData(data, 1)));
					}

				} else {
					// 表示順を後方(大きい番号)に変更する場合、変更前～変更後の間の既存データを表示順-1で前にずらす対象として取得(システム予約行は対象外)
					int searchAIntVal = Integer.parseInt(paymentMethodForm.getPaymentMethodSortBefore()) + 1;
					PaymentMethodInquiryList sortList = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB.from(
							userId, PaymentMethodSort.from(searchAIntVal), newPaymentMethodSort));
					if(!sortList.isEmpty()) {
						sortList.getValues().stream()
								.filter(data -> !data.getPaymentMethodCode().isSystemReserved())
								.forEach(data -> sortValueUpdateList.add(createShiftedData(data, -1)));
					}
				}
			}

			// 更新対象の支払方法情報(ドメイン)を生成
			PaymentMethod paymentMethod = PaymentMethod.from(
					// ユーザID
					userId,
					// 支払方法コード
					PaymentMethodCode.from(paymentMethodForm.getPaymentMethodCode()),
					// 支払方法名
					PaymentMethodName.from(paymentMethodForm.getPaymentMethodName()),
					// 支払方法メモ
					PaymentMethodMemo.from(paymentMethodForm.getPaymentMethodMemo()),
					// 支払方法種別
					kubun,
					// 銀行口座コード(種別に応じて正規化済み)
					normalizedBankAccountCode,
					// 集計開始日(種別に応じて正規化済み)
					normalizedClosingDay,
					// 表示順
					newPaymentMethodSort,
					// 有効/無効フラグ
					EnableFlg.from(enableFlg),
					// 更新可否フラグ(システム予約行はこの画面経由で更新されないため常にtrue)
					EnableUpdateFlg.from(true));
			// 支払方法テーブルを更新
			int updateCount = paymentMethodRepository.update(paymentMethod);
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支払方法テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + paymentMethod + "]");
			}

			response.addMessage("支払方法を更新しました。[code:" + paymentMethod.getPaymentMethodCode() + "]" + paymentMethod.getPaymentMethodName());

		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + paymentMethodForm.getAction());
		}

		// 既存データのソート順を調整
		sortValueUpdateList.forEach(updateSortData -> {
			int updateSortDataCount = paymentMethodRepository.updateSort(updateSortData);
			if(updateSortDataCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException(
						"支払方法テーブルへの更新件数が不正でした。[件数=" + updateSortDataCount + "][update data:" + updateSortData + "]");
			}
		});

		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 指定した支払方法コードに対応する支払方法情報の有効/無効フラグを反転し、更新します。
	 * 紙芝居(ManagePaymentMethod.html)の「無効化／有効化」ボタン押下時の処理に対応します。
	 * 対象がシステム予約行(ENABLE_UPDATE_FLG=false)の場合は更新を拒否し、エラーメッセージを設定して一覧表示に戻します
	 * (一覧に出ないため通常操作では発生しないが、URL直叩き等の不正操作に備えた防御。readPaymentMethodInfo(user, code)と同じ方針)。
	 *</pre>
	 * @param user ログインユーザID
	 * @param paymentMethodCodeStr 対象の支払方法コード
	 * @return 情報管理(支払方法)画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public PaymentMethodInfoManageResponse execToggleEnableFlg(LoginUserInfo user, String paymentMethodCodeStr) {
		log.debug("execToggleEnableFlg:userid=" + user.getUserId() + ",paymentMethodCode=" + paymentMethodCodeStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支払方法コード
		PaymentMethodCode paymentMethodCode = PaymentMethodCode.from(paymentMethodCodeStr);

		// 支払方法コードに対応する支払方法情報を取得
		PaymentMethod paymentMethod = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodCode.from(userId, paymentMethodCode));
		if(paymentMethod == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の支払方法情報が存在しません。管理者に問い合わせてください。paymentMethodCode:" + paymentMethodCode);
		}

		// システム予約行(更新不可)の場合は初期表示に戻し、エラーメッセージを設定する
		if(!paymentMethod.getEnableUpdateFlg().getValue()) {
			PaymentMethodInfoForm addForm = new PaymentMethodInfoForm();
			addForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
			PaymentMethodInfoManageResponse errorResponse = createPaymentMethodInfoManageResponse(userId, addForm);
			errorResponse.addErrorMessage("指定の支払方法は更新できません。管理者に問い合わせてください。paymentMethodCode:" + paymentMethodCode);
			return errorResponse;
		}

		/* 有効/無効フラグを反転して更新 */
		// 有効/無効フラグ以外は既存値をそのまま再利用した新しい支払方法情報を生成
		PaymentMethod toggledPaymentMethod = paymentMethod.toggleEnableFlg();
		// 支払方法テーブルを更新
		int updateCount = paymentMethodRepository.update(toggledPaymentMethod);
		if(updateCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException(
					"支払方法テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + toggledPaymentMethod + "]");
		}

		/* 情報管理(支払方法)初期表示画面の表示情報を生成 */
		PaymentMethodInfoForm addForm = new PaymentMethodInfoForm();
		// アクション：新規登録(トグル後は常に新規登録モードの初期画面に戻す)
		addForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 画面表示情報を取得
		PaymentMethodInfoManageResponse response = createPaymentMethodInfoManageResponse(userId, addForm);
		response.setTransactionSuccessFull();
		// 有効化/無効化いずれが行われたかに応じたメッセージを設定
		response.addMessage("支払方法名「" + toggledPaymentMethod.getPaymentMethodName().getValue() + "」の支払方法を"
				+ (toggledPaymentMethod.getEnableFlg().getValue() ? "有効化" : "無効化") + "しました。");

		return response;
	}

	/**
	 *<pre>
	 * 指定したユーザIDで登録されている支払方法情報を取得し、支払方法情報入力フォームをもとに情報管理(支払方法)画面の表示情報を生成して返します。
	 * 一覧にはシステム予約行を含みません（確認事項③の回答どおり、予約行は一覧非表示）。
	 *</pre>
	 * @param userId 表示対象のユーザID
	 * @param form 支払方法情報入力フォーム
	 * @return 情報管理(支払方法)画面の表示情報
	 *
	 */
	private PaymentMethodInfoManageResponse createPaymentMethodInfoManageResponse(UserId userId, PaymentMethodInfoForm form) {

		PaymentMethodInfoManageResponse response = PaymentMethodInfoManageResponse.getInstance(
				form,
				createPaymentMethodKubunOptions(),
				paymentMethodInfoComponent.getBankAccountOptions(userId),
				createClosingDayOptions());

		PaymentMethodInquiryList searchResult = paymentMethodRepository.findByUserId(SearchQueryUserId.from(userId));
		List<PaymentMethod> displayTargetList = searchResult.getValues().stream()
				.filter(pm -> !pm.getPaymentMethodCode().isSystemReserved())
				.collect(Collectors.toList());

		if(displayTargetList.isEmpty()) {
			response.addMessage("支払方法情報取得結果が0件です。");
		} else {
			// 支払方法コード→銀行口座名の解決用リゾルバ(N+1回避。レビュー指摘④)
			PaymentMethodNameResolver resolver = paymentMethodInfoComponent.createResolver(userId);
			response.addPaymentMethodList(displayTargetList.stream().map(domain ->
				PaymentMethodListItem.from(
						// 支払方法コード
						domain.getPaymentMethodCode(),
						// 支払方法名
						domain.getPaymentMethodName(),
						// 支払方法種別名：コード変換した値を設定
						codeTableItem.getCodeValue(MyHouseholdAccountBookContent.CODE_DEFINES_PAYMENT_METHOD_KUBUN, domain.getPaymentMethodKubun().getValue()),
						// 銀行口座名：ループ内では生成済みのresolverで解決するのみ
						resolver.getBankAccountName(domain.getPaymentMethodCode()).getValue(),
						// 集計開始日：期間表示(開始日～終了日)に変換した値を設定
						domain.getClosingDay().toPeriodDisplayString(),
						// 表示順
						domain.getPaymentMethodSort(),
						// 有効/無効フラグ
						domain.getEnableFlg())
			).collect(Collectors.toUnmodifiableList()));
		}
		return response;
	}

	/**
	 *<pre>
	 * 支払方法種別(PaymentMethodKubun)の全値を、コードテーブルの表示名と述語(requiresAccount()/requiresClosingDay())を
	 * 合成して選択肢データに変換します。種別を追加する際はPaymentMethodKubun enumに定数を1つ追加するだけでよく、
	 * JS側の修正は不要になります(突合レビュー指摘E)。
	 *</pre>
	 * @return 支払方法種別選択肢のリスト
	 *
	 */
	private List<PaymentMethodKubunOptionItem> createPaymentMethodKubunOptions() {
		return Arrays.stream(PaymentMethodKubun.values())
				.map(kubun -> PaymentMethodKubunOptionItem.from(
						// 支払方法種別
						kubun,
						// 支払方法種別名：コード変換した値を設定
						codeTableItem.getCodeValue(MyHouseholdAccountBookContent.CODE_DEFINES_PAYMENT_METHOD_KUBUN, kubun.getValue()),
						// 銀行口座の指定が必要かどうか
						kubun.requiresAccount(),
						// 集計開始日の指定が必要かどうか
						kubun.requiresClosingDay()))
				.collect(Collectors.toList());
	}

	/**
	 *<pre>
	 * 集計開始日(1～28)の選択肢データを生成します。紙芝居(ManagePaymentMethod.html)のプルダウンに準拠し、
	 * 先頭に未選択用の空値オプションを追加します。value属性はClosingDay.from()の仕様(2桁固定)に合わせて
	 * ゼロパディングしますが、表示文言は紙芝居と同じ「1日」～「28日」とします。
	 *</pre>
	 * @return 集計開始日選択肢のリスト
	 *
	 */
	private List<OptionItem> createClosingDayOptions() {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "集計開始日を選択してください"));
		for(int day = 1; day <= 28; day++) {
			optionList.add(OptionItem.from(String.format("%02d", day), day + "日"));
		}
		return optionList;
	}

	/**
	 *<pre>
	 * 指定の支払方法情報のうち、表示順の値を指定した増減分加算・減算した値で支払方法情報を生成して返します。
	 *</pre>
	 * @param data 生成対象の支払方法情報
	 * @param add 表示順の増減分する値(1 or -1)
	 * @return 表示順を増減した値で新たに生成した支払方法情報
	 *
	 */
	private PaymentMethod createShiftedData(PaymentMethod data, int add) {
		return PaymentMethod.from(
				// ユーザID：既存値をそのまま再利用
				data.getUserId(),
				// 支払方法コード：既存値をそのまま再利用
				data.getPaymentMethodCode(),
				// 支払方法名：既存値をそのまま再利用
				data.getPaymentMethodName(),
				// 支払方法メモ：既存値をそのまま再利用
				data.getPaymentMethodMemo(),
				// 支払方法種別：既存値をそのまま再利用
				data.getPaymentMethodKubun(),
				// 銀行口座コード：既存値をそのまま再利用
				data.getBankAccountCode(),
				// 集計開始日：既存値をそのまま再利用
				data.getClosingDay(),
				// 表示順：増減分(add)を加算した新しい値
				PaymentMethodSort.from(String.format("%03d", Integer.parseInt(data.getPaymentMethodSort().getValue()) + add)),
				// 有効/無効フラグ：既存値をそのまま再利用
				data.getEnableFlg(),
				// 更新可否フラグ：既存値をそのまま再利用
				data.getEnableUpdateFlg());
	}
}
