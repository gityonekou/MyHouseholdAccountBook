/**
 * マイ家計簿 年間収支取得ユースケースです。
 * ・指定年の年間収支(マージ)
 * ・指定年の年間収支(明細)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.application.usecase.account.utils.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenseInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.AccountYearMeisaiInquiryRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenseInquiryRepository;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearInquiryForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMageInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMageInquiryResponse.MageInquiryListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMeisaiInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMeisaiInquiryResponse.MeisaiInquiryListItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿 年間収支取得ユースケースです。
 * ・指定年の年間収支(マージ)
 * ・指定年の年間収支(明細)
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
public class AccountYearInquiryUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	
	// 指定年度の収支(マージ)結果取得リポジトリー
	private final IncomeAndExpenseInquiryRepository repositoryMage;
	
	// 指定年度の収支(明細)結果取得リポジトリー
	private final AccountYearMeisaiInquiryRepository repositoryMeisai;
	
	/**
	 *<pre>
	 * 指定年の年間収支(マージ)の値を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param request 指定年の収支取得用リクエスト情報
	 * @return 年間収支(マージ)情報
	 *
	 */
	public AccountYearMageInquiryResponse readMage(UserSession user, YearInquiryForm request) {
		log.debug("read:userid=" + user.getUserId() + ",form=" + request);
		
		// レスポンスを生成
		AccountYearMageInquiryResponse response = AccountYearMageInquiryResponse.getInstance(request);
		
		/* ユーザIDに対応する現在の対象年月の値を取得 */
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(user.getUserId());
		if(yearMonth.isEmpty()) {
			response.addMessage(yearMonth.getMessage());
			return response;
		} else {
			response.setTargetYearMonth(yearMonth.getYearMonth().toString());
		}
		
		/* ユーザID,入力された対象年度を条件に年間収支(マージ)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(
				user.getUserId(), request.getTargetYear());
		log.debug("検索条件=" + inquiryModel);
		
		// ユーザID、対象年度を条件に年間収支(マージ)のリスト(ドメインモデル)を取得
		IncomeAndExpenseInquiryList resultList = repositoryMage.select(inquiryModel);
		log.debug("検索結果(支出項目のリスト)=" + resultList);
		
		// 年間収支(マージ)のリスト(ドメインモデル)をレスポンスに設定
		if(resultList.isEmpty()) {
			// 件数が0件の場合、メッセージを設定
			response.addMessage("年間収支(マージ)取得結果が0件です。");
		} else {
			// 年間収支(マージ)(ドメインモデル)から年間収支(マージ)(レスポンス)への変換
			response.addMageInquiryList(convertMageList(resultList));
			/* 合計値を設定 */
			// 収入金額合計
			response.setSyuunyuuKingakuGoukei(resultList.getSyuunyuuKingakuGoukei().toString());
			// 支出金額合計
			response.setSisyutuKingakuGoukei(resultList.getSisyutuKingakuGoukei().toString());
			// 支出予定金額合計
			response.setSisyutuYoteiKingakuGoukei(resultList.getSisyutuYoteiKingakuGoukei().toString());
			// 収支合計
			response.setSyuusiKingakuGoukei(resultList.getSyuusiKingakuGoukei().toString());
		}
		
		return response;
	}
	
	/**
	 *<pre>
	 * 年間収支(マージ)(ドメインモデル)を年間収支(マージ)(レスポンス)に変換して返却
	 *</pre>
	 * @param resultList 年間収支(マージ)(ドメインモデル)
	 * @return 年間収支(マージ)(レスポンス)
	 *
	 */
	private List<MageInquiryListItem> convertMageList(IncomeAndExpenseInquiryList resultList) {
		return resultList.getValues().stream().map(domain ->
			AccountYearMageInquiryResponse.MageInquiryListItem.from(
					domain.getMonth().toString(),
					domain.getSyuunyuuKingaku().toString(),
					domain.getSisyutuKingaku().toString(),
					domain.getSisyutuYoteiKingaku().toString(),
					domain.getSyuusiKingaku().toString())
		).collect(Collectors.toUnmodifiableList());
	}

	/**
	 *<pre>
	 * 指定年の年間収支(明細)の値を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param request 指定年の収支取得用リクエスト情報
	 * @return 年間収支(明細)情報
	 *
	 */
	public AccountYearMeisaiInquiryResponse readMeisai(UserSession user, YearInquiryForm request) {
		log.debug("read:userid=" + user.getUserId() + ",form=" + request);
		
		// レスポンスを生成
		AccountYearMeisaiInquiryResponse response = AccountYearMeisaiInquiryResponse.getInstance(request);
		
		/* ユーザIDに対応する現在の対象年月の値を取得 */
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(user.getUserId());
		if(yearMonth.isEmpty()) {
			response.addMessage(yearMonth.getMessage());
			return response;
		} else {
			response.setTargetYearMonth(yearMonth.getYearMonth().toString());
		}
		
		/* ユーザID,入力された対象年度を条件に年間収支(明細)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(
				user.getUserId(), request.getTargetYear());
		log.debug("検索条件=" + inquiryModel);
		
		// ユーザID、対象年度を条件に年間収支(明細)のリスト(ドメインモデル)を取得
		AccountYearMeisaiInquiryList resultList = repositoryMeisai.select(inquiryModel);
		log.debug("検索結果(支出項目のリスト)=" + resultList);
		
		// 年間収支(明細)のリスト(ドメインモデル)をレスポンスに設定
		if(resultList.isEmpty()) {
			// 件数が0件の場合、メッセージを設定
			response.addMessage("年間収支(明細)取得結果が0件です。");
		} else {
			// 年間収支(マージ)(ドメインモデル)から年間収支(マージ)(レスポンス)への変換
			response.addMeisaiInquiryList(convertMeisaiList(resultList));
			/* 合計値を設定 */
			// 事業経費合計
			response.setJigyouKeihiKingakuGoukei(resultList.getJigyouKeihiKingakuGoukei().toString());
			// 固定(非課税)合計
			response.setKoteiHikazeiKingakuGoukei(resultList.getKoteiHikazeiKingakuGoukei().toString());
			// 固定(課税)合計
			response.setKoteiKazeiKingakuGoukei(resultList.getKoteiKazeiKingakuGoukei().toString());
			// 衣類住居設備合計
			response.setIruiJyuukyoSetubiKingakuGoukei(resultList.getIruiJyuukyoSetubiKingakuGoukei().toString());
			// 飲食日用品合計
			response.setInsyokuNitiyouhinKingakuGoukei(resultList.getInsyokuNitiyouhinKingakuGoukei().toString());
			// 趣味娯楽合計
			response.setSyumiGotakuKingakuGoukei(resultList.getSyumiGotakuKingakuGoukei().toString());
			// 支出B合計
			response.setSisyutuKingakuBGoukei(resultList.getSisyutuKingakuBGoukei().toSisyutuKingakuBString());
			// 支出合計
			response.setSisyutuKingakuGoukei(resultList.getSisyutuKingakuGoukei().toString());
			// 収支合計
			response.setSyuusiKingakuGoukei(resultList.getSyuusiKingakuGoukei().toString());
		}
		return response;
	}

	/**
	 *<pre>
	 * 年間収支(明細)(ドメインモデル)を年間収支(明細)(レスポンス)に変換して返却
	 *</pre>
	 * @param resultList 年間収支(明細)(ドメインモデル)
	 * @return 年間収支(明細)(レスポンス)
	 *
	 */
	private List<MeisaiInquiryListItem> convertMeisaiList(AccountYearMeisaiInquiryList resultList) {
		return resultList.getValues().stream().map(domain ->
			AccountYearMeisaiInquiryResponse.MeisaiInquiryListItem.from(
					domain.getMonth().toString(),
					domain.getJigyouKeihiKingaku().toString(),
					domain.getKoteiHikazeiKingaku().toString(),
					domain.getKoteiKazeiKingaku().toString(),
					domain.getIruiJyuukyoSetubiKingaku().toString(),
					domain.getInsyokuNitiyouhinKingaku().toString(),
					domain.getSyumiGotakuKingaku().toString(),
					domain.getSisyutuKingakuB().toSisyutuKingakuBString(),
					domain.getSisyutuKingaku().toString(),
					domain.getSyuusiKingaku().toString())
		).collect(Collectors.toUnmodifiableList());
	}
}
