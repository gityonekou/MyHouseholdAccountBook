/**
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/13 : 1.00.00  新規作成
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
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SisyutuKingakuReadWriteDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 支出項目コード
	private final String sisyutuItemCode;
	// 親支出項目コード
	private final String parentSisyutuItemCode;
	// 支出予定金額
	private final BigDecimal sisyutuYoteiKingaku;
	// 支出金額
	private final BigDecimal sisyutuKingaku;
	// 支出金額B
	private final BigDecimal sisyutuKingakuB;
	// 支出金額C
	private final BigDecimal sisyutuKingakuC;
	// 支出支払日
	private final LocalDate sisyutuSiharaiDate;
	
}
