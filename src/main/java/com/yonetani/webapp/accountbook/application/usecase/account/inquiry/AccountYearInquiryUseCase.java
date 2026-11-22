/**
 * マイ家計簿 年間収支取得ユースケースです。
 * ・指定年の年間収支(マージ)
 * ・指定年の年間収支(明細)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/10/09 : 1.00.00                      新規作成
 * 2025/12/28 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/10/12 : 1.02.00  feature-1.03-dev1   追加リファクタリング対応(レスポンスdtoのファクトリ、ドメインモデルのファクトリの引数をドメインタイプに変更)
 *
 */

package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
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
 * @since 家計簿アプリ(1.00)
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
		// 表示する年の対象年月情報
		AccountYearInquiryTargetYearInfo targetYearInfo = AccountYearInquiryTargetYearInfo.from(targetYear, TargetYearMonth.from(returnYearMonthStr));

		/* ユーザID,入力された対象年度を条件に年間収支(マージ)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(userId, targetYear);

		// ユーザID、対象年度を条件に年間収支(マージ)のリスト(ドメインモデル)を取得
		IncomeAndExpenditureInquiryList resultList = repositoryMage.select(inquiryModel);
		// 年間収支(マージ)のリスト(ドメインモデル)が0件の場合、表示金額なしとなるレスポンスにメッセージを設定して返却
		if(resultList.isEmpty()) {
			// レスポンスを生成
			AccountYearMageInquiryResponse response = AccountYearMageInquiryResponse.getInstance(targetYearInfo);
			// 件数が0件の場合、メッセージを設定
			response.addMessage("年間収支(マージ)取得結果が0件です。");
			return response;
		}

		// 合計金額情報からレスポンスを生成
		AccountYearMageInquiryResponse response = AccountYearMageInquiryResponse.getInstance(
				// 表示する年の対象年月情報
				targetYearInfo,
				// 収入金額合計(積立金取崩金額以外の収入金額合計)
				resultList.getRegularIncomeTotalAmount(),
				// 積立金取崩金額合計
				resultList.getWithdrawingTotalAmount(),
				// 支出予定金額合計
				resultList.getExpectedExpenditureTotalAmount(),
				// 支出金額合計
				resultList.getExpenditureTotalAmount(),
				// 収支合計
				resultList.getBalanceTotalAmount());
		
		// 年間収支(マージ)(ドメインモデル)から年間収支(マージ)(レスポンス)への変換
		response.addMageInquiryList(convertMageList(resultList));

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
					domain.getTargetYearMonth().getTargetMonth(),
					domain.getRegularIncomeAmount(),
					domain.getWithdrawingAmount(),
					domain.getExpectedExpenditureAmount(),
					domain.getExpenditureAmount(),
					domain.getBalanceAmount())
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
		// 表示する年の対象年月情報
		AccountYearInquiryTargetYearInfo targetYearInfo = AccountYearInquiryTargetYearInfo.from(targetYear, TargetYearMonth.from(returnYearMonthStr));

		/* ユーザID,入力された対象年度を条件に年間収支(明細)のリストを取得 */
		// フォームオブジェクトからドメインオブジェクトに変換
		SearchQueryUserIdAndYear inquiryModel = SearchQueryUserIdAndYear.from(userId, targetYear);
		// ユーザID、対象年度を条件に年間収支(明細)のリスト(ドメインモデル)を取得
		AccountYearMeisaiInquiryList resultList = repositoryMeisai.select(inquiryModel);
		// 年間収支(明細)のリスト(ドメインモデル)が0件の場合、表示金額なしとなるレスポンスにメッセージを設定して返却
		if(resultList.isEmpty()) {
			// レスポンスを生成
			AccountYearMeisaiInquiryResponse response = AccountYearMeisaiInquiryResponse.getInstance(targetYearInfo);
			// 件数が0件の場合、メッセージを設定
			response.addMessage("年間収支(明細)取得結果が0件です。");
			return response;
		}

		// 合計金額情報からレスポンスを生成
		AccountYearMeisaiInquiryResponse response = AccountYearMeisaiInquiryResponse.getInstance(
				targetYearInfo,
				// 収入金額合計(積立金取崩金額以外の収入金額合計)
				resultList.getRegularIncomeTotalAmount(),
				// 積立金取崩金額合計
				resultList.getWithdrawingTotalAmount(),
				// 事業経費合計
				resultList.getJigyouKeihiKingakuGoukei(),
				// 固定(非課税)合計
				resultList.getKoteiHikazeiKingakuGoukei(),
				// 固定(課税)合計
				resultList.getKoteiKazeiKingakuGoukei(),
				// 衣類住居設備合計
				resultList.getIruiJyuukyoSetubiKingakuGoukei(),
				// 飲食日用品合計
				resultList.getInsyokuNitiyouhinKingakuGoukei(),
				// 趣味娯楽合計
				resultList.getSyumiGotakuKingakuGoukei(),
				// 無駄遣い合計支出金額合計(支出BC合計・支出B合計の割合)
				resultList.getWasteExpenditureTotalAmount(),
				// 支出金額合計
				resultList.getExpenditureTotalAmount(),
				// 収支金額合計
				resultList.getBalanceTotalAmount());
		
		// 年間収支(マージ)(ドメインモデル)から年間収支(マージ)(レスポンス)への変換
		response.addMeisaiInquiryList(convertMeisaiList(resultList));

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
					domain.getMonth(),
					domain.getRegularIncomeAmount(),
					domain.getWithdrawingAmount(),
					domain.getJigyouKeihiKingaku(),
					domain.getKoteiHikazeiKingaku(),
					domain.getKoteiKazeiKingaku(),
					domain.getIruiJyuukyoSetubiKingaku(),
					domain.getInsyokuNitiyouhinKingaku(),
					domain.getSyumiGotakuKingaku(),
					domain.getTotalWasteExpenditureAmount(),
					domain.getExpenditureAmount(),
					domain.getBalanceAmount())
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
			= AccountYearInquiryRedirectResponse.getInquiryMonthRedirectInstance(targetYearMonth);
		return response;
		
	}
}
