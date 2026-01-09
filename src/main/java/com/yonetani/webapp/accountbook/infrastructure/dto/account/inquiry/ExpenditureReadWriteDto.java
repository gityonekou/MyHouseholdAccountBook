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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;

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
	 * 支出テーブル情報ドメインモデルをもとにExpenditureReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 支出テーブル情報ドメインモデル
	 * @return 支出テーブル:EXPENDITURE_TABLE読込・出力情報
	 *
	 */
	public static ExpenditureReadWriteDto from(ExpenditureItem domain) {
		return new ExpenditureReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYear().getValue(),
				// 対象月
				domain.getTargetMonth().getValue(),
				// 支出コード
				domain.getSisyutuCode().getValue(),
				// 支出項目コード
				domain.getSisyutuItemCode().getValue(),
				// イベントコード
				domain.getEventCode().getValue(),
				// 支出名称
				domain.getSisyutuName().getValue(),
				// 支出区分
				domain.getSisyutuKubun().getValue(),
				// 支出詳細
				domain.getSisyutuDetailContext().getValue(),
				// 支払日
				domain.getPaymentDate().getValue(),
				// 支出予定金額
				domain.getExpectedExpenditureAmount().getValue(),
				// 支出金額
				domain.getExpenditureAmount().getValue(),
				// 削除フラグ
				domain.getDeleteFlg().getValue());
	}
}
