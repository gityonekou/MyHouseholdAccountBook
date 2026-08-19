/**
 * 支払方法情報管理ユースケースです。
 * ・情報管理(支払方法)画面の表示情報取得(初期表示)
 * ・情報管理(支払方法)画面の表示情報取得(対象選択時)
 * ・支払方法情報の追加・更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
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
import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.PaymentMethodNameResolver;
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
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.PaymentMethodInfoForm;
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

		UserId userId = UserId.from(user.getUserId());
		PaymentMethodCode paymentMethodCode = PaymentMethodCode.from(paymentMethodCodeStr);

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

		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setPaymentMethodCode(paymentMethod.getPaymentMethodCode().getValue());
		form.setPaymentMethodName(paymentMethod.getPaymentMethodName().getValue());
		form.setPaymentMethodKubun(paymentMethod.getPaymentMethodKubun().getValue());
		form.setBankAccountCode(paymentMethod.getBankAccountCode() == null ? null : paymentMethod.getBankAccountCode().getValue());
		form.setClosingDay(paymentMethod.getClosingDay().getValue());
		form.setPaymentMethodSort(Integer.parseInt(paymentMethod.getPaymentMethodSort().getValue()));
		form.setPaymentMethodSortBefore(paymentMethod.getPaymentMethodSort().getValue());
		form.setEnableFlg(paymentMethod.getEnableFlg().getValue());

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

		UserId userId = UserId.from(user.getUserId());

		// 正常時は初期表示にリダイレクトされるので、ここでは空の一覧で画面表示情報を作成する
		// (以降でエラー発生時は一覧が空のまま画面表示し、再登録の形とする。ShopInfoManageUseCase.execAction()と同じ方針)
		PaymentMethodInfoManageResponse response = PaymentMethodInfoManageResponse.getInstance(
				paymentMethodForm, createPaymentMethodKubunOptions(), paymentMethodInfoComponent.getBankAccountOptions(userId));

		PaymentMethodKubun kubun = PaymentMethodKubun.from(paymentMethodForm.getPaymentMethodKubun());
		// 種別ごとに不要な項目は、フォームに何が送信されていてもnullに正規化する(突合レビュー指摘F)
		String normalizedBankAccountCode = kubun.requiresAccount() ? paymentMethodForm.getBankAccountCode() : null;
		String normalizedClosingDay = kubun.requiresClosingDay() ? paymentMethodForm.getClosingDay() : null;

		boolean enableFlg = paymentMethodForm.getEnableFlg() == null ? true : paymentMethodForm.getEnableFlg();

		// 現在の登録件数を取得(予約帯990～999を除いた件数)
		int count = paymentMethodRepository.countByIdAndLessThanReserved(SearchQueryUserId.from(userId));

		// 既存データの表示順更新データ
		List<PaymentMethod> sortValueUpdateList = new ArrayList<>();

		if(Objects.equals(paymentMethodForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			count++;
			if(count > MAX_PAYMENT_METHOD_SORT) {
				response.addErrorMessage("支払方法は" + MAX_PAYMENT_METHOD_SORT + "件より多く登録できません。管理者に問い合わせてください。");
				return response;
			}

			if(paymentMethodForm.getPaymentMethodSort() == null || paymentMethodForm.getPaymentMethodSort() > count) {
				paymentMethodForm.setPaymentMethodSort(count);

			} else if(paymentMethodForm.getPaymentMethodSort() < count) {
				PaymentMethodInquiryList sortList = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodSort.from(
						userId, PaymentMethodSort.from(paymentMethodForm.getPaymentMethodSort())));
				if(!sortList.isEmpty()) {
					sortList.getValues().stream()
							.filter(data -> !data.getPaymentMethodCode().isSystemReserved())
							.forEach(data -> sortValueUpdateList.add(createShiftedData(data, 1)));
				}
			}

			PaymentMethod paymentMethod = PaymentMethod.from(
					userId.getValue(),
					PaymentMethodCode.getNewCode(count),
					paymentMethodForm.getPaymentMethodName(),
					paymentMethodForm.getPaymentMethodKubun(),
					normalizedBankAccountCode,
					normalizedClosingDay,
					String.format("%03d", paymentMethodForm.getPaymentMethodSort()),
					enableFlg,
					true);

			int addCount = paymentMethodRepository.add(paymentMethod);
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支払方法テーブルへの追加件数が不正でした。[件数=" + addCount + "][add data:" + paymentMethod + "]");
			}

			response.addMessage("新規支払方法を追加しました。[code:" + paymentMethod.getPaymentMethodCode() + "]" + paymentMethod.getPaymentMethodName());

		} else if(Objects.equals(paymentMethodForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			if(!StringUtils.hasLength(paymentMethodForm.getPaymentMethodSortBefore())) {
				throw new MyHouseholdAccountBookRuntimeException(
						"旧表示順の値が不正です。管理者に問い合わせてください。paymentMethodSortBefore=" + paymentMethodForm.getPaymentMethodSortBefore());
			}
			if(count > MAX_PAYMENT_METHOD_SORT) {
				response.addErrorMessage("支払方法が" + MAX_PAYMENT_METHOD_SORT + "件より多く登録されているため支払方法情報を更新できません。管理者に問い合わせてください。");
				return response;
			}

			if(paymentMethodForm.getPaymentMethodSort() == null || paymentMethodForm.getPaymentMethodSort() > count) {
				paymentMethodForm.setPaymentMethodSort(count);
			}
			PaymentMethodSort newPaymentMethodSort = PaymentMethodSort.from(paymentMethodForm.getPaymentMethodSort());
			if(!newPaymentMethodSort.getValue().equals(paymentMethodForm.getPaymentMethodSortBefore())) {
				log.debug(" 既存データの表示順調整ありpaymentMethodSort Before:" + paymentMethodForm.getPaymentMethodSortBefore() + ",New=" + newPaymentMethodSort);

				if(paymentMethodForm.getPaymentMethodSortBefore().compareTo(newPaymentMethodSort.getValue()) > 0) {
					int searchBIntVal = Integer.parseInt(paymentMethodForm.getPaymentMethodSortBefore()) - 1;
					PaymentMethodInquiryList sortList = paymentMethodRepository.findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB.from(
							userId, newPaymentMethodSort, PaymentMethodSort.from(searchBIntVal)));
					if(!sortList.isEmpty()) {
						sortList.getValues().stream()
								.filter(data -> !data.getPaymentMethodCode().isSystemReserved())
								.forEach(data -> sortValueUpdateList.add(createShiftedData(data, 1)));
					}

				} else {
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

			PaymentMethod paymentMethod = PaymentMethod.from(
					userId.getValue(),
					paymentMethodForm.getPaymentMethodCode(),
					paymentMethodForm.getPaymentMethodName(),
					paymentMethodForm.getPaymentMethodKubun(),
					normalizedBankAccountCode,
					normalizedClosingDay,
					newPaymentMethodSort.getValue(),
					enableFlg,
					true);
			int updateCount = paymentMethodRepository.update(paymentMethod);
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支払方法テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + paymentMethod + "]");
			}

			response.addMessage("支払方法を更新しました。[code:" + paymentMethod.getPaymentMethodCode() + "]" + paymentMethod.getPaymentMethodName());

		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + paymentMethodForm.getAction());
		}

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
				paymentMethodInfoComponent.getBankAccountOptions(userId));

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
						domain.getPaymentMethodCode().getValue(),
						domain.getPaymentMethodName().getValue(),
						codeTableItem.getCodeValue(MyHouseholdAccountBookContent.CODE_DEFINES_PAYMENT_METHOD_KUBUN, domain.getPaymentMethodKubun().getValue()),
						resolver.getBankAccountName(domain.getPaymentMethodCode()),
						domain.getClosingDay().getValue() == null ? "－" : domain.getClosingDay().getValue(),
						domain.getPaymentMethodSort().getValue(),
						domain.getEnableFlg().getValue())
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
						kubun.getValue(),
						codeTableItem.getCodeValue(MyHouseholdAccountBookContent.CODE_DEFINES_PAYMENT_METHOD_KUBUN, kubun.getValue()),
						kubun.requiresAccount(),
						kubun.requiresClosingDay()))
				.collect(Collectors.toList());
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
				data.getUserId().getValue(),
				data.getPaymentMethodCode().getValue(),
				data.getPaymentMethodName().getValue(),
				data.getPaymentMethodKubun().getValue(),
				data.getBankAccountCode() == null ? null : data.getBankAccountCode().getValue(),
				data.getClosingDay().getValue(),
				String.format("%03d", Integer.parseInt(data.getPaymentMethodSort().getValue()) + add),
				data.getEnableFlg().getValue(),
				data.getEnableUpdateFlg().getValue());
	}
}
