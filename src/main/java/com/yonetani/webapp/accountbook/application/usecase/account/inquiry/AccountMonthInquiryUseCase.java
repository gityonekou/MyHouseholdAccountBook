/**
 * マイ家計簿 各月の収支取得ユースケースです。
 * ・現在の決算月の収支取得
 * ・指定月の収支取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/23 : 1.00.00  新規作成
 * 2025/12/21 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/05/09 : 1.01.01  リファクタリング追加対応(対象年月ドメインの集約)
 * 2026/06/13 : 1.02.00  支出別一覧追加対応(ExpenditureTableRepository追加・viewType対応・execRead処理順番見直し)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.application.usecase.common.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.service.account.inquiry.IncomeAndExpenditureConsistencyService;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse.ExpenditureListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryTargetYearMonthInfo;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿 各月の収支取得ユースケースです。
 * ・現在の決算月の収支取得
 * ・指定月の収支取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AccountMonthInquiryUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	// 指定月の支出金額情報を取得するリポジトリー
	private final SisyutuKingakuTableRepository sisyutuRepository;
	// 指定月の収支金額を取得するポジトリー
	private final IncomeAndExpenditureTableRepository syuusiRepository;
	// 収支整合性検証ドメインサービス
	private final IncomeAndExpenditureConsistencyService consistencyService;
	// 指定月の支出情報を取得するリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	
	/**
	 *<pre>
	 * 現在の決算月の収支を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(LoginUserInfo user) {
		log.debug("read:userid=" + user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		TargetYearMonth yearMonth = userInquiry.getTargetYearMonth(UserId.from(user.getUserId()));
		
		// 収支画面に表示する対象年月情報を生成
		AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo = AccountMonthInquiryTargetYearMonthInfo.from(
				yearMonth.getValue());
		
		// ユーザID,現在の対象年月を条件に支出項目のリストと収支金額を取得しレスポンス情報を返却
		return execRead(user, targetYearMonthInfo, "item");
	}

	/**
	 *<pre>
	 * 要求された指定月の収支を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth 表示対象の年月
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);

		// ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得しレスポンス情報を返却
		return execRead(user, AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth), "item");
	}

	/**
	 *<pre>
	 * 要求された指定月の収支を取得します。
	 *
	 * 「戻り先の対象の年月」の値は指定した「表示対象の年月」に対応する収支情報がある場合は「戻り先の対象の年月」＝「表示対象の年月」
	 * となるが、レスポンスを生成した段階だと値があるかどうかを判定できないのでサーバー側処理側ではその判定は行わない。
	 * (レスポンスを初期生成時、前画面から渡されてきた「戻り先の対象の年月」の値をそのまま変更不可の値でレスポンスに設定する）
	 * 各月の収支画面のhtmlのソースでreturnYearMonth項目に値を設定時、「表示対象の年月」の値を設定することで
	 * 画面表示データがある場合に「戻り先の対象の年月」の値を更新する処理を代用しているので注意
	 * ⇒表示対象の収支情報がない場合、登録確認画面を表示するが、その画面以降でキャンセルなどで各月の収支画面画面に
	 * 戻る場合は「戻り先の対象の年月」をもとに一つ前の画面に戻ることが可能となっています。
	 *
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth 表示対象の年月
	 * @param returnYearMonth 戻り先の対象の年月
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(LoginUserInfo user, String targetYearMonth, String returnYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",returnYearMonth=" + returnYearMonth);

		// ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得しレスポンス情報を返却
		return execRead(user, AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth, returnYearMonth), "item");
	}

	/**
	 *<pre>
	 * 要求された指定月の収支を、表示種別を指定して取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth 表示対象の年月
	 * @param returnYearMonth 戻り先の対象の年月
	 * @param viewType 表示種別（"item"=支出項目別、"expenditure"=支出別）
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(LoginUserInfo user, String targetYearMonth, String returnYearMonth,
			String viewType) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth
				+ ",returnYearMonth=" + returnYearMonth + ",viewType=" + viewType);

		// ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得しレスポンス情報を返却
		return execRead(user, AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth, returnYearMonth), viewType);
	}
	
	/**
	 *<pre>
	 * 買い物登録画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @return 買い物登録画面リダイレクト情報
	 *
	 */
	public AbstractResponse readShoppingAddRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readShoppingAddRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		AccountMonthInquiryRedirectResponse response
			= AccountMonthInquiryRedirectResponse.getShoppingAddRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 収支登録画面(更新)にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @return 収支登録画面(更新)リダイレクト情報
	 *
	 */
	public AbstractResponse readAccountMonthUpdateRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readAccountMonthUpdateRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		AccountMonthInquiryRedirectResponse response
			= AccountMonthInquiryRedirectResponse.getAccountMonthUpdateRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 支出項目のリストと収支金額を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonthInfo 表示対象の対象年月情報
	 * @param viewType 表示種別（"item"=支出項目別、"expenditure"=支出別。不正値は"item"として扱う）
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	private AccountMonthInquiryResponse execRead(
			LoginUserInfo user, AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo, String viewType) {
		log.debug("execRead:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonthInfo.getTargetYearMonth()
				+ ",viewType=" + viewType);

		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(targetYearMonthInfo);

		// viewType の正規化（不正値は "item" として扱う）
		String normalizedViewType = "expenditure".equals(viewType) ? "expenditure" : "item";
		response.setViewType(normalizedViewType);

		// 検索条件(ユーザID、年月(YYYYMM))をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth searchCondition = SearchQueryUserIdAndYearMonth.from(
				UserId.from(user.getUserId()), TargetYearMonth.from(targetYearMonthInfo.getTargetYearMonth()));

		// ①ユーザID,対象年月を検索条件に支出金額情報(SisyutuKingakuTable)を取得
		AccountMonthInquiryExpenditureItemList expenditureItemList = sisyutuRepository.select(searchCondition);
		// ②ユーザID,対象年月を検索条件に収支集約(IncomeAndExpenditureTable)を取得
		IncomeAndExpenditure incomeAndExpenditure = syuusiRepository.findByPrimaryKey(searchCondition);
		// ③ユーザID,対象年月を検索条件に支出情報(ExpenditureTable)を取得(viewTypeによらず常時取得)
		AccountMonthInquiryExpenditureList monthExpenditureList = AccountMonthInquiryExpenditureList.from(
				expenditureRepository.findBy(searchCondition));

		// ④データ存在の整合性検証(収支データなし&(支出金額データあり OR 支出データあり)の場合はエラー)
		consistencyService.validateDataExistence(incomeAndExpenditure, expenditureItemList, monthExpenditureList, searchCondition);

		// ⑤収支情報(ドメインモデル)をレスポンスに設定
		if(incomeAndExpenditure.isEmpty()) {
			// 該当月の収支データがない場合、メッセージを設定
			response.addMessage("該当月の収支データがありません。");
			response.setSyuusiDataFlg(false);
			// 収支データがない場合は早期リターン(以降の明細データ設定は不要)
			return response;
			
		} else {
			// 収支整合性検証(収入・支出の合計値が収支テーブルの値と一致するかをドメインサービスで検証)
			consistencyService.validateAll(incomeAndExpenditure, searchCondition);

			// 収支情報(ドメインモデル)から収支情報(レスポンス)への変換
			// 収入金額(積立金取崩金額以外の収入金額)
			response.setSyuunyuuKingaku(incomeAndExpenditure.getRegularIncomeAmount().toFormatString());
			// 支出金額
			response.setSisyutuKingaku(incomeAndExpenditure.getExpenditureAmount().toFormatString());
			// 積立金取崩金額
			response.setWithdrewKingaku(incomeAndExpenditure.getWithdrawingAmount().toFormatString());
			// 支出予定金額
			response.setSisyutuYoteiKingaku(incomeAndExpenditure.getExpectedExpenditureAmount().toFormatString());
			// 収支金額
			response.setSyuusiKingaku(incomeAndExpenditure.getBalanceAmount().toFormatString());
		}

		// ⑥viewType=item の場合のみ、支出金額情報のリスト(ドメインモデル)をレスポンスに設定
		if("item".equals(normalizedViewType)) {
			if(expenditureItemList.isEmpty()) {
				// 支出金額情報のリストが0件の場合、メッセージを設定
				response.addMessage("登録済みの支出金額情報が0件です。");
			} else {
				// 支出金額情報のリストをレスポンスに設定(ドメインモデルからレスポンスへの変換)
				response.addExpenditureItemList(convertExpenditureItemList(expenditureItemList));
			}
		}

		// ⑦viewType=expenditure の場合のみ、支出別一覧(③取得済み)をレスポンスに設定
		if("expenditure".equals(normalizedViewType)) {
			if(!monthExpenditureList.isEmpty()) {
				response.addExpenditureList(convertExpenditureList(monthExpenditureList));
			}
			response.setExpenditureTotalAmount(monthExpenditureList.getTotalAmount().toFormatString());
		}

		return response;
	}

	/**
	 *<pre>
	 * 支出項目のリスト(ドメインモデル)を支出項目のリスト(レスポンス)に変換して返却
	 *</pre>
	 * @param resultList 支出項目のリスト(ドメインモデル)
	 * @return 支出項目のリスト(レスポンス)
	 *
	 */
	private List<ExpenditureListItem> convertExpenditureItemList(AccountMonthInquiryExpenditureItemList resultList) {
		// 返却するリストを不変オブジェクトに変換する
		return resultList.getValues().stream().map(domain ->
		AccountMonthInquiryResponse.ExpenditureListItem.form(
				domain.getExpenditureItemLevel().getValue(),
				domain.getExpenditureItemName().getValue(),
				domain.getExpenditureAmount().toFormatString(),
				domain.getMinorWasteExpenditureAmount().toFormatString(),
				domain.getMinorWasteExpenditureAmount().getPercentage(domain.getExpenditureAmount()),
				domain.getSevereWasteExpenditureAmount().toFormatString(),
				domain.getSevereWasteExpenditureAmount().getPercentage(domain.getExpenditureAmount()),
				domain.getTotalWasteExpenditureAmount().toFormatString(),
				domain.getTotalWasteExpenditureAmount().getPercentage(domain.getExpenditureAmount()),
				domain.getPaymentDate().toDisplayString())).collect(Collectors.toUnmodifiableList());
	}

	/**
	 *<pre>
	 * 支出別一覧(ドメインモデル)を支出別一覧(レスポンス)に変換して返却
	 * 支出区分に応じた表示名プレフィックスの付与、支払日・金額のフォーマット変換を行う
	 *</pre>
	 * @param list 支出別一覧(ドメインモデル)
	 * @return 支出別一覧(レスポンス)
	 *
	 */
	private List<AccountMonthInquiryResponse.ExpenditureRow> convertExpenditureList(
			AccountMonthInquiryExpenditureList list) {
		return list.getValues().stream()
				.map(domain -> {
					// 支出区分に応じた表示名のプレフィックスを生成（ラベル生成はドメインに委譲）
					String label = domain.getExpenditureCategory().toDisplayLabel();
					String prefix = label.isEmpty() ? "" : "【" + label + "】";
					return AccountMonthInquiryResponse.ExpenditureRow.from(
							domain.getExpenditureCode().getValue(),
							prefix + domain.getExpenditureName().getValue(),
							domain.getPaymentDate().toDayString(),
							domain.getExpenditureAmount().toFormatString(),
							StringUtils.hasLength(domain.getExpenditureDetailContext().getValue())
									? domain.getExpenditureDetailContext().getValue() : "");
				})
				.collect(Collectors.toUnmodifiableList());
	}
}
