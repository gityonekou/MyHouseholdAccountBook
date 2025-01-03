/**
 * 「日用品」項目の値を表すドメインタイプです。
 * 「日用品金額」項目と「消費税:日用品金額」項目の合計値となります。
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
 * 「日用品」項目の値を表すドメインタイプです。
 * 「日用品金額」項目と「消費税:日用品金額」項目の合計値となります。
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
public class ShoppingConsumerGoodsItem {
	// 日用品金額
	private final BigDecimal value;
	// 「日用品金額」項目
	private final ShoppingConsumerGoodsExpenses shoppingConsumerGoodsExpenses;
	// 「消費税:日用品金額」項目
	private final ShoppingConsumerGoodsTaxExpenses shoppingConsumerGoodsTaxExpenses;
	
	/**
	 *<pre>
	 * 「日用品」項目の値を表すドメインタイプを生成します。
	 * 「日用品金額」項目と「消費税:日用品金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・日用品金額がnull値
	 * [ガード節]
	 * ・「日用品金額」項目がnull
	 * ・「消費税:日用品金額」項目がnull
	 * ・消費税:日用品金額の値ありの場合で、日用品金額がnull値の場合
	 *</pre>
	 * @param expenses 「日用品金額」項目の値
	 * @param taxExpenses 「消費税:日用品金額」項目の値
	 * @return 「日用品」項目ドメインタイプ
	 *
	 */
	public static ShoppingConsumerGoodsItem from(ShoppingConsumerGoodsExpenses expenses, ShoppingConsumerGoodsTaxExpenses taxExpenses) {
		// ガード節(「日用品金額」項目がnull)
		if(expenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「日用品金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:日用品金額」項目がnull)
		if(taxExpenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:日用品金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:日用品金額の値ありの場合で、日用品金額がnull値)
		if(expenses.getValue() == null && taxExpenses.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「日用品」項目の設定値が不正です。管理者に問い合わせてください。[expensesValue=null][taxExpensesValue=" + taxExpenses.toString() + "]");
		}
		
		// 日用品金額がnull値の場合、null値を持った「日用品」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingConsumerGoodsItem(null, expenses, ShoppingConsumerGoodsTaxExpenses.from(null));
		}
		// 消費税:日用品金額がnull値の場合、日用品金額の値で「日用品」項目を生成
		if(taxExpenses.getValue() == null) {
			return new ShoppingConsumerGoodsItem(expenses.getValue(), expenses, taxExpenses);
		}
		// 日用品金額と消費税:日用品金額を加算した値で「日用品」項目を生成
		return new ShoppingConsumerGoodsItem(expenses.getValue().add(taxExpenses.getValue()), expenses, taxExpenses);
	}
	
	/**
	 *<pre>
	 * null値の「日用品」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「日用品」項目ドメインタイプ
	 *
	 */
	public static ShoppingConsumerGoodsItem nullValue() {
		return ShoppingConsumerGoodsItem.from(ShoppingConsumerGoodsExpenses.from(null), ShoppingConsumerGoodsTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 日用品の値を指定した日用品の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する日用品の値
	 * @return 加算した日用品の値(this + addValue)
	 *
	 */
	public ShoppingConsumerGoodsItem add(ShoppingConsumerGoodsItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingConsumerGoodsItem.from(
					addValue.getShoppingConsumerGoodsExpenses(),
					addValue.getShoppingConsumerGoodsTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingConsumerGoodsItem.from(this.shoppingConsumerGoodsExpenses, this.shoppingConsumerGoodsTaxExpenses);
		}
		return ShoppingConsumerGoodsItem.from(
				this.shoppingConsumerGoodsExpenses.add(addValue.getShoppingConsumerGoodsExpenses()),
				this.shoppingConsumerGoodsTaxExpenses.add(addValue.getShoppingConsumerGoodsTaxExpenses()));
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
