/**
 * 「支払日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支払日」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SiharaiDate {
	// 支払日
	private final LocalDate value;
	
	/**
	 *<pre>
	 * 「支払日」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param datetime 支払日
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static SiharaiDate from(LocalDate datetime) {
		return new SiharaiDate(datetime);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// YYYY/MM/DD形式で返却
		return DomainCommonUtils.formatyyyySPMMSPdd(value);
		
	}	
}
