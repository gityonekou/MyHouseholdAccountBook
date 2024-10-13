/**
 * 「支出金額C」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/12 : 1.00.00  新規作成
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
 * 「支出金額C」項目の値を表すドメインタイプです
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
public class SisyutuKingakuC {
	// 支出金額C
	private final BigDecimal value;
	// 割合
	private final BigDecimal percentage;
	
	/**
	 *<pre>
	 * 「支出金額C」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・支出金額Cがnull値
	 * [ガード節]
	 * ・支出金額がnull値
	 * ・支出金額Cがマイナス値
	 * ・支出金額Cがスケール値が2以外
	 * 
	 *</pre>
	 * @param kingakuc 支出金額C
	 * @param sisyutuKingaku 支出金額
	 * @return 「支出金額C」項目(支出金額C, 割合)ドメインタイプ
	 *
	 */
	public static SisyutuKingakuC from(BigDecimal kingakuc, SisyutuKingaku sisyutuKingaku) {
		
		// ガード節(支出金額がnull値)
		if(sisyutuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuKingaku=null]");
		}
		// 非ガード(支出金額Cがnull値の場合、値nullの「支出金額C」項目ドメインタイプを生成
		if (kingakuc == null) {
			return new SisyutuKingakuC(null, null);
		}
		// ガード節(支出金額Cがマイナス値)
		if (BigDecimal.ZERO.compareTo(kingakuc) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakuc.intValue() + "]");
		}
		// ガード節(支出金額Cのスケール値が2以外)
		if (kingakuc.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakuc.scale() + "]");
		}
		
		// 設定値OKの場合、支出金額Cの値と支出金額をもとに支出金額C項目ドメインタイプを生成
		// 支出金額Cの割合=支出金額C/支出金額 * 100(四捨五入)
		BigDecimal pt = kingakuc.divide(sisyutuKingaku.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		return new SisyutuKingakuC(kingakuc, pt);
		
	}
	
	/**
	 *<pre>
	 * 支出金額Cの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Cの値をカンマ編集した文字列
	 *
	 */
	public String toSisyutuKingakuCString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
	
	/**
	 *<pre>
	 * 支出金額Cの値が支出金額の何パーセントかを表した値。小数点以下0桁で四捨五入
	 * 値がnull(支払金額B項目の値なし)の場合、空文字列を返却
	 *</pre>
	 * @return 支出金額Cの割合
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
		return "sisyutuKingaku=" + toSisyutuKingakuCString() + ",percentage=" + toPercentageString();
	}
}
