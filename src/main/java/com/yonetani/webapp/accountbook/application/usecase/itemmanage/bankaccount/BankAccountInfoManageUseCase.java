/**
 * 銀行口座情報管理ユースケースです。
 * ・情報管理(銀行口座)画面の表示情報取得(初期表示)
 * ・情報管理(銀行口座)画面の表示情報取得(対象選択時)
 * ・銀行口座情報の追加・更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   紙芝居準拠の有効/無効切替対応(execToggleEnableFlg追加)、from()型安全性対応(06-§8/§8.4)
 *
 */

package com.yonetani.webapp.accountbook.application.usecase.itemmanage.bankaccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.bankaccount.BankAccountTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountMemo;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankName;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.BankAccountInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.BankAccountInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.BankAccountInfoManageResponse.BankAccountListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 銀行口座情報管理ユースケースです。
 * ・情報管理(銀行口座)画面の表示情報取得(初期表示)
 * ・情報管理(銀行口座)画面の表示情報取得(対象選択時)
 * ・銀行口座情報の追加・更新
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
public class BankAccountInfoManageUseCase {

	// 銀行口座情報取得リポジトリー
	private final BankAccountTableRepository bankAccountRepository;

	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(銀行口座)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(銀行口座)画面の表示情報
	 *
	 */
	public BankAccountInfoManageResponse readBankAccountInfo(LoginUserInfo user) {
		log.debug("readBankAccountInfo:userid=" + user.getUserId());

		BankAccountInfoForm form = new BankAccountInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		return createBankAccountInfoManageResponse(UserId.from(user.getUserId()), form);
	}

	/**
	 *<pre>
	 * 指定したユーザIDと銀行口座に応じた情報管理(銀行口座)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param bankAccountCodeStr 表示対象の銀行口座コード
	 * @return 情報管理(銀行口座)画面の表示情報
	 *
	 */
	public BankAccountInfoManageResponse readBankAccountInfo(LoginUserInfo user, String bankAccountCodeStr) {
		log.debug("readBankAccountInfo:userid=" + user.getUserId() + ",bankAccountCode=" + bankAccountCodeStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:銀行口座コード
		BankAccountCode bankAccountCode = BankAccountCode.from(bankAccountCodeStr);

		// 銀行口座コードに対応する銀行口座情報を取得
		BankAccount bankAccount = bankAccountRepository.findById(SearchQueryUserIdAndBankAccountCode.from(userId, bankAccountCode));
		if(bankAccount == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の銀行口座情報が存在しません。管理者に問い合わせてください。bankAccountCode:" + bankAccountCode);
		}

		/* 取得した銀行口座情報を画面入力フォームに設定 */
		BankAccountInfoForm form = new BankAccountInfoForm();
		// アクション：更新
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 銀行口座コード
		form.setBankAccountCode(bankAccount.getBankAccountCode().getValue());
		// 銀行名
		form.setBankName(bankAccount.getBankName().getValue());
		// 銀行口座メモ
		form.setBankAccountMemo(bankAccount.getBankAccountMemo().getValue());
		// 表示順
		form.setBankAccountSort(Integer.parseInt(bankAccount.getBankAccountSort().getValue()));
		// 更新前の表示順(表示順調整判定用の非表示項目)
		form.setBankAccountSortBefore(bankAccount.getBankAccountSort().getValue());
		// 有効/無効フラグ(メイン更新フォームでは編集不可の非表示項目)
		form.setEnableFlg(bankAccount.getEnableFlg().getValue());

		// 画面表示情報を取得し、更新対象であることのメッセージを設定
		BankAccountInfoManageResponse response = createBankAccountInfoManageResponse(userId, form);
		response.addMessage("銀行名「" + bankAccount.getBankName().getValue() + "」の銀行口座を更新します。");

		return response;
	}

	/**
	 *<pre>
	 * 銀行口座情報登録・更新時のバリデーションチェックエラー時処理
	 *
	 * 情報管理(銀行口座)更新画面で登録実行時のバリデーションチェックNGとなった場合の各画面表示項目を取得します。
	 * バリデーションチェック結果でNGの場合に呼び出してください。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 銀行口座情報入力フォームの入力値
	 * @return 情報管理(銀行口座)更新画面の表示情報
	 *
	 */
	public BankAccountInfoManageResponse readUpdateBindingErrorSetInfo(LoginUserInfo user, BankAccountInfoForm inputForm) {
		log.debug("readUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		return createBankAccountInfoManageResponse(UserId.from(user.getUserId()), inputForm);
	}

	/**
	 *<pre>
	 * 銀行口座情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param bankAccountForm 銀行口座情報入力フォームの入力値
	 * @return 情報管理(銀行口座)画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public BankAccountInfoManageResponse execAction(LoginUserInfo user, BankAccountInfoForm bankAccountForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",bankAccountForm=" + bankAccountForm);

		BankAccountInfoManageResponse response = BankAccountInfoManageResponse.getInstance(bankAccountForm);

		UserId userId = UserId.from(user.getUserId());

		// 未入力の場合は有効として扱う(デフォルトtrue)
		boolean enableFlg = bankAccountForm.getEnableFlg() == null ? true : bankAccountForm.getEnableFlg();

		// 現在の登録件数を取得(上限99件チェック用)
		int count = bankAccountRepository.countById(SearchQueryUserId.from(userId));

		// 既存データの表示順更新データ
		List<BankAccount> sortValueUpdateList = new ArrayList<>();

		if(Objects.equals(bankAccountForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			/* 新規銀行口座情報を追加 */
			// 追加後の件数を算出
			count++;
			// 上限99件チェック
			if(count > 99) {
				response.addErrorMessage("銀行口座は99件より多く登録できません。管理者に問い合わせてください。");
				return response;
			}

			// 表示順の決定、及び既存データの表示順調整
			if(bankAccountForm.getBankAccountSort() == null || bankAccountForm.getBankAccountSort() > count) {
				// 未入力、または末尾より後ろの指定の場合、末尾(count)に設定(既存データの調整不要)
				bankAccountForm.setBankAccountSort(count);

			} else if(bankAccountForm.getBankAccountSort() < count) {
				// 既存データの間に割り込む場合、割り込み先以降の既存データを表示順+1で後ろにずらす対象として取得
				BankAccountInquiryList sortList = bankAccountRepository.findById(SearchQueryUserIdAndBankAccountSort.from(
						userId, BankAccountSort.from(bankAccountForm.getBankAccountSort())));

				if(!sortList.isEmpty()) {
					sortList.getValues().forEach(data -> sortValueUpdateList.add(createShiftedData(data, 1)));
				}
			}

			// 新規銀行口座情報(ドメイン)を生成
			BankAccount bankAccount = BankAccount.from(
					// ユーザID
					userId,
					// 銀行口座コード(追加後の件数をそのままコードとして使用)
					BankAccountCode.from(count),
					// 銀行名
					BankName.from(bankAccountForm.getBankName()),
					// 銀行口座メモ
					BankAccountMemo.from(bankAccountForm.getBankAccountMemo()),
					// 表示順
					BankAccountSort.from(String.format("%02d", bankAccountForm.getBankAccountSort())),
					// 有効/無効フラグ
					EnableFlg.from(enableFlg));

			// 銀行口座テーブルに追加
			int addCount = bankAccountRepository.add(bankAccount);
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("銀行口座テーブルへの追加件数が不正でした。[件数=" + addCount + "][add data:" + bankAccount + "]");
			}

			response.addMessage("新規銀行口座を追加しました。[code:" + bankAccount.getBankAccountCode() + "]" + bankAccount.getBankName());

		} else if(Objects.equals(bankAccountForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			/* 既存銀行口座情報を更新 */
			// 更新前の表示順(画面の非表示項目から引き継いだ値)が未設定の場合、不正データとしてエラー
			if(!StringUtils.hasLength(bankAccountForm.getBankAccountSortBefore())) {
				throw new MyHouseholdAccountBookRuntimeException(
						"旧表示順の値が不正です。管理者に問い合わせてください。bankAccountSortBefore=" + bankAccountForm.getBankAccountSortBefore());
			}
			// 上限99件チェック
			if(count > 99) {
				response.addErrorMessage("銀行口座が99件より多く登録されているため銀行口座情報を更新できません。管理者に問い合わせてください。");
				return response;
			}

			// 表示順の決定(末尾より後ろの指定の場合、末尾(count)に補正)
			if(bankAccountForm.getBankAccountSort() == null || bankAccountForm.getBankAccountSort() > count) {
				bankAccountForm.setBankAccountSort(count);
			}
			BankAccountSort newBankAccountSort = BankAccountSort.from(bankAccountForm.getBankAccountSort());
			// 表示順が変更された場合、変更前後の間にある既存データの表示順をずらして調整
			if(!newBankAccountSort.getValue().equals(bankAccountForm.getBankAccountSortBefore())) {
				log.debug(" 既存データの表示順調整ありbankAccountSort Before:" + bankAccountForm.getBankAccountSortBefore() + ",New=" + newBankAccountSort);

				if(bankAccountForm.getBankAccountSortBefore().compareTo(newBankAccountSort.getValue()) > 0) {
					// 表示順を前方(若い番号)に変更する場合、変更後～変更前の間の既存データを表示順+1で後ろにずらす対象として取得
					int searchBIntVal = Integer.parseInt(bankAccountForm.getBankAccountSortBefore()) - 1;
					BankAccountInquiryList sortList = bankAccountRepository.findById(SearchQueryUserIdAndBankAccountSortBetweenAB.from(
							userId, newBankAccountSort, BankAccountSort.from(searchBIntVal)));
					if(!sortList.isEmpty()) {
						sortList.getValues().forEach(updData -> sortValueUpdateList.add(createShiftedData(updData, 1)));
					}

				} else {
					// 表示順を後方(大きい番号)に変更する場合、変更前～変更後の間の既存データを表示順-1で前にずらす対象として取得
					int searchAIntVal = Integer.parseInt(bankAccountForm.getBankAccountSortBefore()) + 1;
					BankAccountInquiryList sortList = bankAccountRepository.findById(SearchQueryUserIdAndBankAccountSortBetweenAB.from(
							userId, BankAccountSort.from(searchAIntVal), newBankAccountSort));
					if(!sortList.isEmpty()) {
						sortList.getValues().forEach(updData -> sortValueUpdateList.add(createShiftedData(updData, -1)));
					}
				}
			}

			// 更新対象の銀行口座情報(ドメイン)を生成
			BankAccount bankAccount = BankAccount.from(
					// ユーザID
					userId,
					// 銀行口座コード
					BankAccountCode.from(bankAccountForm.getBankAccountCode()),
					// 銀行名
					BankName.from(bankAccountForm.getBankName()),
					// 銀行口座メモ
					BankAccountMemo.from(bankAccountForm.getBankAccountMemo()),
					// 表示順
					newBankAccountSort,
					// 有効/無効フラグ
					EnableFlg.from(enableFlg));
			// 銀行口座テーブルを更新
			int updateCount = bankAccountRepository.update(bankAccount);
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("銀行口座テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + bankAccount + "]");
			}

			response.addMessage("銀行口座を更新しました。[code:" + bankAccount.getBankAccountCode() + "]" + bankAccount.getBankName());

		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + bankAccountForm.getAction());
		}

		// 既存データのソート順を調整
		sortValueUpdateList.forEach(updateSortData -> {
			int updateSortDataCount = bankAccountRepository.updateBankAccountSort(updateSortData);
			if(updateSortDataCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException(
						"銀行口座テーブルへの更新件数が不正でした。[件数=" + updateSortDataCount + "][update data:" + updateSortData + "]");
			}
		});

		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 指定した銀行口座コードに対応する銀行口座情報の有効/無効フラグを反転し、更新します。
	 * 紙芝居(ManageBankAccount.html)の「無効化／有効化」ボタン押下時の処理に対応します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param bankAccountCodeStr 対象の銀行口座コード
	 * @return 情報管理(銀行口座)画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public BankAccountInfoManageResponse execToggleEnableFlg(LoginUserInfo user, String bankAccountCodeStr) {
		log.debug("execToggleEnableFlg:userid=" + user.getUserId() + ",bankAccountCode=" + bankAccountCodeStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:銀行口座コード
		BankAccountCode bankAccountCode = BankAccountCode.from(bankAccountCodeStr);

		// 銀行口座コードに対応する銀行口座情報を取得
		BankAccount bankAccount = bankAccountRepository.findById(SearchQueryUserIdAndBankAccountCode.from(userId, bankAccountCode));
		if(bankAccount == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の銀行口座情報が存在しません。管理者に問い合わせてください。bankAccountCode:" + bankAccountCode);
		}

		/* 有効/無効フラグを反転して更新 */
		// 有効/無効フラグ以外は既存値をそのまま再利用した新しい銀行口座情報を生成
		BankAccount toggledBankAccount = bankAccount.toggleEnableFlg();
		// 銀行口座テーブルを更新
		int updateCount = bankAccountRepository.update(toggledBankAccount);
		if(updateCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException(
					"銀行口座テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + toggledBankAccount + "]");
		}

		/* 情報管理(銀行口座)初期表示画面の表示情報を生成 */
		BankAccountInfoForm addForm = new BankAccountInfoForm();
		// アクション：新規登録(トグル後は常に新規登録モードの初期画面に戻す)
		addForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 画面表示情報を取得
		BankAccountInfoManageResponse response = createBankAccountInfoManageResponse(userId, addForm);
		response.setTransactionSuccessFull();
		// 有効化/無効化いずれが行われたかに応じたメッセージを設定
		response.addMessage("銀行名「" + toggledBankAccount.getBankName().getValue() + "」の銀行口座を"
				+ (toggledBankAccount.getEnableFlg().getValue() ? "有効化" : "無効化") + "しました。");

		return response;
	}

	/**
	 *<pre>
	 * 指定したユーザIDで登録されている銀行口座情報を取得し、銀行口座情報入力フォームをもとに情報管理(銀行口座)画面の表示情報を生成して返します。
	 *</pre>
	 * @param userId 表示対象のユーザID
	 * @param form 銀行口座情報入力フォーム
	 * @return 情報管理(銀行口座)画面の表示情報
	 *
	 */
	private BankAccountInfoManageResponse createBankAccountInfoManageResponse(UserId userId, BankAccountInfoForm form) {

		BankAccountInfoManageResponse response = BankAccountInfoManageResponse.getInstance(form);

		BankAccountInquiryList searchResult = bankAccountRepository.findById(SearchQueryUserId.from(userId));
		if(searchResult.isEmpty()) {
			response.addMessage("銀行口座情報取得結果が0件です。");
		} else {
			response.addBankAccountList(searchResult.getValues().stream().map(domain ->
				BankAccountListItem.from(
						// 銀行口座コード
						domain.getBankAccountCode(),
						// 銀行名
						domain.getBankName(),
						// 銀行口座メモ
						domain.getBankAccountMemo(),
						// 表示順
						domain.getBankAccountSort(),
						// 有効/無効フラグ
						domain.getEnableFlg())
			).collect(Collectors.toUnmodifiableList()));
		}
		return response;
	}

	/**
	 *<pre>
	 * 指定の銀行口座情報のうち、表示順の値を指定した増減分加算・減算した値で銀行口座情報を生成して返します。
	 *</pre>
	 * @param data 生成対象の銀行口座情報
	 * @param add 表示順の増減分する値(1 or -1)
	 * @return 表示順を増減した値で新たに生成した銀行口座情報
	 *
	 */
	private BankAccount createShiftedData(BankAccount data, int add) {
		return BankAccount.from(
				// ユーザID：既存値をそのまま再利用
				data.getUserId(),
				// 銀行口座コード：既存値をそのまま再利用
				data.getBankAccountCode(),
				// 銀行名：既存値をそのまま再利用
				data.getBankName(),
				// 銀行口座メモ：既存値をそのまま再利用
				data.getBankAccountMemo(),
				// 表示順：増減分(add)を加算した新しい値
				BankAccountSort.from(String.format("%02d", Integer.parseInt(data.getBankAccountSort().getValue()) + add)),
				// 有効/無効フラグ：既存値をそのまま再利用
				data.getEnableFlg());
	}
}
