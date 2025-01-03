/**
 * 「衣料品(私服)」項目の値を表すドメインタイプです。
 * 「衣料品(私服)金額」項目と「消費税:衣料品(私服)金額」項目の合計値となります。
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
 * 「衣料品(私服)」項目の値を表すドメインタイプです。
 * 「衣料品(私服)金額」項目と「消費税:衣料品(私服)金額」項目の合計値となります。
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
public class ShoppingClothesItem {
	// 衣料品(私服)金額
	private final BigDecimal value;
	// 「衣料品(私服)金額」項目
	private final ShoppingClothesExpenses shoppingClothesExpenses;
	// 「消費税:衣料品(私服)金額」項目
	private final ShoppingClothesTaxExpenses shoppingClothesTaxExpenses;
	
	/**
	 *<pre>
	 * 「衣料品(私服)」項目の値を表すドメインタイプを生成します。
	 * 「衣料品(私服)金額」項目と「消費税:衣料品(私服)金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・衣料品(私服)金額がnull値
	 * [ガード節]
	 * ・「衣料品(私服)金額」項目がnull
	 * ・「消費税:衣料品(私服)金額」項目がnull
	 * ・消費税:衣料品(私服)金額の値ありの場合で、衣料品(私服)金額がnull値の場合
	 *</pre>
	 * @param expenses 「衣料品(私服)金額」項目の値
	 * @param taxExpenses 「消費税:衣料品(私服)金額」項目の値
	 * @return 「衣料品(私服)」項目ドメインタイプ
	 *
	 */
	public static ShoppingClothesItem from(ShoppingClothesExpenses expenses, ShoppingClothesTaxExpenses taxExpenses) {
		// ガード節(「衣料品(私服)金額」項目がnull)
		if(expenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「衣料品(私服)金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:衣料品(私服)金額」項目がnull)
		if(taxExpenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:衣料品(私服)金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:衣料品(私服)金額の値ありの場合で、衣料品(私服)金額がnull値)
		if(expenses.getValue() == null && taxExpenses.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「衣料品(私服)」項目の設定値が不正です。管理者に問い合わせてください。[expensesValue=null][taxExpensesValue=" + taxExpenses.toString() + "]");
		}
		
		// 衣料品(私服)金額がnull値の場合、null値を持った「衣料品(私服)」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingClothesItem(null, expenses, ShoppingClothesTaxExpenses.from(null));
		}
		// 消費税:衣料品(私服)金額がnull値の場合、衣料品(私服)金額の値で「衣料品(私服)」項目を生成
		if(taxExpenses.getValue() == null) {
			return new ShoppingClothesItem(expenses.getValue(), expenses, taxExpenses);
		}
		// 衣料品(私服)金額と消費税:衣料品(私服)金額を加算した値で「衣料品(私服)」項目を生成
		return new ShoppingClothesItem(expenses.getValue().add(taxExpenses.getValue()), expenses, taxExpenses);
	}
	
	/**
	 *<pre>
	 * null値の「衣料品(私服)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「衣料品(私服)」項目ドメインタイプ
	 *
	 */
	public static ShoppingClothesItem nullValue() {
		return ShoppingClothesItem.from(ShoppingClothesExpenses.from(null), ShoppingClothesTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 衣料品(私服)の値を指定した衣料品(私服)の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する衣料品(私服)の値
	 * @return 加算した衣料品(私服)の値(this + addValue)
	 *
	 */
	public ShoppingClothesItem add(ShoppingClothesItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingClothesItem.from(
					addValue.getShoppingClothesExpenses(),
					addValue.getShoppingClothesTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingClothesItem.from(this.shoppingClothesExpenses, this.shoppingClothesTaxExpenses);
		}
		return ShoppingClothesItem.from(
				this.shoppingClothesExpenses.add(addValue.getShoppingClothesExpenses()),
				this.shoppingClothesTaxExpenses.add(addValue.getShoppingClothesTaxExpenses()));
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
