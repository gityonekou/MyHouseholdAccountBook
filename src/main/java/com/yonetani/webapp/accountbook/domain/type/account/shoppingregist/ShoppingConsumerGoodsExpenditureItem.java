/**
 * 「日用品」項目の値を表すドメインタイプです。
 * 日用品購入金額、日用品消費税の値の合計値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/12/07 : 1.00.00                      新規作成
 * 2026/09/20 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応（NullableMoneyを継承し、購入金額と消費税を買い物登録の共通ドメインタイプに変更、ShoppingConsumerGoodsクラスを集約(ShoppingConsumerGoodsは廃止))
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
 * 「日用品」項目の値を表すドメインタイプです。
 * 日用品購入金額、日用品消費税の値の合計値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ShoppingConsumerGoodsExpenditureItem extends NullableMoney {
	
	/** 値がnullの「日用品」項目の値 */
	public static final ShoppingConsumerGoodsExpenditureItem NULL = ShoppingConsumerGoodsExpenditureItem.from(ShoppingExpenditureAmount.NULL, ShoppingTaxExpenses.NULL);
	
	// 日用品購入金額
	private final ShoppingExpenditureAmount shoppingConsumerGoodsExpenditureAmount;
	// 日用品消費税
	private final ShoppingTaxExpenses shoppingConsumerGoodsTaxExpenses;
	

	/**
	 *<pre>
	 * 「日用品」項目の値から、クーポン金額を適応した後の実際の「日用品」支出金額を表すドメインタイプです。
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
	public static class ShoppingConsumerGoodsItemExpenditureAmount {
		// 日用品支出金額
		private final ExpenditureAmount expenditureAmount;
		// 残クーポン額
		private final CouponAmount residualCouponAmount;
		/**
		 *<pre>
		 * 「日用品」項目の支出金額の値があるかどうかを判定します。
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
	 * ShoppingConsumerGoodsExpenditureItemクラスコンストラクターです。
	 *</pre>
	 * @param value 日用品項目の金額値(購入金額と消費税の合算値)
	 * @param expenditureAmount 日用品購入金額
	 * @param taxExpenses 日用品消費税
	 *
	 */
	private ShoppingConsumerGoodsExpenditureItem(BigDecimal value, ShoppingExpenditureAmount expenditureAmount, ShoppingTaxExpenses taxExpenses) {
		super(value);
		this.shoppingConsumerGoodsExpenditureAmount = expenditureAmount;
		this.shoppingConsumerGoodsTaxExpenses = taxExpenses;
	}
	
	/**
	 *<pre>
	 * 「日用品」項目の値を表すドメインタイプを生成します。
	 * 日用品購入金額、日用品消費税の値の合計値となります。
	 * 
	 * [非ガード節]
	 * ・日用品購入金額の値がnull値
	 * [ガード節]
	 * ・日用品購入金額がnull
	 * ・日用品消費税がnull
	 * ・日用品消費税の値ありの場合で、日用品購入金額の値がnull値の場合
	 *</pre>
	 * @param amount 日用品購入金額
	 * @param taxAmount 日用品消費税
	 * @return 「日用品」項目ドメインタイプ
	 *
	 */
	public static ShoppingConsumerGoodsExpenditureItem from(ShoppingExpenditureAmount amount, ShoppingTaxExpenses taxAmount) {
		// ガード節(「日用品購入金額」項目がnull)
		if(amount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「日用品購入金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「日用品消費税」項目がnull)
		if(taxAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「日用品消費税」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(日用品消費税の値ありの場合で、日用品購入金額の値がnull値)
		if(amount.isNull() && !taxAmount.isNull()) {
			throw new MyHouseholdAccountBookRuntimeException("「日用品」項目の設定値が不正です。管理者に問い合わせてください。[amountValue=null][taxAmountValue=" + taxAmount.toString() + "]");
		}
		
		// 日用品購入金額の値がnull値の場合、null値を持った「日用品」項目を生成
		if(amount.isNull()) {
			validate(amount.getValue(), "日用品");
			return new ShoppingConsumerGoodsExpenditureItem(
					ShoppingExpenditureAmount.NULL.getValue(),
					ShoppingExpenditureAmount.NULL,
					ShoppingTaxExpenses.NULL);
		}
		
		// 日用品消費税の値がnull値の場合、日用品購入金額の値で「日用品」項目を生成
		if(taxAmount.isNull()) {
			validate(amount.getValue(), "日用品");
			return new ShoppingConsumerGoodsExpenditureItem(amount.getValue(), amount, taxAmount);
		}
		// 日用品購入金額と日用品消費税を加算した値で「日用品」項目を生成
		BigDecimal addValue = amount.getValue().add(taxAmount.getValue());
		validate(addValue, "日用品");
		return new ShoppingConsumerGoodsExpenditureItem(addValue, amount, taxAmount);
	}
	
	/**
	 *<pre>
	 * 日用品の値を指定した日用品の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する日用品の値
	 * @return 加算した日用品の値(this + addValue)
	 *
	 */
	public ShoppingConsumerGoodsExpenditureItem add(ShoppingConsumerGoodsExpenditureItem addValue) {
		// 加算値がnullなら、自分自身を返す
		if(addValue == null || addValue.isNull()) {
			return this;
		}
		// 自分自身がnullなら、加算値で新しいドメイン値を生成して返す
		if(isNull()) {
			return ShoppingConsumerGoodsExpenditureItem.from(
					addValue.getShoppingConsumerGoodsExpenditureAmount(),
					addValue.getShoppingConsumerGoodsTaxExpenses());
		}
		// 自分自身と加算値で加算を実施して新しいドメイン値を生成して返す
		return ShoppingConsumerGoodsExpenditureItem.from(
				this.shoppingConsumerGoodsExpenditureAmount.add(addValue.getShoppingConsumerGoodsExpenditureAmount()),
				this.shoppingConsumerGoodsTaxExpenses.add(addValue.getShoppingConsumerGoodsTaxExpenses()));
	}
	
	/**
	 *<pre>
	 * クーポン金額を適用します。
	 * 「日用品」項目の値からクーポンによる割引額を差し引いた、実際の「日用品」支払金額（支出金額）を返却します。
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
	 * @return クーポン適用後の「日用品」支払金額（支出金額）
	 * @throws MyHouseholdAccountBookRuntimeException クーポンがnull、または適用後の金額が0未満になる場合
	 *
	 */
	public ShoppingConsumerGoodsItemExpenditureAmount applyCoupon(CouponAmount coupon) {
		// ガード節(「クーポン」項目がnull)
		if(coupon == null) {
			throw new MyHouseholdAccountBookRuntimeException("適応するクーポン金額にnullが指定されました。管理者に問い合わせてください。");
		}
		
		// 日用品金額無しなら、「日用品」支払金額（支出金額）＝0円で値を生成
		if(isZero()) {
			return new ShoppingConsumerGoodsItemExpenditureAmount(ExpenditureAmount.ZERO, coupon);
		}
		// クーポン指定なしなら割引適応なしで日用品を生成
		if(!coupon.hasDiscount()) {
			return new ShoppingConsumerGoodsItemExpenditureAmount(ExpenditureAmount.from(getNullSafeValue()), coupon);
		}

		// 日用品金額からクーポン金額を割引（支出金額 - クーポン金額）
		BigDecimal discountValue = getValue().subtract(coupon.getValue());

		// 割引後の金額がマイナス値)：日用品金額は割引適応でなし。差額を残のクーポン金額へ
		int compareToValue = discountValue.compareTo(BigDecimal.ZERO);
		if (compareToValue < 0) {
			return new ShoppingConsumerGoodsItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.from(discountValue.abs()));
		}
		// 割引後の金額が0)：日用品金額は割引適応でなし。残クーポン値もなし
		if (compareToValue == 0) {
			return new ShoppingConsumerGoodsItemExpenditureAmount(ExpenditureAmount.ZERO, CouponAmount.ZERO);
		}
		// 割引後の金額が0より大きい)：日用品金額は割引適応後の値。残クーポン値はなし
		return new ShoppingConsumerGoodsItemExpenditureAmount(ExpenditureAmount.from(discountValue),  CouponAmount.ZERO);
	}
}
