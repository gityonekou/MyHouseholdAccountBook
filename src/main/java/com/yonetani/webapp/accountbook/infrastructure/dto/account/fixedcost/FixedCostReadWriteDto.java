/**
 * 固定費テーブル:FIXED_COST_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 固定費テーブル:FIXED_COST_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostReadWriteDto {
	// ユーザID
	private final String userId;
	// 固定費コード
	private final String fixedCostCode;
	// 固定費名(支払名)
	private final String fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	private final String fixedCostDetailContext;
	// 支出項目コード
	private final String sisyutuItemCode;
	// 固定費区分
	private final String fixedCostKubun;
	// 固定費支払月(支払月)
	private final String fixedCostShiharaiTuki;
	// 固定費支払月任意詳細
	private final String fixedCostShiharaiTukiOptionalContext;
	// 固定費支払日(支払日)
	private final String fixedCostShiharaiDay;
	// 支払金額
	private final BigDecimal shiharaiKingaku;
	// 削除フラグ(論理削除状態にするためのフラグ)
	// 注意:アプリ側からこの値を直接利用することはありませんSQLで直接値を指定し、検索条件とします
	private boolean deleteFlg;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにFixedCostReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId   ユーザID
	 * @param fixedCostCode 固定費コード
	 * @param fixedCostName 固定費名(支払名)
	 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
	 * @param sisyutuItemCode 支出項目コード
	 * @param fixedCostKubun 固定費区分
	 * @param fixedCostShiharaiTuki 固定費支払月(支払月)
	 * @param fixedCostShiharaiTukiOptionalContext 固定費支払月任意詳細
	 * @param fixedCostShiharaiDay 固定費支払日(支払日)
	 * @param shiharaiKingaku 支払金額
	 * @return 固定費テーブル:FIXED_COST_TABLE出力情報
	 *
	 */
	public static FixedCostReadWriteDto from(
			String userId,
			String fixedCostCode,
			String fixedCostName,
			String fixedCostDetailContext,
			String sisyutuItemCode,
			String fixedCostKubun,
			String fixedCostShiharaiTuki,
			String fixedCostShiharaiTukiOptionalContext,
			String fixedCostShiharaiDay,
			BigDecimal shiharaiKingaku) {
		return new FixedCostReadWriteDto(
				userId,
				fixedCostCode,
				fixedCostName,
				fixedCostDetailContext, 
				sisyutuItemCode,
				fixedCostKubun,
				fixedCostShiharaiTuki,
				fixedCostShiharaiTukiOptionalContext,
				fixedCostShiharaiDay,
				shiharaiKingaku);
	}
}
