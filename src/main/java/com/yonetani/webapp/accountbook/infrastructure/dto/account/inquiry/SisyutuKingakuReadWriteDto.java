/**
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/13 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;

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
	
	/**
	 *<pre>
	 * 支出金額テーブル情報ドメインモデルをもとにSisyutuKingakuReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 支出金額テーブル情報ドメインモデル
	 * @return 支出金額テーブル：SISYUTU_KINGAKU_TABLE読込・出力情報
	 *
	 */
	public static SisyutuKingakuReadWriteDto from(SisyutuKingakuItem domain) {
		
		return new SisyutuKingakuReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYear().getValue(),
				// 対象月
				domain.getTargetMonth().getValue(),
				// 支出項目コード
				domain.getSisyutuItemCode().getValue(),
				// 親支出項目コード
				domain.getParentSisyutuItemCode().getValue(),
				// 支出予定金額
				domain.getExpectedExpenditureAmount().getValue(),
				// 支出金額
				domain.getSisyutuKingaku().getValue(),
				// 無駄遣い（軽度）支出金額
				domain.getMinorWasteExpenditure().getValue(),
				// 無駄遣い（重度）支出金額
				domain.getSevereWasteExpenditure().getValue(),
				// 支出支払日
				domain.getSisyutushiharaiDate().getValue());
	}
	
}
