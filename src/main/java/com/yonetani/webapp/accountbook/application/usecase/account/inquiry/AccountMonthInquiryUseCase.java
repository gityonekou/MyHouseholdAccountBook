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

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.AccountMonthInquiryRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureInquiryRepository;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse.ExpenditureListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse.TargetYearMonthInfo;
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
	// 指定月の支出項目リスト取得リポジトリー
	private final AccountMonthInquiryRepository repository;
	// 指定月の収支取得リポジトリー
	private final IncomeAndExpenditureInquiryRepository syuusiRepository;
	
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
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(user.getUserId());
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(
				TargetYearMonthInfo.from(yearMonth.getYearMonth().toString()));
		
		// ユーザID,現在の対象年月を条件に支出項目のリストと収支金額を取得
		execRead(user, yearMonth.getYearMonth().toString(), response);
		
		return response;
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
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(
				TargetYearMonthInfo.from(targetYearMonth));
		
		// ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得
		execRead(user, targetYearMonth, response);
		
		return response;
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
	 * @return
	 *
	 */
	public AccountMonthInquiryResponse read(LoginUserInfo user, String targetYearMonth, String returnYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",returnYearMonth=" + returnYearMonth);
		
		// レスポンスを生成
		AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(
				TargetYearMonthInfo.from(targetYearMonth, returnYearMonth));
		
		// ユーザID,入力された対象年月を条件に支出項目のリストと収支金額を取得
		execRead(user, targetYearMonth, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 支出項目のリストと収支金額を取得します。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth 表示対象の年月
	 * @param response 月間収支情報(レスポンス)
	 *
	 */
	private void execRead(LoginUserInfo user, String targetYearMonth, AccountMonthInquiryResponse response) {
		log.debug("execRead:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// 検索条件(ユーザID、年月(YYYYMM))をドメインオブジェクトに変換
		SearchQueryUserIdAndYearMonth inquiryModel = SearchQueryUserIdAndYearMonth.from(user.getUserId(), targetYearMonth);
		
		// ユーザID,対象年月を検索条件に支出金額情報のリストを取得
		AccountMonthInquiryExpenditureItemList resultList = repository.selectExpenditureItem(inquiryModel);

		// 支出金額情報のリスト(ドメインモデル)をレスポンスに設定
		if(resultList.isEmpty()) {
			// 支出金額情報のリストが0件の場合、メッセージを設定
			response.addMessage("登録済みの支出金額情報が0件です。");
		} else {
			// 支出金額情報のリストをレスポンスに設定(ドメインモデルからレスポンスへの変換)
			response.addExpenditureItemList(convertExpenditureItemList(resultList));
		}
		// ユーザID,現在の対象年月を条件に該当月の収支金額を取得
		IncomeAndExpenditureInquiryItem sisyutuResult = syuusiRepository.select(inquiryModel);
		if(sisyutuResult.isEmpty()) {
			// 該当月の収支データ登録なしの場合で支出金額情報が登録済みの場合、不正データ登録のため予期しないエラーとする
			// DBデータのメンテナンスが必要(どのタイミングで登録されたかを調査し、該当ユーザの該当月の出金額情報は削除）
			if(!resultList.isEmpty()) {
				throw new MyHouseholdAccountBookRuntimeException("該当月の収支データが未登録の状態で支出金額情報が登録済みの状態です。管理者に問い合わせてください。[yearMonth=" + inquiryModel.getYearMonth() + "]");
			}
			
			// 検索結果が0件の場合、メッセージを設定
			response.addMessage("該当月の収支データがありません。");
			response.setSyuusiDataFlg(false);
			
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
	private List<ExpenditureListItem> convertExpenditureItemList(AccountMonthInquiryExpenditureItemList resultList) {
		// 返却するリストを不変オブジェクトに変換する
		return resultList.getValues().stream().map(domain ->
		AccountMonthInquiryResponse.ExpenditureListItem.form(
				domain.getSisyutuItemLevel().getValue(),
				domain.getSisyutuItemName().toString(),
				domain.getSisyutuKingaku().toString(),
				domain.getSisyutuKingakuB().toSisyutuKingakuBString(),
				domain.getSisyutuKingakuB().toPercentageString(),
				domain.getShiharaiDate().toString())).collect(Collectors.toUnmodifiableList());
	}
	
}
