/**
 * 「仕事」項目の値を表すドメインタイプです。
 * 「仕事金額」項目と「消費税:仕事金額」項目の合計値となります。
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
 * 「仕事」項目の値を表すドメインタイプです。
 * 「仕事金額」項目と「消費税:仕事金額」項目の合計値となります。
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
public class ShoppingWorkItem {
	// 仕事金額
	private final BigDecimal value;
	// 「仕事金額」項目
	private final ShoppingWorkExpenses shoppingWorkExpenses;
	// 「消費税:仕事金額」項目
	private final ShoppingWorkTaxExpenses shoppingWorkTaxExpenses;
	
	/**
	 *<pre>
	 * 「仕事」項目の値を表すドメインタイプを生成します。
	 * 「仕事金額」項目と「消費税:仕事金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・仕事金額がnull値
	 * [ガード節]
	 * ・「仕事金額」項目がnull
	 * ・「消費税:仕事金額」項目がnull
	 * ・消費税:仕事金額の値ありの場合で、仕事金額がnull値の場合
	 *</pre>
	 * @param expenses 「仕事金額」項目の値
	 * @param taxExpenses 「消費税:仕事金額」項目の値
	 * @return 「仕事」項目ドメインタイプ
	 *
	 */
	public static ShoppingWorkItem from(ShoppingWorkExpenses expenses, ShoppingWorkTaxExpenses taxExpenses) {
		// ガード節(「仕事金額」項目がnull)
		if(expenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「仕事金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:仕事金額」項目がnull)
		if(taxExpenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:仕事金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:仕事金額の値ありの場合で、仕事金額がnull値)
		if(expenses.getValue() == null && taxExpenses.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「仕事」項目の設定値が不正です。管理者に問い合わせてください。[expensesValue=null][taxExpensesValue=" + taxExpenses.toString() + "]");
		}
		
		// 仕事金額がnull値の場合、null値を持った「仕事」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingWorkItem(null, expenses, ShoppingWorkTaxExpenses.from(null));
		}
		// 消費税:仕事金額がnull値の場合、仕事金額の値で「仕事」項目を生成
		if(taxExpenses.getValue() == null) {
			return new ShoppingWorkItem(expenses.getValue(), expenses, taxExpenses);
		}
		// 仕事金額と消費税:仕事金額を加算した値で「仕事」項目を生成
		return new ShoppingWorkItem(expenses.getValue().add(taxExpenses.getValue()), expenses, taxExpenses);
	}
	
	/**
	 *<pre>
	 * null値の「仕事」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「仕事」項目ドメインタイプ
	 *
	 */
	public static ShoppingWorkItem nullValue() {
		return ShoppingWorkItem.from(ShoppingWorkExpenses.from(null), ShoppingWorkTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 仕事の値を指定した仕事の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する仕事の値
	 * @return 加算した仕事の値(this + addValue)
	 *
	 */
	public ShoppingWorkItem add(ShoppingWorkItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingWorkItem.from(
					addValue.getShoppingWorkExpenses(),
					addValue.getShoppingWorkTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingWorkItem.from(this.shoppingWorkExpenses, this.shoppingWorkTaxExpenses);
		}
		return ShoppingWorkItem.from(
				this.shoppingWorkExpenses.add(addValue.getShoppingWorkExpenses()),
				this.shoppingWorkTaxExpenses.add(addValue.getShoppingWorkTaxExpenses()));
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
