/**
 * 「買い物合計金額」項目の値を表すドメインタイプです
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
 * 「買い物合計金額」項目の値を表すドメインタイプです
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
public class ShoppingTotalAmount {
	// 買い物合計金額
	private final BigDecimal value;
	
	// 値が0の「買い物合計金額」項目の値
	public static final ShoppingTotalAmount ZERO = ShoppingTotalAmount.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「買い物合計金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [ガード節]
	 * ・買い物合計金額がnull値
	 * ・買い物合計金額がマイナス値
	 * ・買い物合計金額がスケール値が2以外
	 * 
	 *</pre>
	 * @param price 買い物合計金額
	 * @return 「買い物合計金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingTotalAmount from(BigDecimal price) {
		
		// ガード節(買い物合計金額がnull値)
		if (price == null) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物合計金額」項目の設定値が不正です。管理者に問い合わせてください。[value=null]");
		}
		// ガード節(買い物合計金額がマイナス値)
		if (BigDecimal.ZERO.compareTo(price) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物合計金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}
		// ガード節(買い物合計金額のスケール値が2以外)
		if (price.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物合計金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.scale() + "]");
		}
		
		// 買い物合計金額項目ドメインタイプを生成
		return new ShoppingTotalAmount(price);
		
	}
	
	/**
	 *<pre>
	 * 買い物合計金額の値を指定した買い物合計金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する買い物合計金額の値
	 * @return 加算した買い物合計金額の値(this + addValue)
	 *
	 */
	public ShoppingTotalAmount add(ShoppingTotalAmount addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return new ShoppingTotalAmount(addValue.getValue());
		}
		if(addValue.getValue() == null) {
			return new ShoppingTotalAmount(this.value);
		}
		return new ShoppingTotalAmount(this.value.add(addValue.getValue()));
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
