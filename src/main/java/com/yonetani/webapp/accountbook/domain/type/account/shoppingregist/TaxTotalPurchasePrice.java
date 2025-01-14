/**
 * 「消費税合計」項目の値を表すドメインタイプです
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
 * 「消費税合計」項目の値を表すドメインタイプです
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
public class TaxTotalPurchasePrice {
	// 消費税合計
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「消費税合計」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・消費税合計がnull値
	 * [ガード節]
	 * ・消費税合計がマイナス値
	 * ・消費税合計がスケール値が2以外
	 * 
	 *</pre>
	 * @param price 消費税合計
	 * @return 「消費税合計」項目ドメインタイプ
	 *
	 */
	public static TaxTotalPurchasePrice from(BigDecimal price) {
		
		// 非ガード(消費税合計がnull値の場合、値nullの「消費税合計」項目ドメインタイプを生成
		if (price == null) {
			return new TaxTotalPurchasePrice(null);
		}
		// ガード節(消費税合計がマイナス値)
		if (BigDecimal.ZERO.compareTo(price) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税合計」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}
		// ガード節(消費税合計のスケール値が2以外)
		if (price.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「消費税合計」項目の設定値が不正です。管理者に問い合わせてください。[value=" + price.scale() + "]");
		}
		
		// 消費税合計項目ドメインタイプを生成
		return new TaxTotalPurchasePrice(price);
		
	}
	
	/**
	 *<pre>
	 * 消費税合計の値を指定した消費税合計の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する消費税合計の値
	 * @return 加算した消費税合計の値(this + addValue)
	 *
	 */
	public TaxTotalPurchasePrice add(TaxTotalPurchasePrice addValue) {
		if(this.value == null) {
			// addValueがnullの場合はnullポを投げる
			return new TaxTotalPurchasePrice(addValue.getValue());
		}
		if(addValue.getValue() == null) {
			return new TaxTotalPurchasePrice(this.value);
		}
		return new TaxTotalPurchasePrice(this.value.add(addValue.getValue()));
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
