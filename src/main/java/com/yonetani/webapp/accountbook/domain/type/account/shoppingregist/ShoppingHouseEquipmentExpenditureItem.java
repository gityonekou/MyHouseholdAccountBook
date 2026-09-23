/**
 * 「住居設備」項目の値を表すドメインタイプです。
 * 住居設備購入金額、住居設備消費税の値の合計値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2025/01/01 : 1.00.00                      新規作成
 * 2026/09/20 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応（NullableMoneyを継承し、購入金額と消費税を買い物登録の共通ドメインタイプに変更、ShoppingHouseEquipmentクラスを集約(ShoppingHouseEquipmentは廃止))
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.CouponAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「住居設備」項目の値を表すドメインタイプです。
 * 住居設備購入金額、住居設備消費税の値の合計値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ShoppingHouseEquipmentExpenditureItem extends NullableMoney {
	
	/** 値がnullの「住居設備」項目の値 */
	public static final ShoppingHouseEquipmentExpenditureItem NULL = ShoppingHouseEquipmentExpenditureItem.from(ShoppingExpenditureAmount.NULL, ShoppingTaxExpenses.NULL);
	
	// 住居設備購入金額
	private final ShoppingExpenditureAmount shoppingHouseEquipmentExpenditureAmount;
	// 住居設備消費税
	private final ShoppingTaxExpenses shoppingHouseEquipmentTaxExpenses;
	

	/**
	 *<pre>
	 * 「住居設備」項目の値から、クーポン金額を適応した後の実際の「住居設備」支出金額を表すドメインタイプです。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.03)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class ShoppingHouseEquipmentItemExpenditureAmount {
		// 住居設備支出金額
		private final ExpenditureAmount expenditureAmount;
		// 残クーポン額
		private final CouponAmount residualCouponAmount;
		/**
		 *<pre>
		 * 「住居設備」項目の支出金額の値があるかどうかを判定します。
		 * 　支出金額がゼロの場合は値なし、それ以外の場合は値ありとなります。
		 * 
		 * 「支出金額」項目はnull不可なのでnull値は設定されません。
		 *</pre>
		 * @return 支出金額がゼロの場合はfalse、それ以外の場合はtrueとなります。
		 *
		 */
		public boolean hasExpenditureAmount() {
			return !expenditureAmount.equals(ExpenditureAmount.ZERO);
		}
	}
	
	/**
	 *<pre>
	 * ShoppingHouseEquipmentExpenditureItemクラスコンストラクターです。
	 *</pre>
	 * @param value 住居設備項目の金額値(購入金額と消費税の合算値)
	 * @param expenditureAmount 住居設備購入金額
	 * @param taxExpenses 住居設備消費税
	 *
	 */
	private ShoppingHouseEquipmentExpenditureItem(BigDecimal value, ShoppingExpenditureAmount expenditureAmount, ShoppingTaxExpenses taxExpenses) {
		super(value);
		this.shoppingHouseEquipmentExpenditureAmount = expenditureAmount;
		this.shoppingHouseEquipmentTaxExpenses = taxExpenses;
	}
	
	/**
	 *<pre>
	 * 「住居設備」項目の値を表すドメインタイプを生成します。
	 * 住居設備購入金額、住居設備消費税の値の合計値となります。
	 * 
	 * [非ガード節]
	 * ・住居設備購入金額の値がnull値
	 * [ガード節]
	 * ・住居設備購入金額がnull
	 * ・住居設備消費税がnull
	 * ・住居設備消費税の値ありの場合で、住居設備購入金額の値がnull値の場合
	 *</pre>
	 * @param amount 住居設備購入金額
	 * @param taxAmount 住居設備消費税
	 * @return 「住居設備」項目ドメインタイプ
	 *
	 */
	public static ShoppingHouseEquipmentExpenditureItem from(ShoppingExpenditureAmount amount, ShoppingTaxExpenses taxAmount) {
		// ガード節(「住居設備購入金額」項目がnull)
		if(amount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「住居設備購入金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「住居設備消費税」項目がnull)
		if(taxAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「住居設備消費税」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(住居設備消費税の値ありの場合で、住居設備購入金額の値がnull値)
		if(amount.isNull() && !taxAmount.isNull()) {
			throw new MyHouseholdAccountBookRuntimeException("「住居設備」項目の設定値が不正です。管理者に問い合わせてください。[amountValue=null][taxAmountValue=" + taxAmount.toString() + "]");
		}
		
		// 住居設備購入金額の値がnull値の場合、null値を持った「住居設備」項目を生成
		if(amount.isNull()) {
			validate(amount.getValue(), "住居設備");
			return new ShoppingHouseEquipmentExpenditureItem(
					ShoppingExpenditureAmount.NULL.getValue(),
					ShoppingExpenditureAmount.NULL,
					ShoppingTaxExpenses.NULL);
		}
		
		// 住居設備消費税の値がnull値の場合、住居設備購入金額の値で「住居設備」項目を生成
		if(taxAmount.isNull()) {
			validate(amount.getValue(), "住居設備");
			return new ShoppingHouseEquipmentExpenditureItem(amount.getValue(), amount, taxAmount);
		}
		// 住居設備購入金額と住居設備消費税を加算した値で「住居設備」項目を生成
		BigDecimal addValue = amount.getValue().add(taxAmount.getValue());
		validate(addValue, "住居設備");
		return new ShoppingHouseEquipmentExpenditureItem(addValue, amount, taxAmount);
	}
	
	/**
	 *<pre>
	 * 住居設備の値を指定した住居設備の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する住居設備の値
	 * @return 加算した住居設備の値(this + addValue)
	 *
	 */
	public ShoppingHouseEquipmentExpenditureItem add(ShoppingHouseEquipmentExpenditureItem addValue) {
		// 加算値がnullなら、自分自身を返す
		if(addValue == null || addValue.isNull()) {
			return this;
		}
		// 自分自身がnullなら、加算値で新しいドメイン値を生成して返す
		if(isNull()) {
			return ShoppingHouseEquipmentExpenditureItem.from(
					addValue.getShoppingHouseEquipmentExpenditureAmount(),
					addValue.getShoppingHouseEquipmentTaxExpenses());
		}
		// 自分自身と加算値で加算を実施して新しいドメイン値を生成して返す
		return ShoppingHouseEquipmentExpenditureItem.from(
				this.shoppingHouseEquipmentExpenditureAmount.add(addValue.getShoppingHouseEquipmentExpenditureAmount()),
				this.shoppingHouseEquipmentTaxExpenses.add(addValue.getShoppingHouseEquipmentTaxExpenses()));
	}
	
	/**
	 *<pre>
	 * クーポン金額を適用します。
	 * 「住居設備」項目の値からクーポンによる割引額を差し引いた、実際の「住居設備」支払金額（支出金額）を返却します。
	 *
	 * [ビジネスルール]
	 * ・クーポン適用後の金額が0未満になってはならない。
	 * ・クーポン適応後、金額が0となった場合でクーポン残高がある場合、その残高が残クーポン額に設定される。
	 *
	 * [使用例]
	 * - 買い物登録時の支払金額計算（支出テーブル、支出金額テーブル、収支テーブルのデータ更新値計算に使用）
	 * - 商品金額合計 + 消費税 - クーポン金額 = 支払金額
	 *
	 *</pre>
	 * @param coupon 適用するクーポン金額
	 * @return クーポン適用後の「住居設備」支払金額（支出金額）
	 * @throws MyHouseholdAccountBookRuntimeException クーポンがnull、または適用後の金額が0未満になる場合
	 *
	 */
	public ShoppingHouseEquipmentItemExpenditureAmount applyCoupon(CouponAmount coupon) {
		// ガード節(「クーポン」項目がnull)
		if(coupon == null) {
			throw new MyHouseholdAccountBookRuntimeException("適応するクーポン金額にnullが指定されました。管理者に問い合わせてください。");
		}
		
		// 住居設備金額無しなら、「住居設備」支払金額（支出金額）＝0円で値を生成
		if(isZero()) {
			return new ShoppingHouseEquipmentItemExpenditureAmount(ExpenditureAmount.ZERO, coupon);
		}
		// クーポン指定なしなら割引適応なしで住居設備を生成
		if(!coupon.hasDiscount()) {
			return new ShoppingHouseEquipmentItemExpenditureAmount(ExpenditureAmount.from(getNullSafeValue()), coupon);
		}

		// 住居設備金額からクーポン金額を割引（支出金額 - クーポン金額）
		BigDecimal discountValue = getValue().subtract(coupon.getValue());

		// 割引後の金額がマイナス値)：住居設備金額は割引適応でなし。差額を残のクーポン金額へ
		int compareToValue = discountValue.compareTo(BigDecimal.ZERO);
		if (compareToValue < 0) {
			return new ShoppingHouseEquipmentItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.from(discountValue.abs()));
		}
		// 割引後の金額が0)：住居設備金額は割引適応でなし。残クーポン値もなし
		if (compareToValue == 0) {
			return new ShoppingHouseEquipmentItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.ZERO);
		}
		// 割引後の金額が0より大きい)：住居設備金額は割引適応後の値。残クーポン値はなし
		return new ShoppingHouseEquipmentItemExpenditureAmount(ExpenditureAmount.from(discountValue),  CouponAmount.ZERO);
	}
}
