/**
 * 支出テーブル:EXPENDITURE_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
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
 * 支出テーブル:EXPENDITURE_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ExpenditureReadWriteDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 支出コード
	private final String sisyutuCode;
	// 支出項目コード
	private final String sisyutuItemCode;
	// イベントコード
	private final String eventCode;
	// 支出名称
	private final String sisyutuName;
	// 支出区分
	private final String sisyutuKubun;
	// 支出詳細
	private final String sisyutuDetailContext;
	// 支払日
	private final LocalDate shiharaiDate;
	// 支出予定金額
	private final BigDecimal sisyutuYoteiKingaku;
	// 支出金額
	private final BigDecimal sisyutuKingaku;
	// 削除フラグ
	private final boolean deleteFlg;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにExpenditureReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId   ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param sisyutuCode 支出コード
	 * @param sisyutuItemCode 支出項目コード
	 * @param eventCode イベントコード
	 * @param sisyutuName 支出名称
	 * @param sisyutuKubun 支出区分
	 * @param sisyutuDetailContext 支出詳細
	 * @param shiharaiDate 支払日
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @param sisyutuKingaku 支出金額
	 * @param deleteFlg 削除フラグ
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 *
	 */
	public static ExpenditureReadWriteDto from(
			String userId,
			String targetYear,
			String targetMonth,
			String sisyutuCode,
			String sisyutuItemCode,
			String eventCode,
			String sisyutuName,
			String sisyutuKubun,
			String sisyutuDetailContext,
			LocalDate shiharaiDate,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			boolean deleteFlg) {
		return new ExpenditureReadWriteDto(
				userId,
				targetYear,
				targetMonth,
				sisyutuCode,
				sisyutuItemCode,
				eventCode,
				sisyutuName, 
				sisyutuKubun,
				sisyutuDetailContext,
				shiharaiDate,
				sisyutuYoteiKingaku,
				sisyutuKingaku,
				deleteFlg);
	}
}
