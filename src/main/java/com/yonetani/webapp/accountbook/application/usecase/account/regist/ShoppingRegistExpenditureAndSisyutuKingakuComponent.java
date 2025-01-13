/**
 * 【説明を入力してください】
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録時、必須の支出項目に対応する支出テーブル情報と支出金額テーブル情報が登録されているかどうかのチェックと該当支出項目に対応する情報取得を行うコンポーネントです。
 * [対象の支出項目]
 * ・支出項目コード:飲食(0051)＋支出区分(無駄使いなし)
 * ・支出項目コード:飲食(0051)＋支出区分(無駄遣いB)
 * ・支出項目コード:飲食(0051)＋支出区分(無駄遣いC)
 * ・支出項目コード:支出項目コード:一人プチ贅沢・外食(0052)
 * ・支出項目コード:日用消耗品(0050)
 * ・支出項目コード:被服費(0046)
 * ・支出項目コード:流動経費(0007) 
 * ・支出項目コード:住居設備(0047)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ShoppingRegistExpenditureAndSisyutuKingakuComponent {
	// 定数:飲食の支出項目コード
	private final SisyutuItemCode foodItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE);
	// 定数:一人プチ贅沢・外食の支出項目コード
	private final SisyutuItemCode dineOutItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_GAISYOKU_VALUE);
	// 定数:日用消耗品の支出項目コード
	private final SisyutuItemCode consumerGoodsItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE);
	// 定数:被服費の支出項目コード
	private final SisyutuItemCode clothesItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_HIFUKU_VALUE);
	// 定数:仕事(流動経費)の支出項目コード
	private final SisyutuItemCode workItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE);
	// 定数:住居設備の支出項目コード
	private final SisyutuItemCode houseEquipmentItemCode = SisyutuItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE);
	
	// 支出テーブル:EXPENDITURE_TABLEリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	// 支出金額テーブル:SISYUTU_KINGAKU_TABLEポジトリー
	private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	
	/**
	 *<pre>
	 * 買い物リストの支出項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェックし、NGの項目に対応するエラーメッセージを返します。
	 * NG項目なしの場合、空のリストが返されます。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return NG項目に対応するエラーメッセージのリスト。NG項目なしの場合、空のリスト
	 *
	 */
	public List<String> checkExpenditureAndSisyutuKingaku(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("checkExpenditureAndSisyutuKingaku:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		
		List<String> responseMessage = new ArrayList<>();
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報を取得
		// 支出テーブル情報には外食、仕事のデータ登録なしでOK。データがある場合でも値の更新は不要
		// 飲食(無駄遣いなし)
		ExpenditureItemInquiryList beforeFoodList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.NON_WASTED));
		if(!beforeFoodList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いなし)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄遣いB)
		ExpenditureItemInquiryList beforeFoodBList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_B));
		if(!beforeFoodBList.isOne() ) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄使いC)
		ExpenditureItemInquiryList beforeFoodCList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_C));
		if(!beforeFoodCList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄使いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いC)の支出情報を登録してから再度実行してください。");
		}
		// 支出金額テーブル情報(飲食)
		SisyutuKingakuItem beforeFood = sisyutuKingakuTableRepository.findByUniqueKey(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, foodItemCode));
		if(beforeFood == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(飲食)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 一人プチ贅沢・外食
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchDineOut = SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, dineOutItemCode);
		ExpenditureItemInquiryList beforeDineOutList = expenditureRepository.findById(searchDineOut);
		if(!beforeDineOutList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("一人プチ贅沢・外食に対応する支出情報が登録されていないか複数登録されています。支出登録画面で一人プチ贅沢・外食の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeDineOut = sisyutuKingakuTableRepository.findByUniqueKey(searchDineOut);
		if(beforeDineOut == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(一人プチ贅沢・外食)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 日用消耗品
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchConsumerGoods = SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, consumerGoodsItemCode);
		ExpenditureItemInquiryList beforeConsumerGoodsList = expenditureRepository.findById(searchConsumerGoods);
		if(!beforeConsumerGoodsList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("日用消耗品に対応する支出情報が登録されていないか複数登録されています。支出登録画面で日用消耗品の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeConsumerGoods = sisyutuKingakuTableRepository.findByUniqueKey(searchConsumerGoods);
		if(beforeConsumerGoods == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(日用消耗品)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 被服費
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchClothes = SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, clothesItemCode);
		ExpenditureItemInquiryList beforeClothesList = expenditureRepository.findById(searchClothes);
		if(!beforeClothesList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("被服費に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeClothes = sisyutuKingakuTableRepository.findByUniqueKey(searchClothes);
		if(beforeClothes == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(被服費)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 仕事(流動経費)
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchWork = SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, workItemCode);
		ExpenditureItemInquiryList beforeWorkList = expenditureRepository.findById(searchWork);
		if(!beforeWorkList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("仕事(流動経費)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeWork = sisyutuKingakuTableRepository.findByUniqueKey(searchWork);
		if(beforeWork == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(仕事(流動経費))が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		
		// 住居設備
		SearchQueryUserIdAndYearMonthAndSisyutuItemCode searchHouseEquipment = SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, houseEquipmentItemCode);
		ExpenditureItemInquiryList beforeHouseEquipmentList = expenditureRepository.findById(searchHouseEquipment);
		if(!beforeHouseEquipmentList.isOne()) {
			// エラーメッセージを追加
			responseMessage.add("住居設備に対応する支出情報が登録されていないか複数登録されています。支出登録画面で住居設備の支出情報を登録してから再度実行してください。");
		}
		SisyutuKingakuItem beforeHouseEquipment = sisyutuKingakuTableRepository.findByUniqueKey(searchHouseEquipment);
		if(beforeHouseEquipment == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル情報(住居設備)が登録されていません。管理者に問い合わせてください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return responseMessage;
	}
	
	/**
	 *<pre>
	 * 買い物リストの支出項目に対応する情報が支出登録情報のリスト(セッション登録情報)に設定されてるかどうかをチェックし、NGの項目に対応するエラーメッセージを返します。
	 * NG項目なしの場合、空のリストが返されます。
	 *</pre>
	 * @param uscheckListerId 支出登録情報のリスト(セッション登録情報)
	 * @return NG項目に対応するエラーメッセージのリスト。NG項目なしの場合、空のリスト
	 *
	 */
	public List<String> checkExpenditureRegistItemList(List<ExpenditureRegistItem> checkList) {
		log.debug("checkExpenditureRegistItemList:");
		List<String> responseMessage = new ArrayList<>();
		// チェック用のマップを作成
		Map<String, ExpenditureRegistItem> checkMap = new HashMap<String, ExpenditureRegistItem>();
		checkList.forEach(data -> {
			StringBuilder key = new StringBuilder(data.getSisyutuItemCode());
			if(StringUtils.hasLength(data.getExpenditureKubun())) {
				key.append(data.getExpenditureKubun());
			} else {
				throw new MyHouseholdAccountBookRuntimeException("支出区分が未設定です。管理者に問い合わせてください");
			}
			checkMap.put(key.toString(), data);
		});
		
		// 飲食(無駄遣いなし)
		if(checkMap.get(foodItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いなし)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄遣いB)
		if(checkMap.get(foodItemCode.getValue() + SisyutuKubun.WASTED_B.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を登録してから再度実行してください。");
		}
		// 飲食(無駄使いC)
		if(checkMap.get(foodItemCode.getValue() + SisyutuKubun.WASTED_C.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("飲食(無駄使いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いC)の支出情報を登録してから再度実行してください。");
		}
		// 一人プチ贅沢・外食
		if(checkMap.get(dineOutItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("一人プチ贅沢・外食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で一人プチ贅沢・外食(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		}
		// 日用消耗品
		if(checkMap.get(consumerGoodsItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("日用消耗品(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で日用消耗品(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		}
		// 被服費
		if(checkMap.get(clothesItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("被服費(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		}
		// 仕事(流動経費)
		if(checkMap.get(workItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("仕事(流動経費)(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		}
		// 住居設備
		if(checkMap.get(houseEquipmentItemCode.getValue() + SisyutuKubun.NON_WASTED.getValue()) == null) {
			// エラーメッセージを追加
			responseMessage.add("住居設備(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で住居設備(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		}
		return responseMessage;
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている飲食(無駄づかいなし)の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている飲食(無駄づかいなし)の情報
	 *
	 */
	public ExpenditureItem getFoodExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getFoodExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 飲食(無駄づかいなし)
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.NON_WASTED));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("飲食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いなし)の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている飲食(無駄遣いB)の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている飲食(無駄遣いB)の情報
	 *
	 */
	public ExpenditureItem getFoodBExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getFoodBExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 飲食(無駄遣いB)
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_B));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている飲食(無駄遣いC)の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている飲食(無駄遣いC)の情報
	 *
	 */
	public ExpenditureItem getFoodCExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getFoodCExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 飲食(無駄遣いC)
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(
				SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun.from(userId, targetYearMonth, foodItemCode, SisyutuKubun.WASTED_C));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("飲食(無駄遣いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いC)の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている一人プチ贅沢・外食の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている一人プチ贅沢・外食の情報
	 *
	 */
	public ExpenditureItem getDineOutExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getDineOutExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 一人プチ贅沢・外食
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, dineOutItemCode));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("一人プチ贅沢・外食に対応する支出情報が登録されていないか複数登録されています。支出登録画面で一人プチ贅沢・外食の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている日用消耗品の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている日用消耗品の情報
	 *
	 */
	public ExpenditureItem getConsumerGoodsExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getConsumerGoodsExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 日用消耗品
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, consumerGoodsItemCode));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("日用消耗品に対応する支出情報が登録されていないか複数登録されています。支出登録画面で日用消耗品の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている被服費の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている被服費の情報
	 *
	 */
	public ExpenditureItem getClothesExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getClothesExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 被服費
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, clothesItemCode));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("被服費に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている仕事(流動経費)の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている仕事(流動経費)の情報
	 *
	 */
	public ExpenditureItem getWorkExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getWorkExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 仕事(流動経費)
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, workItemCode));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("仕事(流動経費)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報に登録されている住居設備の情報を取得して返します。
	 * 対象データなしか複数件登録されている場合、予期しないエラーを返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @return 支出テーブル情報に登録されている住居設備の情報
	 *
	 */
	public ExpenditureItem getHouseEquipmentExpenditureItem(UserId userId, TargetYearMonth targetYearMonth) {
		log.debug("getHouseEquipmentExpenditureItem:userid=" + userId.getValue() + ",targetYearMonth=" + targetYearMonth.getValue());
		// 住居設備
		ExpenditureItemInquiryList beforeList = expenditureRepository.findById(SearchQueryUserIdAndYearMonthAndSisyutuItemCode.from(userId, targetYearMonth, houseEquipmentItemCode));
		if(!beforeList.isOne()) {
			throw new MyHouseholdAccountBookRuntimeException("住居設備に対応する支出情報が登録されていないか複数登録されています。支出登録画面で住居設備の支出情報を再登録してから再度実行してください。[userId=" + userId.getValue() + "][targetYearMonth:" + targetYearMonth.getValue() + "]");
		}
		return beforeList.getValues().get(0);
	}
}
