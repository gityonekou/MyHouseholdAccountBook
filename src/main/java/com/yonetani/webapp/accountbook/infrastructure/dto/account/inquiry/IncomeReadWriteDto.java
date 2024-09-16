/**
 * 収入テーブル:INCOME_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収入テーブル:INCOME_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeReadWriteDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 収入コード
	private final String syuunyuuCode;
	// 収入区分
	private final String syuunyuuKubun;
	// 収入詳細
	private final String syuunyuuDetailContext;
	// 収入金額
	private final BigDecimal syuunyuuKingaku;
	// 削除フラグ
	private final boolean deleteFlg;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにIncomeReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param syuunyuuCode 収入コード
	 * @param syuunyuuKubun 収入区分
	 * @param syuunyuuDetailContext 収入詳細
	 * @param syuunyuuKingaku 収入金額
	 * @param deleteFlg 削除フラグ
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 *
	 */
	public static IncomeReadWriteDto from(
			String userId,
			String targetYear,
			String targetMonth,
			String syuunyuuCode,
			String syuunyuuKubun,
			String syuunyuuDetailContext,
			BigDecimal syuunyuuKingaku,
			boolean deleteFlg) {
		return new IncomeReadWriteDto(
				userId,
				targetYear,
				targetMonth,
				syuunyuuCode,
				syuunyuuKubun,
				syuunyuuDetailContext,
				syuunyuuKingaku,
				deleteFlg);
	}
}
