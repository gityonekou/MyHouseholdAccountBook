/**
 * 支出金額テーブルと支出アイテムテーブルと結合し、
 * 支出項目ごとに対応する金額を表した結果を格納するDTOです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支出金額テーブルと支出アイテムテーブルと結合し、
 * 支出項目ごとに対応する金額を表した結果を格納するDTOです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SisyutuKingakuAndSisyutuItemReadDto {
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 支出項目コード
	private final String sisyutuItemCode;
	// 支出項目名
	private final String sisyutuItemName;
	// 支出項目レベル(1～5):
	private final String sisyutuItemLevel;
	// 支出金額
	private final BigDecimal sisyutuKingaku;
	// 支出金額b
	private final BigDecimal sisyutuKingakuB;
	// 支払日
	private final LocalDate siharaiDate;
}
