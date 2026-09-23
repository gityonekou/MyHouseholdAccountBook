/**
 * 簡易タイプの買い物登録を行うユースケース（登録系）です。画面入力された買い物情報の新規登録・更新処理を行います。
 * ・買い物登録入力フォームの入力値に従ったアクション(登録 or 更新)実行処理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/03 : 1.00.00                      新規作成
 * 2025/12/28 : 1.01.00  feature-1.00-dev00  リファクタリング対応（DDD適応)
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応、追加リファクタリング対応(買い物登録ドメインの見直し)
 * 2026/09/23 : 1.02.01  feature-1.03-dev1   追加リファクタリング対応(SimpleShoppingRegistUseCaseを照会系、登録系に分割)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.application.usecase.account.component.ExpenditureAmountItemHolderComponent;
import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent;
import com.yonetani.webapp.accountbook.application.usecase.account.component.ShoppingRegistExpenditureItemComponent;
import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureAmountItemHolder;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.BeforeAndAfterShoppingSisyutuKingakuData;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem.ShoppingClothesItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenditureItem.ShoppingConsumerGoodsItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenditureItem.ShoppingDineOutItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenditureItem.ShoppingFoodItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodMinorWasteExpenditureItem.ShoppingFoodMinorWasteItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodSevereWasteExpenditureItem.ShoppingFoodSevereWasteItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenditureItem.ShoppingHouseEquipmentItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenditureItem.ShoppingWorkItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.CouponAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 簡易タイプの買い物登録を行うユースケース（登録系）です。画面入力された買い物情報の新規登録・更新処理を行います。
 * ・買い物登録入力フォームの入力値に従ったアクション(登録 or 更新)実行処理
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
public class SimpleShoppingRegistConfirmUseCase {

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
	private final ExpenditureAmountItemHolderComponent expenditureAmountItemHolderComponent;
	// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEリポジトリー
	private final IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;
	// 買い物登録時の必須支出項目をまとめたコンポーネント
	private final ShoppingRegistExpenditureItemComponent expenditureAndSisyutuKingakuComponent;
	// 支払方法情報取得コンポーネント
	private final PaymentMethodInfoComponent paymentMethodInfoComponent;

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
		ExpenditureAmountItemHolder expenditureAmountItemHolder = expenditureAmountItemHolderComponent.build(searchYearMonth);

		// 収支テーブル情報を取得
		IncomeAndExpenditure beforeSyuusiData = incomeAndExpenditureRepository.findByPrimaryKey(searchYearMonth);
		// 収支テーブル更新情報
		IncomeAndExpenditure updSyuusiData = null;

		// レスポンスを生成
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance(inputForm.getTargetYearMonth());

		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {

			// 新規採番する買い物登録コードの値を取得
			int count = shoppingRegistRepository.countBy(searchYearMonth);
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
			CouponAmount couponResidualValue = addData.getShoppingCouponPrice().toCouponAmount();

			// 支出テーブル情報を更新
			// 飲食(無駄づかいなし)
			ShoppingFoodItemExpenditureAmount food = addData.getShoppingFoodExpenditureItem().applyCoupon(couponResidualValue);
			if(food.hasExpenditureAmount()) {
				// 飲食(無駄づかいなし)の支出テーブル情報を作成
				ExpenditureItem updFoodExpenditureItem = beforeFoodItem.addSisyutuKingaku(food.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodItem, updFoodExpenditureItem);
			}
			couponResidualValue = food.getResidualCouponAmount();

			// 食料品(無駄遣い（軽度）)
			ShoppingFoodMinorWasteItemExpenditureAmount foodB = addData.getShoppingFoodMinorWasteExpenditureItem().applyCoupon(couponResidualValue);
			if(foodB.hasExpenditureAmount()) {
				// 飲食(無駄遣いB)の支出テーブル情報を作成
				ExpenditureItem updFoodBExpenditureItem = beforeFoodBItem.addSisyutuKingaku(foodB.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodBExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodBItem, updFoodBExpenditureItem);
			}
			couponResidualValue = foodB.getResidualCouponAmount();

			// 食料品(無駄遣い（重度）)
			ShoppingFoodSevereWasteItemExpenditureAmount foodC = addData.getShoppingFoodSevereWasteExpenditureItem().applyCoupon(couponResidualValue);
			if(foodC.hasExpenditureAmount()) {
				// 飲食(無駄遣いC)の支出テーブル情報を作成
				ExpenditureItem updFoodCExpenditureItem = beforeFoodCItem.addSisyutuKingaku(foodC.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodCExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodCItem, updFoodCExpenditureItem);

			}
			couponResidualValue = foodC.getResidualCouponAmount();

			// 外食
			ShoppingDineOutItemExpenditureAmount dineOut = addData.getShoppingDineOutExpenditureItem().applyCoupon(couponResidualValue);
			if(dineOut.hasExpenditureAmount()) {
				// 外食の支出テーブル情報を作成
				ExpenditureItem updDineOutExpenditureItem = beforeDineOutItem.addSisyutuKingaku(dineOut.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updDineOutExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeDineOutItem, updDineOutExpenditureItem);

			}
			couponResidualValue = dineOut.getResidualCouponAmount();

			// 日用消耗品
			ShoppingConsumerGoodsItemExpenditureAmount consumerGoods = addData.getShoppingConsumerGoodsExpenditureItem().applyCoupon(couponResidualValue);
			if(consumerGoods.hasExpenditureAmount()) {
				// 日用消耗品の支出テーブル情報を作成
				ExpenditureItem updConsumerGoodsExpenditureItem = beforeConsumerGoodsItem.addSisyutuKingaku(consumerGoods.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updConsumerGoodsExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeConsumerGoodsItem, updConsumerGoodsExpenditureItem);

			}
			couponResidualValue = consumerGoods.getResidualCouponAmount();

			// 被服費
			ShoppingClothesItemExpenditureAmount clothes = addData.getShoppingClothesExpenditureItem().applyCoupon(couponResidualValue);
			if(clothes.hasExpenditureAmount()) {
				// 被服費の支出テーブル情報を作成
				ExpenditureItem updClothesExpenditureItem = beforeClothesItem.addSisyutuKingaku(clothes.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updClothesExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeClothesItem, updClothesExpenditureItem);
			}
			couponResidualValue = clothes.getResidualCouponAmount();

			// 仕事
			ShoppingWorkItemExpenditureAmount work = addData.getShoppingWorkExpenditureItem().applyCoupon(couponResidualValue);
			if(work.hasExpenditureAmount()) {
				// 仕事の支出テーブル情報を作成
				ExpenditureItem updWorkExpenditureItem = beforeWorkItem.addSisyutuKingaku(work.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updWorkExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeWorkItem, updWorkExpenditureItem);
			}
			couponResidualValue = work.getResidualCouponAmount();

			// 住居設備
			ShoppingHouseEquipmentItemExpenditureAmount houseEquipment = addData.getShoppingHouseEquipmentExpenditureItem().applyCoupon(couponResidualValue);
			if(houseEquipment.hasExpenditureAmount()) {
				// 住居設備の支出テーブル情報を作成
				ExpenditureItem updHouseEquipmentExpenditureItem = beforeHouseEquipmentItem.addSisyutuKingaku(houseEquipment.getExpenditureAmount());
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updHouseEquipmentExpenditureItem);
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeHouseEquipmentItem, updHouseEquipmentExpenditureItem);
			}
			couponResidualValue = houseEquipment.getResidualCouponAmount();

			// 収支テーブル情報に合計値を設定し更新情報とする
			updSyuusiData = beforeSyuusiData.addExpenditureAmount(ExpenditureAmount.from(addData.getShoppingTotalAmount().getValue()));

			// 完了メッセージ
			response.addMessage("買い物情報を新規登録しました。[code:" + addData.getShoppingRegistCode() + "]");

		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {

			// 対象年月のドメインタイプを生成
			TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(inputForm.getTargetYearMonth());
			// 買い物登録コードのドメインタイプを生成
			ShoppingRegistCode domainShoppingRegistCode = ShoppingRegistCode.from(inputForm.getShoppingRegistCode());

			// 更新対象のデータを取得(更新前と更新後の差額計算用)
			ShoppingRegist beforeData = shoppingRegistRepository.findByPrimaryKey(
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
			CouponAmount beforeCouponResidualValue = beforeData.getShoppingCouponPrice().toCouponAmount();
			CouponAmount afterCouponResidualValue = updData.getShoppingCouponPrice().toCouponAmount();

			/* 支出テーブル情報を更新 */
			// 飲食(無駄づかいなし)
			ShoppingFoodItemExpenditureAmount beforeFood = beforeData.getShoppingFoodExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingFoodItemExpenditureAmount afterFood = updData.getShoppingFoodExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updFood = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄づかいなし)設定値
					beforeFood.getExpenditureAmount(),
					// 更新後の飲食(無駄づかいなし)設定値
					afterFood.getExpenditureAmount(),
					// 更新前の飲食(無駄づかいなし)の支出テーブル情報
					beforeFoodItem);
			if(updFood.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFood.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodItem, updFood.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeFood.getResidualCouponAmount();
			afterCouponResidualValue = afterFood.getResidualCouponAmount();

			// 飲食(無駄遣いB)
			ShoppingFoodMinorWasteItemExpenditureAmount beforeFoodB = beforeData.getShoppingFoodMinorWasteExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingFoodMinorWasteItemExpenditureAmount afterFoodB = updData.getShoppingFoodMinorWasteExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updFoodB = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄遣いB)設定値
					beforeFoodB.getExpenditureAmount(),
					// 更新後の飲食(無駄遣いB)設定値
					afterFoodB.getExpenditureAmount(),
					// 更新前の飲食(無駄遣いB)の支出テーブル情報
					beforeFoodBItem);
			if(updFoodB.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodB.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodBItem, updFoodB.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeFoodB.getResidualCouponAmount();
			afterCouponResidualValue = afterFoodB.getResidualCouponAmount();

			// 飲食(無駄遣いC)
			ShoppingFoodSevereWasteItemExpenditureAmount beforeFoodC = beforeData.getShoppingFoodSevereWasteExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingFoodSevereWasteItemExpenditureAmount afterFoodC = updData.getShoppingFoodSevereWasteExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updFoodC = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の飲食(無駄遣いC)設定値
					beforeFoodC.getExpenditureAmount(),
					// 更新後の飲食(無駄遣いC)設定値
					afterFoodC.getExpenditureAmount(),
					// 更新前の飲食(無駄遣いC)の支出テーブル情報
					beforeFoodCItem);
			if(updFoodC.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updFoodC.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeFoodCItem, updFoodC.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeFoodC.getResidualCouponAmount();
			afterCouponResidualValue = afterFoodC.getResidualCouponAmount();

			// 外食
			ShoppingDineOutItemExpenditureAmount beforeDineOut = beforeData.getShoppingDineOutExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingDineOutItemExpenditureAmount afterDineOut = updData.getShoppingDineOutExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updDineOut = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の外食設定値
					beforeDineOut.getExpenditureAmount(),
					// 更新後の外食設定値
					afterDineOut.getExpenditureAmount(),
					// 更新前の外食の支出テーブル情報
					beforeDineOutItem);
			if(updDineOut.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updDineOut.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeDineOutItem, updDineOut.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeDineOut.getResidualCouponAmount();
			afterCouponResidualValue = afterDineOut.getResidualCouponAmount();

			// 日用消耗品
			ShoppingConsumerGoodsItemExpenditureAmount beforeConsumerGoods = beforeData.getShoppingConsumerGoodsExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingConsumerGoodsItemExpenditureAmount afterConsumerGoods = updData.getShoppingConsumerGoodsExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updConsumerGoods = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の日用消耗品設定値
					beforeConsumerGoods.getExpenditureAmount(),
					// 更新後の日用消耗品設定値
					afterConsumerGoods.getExpenditureAmount(),
					// 更新前の日用消耗品の支出テーブル情報
					beforeConsumerGoodsItem);
			if(updConsumerGoods.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updConsumerGoods.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeConsumerGoodsItem, updConsumerGoods.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeConsumerGoods.getResidualCouponAmount();
			afterCouponResidualValue = afterConsumerGoods.getResidualCouponAmount();

			// 被服費
			ShoppingClothesItemExpenditureAmount beforeClothes = beforeData.getShoppingClothesExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingClothesItemExpenditureAmount afterClothes = updData.getShoppingClothesExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updClothes = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の被服費設定値
					beforeClothes.getExpenditureAmount(),
					// 更新後の被服費設定値
					afterClothes.getExpenditureAmount(),
					// 更新前の被服費の支出テーブル情報
					beforeClothesItem);
			if(updClothes.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updClothes.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeClothesItem, updClothes.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeClothes.getResidualCouponAmount();
			afterCouponResidualValue = afterClothes.getResidualCouponAmount();

			// 仕事
			ShoppingWorkItemExpenditureAmount beforeWork = beforeData.getShoppingWorkExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingWorkItemExpenditureAmount afterWork = updData.getShoppingWorkExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updWork = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の仕事設定値
					beforeWork.getExpenditureAmount(),
					// 更新後の仕事設定値
					afterWork.getExpenditureAmount(),
					// 更新前の仕事の支出テーブル情報
					beforeWorkItem);
			if(updWork.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updWork.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeWorkItem, updWork.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeWork.getResidualCouponAmount();
			afterCouponResidualValue = afterWork.getResidualCouponAmount();

			// 住居設備
			ShoppingHouseEquipmentItemExpenditureAmount beforeHouseEquipment = beforeData.getShoppingHouseEquipmentExpenditureItem().applyCoupon(beforeCouponResidualValue);
			ShoppingHouseEquipmentItemExpenditureAmount afterHouseEquipment = updData.getShoppingHouseEquipmentExpenditureItem().applyCoupon(afterCouponResidualValue);
			BeforeAndAfterShoppingSisyutuKingakuData updHouseEquipment = BeforeAndAfterShoppingSisyutuKingakuData.from(
					// 更新前の住居設備設定値
					beforeHouseEquipment.getExpenditureAmount(),
					// 更新後の住居設備設定値
					afterHouseEquipment.getExpenditureAmount(),
					// 更新前の住居設備の支出テーブル情報
					beforeHouseEquipmentItem);
			if(updHouseEquipment.isUpdated()) {
				// 更新対象の支出テーブル情報に追加
				updExpenditureItemList.add(updHouseEquipment.getUpdExpenditureItem());
				// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
				expenditureAmountItemHolder.update(beforeHouseEquipmentItem, updHouseEquipment.getUpdExpenditureItem());
			}
			beforeCouponResidualValue = beforeHouseEquipment.getResidualCouponAmount();
			afterCouponResidualValue = afterHouseEquipment.getResidualCouponAmount();

			// 更新前・更新後の合計金額差額を収支テーブルの支出金額の値に反映
			int comp = beforeData.getShoppingTotalAmount().getValue().compareTo(updData.getShoppingTotalAmount().getValue());
			// 支出金額増減値
			ExpenditureAmount zougenti = ExpenditureAmount.from(beforeData.getShoppingTotalAmount().getValue().subtract(updData.getShoppingTotalAmount().getValue()).abs());
			if(comp > 0) {
				updSyuusiData = beforeSyuusiData.subtractExpenditureAmount(zougenti);
			}
			if(comp < 0) {
				updSyuusiData = beforeSyuusiData.addExpenditureAmount(zougenti);
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
		expenditureAmountItemHolder.getAddList().forEach(addData -> {
			// 支出金額テーブルに登録
			int addCount = sisyutuKingakuTableRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル:SISYUTU_KINGAKU_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
		});
		// ホルダーから更新データの支出金額テーブル情報を取得し対象件数分データを更新
		expenditureAmountItemHolder.getUpdateList().forEach(updData -> {
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
			ExpenditureTotalAmount expenditureKingakuTotalAmount = expenditureRepository.getExpenditureTotalAmount(searchYearMonth);
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
		// 店舗情報ありの場合、店舗名選択肢のリストを作成(デフォルト支払方法をdata属性経由でJSに渡すため、5.6節)
		List<SimpleShoppingRegistResponse.ShopOptionItem> shopNameOptionItemList = null;
		if(!shopSearchResult.isEmpty()) {
			// 店舗情報をレスポンスに設定
			shopNameOptionItemList = shopSearchResult.getValues().stream().map(domain ->
				SimpleShoppingRegistResponse.ShopOptionItem.from(
						domain.getShopCode().getValue(),
						domain.getShopName().getValue(),
						domain.getDefaultPaymentMethodCode() == null ? null : domain.getDefaultPaymentMethodCode().getValue()))
				.collect(Collectors.toUnmodifiableList());
		}
		// 支出テーブルに該当項目の支出データが登録されていることを確認
		// レスポンスを生成
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getInstance(shopKubunOptionItemList, shopNameOptionItemList,
				paymentMethodInfoComponent.getPaymentMethodOptions(userId), registInfoForm);
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
