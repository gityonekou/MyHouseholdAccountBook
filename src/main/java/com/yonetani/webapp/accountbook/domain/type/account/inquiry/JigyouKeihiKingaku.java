/**
 * 「事業経費」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/20 : 1.00.00  新規作成
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
 * 「事業経費」項目の値を表すドメインタイプです
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
public class JigyouKeihiKingaku {
	
	// 事業経費金額
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「事業経費」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param value 事業経費金額
	 * @return 「事業経費」項目ドメインタイプ
	 *
	 */
	public static JigyouKeihiKingaku from(BigDecimal value) {
		return new JigyouKeihiKingaku(value);
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
