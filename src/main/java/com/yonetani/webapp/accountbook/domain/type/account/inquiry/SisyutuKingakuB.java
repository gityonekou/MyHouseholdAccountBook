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

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
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
@Getter
@EqualsAndHashCode
public class SisyutuKingakuB {	
	// 支出金額B
	private final BigDecimal value;
	// 割合
	private final BigDecimal percentage;
	
	/**
	 *<pre>
	 * 「支出金額B」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・支出金額Bがnull値
	 * [ガード節]
	 * ・支出金額がnull値
	 * ・支出金額Bがマイナス値
	 * ・支出金額Bがスケール値が2以外
	 * 
	 *</pre>
	 * @param kingakub 支出金額B
	 * @param sisyutuKingaku 支出金額
	 * @return 「支出金額B」項目(支出金額B, 割合)ドメインタイプ
	 *
	 */
	public static SisyutuKingakuB from(BigDecimal kingakub, SisyutuKingaku sisyutuKingaku) {
		
		// ガード節(支出金額がnull値)
		if(sisyutuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuKingaku=null]");
		}
		// 非ガード(支出金額Bがnull値の場合、値nullの「支出金額B」項目ドメインタイプを生成
		if (kingakub == null) {
			return new SisyutuKingakuB(null, null);
		}
		// ガード節(支出金額Bがマイナス値)
		if (BigDecimal.ZERO.compareTo(kingakub) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakub.intValue() + "]");
		}
		// ガード節(支出金額Bのスケール値が2以外)
		if (kingakub.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakub.scale() + "]");
		}
		
		// 設定値OKの場合、支出金額Bの値と支出金額をもとに支出金額B項目ドメインタイプを生成
		// 支出金額Bの割合=支出金額B/支出金額 * 100(四捨五入)
		BigDecimal pt = kingakub.divide(sisyutuKingaku.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		return new SisyutuKingakuB(kingakub, pt);
		
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
		return DomainCommonUtils.formatKingakuAndYen(value);
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
