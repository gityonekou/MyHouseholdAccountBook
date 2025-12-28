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
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
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
 * @since 家計簿アプリ(1.00.A)
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
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(UserId.from(user.getUserId()));
		
		// 収支画面に表示する対象年月情報を生成
		AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo = AccountMonthInquiryTargetYearMonthInfo.from(
				yearMonth.getYearMonth().getValue());
		
		// ユーザID,現在の対象年月を条件に支出項目のリストと収支金額を取得しレスポンス情報を返却
		return execRead(user, targetYearMonthInfo);
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
		return execRead(user, AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth));
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
		return execRead(user, AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth, returnYearMonth));
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
	 * @return 月間収支情報(レスポンス)
	 *
	 */
	private AccountMonthInquiryResponse execRead(
			LoginUserInfo user, AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo) {
		log.debug("execRead:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonthInfo.getTargetYearMonth());
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(targetYearMonthInfo);
		
		// 検索条件(ユーザID、年月(YYYYMM))をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth searchCondition = SearchQueryUserIdAndYearMonth.from(
				UserId.from(user.getUserId()), TargetYearMonth.from(targetYearMonthInfo.getTargetYearMonth()));

		// ユーザID,対象年月を検索条件にドメインデータを取得
		AccountMonthInquiryExpenditureItemList expenditureList = sisyutuRepository.select(searchCondition);
		IncomeAndExpenditure incomeAndExpenditure = syuusiRepository.findByUserIdAndYearMonth(searchCondition);

		// データ存在の整合性検証(収支データなし&支出金額データありの場合はエラー)
		consistencyService.validateDataExistence(incomeAndExpenditure, expenditureList, searchCondition);

		// 支出金額情報のリスト(ドメインモデル)をレスポンスに設定
		if(expenditureList.isEmpty()) {
			// 支出金額情報のリストが0件の場合、メッセージを設定
			response.addMessage("登録済みの支出金額情報が0件です。");
		} else {
			// 支出金額情報のリストをレスポンスに設定(ドメインモデルからレスポンスへの変換)
			response.addExpenditureItemList(convertExpenditureItemList(expenditureList));
		}

		// 収支情報(ドメインモデル)をレスポンスに設定
		if(incomeAndExpenditure.isEmpty()) {
			// 該当月の収支データがない場合、メッセージを設定
			response.addMessage("該当月の収支データがありません。");
			response.setSyuusiDataFlg(false);
		} else {
			// 収支整合性検証(収入・支出の合計値が収支テーブルの値と一致するかをドメインサービスで検証)
			consistencyService.validateAll(incomeAndExpenditure, searchCondition);

			// 収支情報(ドメインモデル)から収支情報(レスポンス)への変換
			// 収入金額
			response.setSyuunyuuKingaku(incomeAndExpenditure.getIncomeAmount().toFormatString());
			// 支出金額
			response.setSisyutuKingaku(incomeAndExpenditure.getExpenditureAmount().toFormatString());
			// 積立金取崩金額
			response.setWithdrewKingaku(incomeAndExpenditure.getWithdrewAmount().toFormatString());
			// 支出予定金額
			response.setSisyutuYoteiKingaku(incomeAndExpenditure.getSisyutuYoteiKingaku().toFormatString());
			// 収支金額
			response.setSyuusiKingaku(incomeAndExpenditure.getBalanceAmount().toFormatString());
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
				domain.getSisyutuItemLevel().getValue(),
				domain.getSisyutuItemName().getValue(),
				domain.getSisyutuKingaku().toFormatString(),
				domain.getSisyutuKingakuB().toFormatString(),
				domain.getSisyutuKingakuB().getPercentage(domain.getSisyutuKingaku()),
				domain.getSisyutuKingakuC().toFormatString(),
				domain.getSisyutuKingakuC().getPercentage(domain.getSisyutuKingaku()),
				domain.getSisyutuKingakuBC().toFormatString(),
				domain.getSisyutuKingakuBC().getPercentage(domain.getSisyutuKingaku()),
				domain.getShiharaiDate().toString())).collect(Collectors.toUnmodifiableList());
	}	
}
