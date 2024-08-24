/**
 * 固定費情報一覧の明細情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 固定費情報一覧の明細情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostInquiryReadDto {
	// 固定費コード
	private final String fixedCostCode;
	// 固定費名(支払名)
	private final String fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	private final String fixedCostDetailContext;
	// 支出項目名
	private final String sisyutuItemName;
	// 固定費支払月(支払月)
	private final String fixedCostShiharaiTuki;
	// 固定費支払月任意詳細
	private final String fixedCostShiharaiTukiOptionalContext;
	// 固定費支払日(支払日)
	private final String fixedCostShiharaiDay;
	// 支払金額
	private final BigDecimal shiharaiKingaku;
}
