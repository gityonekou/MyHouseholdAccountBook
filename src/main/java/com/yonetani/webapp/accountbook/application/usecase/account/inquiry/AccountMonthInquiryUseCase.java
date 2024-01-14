/**
 * マイ家計簿 各月の収支取得ユースケースです。
 * ・現在の決算月の収支取得
 * ・指定月の収支取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.application.usecase.account.utils.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenseInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.common.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.AccountMonthInquiryRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenseInquiryRepository;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearMonthInquiryForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse.ExpenditureItem;

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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AccountMonthInquiryUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	// 指定月の支出項目リスト取得リポジトリー
	private final AccountMonthInquiryRepository repository;
	// 指定月の収支取得リポジトリー
	private final IncomeAndExpenseInquiryRepository syuusiRepository;
	
	/**
	 *<pre>
	 * 現在の決算月の収支を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(UserSession user) {
		log.debug("read:userid=" + user.getUserId());
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance();
		
		/* ユーザIDに対応する現在の対象年月の値を取得 */
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(user.getUserId());
		if(yearMonth.isEmpty()) {
			response.addMessage(yearMonth.getMessage());
			return response;
		} else {
			response.setYearMonth(yearMonth.getYearMonth().toString());
		}
		
		/* ユーザID,現在の対象年月を条件に支出項目のリストと収支金額を取得 */
		// ユーザID,現在の対象年月をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth inquiryModel;
		
			inquiryModel = SearchQueryUserIdAndYearMonth.from(
					user.getUserId(), yearMonth.getYearMonth().toString());
		
		// 支出項目のリストと収支金額を取得
		execRead(inquiryModel, response);
		
		return response;
	}

	/**
	 *<pre>
	 * 要求された指定月の収支を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param request 指定月の収支取得用リクエスト情報
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	public AccountMonthInquiryResponse read(UserSession user, YearMonthInquiryForm request) {
		log.debug("read:userid=" + user.getUserId() + ",form=" + request);
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(request);
		
		/* ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth inquiryModel;
		
			inquiryModel = SearchQueryUserIdAndYearMonth.from(
					user.getUserId(), request.getTargetYearMonth());
		// 支出項目のリストと収支金額を取得
		execRead(inquiryModel, response);
		return response;
	}
	
	/**
	 *<pre>
	 * 支出項目のリストと収支金額を取得します。
	 *</pre>
	 * @param inquiryModel 検索条件(ユーザID, 年月)
	 * @param response 月間収支情報(レスポンス)
	 *
	 */
	private void execRead(SearchQueryUserIdAndYearMonth inquiryModel, AccountMonthInquiryResponse response) {
		
		log.debug("検索条件=" + inquiryModel);
		
		// ユーザID,対象年月を検索条件に支出項目のリストを取得
		AccountMonthInquiryExpenditureItemList resultList = repository.selectExpenditureItem(inquiryModel);
		log.debug("検索結果(支出項目のリスト)=" + resultList);

		// 支出項目のリスト(ドメインモデル)をレスポンスに設定
		if(CollectionUtils.isEmpty(resultList.getValues())) {
			// 支出項目のリストが0件の場合、メッセージを設定
			response.addMessage("支出項目のリスト取得結果が0件です。");
		} else {
			// 支出項目のリスト(ドメインモデル)から支出項目のリスト(レスポンス)への変換
			response.addExpenditureItemList(convertExpenditureItemList(resultList));
		}
		// ユーザID,現在の対象年月を条件に該当月の収支金額を取得
		IncomeAndExpenseInquiryItem sisyutuResult = syuusiRepository.select(inquiryModel);
		if(sisyutuResult.isEmpty()) {
			// 検索結果が0件の場合、メッセージを設定
			response.addMessage("該当月の収支データがありません。");
		} else {
			// 収支情報(ドメインモデル)から収支情報(レスポンス)への変換
			// 収入金額
			response.setSyuunyuuKingaku(sisyutuResult.getSyuunyuuKingaku().toString());
			// 支出金額
			response.setSisyutuKingaku(sisyutuResult.getSisyutuKingaku().toString());
			// 支出予定金額
			response.setSisyutuYoteiKingaku(sisyutuResult.getSisyutuYoteiKingaku().toString());
			// 収支金額
			response.setSyuusiKingaku(sisyutuResult.getSyuusiKingaku().toString());
		}
	}
	
	/**
	 *<pre>
	 * 支出項目のリスト(ドメインモデル)を支出項目のリスト(レスポンス)に変換して返却
	 *</pre>
	 * @param resultList 支出項目のリスト(ドメインモデル)
	 * @return 支出項目のリスト(レスポンス)
	 *
	 */
	private List<ExpenditureItem> convertExpenditureItemList(AccountMonthInquiryExpenditureItemList resultList) {
		// 返却するリストを不変オブジェクトに変換する
		return resultList.getValues().stream().map(domain ->
		AccountMonthInquiryResponse.ExpenditureItem.form(
				domain.getSisyutuItemLevel().getValue(),
				domain.getSisyutuItemName().toString(),
				domain.getSisyutuKingaku().toString(),
				domain.getSisyutuKingakuB().toSisyutuKingakuBString(),
				domain.getSisyutuKingakuB().toPercentageString(),
				domain.getSiharaiDate().toString(),
				domain.getClosingFlg().getValue())).collect(Collectors.toUnmodifiableList());
	}
	
}
