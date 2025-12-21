/**
 * 「外食」項目のドメインです。
 * 「外食金額」項目と「消費税:外食金額」項目の加算値から「クーポン」項目を適応(減算)した値が「外食」項目の値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutTaxExpenses;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「外食」項目のドメインです。
 * 「外食金額」項目と「消費税:外食金額」項目の加算値から「クーポン」項目を適応(減算)した値が「外食」項目の値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShoppingDineOut {
	// 外食金額
	private final ExpenditureAmount value;
	// 残クーポン額
	private final ShoppingCouponPrice residualCouponPrice;
	
	/**
	 *<pre>
	 * 「外食」項目のドメインを生成します。
	 * 「外食金額」項目と「消費税:外食金額」項目の加算値から「クーポン」項目を適応(減算)した値となります。
	 * 　クーポン適応により、金額が0円となる場合は外食金額にnullが設定されます。
	 * 
	 * [非ガード節]
	 * ・外食金額がnull値
	 * ・クーポン金額がnull値
	 * [ガード節]
	 * ・「外食金額」項目がnull
	 * ・「消費税:外食金額」項目がnull
	 * ・「クーポン」項目がnull
	 * ・消費税:外食金額の値ありの場合で、外食金額がnull値の場合
	 *</pre>
	 * @param expenses 「外食金額」項目の値
	 * @param taxExpenses 「消費税:外食金額」項目の値
	 * @param couponPrice クーポン金額
	 * @return 「食料品B(必須)」項目ドメイン
	 *
	 */
	public static ShoppingDineOut from(ShoppingDineOutExpenses expenses, ShoppingDineOutTaxExpenses taxExpenses, ShoppingCouponPrice couponPrice) {
		// ガード節(「外食金額」項目がnull)
		if(expenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「外食金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:外食金額」項目がnull)
		if(taxExpenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:外食金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:外食金額の値ありの場合で、外食金額がnull値)
		if(expenses.getValue() == null && taxExpenses.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「外食」項目の設定値が不正です。管理者に問い合わせてください。[expensesValue=null][taxExpensesValue=" + taxExpenses.toString() + "]");
		}
		// ガード節(「クーポン」項目がnull)
		if(couponPrice == null) {
			throw new MyHouseholdAccountBookRuntimeException("「クーポン」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		
		// 外食金額がnull値の場合、null値を持った「外食」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingDineOut(ExpenditureAmount.ZERO, couponPrice);
		}

		// 外食金額を計算
		BigDecimal dineOutValue = (taxExpenses.getValue() == null) ? expenses.getValue() : expenses.getValue().add(taxExpenses.getValue());

		// クーポン指定なしなら割引適応なしで外食を生成
		if(couponPrice.getValue() == null) {
			return new ShoppingDineOut(ExpenditureAmount.from(dineOutValue), ShoppingCouponPrice.from(null));
		}

		// 外食金額からクーポン金額を割引
		BigDecimal discountValue = dineOutValue.add(couponPrice.getValue());

		// 割引後の金額がマイナス値)：外食金額は割引適応でなし。残クーポン値は外食金額の値(マイナス値)
		int compareToValue = BigDecimal.ZERO.compareTo(discountValue);
		if (compareToValue > 0) {
			return new ShoppingDineOut(ExpenditureAmount.ZERO, ShoppingCouponPrice.from(discountValue));
		}
		// 割引後の金額が0)：外食金額は割引適応でなし。残クーポン値もなし
		if (compareToValue == 0) {
			return new ShoppingDineOut(ExpenditureAmount.ZERO, ShoppingCouponPrice.from(null));
		}
		// 割引後の金額が0より大きい)：外食金額は割引適応後の値。残クーポン値はなし
		return new ShoppingDineOut(ExpenditureAmount.from(discountValue), ShoppingCouponPrice.from(null));
		
	}
	
	/**
	 *<pre>
	 * 「外食」項目の値があるかどうかを判定します。
	 * 　支出金額がゼロの場合は値なし、それ以外の場合は値ありとなります。
	 * 
	 * 「支出金額」項目はnull不可なのでnull値は設定されません。
	 *</pre>
	 * @return 支出金額がゼロの場合はfalse、それ以外の場合はtrueとなります。
	 *
	 */
	public boolean hasSisyutuKingaku() {
		return !value.equals(ExpenditureAmount.ZERO);
	}
}
