/**
 * 「外食」項目の値を表すドメインタイプです。
 * 「外食金額」項目と「消費税:外食金額」項目の合計値となります。
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
 * 「外食」項目の値を表すドメインタイプです。
 * 「外食金額」項目と「消費税:外食金額」項目の合計値となります。
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
public class ShoppingDineOutItem {
	// 外食金額
	private final BigDecimal value;
	// 「外食金額」項目
	private final ShoppingDineOutExpenses shoppingDineOutExpenses;
	// 「消費税:外食金額」項目
	private final ShoppingDineOutTaxExpenses shoppingDineOutTaxExpenses;
	
	/**
	 *<pre>
	 * 「外食」項目の値を表すドメインタイプを生成します。
	 * 「外食金額」項目と「消費税:外食金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・外食金額がnull値
	 * [ガード節]
	 * ・「外食金額」項目がnull
	 * ・「消費税:外食金額」項目がnull
	 * ・消費税:外食金額の値ありの場合で、外食金額がnull値の場合
	 *</pre>
	 * @param expenses 「外食金額」項目の値
	 * @param taxExpenses 「消費税:外食金額」項目の値
	 * @return 「外食」項目ドメインタイプ
	 *
	 */
	public static ShoppingDineOutItem from(ShoppingDineOutExpenses expenses, ShoppingDineOutTaxExpenses taxExpenses) {
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
		
		// 外食金額がnull値の場合、null値を持った「外食」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingDineOutItem(null, expenses, ShoppingDineOutTaxExpenses.from(null));
		}
		// 消費税:外食金額がnull値の場合、外食金額の値で「外食」項目を生成
		if(taxExpenses.getValue() == null) {
			return new ShoppingDineOutItem(expenses.getValue(), expenses, taxExpenses);
		}
		// 外食金額と消費税:外食金額を加算した値で「外食」項目を生成
		return new ShoppingDineOutItem(expenses.getValue().add(taxExpenses.getValue()), expenses, taxExpenses);
	}
	
	/**
	 *<pre>
	 * null値の「外食」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「外食」項目ドメインタイプ
	 *
	 */
	public static ShoppingDineOutItem nullValue() {
		return ShoppingDineOutItem.from(ShoppingDineOutExpenses.from(null), ShoppingDineOutTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 外食の値を指定した外食の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する外食の値
	 * @return 加算した外食の値(this + addValue)
	 *
	 */
	public ShoppingDineOutItem add(ShoppingDineOutItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingDineOutItem.from(
					addValue.getShoppingDineOutExpenses(),
					addValue.getShoppingDineOutTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingDineOutItem.from(this.shoppingDineOutExpenses, this.shoppingDineOutTaxExpenses);
		}
		return ShoppingDineOutItem.from(
				this.shoppingDineOutExpenses.add(addValue.getShoppingDineOutExpenses()),
				this.shoppingDineOutTaxExpenses.add(addValue.getShoppingDineOutTaxExpenses()));
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
