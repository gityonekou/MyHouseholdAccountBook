/**
 * 「支払金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支払金額」項目の値を表すドメインタイプです
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
public class ShiharaiKingaku {
	// 支払金額
	@Getter
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「支払金額」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param kingaku 支払金額
	 * @return 「支払金額」項目ドメインタイプ
	 *
	 */
	public static ShiharaiKingaku from(BigDecimal kingaku) {
		
		// ガード節(null)
		if(kingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支払金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(kingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支払金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + kingaku.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(kingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支払金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + kingaku.scale() + "]");
		}
		
		return new ShiharaiKingaku(kingaku);
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
