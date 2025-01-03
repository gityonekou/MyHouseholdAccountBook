/**
 * 「住居設備」項目の値を表すドメインタイプです。
 * 「住居設備金額」項目と「消費税:住居設備金額」項目の合計値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/01 : 1.00.00  新規作成
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
 * 「住居設備」項目の値を表すドメインタイプです。
 * 「住居設備金額」項目と「消費税:住居設備金額」項目の合計値となります。
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
public class ShoppingHouseEquipmentItem {
	// 住居設備金額
	private final BigDecimal value;
	// 「住居設備金額」項目
	private final ShoppingHouseEquipmentExpenses shoppingHouseEquipmentExpenses;
	// 「消費税:住居設備金額」項目
	private final ShoppingHouseEquipmentTaxExpenses shoppingHouseEquipmentTaxExpenses;
	
	/**
	 *<pre>
	 * 「住居設備」項目の値を表すドメインタイプを生成します。
	 * 「住居設備金額」項目と「消費税:住居設備金額」項目の加算値となります。
	 * 
	 * [非ガード節]
	 * ・住居設備金額がnull値
	 * [ガード節]
	 * ・「住居設備金額」項目がnull
	 * ・「消費税:住居設備金額」項目がnull
	 * ・消費税:住居設備金額の値ありの場合で、住居設備金額がnull値の場合
	 *</pre>
	 * @param expenses 「住居設備金額」項目の値
	 * @param taxExpenses 「消費税:住居設備金額」項目の値
	 * @return 「住居設備」項目ドメインタイプ
	 *
	 */
	public static ShoppingHouseEquipmentItem from(ShoppingHouseEquipmentExpenses expenses, ShoppingHouseEquipmentTaxExpenses taxExpenses) {
		// ガード節(「住居設備金額」項目がnull)
		if(expenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「住居設備金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(「消費税:住居設備金額」項目がnull)
		if(taxExpenses == null) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:住居設備金額」項目にnullが指定されました。管理者に問い合わせてください。");
		}
		// ガード節(消費税:住居設備金額の値ありの場合で、住居設備金額がnull値)
		if(expenses.getValue() == null && taxExpenses.getValue() != null) {
			throw new MyHouseholdAccountBookRuntimeException("「住居設備」項目の設定値が不正です。管理者に問い合わせてください。[expensesValue=null][taxExpensesValue=" + taxExpenses.toString() + "]");
		}
		
		// 住居設備金額がnull値の場合、null値を持った「住居設備」項目を生成
		if(expenses.getValue() == null) {
			return new ShoppingHouseEquipmentItem(null, expenses, ShoppingHouseEquipmentTaxExpenses.from(null));
		}
		// 消費税:住居設備金額がnull値の場合、住居設備金額の値で「住居設備」項目を生成
		if(taxExpenses.getValue() == null) {
			return new ShoppingHouseEquipmentItem(expenses.getValue(), expenses, taxExpenses);
		}
		// 住居設備金額と消費税:住居設備金額を加算した値で「住居設備」項目を生成
		return new ShoppingHouseEquipmentItem(expenses.getValue().add(taxExpenses.getValue()), expenses, taxExpenses);
	}
	
	/**
	 *<pre>
	 * null値の「住居設備」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @return null値の「住居設備」項目ドメインタイプ
	 *
	 */
	public static ShoppingHouseEquipmentItem nullValue() {
		return ShoppingHouseEquipmentItem.from(ShoppingHouseEquipmentExpenses.from(null), ShoppingHouseEquipmentTaxExpenses.from(null));
	}
	
	/**
	 *<pre>
	 * 住居設備の値を指定した住居設備の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する住居設備の値
	 * @return 加算した住居設備の値(this + addValue)
	 *
	 */
	public ShoppingHouseEquipmentItem add(ShoppingHouseEquipmentItem addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return ShoppingHouseEquipmentItem.from(
					addValue.getShoppingHouseEquipmentExpenses(),
					addValue.getShoppingHouseEquipmentTaxExpenses());
		}
		if(addValue.getValue() == null) {
			return ShoppingHouseEquipmentItem.from(this.shoppingHouseEquipmentExpenses, this.shoppingHouseEquipmentTaxExpenses);
		}
		return ShoppingHouseEquipmentItem.from(
				this.shoppingHouseEquipmentExpenses.add(addValue.getShoppingHouseEquipmentExpenses()),
				this.shoppingHouseEquipmentTaxExpenses.add(addValue.getShoppingHouseEquipmentTaxExpenses()));
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
