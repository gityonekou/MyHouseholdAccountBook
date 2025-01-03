/**
 * 「クーポン金額」項目の値を表すドメインタイプです
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
 * 「クーポン金額」項目の値を表すドメインタイプです
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
public class ShoppingCouponPrice {
	// クーポン金額
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「クーポン金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・クーポン金額がnull値
	 * [ガード節]
	 * ・クーポン金額がプラス値
	 * ・クーポン金額がスケール値が2以外
	 * 
	 *</pre>
	 * @param price クーポン金額
	 * @return 「クーポン金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingCouponPrice from(BigDecimal price) {
		
		// 非ガード(クーポン金額がnull値の場合、値nullの「クーポン金額」項目ドメインタイプを生成
		if (price == null) {
			return new ShoppingCouponPrice(null);
		}
		// ガード節(クーポン金額がプラス値)
		if (BigDecimal.ZERO.compareTo(price) < 0) {
			throw new MyHouseholdAccountBookRuntimeException("「クーポン金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}
		// ガード節(クーポン金額のスケール値が2以外)
		if (price.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「クーポン金額」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.scale() + "]");
		}
		
		// クーポン金額項目ドメインタイプを生成
		return new ShoppingCouponPrice(price);
		
	}
	
	/**
	 *<pre>
	 * クーポン金額の値を指定したクーポン金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算するクーポン金額の値
	 * @return 加算したクーポン金額の値(this + addValue)
	 *
	 */
	public ShoppingCouponPrice add(ShoppingCouponPrice addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return new ShoppingCouponPrice(addValue.getValue());
		}
		if(addValue.getValue() == null) {
			return new ShoppingCouponPrice(this.value);
		}
		return new ShoppingCouponPrice(this.value.add(addValue.getValue()));
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
