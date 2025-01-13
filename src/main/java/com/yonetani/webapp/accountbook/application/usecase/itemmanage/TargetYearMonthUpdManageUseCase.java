/**
 * 対象年月更新ユースケースです。
 * 以下処理を対応します。
 * ・対象年月更新を実行していいかどうかのチェック（情報管理(対象年月更新)画面に結果を表示）
 * ・対象年月更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.domain.type.common.NextTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.TargetYearMonthUpdManageResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 対象年月更新ユースケースです。
 * 以下処理を対応します。
 * ・情報管理(対象年月更新)表示画面情報取得：対象年月更新を実行していいかどうかのチェックを行う
 * ・対象年月更新
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
public class TargetYearMonthUpdManageUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	// 指定ユーザIDの現在の対象年月取得リポジトリー
	private final AccountBookUserRepository userInfoSearchRepository;
	// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEリポジトリー
	private final IncomeAndExpenditureTableRepository syuusiRepository;
	
	/**
	 *<pre>
	 * 情報管理(対象年月更新)画面情報取得
	 * 
	 * 指定したユーザIDの現在の対象年月を更新可能かどうかを判定し、画面表示情報を生成します。
	 * 現在の対象年月の収支情報が未登録の場合は対象年月更新不可となります。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 情報管理(対象年月更新)画面の表示情報
	 *
	 */
	public TargetYearMonthUpdManageResponse readInitInfo(LoginUserInfo user) {
		log.debug("readInitInfo:userid=" + user.getUserId());
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(userId);
		TargetYearMonth targetYearMonth = yearMonth.getYearMonth();
		// 検索条件(ユーザID、年月(YYYYMM))をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth inquiryModel = SearchQueryUserIdAndYearMonth.from(userId, targetYearMonth);
		// ユーザID,現在の対象年月を条件に該当月の収支金額を取得
		IncomeAndExpenditureItem sisyutuResult = syuusiRepository.select(inquiryModel);
		if(sisyutuResult.isEmpty()) {
			// 現在の対象年月に対応する収支情報が未登録の場合、対象年月更新不可の画面表示情報を生成して返却
			TargetYearMonthUpdManageResponse response = TargetYearMonthUpdManageResponse.getUpdateFailInstance();
			// 表示メッセージを設定
			response.addErrorMessage(String.format(
					"現在の対象年月（%s年%s月）の収支情報が未登録です。対象年月を更新できません。",
					targetYearMonth.getYear(),
					targetYearMonth.getMonth()));
			return response;
			
		} else {
			// 対象年月更新可の画面表示情報を生成して返却
			return TargetYearMonthUpdManageResponse.getUpdateAcceptInstance(
					// 現在の対象年月
					targetYearMonth,
					// 更新後の対象年月：現在の対象年月の次の月
					NextTargetYearMonth.from(targetYearMonth));
		}
	}

	/**
	 *<pre>
	 * 対象年月更新処理
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 情報管理(対象年月更新)画面の表示情報(各月の収支参照画面にリダイレクトを設定)
	 *
	 */
	public TargetYearMonthUpdManageResponse execAction(LoginUserInfo user) {
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(userId);
		TargetYearMonth targetYearMonth = yearMonth.getYearMonth();
		// 検索条件(ユーザID、年月(YYYYMM))をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth inquiryModel = SearchQueryUserIdAndYearMonth.from(userId, targetYearMonth);
		// ユーザID,現在の対象年月を条件に該当月の収支金額を取得
		IncomeAndExpenditureItem sisyutuResult = syuusiRepository.select(inquiryModel);
		if(sisyutuResult.isEmpty()) {
			throw new MyHouseholdAccountBookRuntimeException("現在の対象年月に対応する収支情報が収支テーブル:INCOME_AND_EXPENDITURE_TABLEに存在しません。管理者に問い合わせてください。[対象年月=" + targetYearMonth.getValue() + "]");
		}
		// 更新後の対象年月：現在の対象年月の次の月
		NextTargetYearMonth nextTargetYearMonth = NextTargetYearMonth.from(targetYearMonth);
		// レスポンスを生成
		TargetYearMonthUpdManageResponse response = TargetYearMonthUpdManageResponse.getUpdateAcceptInstance(
				// 現在の対象年月
				targetYearMonth,
				// 更新後の対象年月：現在の対象年月の次の月
				nextTargetYearMonth);
		
		// 指定ユーザの家計簿利用ユーザ情報を取得
		AccountBookUser beforeUserInfo = userInquiry.getUserInfo(userId);
		// 更新後の家計簿利用ユーザ情報
		AccountBookUser updUserInfo = AccountBookUser.from(
				// ユーザID
				beforeUserInfo.getUserId(),
				// 対象年
				nextTargetYearMonth.getYear(),
				// 対象月
				nextTargetYearMonth.getMonth(),
				// ユーザ名
				beforeUserInfo.getUserName());
		// 新しい対象年月で家計簿利用ユーザ情報を作成しデータを更新
		int updateCount = userInfoSearchRepository.update(updUserInfo);
		// 更新件数が1件以上の場合、業務エラー
		if(updateCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException("家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updUserInfo + "]");
		}
		
		// 完了メッセージ
		response.addMessage("対象年月を更新しました。[" + updUserInfo.getNowTargetYear().getValue() + "年" + updUserInfo.getNowTargetMonth().getValue()+ "月]");
		
		// 処理結果OKを設定(getリダイレクトを行う)
		response.setTransactionSuccessFull();
		
		return response;
	}
}
