/**
 * 「食料品(必須)」項目の値を表すドメインタイプです。
 * 「食料品(必須)金額」項目と「消費税:食料品(必須)金額」項目の合計値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「食料品(必須)」項目の値を表すドメインタイプです。
 * 「食料品(必須)金額」項目と「消費税:食料品(必須)金額」項目の合計値となります。
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
public class ShoppingFoodItem {
	// 食料品(必須)金額
	private final BigDecimal value;
	// 「食料品(必須)金額」項目
	private final ShoppingFoodExpenditureAmount shoppingFoodExpenditureAmount;
	// 「消費税:食料品(必須)金額」項目
	private final ShoppingFoodTaxExpenses shoppingFoodTaxExpenses;
	
	/**
	 *<pre>
	 * 「食料品(必須)」項目の値を表すドメインタイプを生成します。
	 * 「食料品(必須)金額」項目と「消費税:食料品(必須)金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・食料品(必須)金額がnull値
	 * [ガード節]
	 * ・「食料品(必須)金額」項目がnull
	 * ・「消費税:食料品(必須)金額」項目がnull
	 * ・消費税:食料品(必須)金額の値ありの場合で、食料品(必須)金額がnull値の場合
	 *</pre>
	 * @param amount 「食料品(必須)金額」項目の値
	 * @param taxAmount 「消費税:食料品(必須)金額」項目の値
	 * @return 「食料品(必須)」項目ドメインタイプ
	 *
	 */
	public static ShoppingFoodItem from(ShoppingFoodExpenditureAmount amount, ShoppingFoodTaxExpenses taxAmount) {
		// ガード節(「食料品(必須)金額」項目がnull)
		if(amount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「食料品(必須)金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:食料品(必須)金額」項目がnull)
		if(taxAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:食料品(必須)金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:食料品(必須)金額の値ありの場合で、食料品(必須)金額がnull値)
		if(amount.getValue() == null && taxAmount.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「食料品(必須)」項目の設定値が不正です。管理者に問い合わせてください。[amountValue=null][taxAmountValue=" + taxAmount.toString() + "]");
		}
		
		// 食料品(必須)金額がnull値の場合、null値を持った「食料品(必須)」項目を生成
		if(amount.getValue() == null) {
			return new ShoppingFoodItem(null, amount, ShoppingFoodTaxExpenses.from(null));
		}
		// 消費税:食料品(必須)金額がnull値の場合、食料品(必須)金額の値で「食料品(必須)」項目を生成
		if(taxAmount.getValue() == null) {
			return new ShoppingFoodItem(amount.getValue(), amount, taxAmount);
		}
		// 食料品(必須)金額と消費税:食料品(必須)金額を加算した値で「食料品(必須)」項目を生成
		return new ShoppingFoodItem(amount.getValue().add(taxAmount.getValue()), amount, taxAmount);
	}
	
	/**
	 *<pre>
	 * null値の「食料品(必須)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「食料品(必須)」項目ドメインタイプ
	 *
	 */
	public static ShoppingFoodItem nullValue() {
		return ShoppingFoodItem.from(ShoppingFoodExpenditureAmount.from(null), ShoppingFoodTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 食料品(必須)の値を指定した食料品(必須)の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する食料品(必須)の値
	 * @return 加算した食料品(必須)の値(this + addValue)
	 *
	 */
	public ShoppingFoodItem add(ShoppingFoodItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingFoodItem.from(
					addValue.getShoppingFoodExpenditureAmount(),
					addValue.getShoppingFoodTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingFoodItem.from(this.shoppingFoodExpenditureAmount, this.shoppingFoodTaxExpenses);
		}
		//valueの値と各ドメインを足した値が等しいことのチェックは不要でよいか（現在のコンストラクタで比較可能か？？)
		return ShoppingFoodItem.from(
				this.shoppingFoodExpenditureAmount.add(addValue.getShoppingFoodExpenditureAmount()),
				this.shoppingFoodTaxExpenses.add(addValue.getShoppingFoodTaxExpenses()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
}
