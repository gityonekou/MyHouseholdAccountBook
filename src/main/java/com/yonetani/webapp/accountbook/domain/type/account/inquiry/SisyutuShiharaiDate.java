/**
 * 「支出支払日」項目の値を表すドメインタイプです
 * 
 * [注意]
 * 支出支払日項目は支出テーブルの支出支払日項目がNULL値を許容するため、支出支払日にnullを設定可能となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出支払日」項目の値を表すドメインタイプです
 * 
 * [注意]
 * 支出支払日項目は支出テーブルの支出支払日項目がNULL値を許容するため、支出支払日にnullを設定可能となります。
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
public class SisyutuShiharaiDate {
	
	// 支出支払日
	private final LocalDate value;
	
	/**
	 *<pre>
	 * 「支出支払日」項目の値を表すドメインタイプを生成します。
	 * 
	 * 支出支払日にnullを指定した場合、null値を保持したドメインタイプが生成されます。
	 * [ガード節]：なし
	 * 
	 *</pre>
	 * @param datetime 支出支払日
	 * @return 「支出支払日」項目ドメインタイプ
	 *
	 */
	public static SisyutuShiharaiDate from(LocalDate datetime) {
		return new SisyutuShiharaiDate(datetime);
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
