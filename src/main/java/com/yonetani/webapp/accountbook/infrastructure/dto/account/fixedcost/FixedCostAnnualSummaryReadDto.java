/**
 * 年間固定費合計画面用の固定費情報一覧の明細情報です。
 * 固定費テーブルと支出項目テーブルを結合し、Level-1・Level-2 祖先コードを含む情報を保持します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 年間固定費合計画面用の固定費情報一覧の明細情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostAnnualSummaryReadDto {
	// 固定費コード
	private final String fixedCostCode;
	// 固定費名(支払名)
	private final String fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	private final String fixedCostDetailContext;
	// 支出項目名
	private final String sisyutuItemName;
	// 固定費に紐付く支出項目コード（リーフ）
	private final String sisyutuItemCode;
	// Level-1 祖先支出項目コード（ANCLVL1 から取得）
	private final String level1SisyutuItemCode;
	// Level-2 祖先支出項目コード（Level-1 項目は null）
	private final String level2SisyutuItemCode;
	// 固定費支払月(支払月コード)
	private final String fixedCostShiharaiTuki;
	// 固定費支払月任意詳細
	private final String fixedCostShiharaiTukiOptionalContext;
	// 固定費支払日(支払日)
	private final String fixedCostShiharaiDay;
	// 支払金額
	private final BigDecimal shiharaiKingaku;
}
