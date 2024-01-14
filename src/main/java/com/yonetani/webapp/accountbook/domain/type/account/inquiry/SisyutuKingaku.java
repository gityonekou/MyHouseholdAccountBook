/**
 * 「支出金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出金額」項目の値を表すドメインタイプです
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
public class SisyutuKingaku {
	// 支出金額
	private final BigDecimal value;

	/**
	 *<pre>
	 * 「支出金額」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param sisyutuKingaku 支出金額
	 * @return 「支出金額」項目ドメインタイプ
	 *
	 */
	public static SisyutuKingaku from(BigDecimal sisyutuKingaku) {
		return new SisyutuKingaku(sisyutuKingaku);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingaku(value);
	}
}
