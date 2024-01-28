/**
 * 「支出金額B」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出金額B」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class SisyutuKingakuB {	
	// 支出金額B
	private final BigDecimal value;
	// 割合
	private final BigDecimal percentage;
	
	/**
	 *<pre>
	 * 「支出金額B」項目の値を表すドメインタイプを生成します
	 *</pre>
	 *@param sisyutuKingaku 支出金額
	 * @param sisyutuKingakuB 支出金額B
	 * @return 「支出金額B」項目(支出金額B, 割合)ドメインタイプ
	 *
	 */
	public static SisyutuKingakuB from(BigDecimal kingaku, BigDecimal kingakub) {
		// null値判定
		if(kingaku == null || kingakub == null) {
			return new SisyutuKingakuB(null, null);
		// 支払金額Bの値から「支出金額B」項目を作成する
		} else if(kingaku.compareTo(BigDecimal.ZERO) > 0 && kingakub.compareTo(BigDecimal.ZERO) > 0) {
			// 支払金額Bの値が0以上の場合は支払金額項目Bの値から割合の値を設定
			// 支払金額Bの割合=支出金額B/支出金額 * 100(四捨五入)
			BigDecimal pt = kingakub.divide(kingaku, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			return new SisyutuKingakuB(kingakub, pt);

		} else {
			// 支払金額Bの値が0以下の場合は支払金額項目Bの値はnullで設定
			return new SisyutuKingakuB(kingakub, null);
		}
		
		
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Bの値をカンマ編集した文字列
	 *
	 */
	public String toSisyutuKingakuBString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingaku(value);
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値が支出金額の何パーセントかを表した値。小数点以下0桁で四捨五入
	 * 値がnull(支払金額B項目の値なし)の場合、空文字列を返却
	 *</pre>
	 * @return 支出金額Bの割合
	 *
	 */
	public String toPercentageString() {
		// 値がnullの場合空文字列を返却、null以外の場合はスケール0で四捨五入した文字列を返却
		return (percentage == null) ? "" : percentage.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "sisyutuKingaku=" + toSisyutuKingakuBString() + ",percentage=" + toPercentageString();
	}
}
