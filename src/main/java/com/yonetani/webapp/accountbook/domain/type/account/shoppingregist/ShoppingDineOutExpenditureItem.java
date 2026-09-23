/**
 * 「外食」項目の値を表すドメインタイプです。
 * 外食購入金額、外食消費税の値の合計値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/12/07 : 1.00.00                      新規作成
 * 2026/09/20 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応（NullableMoneyを継承し、購入金額と消費税を買い物登録の共通ドメインタイプに変更、ShoppingDineOutクラスを集約(ShoppingDineOutは廃止))
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
 * 「外食」項目の値を表すドメインタイプです。
 * 外食購入金額、外食消費税の値の合計値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ShoppingDineOutExpenditureItem extends NullableMoney {
	
	/** 値がnullの「外食」項目の値 */
	public static final ShoppingDineOutExpenditureItem NULL = ShoppingDineOutExpenditureItem.from(ShoppingExpenditureAmount.NULL, ShoppingTaxExpenses.NULL);
	
	// 外食購入金額
	private final ShoppingExpenditureAmount shoppingDineOutExpenditureAmount;
	// 外食消費税
	private final ShoppingTaxExpenses shoppingDineOutTaxExpenses;

	/**
	 *<pre>
	 * 「外食」項目の値から、クーポン金額を適応した後の実際の「外食」支出金額を表すドメインタイプです。
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
	public static class ShoppingDineOutItemExpenditureAmount {
		// 外食支出金額
		private final ExpenditureAmount expenditureAmount;
		// 残クーポン額
		private final CouponAmount residualCouponAmount;
		/**
		 *<pre>
		 * 「外食」項目の支出金額の値があるかどうかを判定します。
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
	 * ShoppingDineOutExpenditureItemクラスコンストラクターです。
	 *</pre>
	 * @param value 外食項目の金額値(購入金額と消費税の合算値)
	 * @param expenditureAmount 外食購入金額
	 * @param taxExpenses 外食消費税
	 *
	 */
	private ShoppingDineOutExpenditureItem(BigDecimal value, ShoppingExpenditureAmount expenditureAmount, ShoppingTaxExpenses taxExpenses) {
		super(value);
		this.shoppingDineOutExpenditureAmount = expenditureAmount;
		this.shoppingDineOutTaxExpenses = taxExpenses;
	}
	
	/**
	 *<pre>
	 * 「外食」項目の値を表すドメインタイプを生成します。
	 * 外食購入金額、外食消費税の値の合計値となります。
	 * 
	 * [非ガード節]
	 * ・外食購入金額の値がnull値
	 * [ガード節]
	 * ・外食購入金額がnull
	 * ・外食消費税がnull
	 * ・外食消費税の値ありの場合で、外食購入金額の値がnull値の場合
	 *</pre>
	 * @param amount 外食購入金額
	 * @param taxAmount 外食消費税
	 * @return 「外食」項目ドメインタイプ
	 *
	 */
	public static ShoppingDineOutExpenditureItem from(ShoppingExpenditureAmount amount, ShoppingTaxExpenses taxAmount) {
		// ガード節(「外食購入金額」項目がnull)
		if(amount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「外食購入金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「外食消費税」項目がnull)
		if(taxAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「外食消費税」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(外食消費税の値ありの場合で、外食購入金額の値がnull値)
		if(amount.isNull() && !taxAmount.isNull()) {
			throw new MyHouseholdAccountBookRuntimeException("「外食」項目の設定値が不正です。管理者に問い合わせてください。[amountValue=null][taxAmountValue=" + taxAmount.toString() + "]");
		}
		
		// 外食購入金額の値がnull値の場合、null値を持った「外食」項目を生成
		if(amount.isNull()) {
			validate(amount.getValue(), "外食");
			return new ShoppingDineOutExpenditureItem(
					ShoppingExpenditureAmount.NULL.getValue(),
					ShoppingExpenditureAmount.NULL,
					ShoppingTaxExpenses.NULL);
		}
		
		// 外食消費税の値がnull値の場合、外食購入金額の値で「外食」項目を生成
		if(taxAmount.isNull()) {
			validate(amount.getValue(), "外食");
			return new ShoppingDineOutExpenditureItem(amount.getValue(), amount, taxAmount);
		}
		// 外食購入金額と外食消費税を加算した値で「外食」項目を生成
		BigDecimal addValue = amount.getValue().add(taxAmount.getValue());
		validate(addValue, "外食");
		return new ShoppingDineOutExpenditureItem(addValue, amount, taxAmount);
	}
	
	/**
	 *<pre>
	 * 外食の値を指定した外食の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する外食の値
	 * @return 加算した外食の値(this + addValue)
	 *
	 */
	public ShoppingDineOutExpenditureItem add(ShoppingDineOutExpenditureItem addValue) {
		// 加算値がnullなら、自分自身を返す
		if(addValue == null || addValue.isNull()) {
			return this;
		}
		// 自分自身がnullなら、加算値で新しいドメイン値を生成して返す
		if(isNull()) {
			return ShoppingDineOutExpenditureItem.from(
					addValue.getShoppingDineOutExpenditureAmount(),
					addValue.getShoppingDineOutTaxExpenses());
		}
		// 自分自身と加算値で加算を実施して新しいドメイン値を生成して返す
		return ShoppingDineOutExpenditureItem.from(
				this.shoppingDineOutExpenditureAmount.add(addValue.getShoppingDineOutExpenditureAmount()),
				this.shoppingDineOutTaxExpenses.add(addValue.getShoppingDineOutTaxExpenses()));
	}
	
	/**
	 *<pre>
	 * クーポン金額を適用します。
	 * 「外食」項目の値からクーポンによる割引額を差し引いた、実際の「外食」支払金額（支出金額）を返却します。
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
	 * @return クーポン適用後の「外食」支払金額（支出金額）
	 * @throws MyHouseholdAccountBookRuntimeException クーポンがnull、または適用後の金額が0未満になる場合
	 *
	 */
	public ShoppingDineOutItemExpenditureAmount applyCoupon(CouponAmount coupon) {
		// ガード節(「クーポン」項目がnull)
		if(coupon == null) {
			throw new MyHouseholdAccountBookRuntimeException("適応するクーポン金額にnullが指定されました。管理者に問い合わせてください。");
		}
		
		// 外食金額無しなら、「外食」支払金額（支出金額）＝0円で値を生成
		if(isZero()) {
			return new ShoppingDineOutItemExpenditureAmount(ExpenditureAmount.ZERO, coupon);
		}
		// クーポン指定なしなら割引適応なしで外食を生成
		if(coupon.isZero()) {
			return new ShoppingDineOutItemExpenditureAmount(ExpenditureAmount.from(getNullSafeValue()), coupon);
		}

		// 外食金額からクーポン金額を割引（支出金額 - クーポン金額）
		BigDecimal discountValue = getValue().subtract(coupon.getValue());

		// 割引後の金額がマイナス値)：外食金額は割引適応でなし。差額を残のクーポン金額へ
		int compareToValue = discountValue.compareTo(BigDecimal.ZERO);
		if (compareToValue < 0) {
			return new ShoppingDineOutItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.from(discountValue.abs()));
		}
		// 割引後の金額が0)：外食金額は割引適応でなし。残クーポン値もなし
		if (compareToValue == 0) {
			return new ShoppingDineOutItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.ZERO);
		}
		// 割引後の金額が0より大きい)：外食金額は割引適応後の値。残クーポン値はなし
		return new ShoppingDineOutItemExpenditureAmount(ExpenditureAmount.from(discountValue),  CouponAmount.ZERO);
	}
}
