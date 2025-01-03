/**
 * 買い物登録方法選択画面の情報取得を行うユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistTopMenuResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録方法選択画面の情報取得を行うユースケースです。
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
public class ShoppingRegistTopMenuUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	// 簡易タイプ買い物リスト取得コンポーネント
	private final SimpleShoppingRegistListComponent simpleShoppingRegistListComponent;
	// 支出テーブル:EXPENDITURE_TABLEリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	// 支出金額テーブル:SISYUTU_KINGAKU_TABLEポジトリー
	private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	
	/**
	 *<pre>
	 * 現在の決算月情報を元に買い物登録方法選択画面のレスポンス情報を生成し返却します	。
	 *</pre>
	 * @param user ユーザ情報
	 * @return 買い物登録方法選択画面情報(レスポンス)
	 *
	 */
	public ShoppingRegistTopMenuResponse read(LoginUserInfo user) {
		log.debug("read:userid=" + user.getUserId());
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(UserId.from(user.getUserId()));
		
		// レスポンス情報を作成
		ShoppingRegistTopMenuResponse response = ShoppingRegistTopMenuResponse.getInstance(yearMonth.getYearMonth());
		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, yearMonth.getYearMonth()),
				// 値を設定するレスポンス
				response);
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェック
		checkExpenditureAndSisyutuKingaku(userId, yearMonth.getYearMonth(), response);
		
		// 買い物登録方法選択画面情報を返却
		return response;
		
	}
	
	/**
	 *<pre>
	 * 指定された対象年月の値を元に買い物登録方法選択画面のレスポンス情報を生成し返却します	。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth ユーザ情報
	 * @return 買い物登録方法選択画面情報(レスポンス)
	 *
	 */
	public ShoppingRegistTopMenuResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);
		
		// レスポンス情報を作成
		ShoppingRegistTopMenuResponse response = ShoppingRegistTopMenuResponse.getInstance(domainTargetYearMonth);
		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, domainTargetYearMonth),
				// 値を設定するレスポンス
				response);
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェック
		checkExpenditureAndSisyutuKingaku(userId, domainTargetYearMonth, response);
		
		// 買い物登録方法選択画面情報を返却
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @return 買い物登録画面リダイレクト情報
	 *
	 */
	public AbstractResponse readShoppingRegistRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readShoppingRegistRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getShoppingRegistRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @return 買い物登録(簡易タイプ)画面リダイレクト情報
	 *
	 */
	public AbstractResponse readSimpleShoppingRegistRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readSimpleShoppingRegistRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getSimpleShoppingRegistRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェックします。
	 * 未登録の場合、買い物情報の登録は不可となります。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @param response 買い物登録方法選択画面情報
	 *
	 */
	private void checkExpenditureAndSisyutuKingaku(UserId userId, TargetYearMonth targetYearMonth, ShoppingRegistTopMenuResponse response) {
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報を取得
		// 支出テーブル情報には外食、仕事のデータ登録なしでOK。データがある場合でも値の更新は不要
		// 飲食の支出項目コード
		SisyutuItemCode foodItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE);
		
		// 飲食(無駄づかいなし)
		ExpenditureItemInquiryList beforeFoodList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.NON_WASTED));
		if(!beforeFoodList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("飲食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いなし)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄遣いB)
		ExpenditureItemInquiryList beforeFoodBList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_B));
		if(!beforeFoodBList.isOne() ) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄使いC)
		ExpenditureItemInquiryList beforeFoodCList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_C));
		if(!beforeFoodCList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("飲食(無駄使いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いC)の支出情報を登録してから再度実行してください。");
		}
		// 支出金額テーブル情報(飲食)
		SisyutuKingakuItem beforeFood = sisyutuKingakuTableRepository.findByUniqueKey(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, foodItemCode));
		if(beforeFood == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(飲食)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 一人プチ贅沢・外食
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchDineOut = 
				SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_GAISYOKU_VALUE));
		ExpenditureItemInquiryList beforeDineOutList = expenditureRepository.findById(searchDineOut);
		if(!beforeDineOutList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("一人プチ贅沢・外食に対応する支出情報が登録されていないか複数登録されています。支出登録画面で一人プチ贅沢・外食の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeDineOut = sisyutuKingakuTableRepository.findByUniqueKey(searchDineOut);
		if(beforeDineOut == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(一人プチ贅沢・外食)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 日用消耗品
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchConsumerGoods = 
				SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE));
		ExpenditureItemInquiryList beforeConsumerGoodsList = expenditureRepository.findById(searchConsumerGoods);
		if(!beforeConsumerGoodsList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("日用消耗品に対応する支出情報が登録されていないか複数登録されています。支出登録画面で日用消耗品の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeConsumerGoods = sisyutuKingakuTableRepository.findByUniqueKey(searchConsumerGoods);
		if(beforeConsumerGoods == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(日用消耗品)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 被服費
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchClothes =
				SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_HIFUKU_VALUE));
		ExpenditureItemInquiryList beforeClothesList = expenditureRepository.findById(searchClothes);
		if(!beforeClothesList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("被服費に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeClothes = sisyutuKingakuTableRepository.findByUniqueKey(searchClothes);
		if(beforeClothes == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(被服費)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 仕事(流動経費)
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchWork = 
				SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE));
		ExpenditureItemInquiryList beforeWorkList = expenditureRepository.findById(searchWork);
		if(!beforeWorkList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("仕事(流動経費)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeWork = sisyutuKingakuTableRepository.findByUniqueKey(searchWork);
		if(beforeWork == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(仕事(流動経費))が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 住居設備
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchHouseEquipment =
				SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE));
		ExpenditureItemInquiryList beforeHouseEquipmentList = expenditureRepository.findById(searchHouseEquipment);
		if(!beforeHouseEquipmentList.isOne()) {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage("住居設備に対応する支出情報が登録されていないか複数登録されています。支出登録画面で住居設備の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeHouseEquipment = sisyutuKingakuTableRepository.findByUniqueKey(searchHouseEquipment);
		if(beforeHouseEquipment == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(住居設備)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
	}
}
