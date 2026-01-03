/**
 * 簡易タイプの買い物登録を行うユースケースです。買い物登録(簡易タイプ)画面の情報取得、及び、画面入力された買い物情報を登録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応（DDD適応：Phase4までの内容を反映) 
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuKingakuItemHolderComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemHolder;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.BeforeAndAfterShoppingSisyutuKingakuData;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingClothes;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingConsumerGoods;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingDineOut;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingFood;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingFoodB;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingFoodC;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingHouseEquipment;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingWork;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 簡易タイプの買い物登録を行うユースケースです。買い物登録(簡易タイプ)画面の情報取得、及び、画面入力された買い物情報を登録します。
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
public class SimpleShoppingRegistUseCase {
	
	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 店舗情報取得リポジトリー
	private final ShopTableRepository shopRepository;
	// 買い物登録情報リポジトリー
	private final ShoppingRegistTableRepository shoppingRegistRepository;
	// 簡易タイプ買い物リスト取得コンポーネント
	private final SimpleShoppingRegistListComponent simpleShoppingRegistListComponent;
	// 支出テーブル:EXPENDITURE_TABLEリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	// 支出金額テーブル:SISYUTU_KINGAKU_TABLEポジトリー
	private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	// 支出金額テーブル情報保持ホルダー生成用コンポーネント
	private final SisyutuKingakuItemHolderComponent sisyutuKingakuItemHolderComponent;
	// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEリポジトリー
	private final IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;
	// 買い物登録時の支出項目に対応する支出テーブル情報と支出金額テーブル情報にアクセスするコンポーネント
	private final ShoppingRegistExpenditureAndSisyutuKingakuComponent expenditureAndSisyutuKingakuComponent;
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面情報取得
	 * 
	 * 指定した対象年月に応じた買い物登録(簡易タイプ)画面の表示情報を取得します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth  買い物登録を行う対象年月
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);
		
		// デフォルトの簡易タイプ買い物登録情報フォームデータを生成
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		// アクション(新規登録)
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 店舗区分は店舗区分で食品・日用品店舗(901)をデフォルト選択する
		inputForm.setShopKubunCode(MyHouseholdAccountBookContent.SHOP_KUBUN_GROCERIES_SELECTED_VALUE);
		// 対象年月：ドメインタイプで入力値チェックを行った値を設定
		inputForm.setTargetYearMonth(domainTargetYearMonth.getValue());
		// デフォルトのカレンダー日付を設定する(targetYearMonth + 01)
		inputForm.setShoppingDate(LocalDate.parse(domainTargetYearMonth.getValue() + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER));
		
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(userId, inputForm);
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面情報取得
	 * 
	 * 指定した買い物登録情報に応じた買い物登録(簡易タイプ)画面の表示情報を取得します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @param shoppingRegistCode 更新対象の買い物登録コード
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse read(LoginUserInfo user, String targetYearMonth, String shoppingRegistCode) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",shoppingRegistCode=" + shoppingRegistCode);
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);
		// 買い物登録コードのドメインタイプを生成
		ShoppingRegistCode domainShoppingRegistCode = ShoppingRegistCode.from(shoppingRegistCode);
		
		// 登録されている買い物情報を取得
		ShoppingRegist result = shoppingRegistRepository.findByUniqueKey(
				SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(userId, domainTargetYearMonth, domainShoppingRegistCode));
		// 選択した買い物登録コードに対応するデータなしの場合、予期しないエラーとする
		if(result == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の買い物登録情報が存在しません。管理者に問い合わせてください。[targetYearMonth:" 
					+ targetYearMonth + "][shoppingRegistCode:" + shoppingRegistCode + "]");
		}
		// 取得した買い物情報に対応する簡易タイプ買い物登録情報フォームデータを生成
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		// アクション(更新)
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 対象年月
		inputForm.setTargetYearMonth(result.getTargetYearMonth().getValue());
		// 買い物登録コード
		inputForm.setShoppingRegistCode(result.getShoppingRegistCode().getValue());
		// 店舗区分
		inputForm.setShopKubunCode(result.getShopKubunCode().getValue());
		// 店舗コード
		inputForm.setShopCode(result.getShopCode().getValue());
		// 買い物日
		inputForm.setShoppingDate(result.getShoppingDate().getValue());
		// 備考
		inputForm.setShoppingRemarks(result.getShoppingRemarks().getValue());
		// 食料品(必須)
		inputForm.setShoppingFoodExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodExpenses().getValue()));
		// 消費税：食料品(必須)
		inputForm.setShoppingFoodTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodTaxExpenses().getValue()));
		// 食料品B(無駄遣い)
		inputForm.setShoppingFoodBExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodBExpenses().getValue()));
		// 消費税：食料品B(無駄遣い)
		inputForm.setShoppingFoodBTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodBTaxExpenses().getValue()));
		// 食料品C(お酒類)
		inputForm.setShoppingFoodCExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodCExpenses().getValue()));
		// 消費税：食料品C(お酒類)
		inputForm.setShoppingFoodCTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingFoodCTaxExpenses().getValue()));
		// 外食
		inputForm.setShoppingDineOutExpenses(DomainCommonUtils.convertInteger(result.getShoppingDineOutExpenses().getValue()));
		// 消費税：外食
		inputForm.setShoppingDineOutTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingDineOutTaxExpenses().getValue()));
		// 日用品
		inputForm.setShoppingConsumerGoodsExpenses(DomainCommonUtils.convertInteger(result.getShoppingConsumerGoodsExpenses().getValue()));
		// 消費税：日用品
		inputForm.setShoppingConsumerGoodsTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingConsumerGoodsTaxExpenses().getValue()));
		// 衣料品(私服)
		inputForm.setShoppingClothesExpenses(DomainCommonUtils.convertInteger(result.getShoppingClothesExpenses().getValue()));
		// 消費税：衣料品(私服)
		inputForm.setShoppingClothesTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingClothesTaxExpenses().getValue()));
		// 仕事
		inputForm.setShoppingWorkExpenses(DomainCommonUtils.convertInteger(result.getShoppingWorkExpenses().getValue()));
		// 消費税：仕事
		inputForm.setShoppingWorkTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingWorkTaxExpenses().getValue()));
		// 住居設備
		inputForm.setShoppingHouseEquipmentExpenses(DomainCommonUtils.convertInteger(result.getShoppingHouseEquipmentExpenses().getValue()));
		// 消費税：住居設備
		inputForm.setShoppingHouseEquipmentTaxExpenses(DomainCommonUtils.convertInteger(result.getShoppingHouseEquipmentTaxExpenses().getValue()));
		// クーポン
		inputForm.setShoppingCouponPrice(DomainCommonUtils.convertInteger(result.getShoppingCouponPrice().getValue()));
		// 購入金額合計
		inputForm.setTotalPurchasePrice(DomainCommonUtils.convertInteger(result.getTotalPurchasePrice().getValue()));
		// 購入金額合計(disabled)
		inputForm.setTotalPurchasePriceView(DomainCommonUtils.convertInteger(result.getTotalPurchasePrice().getValue()));
		// 消費税合計
		inputForm.setTaxTotalPurchasePrice(DomainCommonUtils.convertInteger(result.getTaxTotalPurchasePrice().getValue()));
		// 消費税合計(disabled)
		inputForm.setTaxTotalPurchasePriceView(DomainCommonUtils.convertInteger(result.getTaxTotalPurchasePrice().getValue()));
		// 買い物合計金額
		inputForm.setShoppingTotalAmount(DomainCommonUtils.convertInteger(result.getShoppingTotalAmount().getValue()));
		// 買い物合計金額(disabled)
		inputForm.setShoppingTotalAmountView(DomainCommonUtils.convertInteger(result.getShoppingTotalAmount().getValue()));
		
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(userId, inputForm);
		
	}
	
	/**
	 *<pre>
	 * 店舗区分変更時の画面返却データのModelAndViewを生成して返します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param registInfoForm 買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse readChangeShopKubun(LoginUserInfo user, SimpleShoppingRegistInfoForm registInfoForm) {
		log.debug("readChangeShopKubun:userid=" + user.getUserId() + ",inputForm=" + registInfoForm);
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(UserId.from(user.getUserId()), registInfoForm);
	}
	
	/**
	 *<pre>
	 * バリデーションチェックエラー時の入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param registInfoForm 買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse readBindingError(LoginUserInfo user, SimpleShoppingRegistInfoForm registInfoForm) {
		log.debug("readBindingError:userid=" + user.getUserId() + ",inputForm=" + registInfoForm);
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(UserId.from(user.getUserId()), registInfoForm);
	}
	
	/**
	 *<pre>
	 * 買い物登録入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	@Transactional
	public SimpleShoppingRegistResponse execAction(LoginUserInfo user, SimpleShoppingRegistInfoForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:対象年月
		TargetYearMonth targetYearMonth = TargetYearMonth.from(inputForm.getTargetYearMonth());
		
		// 支出テーブルの更新情報
		List<ExpenditureItem> updExpenditureItemList = new ArrayList<>();
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報を取得
		// 必須データの存在チェックは買い物登録のトップメニューで確認済みなので、ここではもしデータがない場合はNULLポ発生か要素数アクセスエラーで対応する
		// 支出テーブル情報には外食、仕事のデータ登録なしでOK。データがある場合でも値の更新は不要
		// 飲食(無駄づかいなし)の支出テーブル情報を取得
		ExpenditureItem beforeFoodItem = expenditureAndSisyutuKingakuComponent.getFoodExpenditureItem(userId, targetYearMonth);
		// 飲食(無駄遣いB)の支出テーブル情報を取得
		ExpenditureItem beforeFoodBItem = expenditureAndSisyutuKingakuComponent.getFoodBExpenditureItem(userId, targetYearMonth);
		// 飲食(無駄遣いC)の支出テーブル情報を取得
		ExpenditureItem beforeFoodCItem = expenditureAndSisyutuKingakuComponent.getFoodCExpenditureItem(userId, targetYearMonth);
		// 一人プチ贅沢・外食の支出テーブル情報を取得
		ExpenditureItem beforeDineOutItem = expenditureAndSisyutuKingakuComponent.getDineOutExpenditureItem(userId, targetYearMonth);
		// 日用消耗品
		ExpenditureItem beforeConsumerGoodsItem = expenditureAndSisyutuKingakuComponent.getConsumerGoodsExpenditureItem(userId, targetYearMonth);
		// 被服費
		ExpenditureItem beforeClothesItem = expenditureAndSisyutuKingakuComponent.getClothesExpenditureItem(userId, targetYearMonth);
		// 仕事(流動経費)
		ExpenditureItem beforeWorkItem = expenditureAndSisyutuKingakuComponent.getWorkExpenditureItem(userId, targetYearMonth);
		// 住居設備
		ExpenditureItem beforeHouseEquipmentItem = expenditureAndSisyutuKingakuComponent.getHouseEquipmentExpenditureItem(userId, targetYearMonth);
				
		// 検索条件ドメインを生成(ユーザID、対象年月)
		SearchQueryUserIdAndYearMonth searchYearMonth = SearchQueryUserIdAndYearMonth.from(userId, targetYearMonth);
		
		// 対象年月の支出金額テーブル情報を保持したホルダーを生成
		SisyutuKingakuItemHolder sisyutuKingakuItemHolder = sisyutuKingakuItemHolderComponent.build(searchYearMonth);
		
		// 収支テーブル情報を取得
		IncomeAndExpenditureItem beforeSyuusiData = incomeAndExpenditureRepository.select(searchYearMonth);
		// 収支テーブル更新情報		
		IncomeAndExpenditureItem updSyuusiData = null;
		
		// レスポンスを生成
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance(inputForm.getTargetYearMonth());
		
		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {

			// 新規採番する買い物登録コードの値を取得
			int count = shoppingRegistRepository.countById(searchYearMonth);
			count++;
			if(count > 999) {
				// レスポンスを生成してエラーメッセージを追加
				// レスポンスを生成してエラーメッセージを追加
				SimpleShoppingRegistResponse errorResponse = createResponse(userId, inputForm);
				errorResponse.addErrorMessage("ひと月の買い物登録情報は999件以上登録できません。管理者に問い合わせてください。");
				return errorResponse;
			}
			
			// 買い物登録コードを入力フォームに設定
			inputForm.setShoppingRegistCode(ShoppingRegistCode.getNewCode(count));
			
			// 追加する買い物登録情報を作成
			ShoppingRegist addData = ShoppingRegist.createShoppingRegist(userId, inputForm);
			
			// 買い物登録情報テーブルに登録
			int addCount = shoppingRegistRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("買い物登録情報テーブル:SHOPPING_REGIST_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// クーポン金額を取得
			ShoppingCouponPrice couponResidualValue = addData.getShoppingCouponPrice();
			
			// 支出テーブル情報を更新
			// 飲食(無駄づかいなし)
			ShoppingFood food = ShoppingFood.from(addData.getShoppingFoodExpenses(), addData.getShoppingFoodTaxExpenses(), couponResidualValue);
			if(food.hasSisyutuKingaku()) {
				// 飲食(無駄づかいなし)の支出テーブル情報を作成
				ExpenditureItem updFoodExpenditureItem = beforeFoodItem.addSisyutuKingaku(food.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodItem, updFoodExpenditureItem);
			}
			couponResidualValue = food.getResidualCouponPrice();
			
			// 飲食(無駄遣いB)
			ShoppingFoodB foodB = ShoppingFoodB.from(addData.getShoppingFoodBExpenses(), addData.getShoppingFoodBTaxExpenses(), couponResidualValue);
			if(foodB.hasSisyutuKingaku()) {
				// 飲食(無駄遣いB)の支出テーブル情報を作成
				ExpenditureItem updFoodBExpenditureItem = beforeFoodBItem.addSisyutuKingaku(foodB.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodBExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodBItem, updFoodBExpenditureItem);
			}
			couponResidualValue = foodB.getResidualCouponPrice();
			
			// 飲食(無駄遣いC)
			ShoppingFoodC foodC = ShoppingFoodC.from(addData.getShoppingFoodCExpenses(), addData.getShoppingFoodCTaxExpenses(), couponResidualValue);
			if(foodC.hasSisyutuKingaku()) {
				// 飲食(無駄遣いC)の支出テーブル情報を作成
				ExpenditureItem updFoodCExpenditureItem = beforeFoodCItem.addSisyutuKingaku(foodC.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodCExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodCItem, updFoodCExpenditureItem);
				
			}
			couponResidualValue = foodC.getResidualCouponPrice();
			
			// 外食
			ShoppingDineOut dineOut = ShoppingDineOut.from(addData.getShoppingDineOutExpenses(), addData.getShoppingDineOutTaxExpenses(), couponResidualValue);
			if(dineOut.hasSisyutuKingaku()) {
				// 外食の支出テーブル情報を作成
				ExpenditureItem updDineOutExpenditureItem = beforeDineOutItem.addSisyutuKingaku(dineOut.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updDineOutExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeDineOutItem, updDineOutExpenditureItem);
				
			}
			couponResidualValue = dineOut.getResidualCouponPrice();
			
			// 日用消耗品
			ShoppingConsumerGoods consumerGoods = ShoppingConsumerGoods.from(addData.getShoppingConsumerGoodsExpenses(), addData.getShoppingConsumerGoodsTaxExpenses(), couponResidualValue);
			if(consumerGoods.hasSisyutuKingaku()) {
				// 日用消耗品の支出テーブル情報を作成
				ExpenditureItem updConsumerGoodsExpenditureItem = beforeConsumerGoodsItem.addSisyutuKingaku(consumerGoods.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updConsumerGoodsExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeConsumerGoodsItem, updConsumerGoodsExpenditureItem);
				
			}
			couponResidualValue = consumerGoods.getResidualCouponPrice();
			
			// 被服費
			ShoppingClothes clothes = ShoppingClothes.from(addData.getShoppingClothesExpenses(), addData.getShoppingClothesTaxExpenses(), couponResidualValue);
			if(clothes.hasSisyutuKingaku()) {
				// 被服費の支出テーブル情報を作成
				ExpenditureItem updClothesExpenditureItem = beforeClothesItem.addSisyutuKingaku(clothes.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updClothesExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeClothesItem, updClothesExpenditureItem);
			}	
			couponResidualValue = clothes.getResidualCouponPrice();
			
			// 仕事
			ShoppingWork work = ShoppingWork.from(addData.getShoppingWorkExpenses(), addData.getShoppingWorkTaxExpenses(), couponResidualValue);
			if(work.hasSisyutuKingaku()) {
				// 仕事の支出テーブル情報を作成
				ExpenditureItem updWorkExpenditureItem = beforeWorkItem.addSisyutuKingaku(work.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updWorkExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeWorkItem, updWorkExpenditureItem);
			}
			couponResidualValue = work.getResidualCouponPrice();
			
			// 住居設備
			ShoppingHouseEquipment houseEquipment = ShoppingHouseEquipment.from(addData.getShoppingHouseEquipmentExpenses(), addData.getShoppingHouseEquipmentTaxExpenses(), couponResidualValue);
			if(houseEquipment.hasSisyutuKingaku()) {
				// 住居設備の支出テーブル情報を作成
				ExpenditureItem updHouseEquipmentExpenditureItem = beforeHouseEquipmentItem.addSisyutuKingaku(houseEquipment.getValue());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updHouseEquipmentExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeHouseEquipmentItem, updHouseEquipmentExpenditureItem);
			}
			couponResidualValue = houseEquipment.getResidualCouponPrice();
			
			// 収支テーブル情報に合計値を設定し更新情報とする
			updSyuusiData = beforeSyuusiData.addSisyutuKingaku(ExpenditureAmount.from(addData.getShoppingTotalAmount().getValue()));
			
			// 完了メッセージ
			response.addMessage("買い物情報を新規登録しました。[code:" + addData.getShoppingRegistCode() + "]");
			
		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
			// 対象年月のドメインタイプを生成
			TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(inputForm.getTargetYearMonth());
			// 買い物登録コードのドメインタイプを生成
			ShoppingRegistCode domainShoppingRegistCode = ShoppingRegistCode.from(inputForm.getShoppingRegistCode());
			
			// 更新対象のデータを取得(更新前と更新後の差額計算用)
			ShoppingRegist beforeData = shoppingRegistRepository.findByUniqueKey(
					SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(userId, domainTargetYearMonth, domainShoppingRegistCode));
			// 買い物登録コードに対応するデータなしの場合、予期しないエラーとする
			if(beforeData == null) {
				throw new MyHouseholdAccountBookRuntimeException("更新対象の買い物登録情報が存在しません。管理者に問い合わせてください。[targetYearMonth:" 
						+ domainTargetYearMonth.getValue()+ "][shoppingRegistCode:" + domainShoppingRegistCode.getValue() + "]");
			}
			
			// 更新する買い物登録情報を作成
			ShoppingRegist updData = ShoppingRegist.createShoppingRegist(userId, inputForm);
			
			// 買い物登録情報テーブルを更新
			int updCount = shoppingRegistRepository.update(updData);
			// 更新件数が1件以上の場合、業務エラー
			if(updCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("買い物登録情報テーブル:SHOPPING_REGIST_TABLEの更新件数が不正でした。[件数=" + updCount + "][upd data:" + updData + "]");
			}
			
			
			// クーポン金額を取得
			ShoppingCouponPrice beforeCouponResidualValue = beforeData.getShoppingCouponPrice();
			ShoppingCouponPrice afterCouponResidualValue = updData.getShoppingCouponPrice();
			
			/* 支出テーブル情報を更新 */
			// 飲食(無駄づかいなし)
			ShoppingFood beforeFood = ShoppingFood.from(beforeData.getShoppingFoodExpenses(), beforeData.getShoppingFoodTaxExpenses(), beforeCouponResidualValue);
			ShoppingFood afterFood = ShoppingFood.from(updData.getShoppingFoodExpenses(), updData.getShoppingFoodTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeFood.getResidualCouponPrice();
			afterCouponResidualValue = afterFood.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updFood = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄づかいなし)設定値
					beforeFood.getValue(),
					// 更新後の飲食(無駄づかいなし)設定値
					afterFood.getValue(),
					// 更新前の飲食(無駄づかいなし)の支出テーブル情報
					beforeFoodItem);
			if(updFood.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFood.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodItem, updFood.getUpdExpenditureItem());
			}
			
			// 飲食(無駄遣いB)
			ShoppingFoodB beforeFoodB = ShoppingFoodB.from(beforeData.getShoppingFoodBExpenses(), beforeData.getShoppingFoodBTaxExpenses(), beforeCouponResidualValue);
			ShoppingFoodB afterFoodB = ShoppingFoodB.from(updData.getShoppingFoodBExpenses(), updData.getShoppingFoodBTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeFoodB.getResidualCouponPrice();
			afterCouponResidualValue = afterFoodB.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updFoodB = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄遣いB)設定値
					beforeFoodB.getValue(),
					// 更新後の飲食(無駄遣いB)設定値
					afterFoodB.getValue(),
					// 更新前の飲食(無駄遣いB)の支出テーブル情報
					beforeFoodBItem);
			if(updFoodB.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodB.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodBItem, updFoodB.getUpdExpenditureItem());
			}
			
			// 飲食(無駄遣いC)
			ShoppingFoodC beforeFoodC = ShoppingFoodC.from(beforeData.getShoppingFoodCExpenses(), beforeData.getShoppingFoodCTaxExpenses(), beforeCouponResidualValue);
			ShoppingFoodC afterFoodC = ShoppingFoodC.from(updData.getShoppingFoodCExpenses(), updData.getShoppingFoodCTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeFoodC.getResidualCouponPrice();
			afterCouponResidualValue = afterFoodC.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updFoodC = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄遣いC)設定値
					beforeFoodC.getValue(),
					// 更新後の飲食(無駄遣いC)設定値
					afterFoodC.getValue(),
					// 更新前の飲食(無駄遣いC)の支出テーブル情報
					beforeFoodCItem);
			if(updFoodC.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodC.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeFoodCItem, updFoodC.getUpdExpenditureItem());
			}
			
			// 外食
			ShoppingDineOut beforeDineOut = ShoppingDineOut.from(beforeData.getShoppingDineOutExpenses(), beforeData.getShoppingDineOutTaxExpenses(), beforeCouponResidualValue);
			ShoppingDineOut afterDineOut = ShoppingDineOut.from(updData.getShoppingDineOutExpenses(), updData.getShoppingDineOutTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeDineOut.getResidualCouponPrice();
			afterCouponResidualValue = afterDineOut.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updDineOut = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の外食設定値
					beforeDineOut.getValue(),
					// 更新後の外食設定値
					afterDineOut.getValue(),
					// 更新前の外食の支出テーブル情報
					beforeDineOutItem);
			if(updDineOut.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updDineOut.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeDineOutItem, updDineOut.getUpdExpenditureItem());
			}
			
			// 日用消耗品
			ShoppingConsumerGoods beforeConsumerGoods = ShoppingConsumerGoods.from(beforeData.getShoppingConsumerGoodsExpenses(), beforeData.getShoppingConsumerGoodsTaxExpenses(), beforeCouponResidualValue);
			ShoppingConsumerGoods afterConsumerGoods = ShoppingConsumerGoods.from(updData.getShoppingConsumerGoodsExpenses(), updData.getShoppingConsumerGoodsTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeConsumerGoods.getResidualCouponPrice();
			afterCouponResidualValue = afterConsumerGoods.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updConsumerGoods = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の日用消耗品設定値
					beforeConsumerGoods.getValue(),
					// 更新後の日用消耗品設定値
					afterConsumerGoods.getValue(),
					// 更新前の日用消耗品の支出テーブル情報
					beforeConsumerGoodsItem);
			if(updConsumerGoods.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updConsumerGoods.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeConsumerGoodsItem, updConsumerGoods.getUpdExpenditureItem());
			}
			
			// 被服費
			ShoppingClothes beforeClothes = ShoppingClothes.from(beforeData.getShoppingClothesExpenses(), beforeData.getShoppingClothesTaxExpenses(), beforeCouponResidualValue);
			ShoppingClothes afterClothes = ShoppingClothes.from(updData.getShoppingClothesExpenses(), updData.getShoppingClothesTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeClothes.getResidualCouponPrice();
			afterCouponResidualValue = afterClothes.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updClothes = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の被服費設定値
					beforeClothes.getValue(),
					// 更新後の被服費設定値
					afterClothes.getValue(),
					// 更新前の被服費の支出テーブル情報
					beforeClothesItem);
			if(updClothes.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updClothes.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeClothesItem, updClothes.getUpdExpenditureItem());
			}
			
			// 仕事
			ShoppingWork beforeWork = ShoppingWork.from(beforeData.getShoppingWorkExpenses(), beforeData.getShoppingWorkTaxExpenses(), beforeCouponResidualValue);
			ShoppingWork afterWork = ShoppingWork.from(updData.getShoppingWorkExpenses(), updData.getShoppingWorkTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeWork.getResidualCouponPrice();
			afterCouponResidualValue = afterWork.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updWork = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の仕事設定値
					beforeWork.getValue(),
					// 更新後の仕事設定値
					afterWork.getValue(),
					// 更新前の仕事の支出テーブル情報
					beforeWorkItem);
			if(updWork.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updWork.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeWorkItem, updWork.getUpdExpenditureItem());
			}
			
			// 住居設備
			ShoppingHouseEquipment beforeHouseEquipment = ShoppingHouseEquipment.from(beforeData.getShoppingHouseEquipmentExpenses(), beforeData.getShoppingHouseEquipmentTaxExpenses(), beforeCouponResidualValue);
			ShoppingHouseEquipment afterHouseEquipment = ShoppingHouseEquipment.from(updData.getShoppingHouseEquipmentExpenses(), updData.getShoppingHouseEquipmentTaxExpenses(), afterCouponResidualValue);
			beforeCouponResidualValue = beforeHouseEquipment.getResidualCouponPrice();
			afterCouponResidualValue = afterHouseEquipment.getResidualCouponPrice();
			BeforeAndAfterShoppingSisyutuKingakuData updHouseEquipment = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の住居設備設定値
					beforeHouseEquipment.getValue(),
					// 更新後の住居設備設定値
					afterHouseEquipment.getValue(),
					// 更新前の住居設備の支出テーブル情報
					beforeHouseEquipmentItem);
			if(updHouseEquipment.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updHouseEquipment.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				sisyutuKingakuItemHolder.update(beforeHouseEquipmentItem, updHouseEquipment.getUpdExpenditureItem());
			}
			
			// 更新前・更新後の合計金額差額を収支テーブルの支出金額の値に反映
			int comp = beforeData.getShoppingTotalAmount().getValue().compareTo(updData.getShoppingTotalAmount().getValue());
			// 支出金額増減値
			ExpenditureAmount zougenti = ExpenditureAmount.from(beforeData.getShoppingTotalAmount().getValue().subtract(updData.getShoppingTotalAmount().getValue()).abs());
			if(comp > 0) {
				updSyuusiData = beforeSyuusiData.subtractSisyutuKingaku(zougenti);
			}
			if(comp < 0) {
				updSyuusiData = beforeSyuusiData.addSisyutuKingaku(zougenti);
			}
			
			// 完了メッセージ
			response.addMessage("買い物情報を更新しました。[code:" + updData.getShoppingRegistCode() + "]");
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
		
		// 支出テーブル情報を更新
		for(ExpenditureItem updExpenditureData : updExpenditureItemList) {
			int updCount = expenditureRepository.update(updExpenditureData);
			// 更新件数が1件以上の場合、業務エラー
			if(updCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出テーブル：EXPENDITURE_TABLEへの更新件数が不正でした。[件数=" + updCount + "][update data:" + updExpenditureData + "]");
			}
		}
		
		// ホルダーから新規追加データの支出金額テーブル情報を取得し対象件数分データを追加
		sisyutuKingakuItemHolder.getAddList().forEach(addData -> {
			// 支出金額テーブルに登録
			int addCount = sisyutuKingakuTableRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル:SISYUTU_KINGAKU_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
		});
		// ホルダーから更新データの支出金額テーブル情報を取得し対象件数分データを更新
		sisyutuKingakuItemHolder.getUpdateList().forEach(updData -> {
			// 支出金額テーブルを更新
			int updCount = sisyutuKingakuTableRepository.update(updData);
			// 更新件数が1件以上の場合、業務エラー
			if(updCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル:SISYUTU_KINGAKU_TABLEへの更新件数が不正でした。[件数=" + updCount + "][add data:" + updData + "]");
			}
		});
		
		// 収支テーブル更新ありの場合、更新データで収支テーブルを更新
		if(updSyuusiData != null) {
			int updCount = incomeAndExpenditureRepository.update(updSyuusiData);
			// 更新件数が1件以上の場合、業務エラー
			if(updCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("収支テーブル:INCOME_AND_EXPENDITURE_TABLEへの更新件数が不正でした。[件数=" + updCount + "][update data:" + updSyuusiData + "]");
			}
			// 支出テーブルから対象月の支出金額合計値を取得
			ExpenditureTotalAmount expenditureKingakuTotalAmount = expenditureRepository.sumExpenditureKingaku(searchYearMonth);
			// 収支テーブルの支出金額の値と対象月の支出テーブルの支出金額合計値が一致するかを確認
			ExpenditureTotalAmount chkExpenditureKingaku = ExpenditureTotalAmount.from(updSyuusiData.getExpenditureAmount().getValue());
			if(!chkExpenditureKingaku.equals(expenditureKingakuTotalAmount)) {
				throw new MyHouseholdAccountBookRuntimeException("該当月の支出情報が一致しません。管理者に問い合わせてください。[yearMonth=" + searchYearMonth.getYearMonth() + "]");
			}
		}
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録方法選択画面(メニュー選択画面)にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 表示対象の対象年月
	 * @return 買い物登録方法選択画面(メニュー選択画面)リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnShoppingTopRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readReturnShoppingTopRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getReturnShoppingTopRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 各月の収支参照画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 表示対象の対象年月
	 * @return 各月の収支参照画面リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnInquiryMonthRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readReturnInquiryMonthRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getReturnInquiryMonthRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面の表示情報を生成して返します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param registInfoForm  買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	private SimpleShoppingRegistResponse createResponse(UserId userId, SimpleShoppingRegistInfoForm registInfoForm) {
		
		// コードテーブルから店舗区分情報を取得
		List<CodeAndValuePair> shopKubunCodeList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN);
		if(shopKubunCodeList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「店舗区分情報：" + MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		// 店舗区分情報から店舗区分のオプションリスト情報を作成
		List<OptionItem> shopKubunOptionItemList = shopKubunCodeList.stream().map(pair ->
			OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toUnmodifiableList());
		
		// 選択した店舗区分に属する店舗情報を取得
		ShopInquiryList shopSearchResult = shopRepository.findById(SearchQueryUserIdAndShopKubunCode.from(userId, ShopKubunCode.from(registInfoForm.getShopKubunCode())));
		// 店舗情報ありの場合、店舗名選択ボックスの表示リストを作成
		List<OptionItem> shopNameOptionItemList = null;
		if(!shopSearchResult.isEmpty()) {
			// 店舗情報をレスポンスに設定
			shopNameOptionItemList = shopSearchResult.getValues().stream().map(domain ->
				OptionItem.from(domain.getShopCode().getValue(), domain.getShopName().getValue())).collect(Collectors.toUnmodifiableList());
		}
		// 支出テーブルに該当項目の支出データが登録されていることを確認
		// レスポンスを生成
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getInstance(shopKubunOptionItemList, shopNameOptionItemList, registInfoForm);
		if(shopSearchResult.isEmpty()) {
			// 店舗情報が0件の場合、メッセージを設定
			response.addMessage("選択した店舗区分に属する店舗情報が0件です。店舗区分を再選択してください。");
		}
		
		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, TargetYearMonth.from(registInfoForm.getTargetYearMonth())),
				// 値を設定するレスポンス
				response);
		
		return response;
	}
}
