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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearInquiryRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearInquiryTargetYearInfo;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMageInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMageInquiryResponse.MageInquiryListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMeisaiInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMeisaiInquiryResponse.MeisaiInquiryListItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

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
	
	// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEリポジトリー：指定年度の収支(マージ)結果取得
	private final IncomeAndExpenditureTableRepository repositoryMage;
	// 指定年度の収支(明細)結果取得リポジトリー
	private final SisyutuKingakuTableRepository repositoryMeisai;
	
	/**
	 *<pre>
	 * 指定年の年間収支(マージ)の値を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearStr 表示対象の年度
	 * @param returnYearMonthStr 各月の収支画面に戻る場合に表示する年月の値
	 * @return 年間収支(マージ)情報
	 *
	 */
	public AccountYearMageInquiryResponse readMage(LoginUserInfo user, String targetYearStr, String returnYearMonthStr) {
		log.debug("read:userid=" + user.getUserId() + ",targetYear=" + targetYearStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:年
		TargetYear targetYear = TargetYear.from(targetYearStr);
		
		// レスポンスを生成
		AccountYearMageInquiryResponse response = AccountYearMageInquiryResponse.getInstance(
				AccountYearInquiryTargetYearInfo.from(targetYearStr, returnYearMonthStr));
		
		/* ユーザID,入力された対象年度を条件に年間収支(マージ)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(userId, targetYear);
		
		// ユーザID、対象年度を条件に年間収支(マージ)のリスト(ドメインモデル)を取得
		IncomeAndExpenditureInquiryList resultList = repositoryMage.select(inquiryModel);
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
			// 積立金取崩金額合計
			response.setWithdrewKingakuGoukei(resultList.getWithdrewKingakuGoukei().toString());
			// 支出予定金額合計
			response.setSisyutuYoteiKingakuGoukei(resultList.getSisyutuYoteiKingakuGoukei().toString());
			// 支出金額合計
			response.setSisyutuKingakuGoukei(resultList.getSisyutuKingakuGoukei().toString());
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
	private List<MageInquiryListItem> convertMageList(IncomeAndExpenditureInquiryList resultList) {
		return resultList.getValues().stream().map(domain ->
			AccountYearMageInquiryResponse.MageInquiryListItem.from(
					domain.getTargetMonth().getValue(),
					domain.getSyuunyuuKingaku().toString(),
					domain.getWithdrewKingaku().toString(),
					domain.getSisyutuYoteiKingaku().toFormatString(),
					domain.getSisyutuKingaku().toString(),
					domain.getSyuusiKingaku().toString())
		).collect(Collectors.toUnmodifiableList());
	}

	/**
	 *<pre>
	 * 指定年の年間収支(明細)の値を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearStr 表示対象の年度
	 * @param returnYearMonthStr 各月の収支画面に戻る場合に表示する年月の値
	 * @return 年間収支(明細)情報
	 *
	 */
	public AccountYearMeisaiInquiryResponse readMeisai(LoginUserInfo user, String targetYearStr, String returnYearMonthStr) {
		log.debug("read:userid=" + user.getUserId() + ",targetYear=" + targetYearStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:年
		TargetYear targetYear = TargetYear.from(targetYearStr);
		
		// レスポンスを生成
		AccountYearMeisaiInquiryResponse response = AccountYearMeisaiInquiryResponse.getInstance(
				AccountYearInquiryTargetYearInfo.from(targetYearStr, returnYearMonthStr));
		
		/* ユーザID,入力された対象年度を条件に年間収支(明細)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(userId, targetYear);
		// ユーザID、対象年度を条件に年間収支(明細)のリスト(ドメインモデル)を取得
		AccountYearMeisaiInquiryList resultList = repositoryMeisai.select(inquiryModel);
		// 年間収支(明細)のリスト(ドメインモデル)をレスポンスに設定
		if(resultList.isEmpty()) {
			// 件数が0件の場合、メッセージを設定
			response.addMessage("年間収支(明細)取得結果が0件です。");
		} else {
			// 年間収支(マージ)(ドメインモデル)から年間収支(マージ)(レスポンス)への変換
			response.addMeisaiInquiryList(convertMeisaiList(resultList));
			/* 合計値を設定 */
			// 収入金額合計
			response.setSyuunyuuKingakuGoukei(resultList.getSyuunyuuKingakuGoukei().toString());
			// 積立金取崩金額合計
			response.setWithdrewKingakuGoukei(resultList.getWithdrewKingakuGoukei().toString());
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
			response.setSisyutuKingakuBCGoukei(resultList.getSisyutuKingakuBCGoukei().toFormatString());
			// 支出BC合計のうち、支出B合計の割合
			response.setPercentageBGoukei(resultList.getSisyutuKingakuBCGoukei().getSisyutuKingakuBPercentage());
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
					domain.getMonth().getValue(),
					domain.getSyuunyuuKingaku().toString(),
					domain.getWithdrewKingaku().toString(),
					domain.getJigyouKeihiKingaku().toString(),
					domain.getKoteiHikazeiKingaku().toString(),
					domain.getKoteiKazeiKingaku().toString(),
					domain.getIruiJyuukyoSetubiKingaku().toString(),
					domain.getInsyokuNitiyouhinKingaku().toString(),
					domain.getSyumiGotakuKingaku().toString(),
					domain.getSisyutuKingakuBC().toFormatString(),
					domain.getSisyutuKingakuBC().getSisyutuKingakuBPercentage(),
					domain.getSisyutuKingaku().toString(),
					domain.getSyuusiKingaku().toString())
		).collect(Collectors.toUnmodifiableList());
	}
	
	/**
	 *<pre>
	 * 各月の収支参照画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYear 表示対象の年度
	 * @param targetMonth 表示対象の月度
	 * @return 各月の収支参照画面リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnInquiryMonthRedirectInfo(LoginUserInfo user, String targetYear, String targetMonth) {
		log.debug("readReturnInquiryMonthRedirectInfo:userid=" + user.getUserId() + ",targetYear=" + targetYear + ",targetMonth=" + targetMonth);
		
		// 表示対象の年度、月度の値からドメインタイプ:対象年月を生成
		TargetYearMonth targetYearMonth = TargetYearMonth.from(
				TargetYear.from(targetYear).getValue() + TargetMonth.from(targetMonth).getValue());
		// 各月の収支画面にリダイレクトするためのリダイレクト情報を生成して返却
		AccountYearInquiryRedirectResponse response
			= AccountYearInquiryRedirectResponse.getInquiryMonthRedirectInstance(targetYearMonth.getValue());
		return response;
		
	}
}
