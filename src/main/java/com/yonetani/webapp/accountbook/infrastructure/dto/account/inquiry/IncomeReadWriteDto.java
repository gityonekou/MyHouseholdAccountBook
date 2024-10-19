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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItem;

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
	 * 収入テーブル情報ドメインモデルをもとにIncomeReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 収入テーブル情報ドメインモデル
	 * @return 収入テーブル:INCOME_TABLE読込・出力情報
	 *
	 */
	public static IncomeReadWriteDto from(IncomeItem domain) {
		return new IncomeReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYear().getValue(),
				// 対象月
				domain.getTargetMonth().getValue(),
				// 収入コード
				domain.getSyuunyuuCode().getValue(),
				// 収入区分
				domain.getSyuunyuuKubun().getValue(),
				// 収入詳細
				domain.getSyuunyuuDetailContext().getValue(),
				// 収入金額
				domain.getSyuunyuuKingaku().getValue(),
				// 削除フラグ
				domain.getDeleteFlg().getValue());
	}
}
