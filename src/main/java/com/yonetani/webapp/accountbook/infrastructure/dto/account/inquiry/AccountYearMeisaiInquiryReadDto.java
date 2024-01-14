/**
 * 年間収支(明細)情報のDB取得項目を格納するDTOです。
 * 収支テーブルと支出金額テーブルの検索結果になります。
 * 支出金額テーブルは指定の支出項目コード(支出項目テーブルのレベルが1に設定されている項目コード)の値が
 * 1レコードとして格納されます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/17 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 年間収支(明細)情報のDB取得項目を格納するDTOです。
 * 収支テーブルと支出金額テーブルの検索結果になります。
 * 支出金額テーブルは指定の支出項目コード(支出項目テーブルのレベルが1に設定されている項目コード)の値が
 * 1レコードとして格納されます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountYearMeisaiInquiryReadDto {
	// 対象月
	private final String month;
	// 支出
	private final BigDecimal sisyutuKingaku;
	// 収支
	private final BigDecimal syuusiKingaku;
	// 事業経費
	private final BigDecimal jigyouKeihiKingaku;
	// 固定(非課税)
	private final BigDecimal koteiHikazeiKingaku;
	// 固定(課税)
	private final BigDecimal koteiKazeiKingaku;
	// 衣類住居設備
	private final BigDecimal iruiJyuukyoSetubiKingaku;
	// 飲食日用品
	private final BigDecimal insyokuNitiyouhinKingaku;
	// 趣味娯楽
	private final BigDecimal syumiGotakuKingaku;
	// 支出B
	private final BigDecimal sisyutuKingakuB;
}
