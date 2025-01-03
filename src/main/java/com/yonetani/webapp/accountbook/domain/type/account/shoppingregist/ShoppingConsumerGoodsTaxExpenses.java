/**
 * 「消費税:日用品金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/26 : 1.00.00  新規作成
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
 * 「消費税:日用品金額」項目の値を表すドメインタイプです
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
public class ShoppingConsumerGoodsTaxExpenses {
	// 消費税:日用品金額
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「消費税:日用品金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・消費税:日用品金額がnull値
	 * [ガード節]
	 * ・消費税:日用品金額がマイナス値
	 * ・消費税:日用品金額がスケール値が2以外
	 * 
	 *</pre>
	 * @param price 消費税:日用品金額
	 * @return 「消費税:日用品金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingConsumerGoodsTaxExpenses from(BigDecimal price) {
		
		// 非ガード(消費税:日用品金額がnull値の場合、値nullの「消費税:日用品金額」項目ドメインタイプを生成
		if (price == null) {
			return new ShoppingConsumerGoodsTaxExpenses(null);
		}
		// ガード節(消費税:日用品金額がマイナス値)
		if (BigDecimal.ZERO.compareTo(price) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:日用品金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}
		// ガード節(消費税:日用品金額のスケール値が2以外)
		if (price.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税:日用品金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.scale() + "]");
		}
		
		// 消費税:日用品金額項目ドメインタイプを生成
		return new ShoppingConsumerGoodsTaxExpenses(price);
		
	}
	
	/**
	 *<pre>
	 * 消費税:日用品金額の値を指定した消費税:日用品金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する消費税:日用品金額の値
	 * @return 加算した消費税:日用品金額の値(this + addValue)
	 *
	 */
	public ShoppingConsumerGoodsTaxExpenses add(ShoppingConsumerGoodsTaxExpenses addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return new ShoppingConsumerGoodsTaxExpenses(addValue.getValue());
		}
		if(addValue.getValue() == null) {
			return new ShoppingConsumerGoodsTaxExpenses(this.value);
		}
		return new ShoppingConsumerGoodsTaxExpenses(this.value.add(addValue.getValue()));
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