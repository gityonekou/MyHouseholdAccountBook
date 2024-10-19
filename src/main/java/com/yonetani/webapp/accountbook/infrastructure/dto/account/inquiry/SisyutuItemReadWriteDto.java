/**
 * 支出項目テーブル:SISYUTU_ITEM_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支出項目テーブル:SISYUTU_ITEM_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SisyutuItemReadWriteDto {
	// ユーザID
	private final String userId;
	// 支出項目コード
	private final String sisyutuItemCode;
	// 支出項目名
	private final String sisyutuItemName;
	// 支出項目詳細内容
	private final String sisyutuItemDetailContext;
	// 親支出項目コード
	private final String parentSisyutuItemCode;
	// 支出項目レベル(1～5)
	private final String sisyutuItemLevel;
	// 支出項目表示順
	private final String sisyutuItemSort;
	// 更新可否フラグ
	private final boolean enableUpdateFlg;
	
	/**
	 *<pre>
	 * 支出項目テーブル情報ドメインモデルをもとにSisyutuItemReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 支出項目テーブル情報ドメインモデル
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE読込・出力情報
	 *
	 */
	public static SisyutuItemReadWriteDto from(SisyutuItem domain) {
		
		return new SisyutuItemReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 支出項目コード
				domain.getSisyutuItemCode().getValue(),
				// 支出項目名
				domain.getSisyutuItemName().getValue(),
				// 支出項目詳細内容
				domain.getSisyutuItemDetailContext().getValue(),
				// 親支出項目コード
				domain.getParentSisyutuItemCode().getValue(),
				// 支出項目レベル(1～5)
				domain.getSisyutuItemLevel().toString(),
				// 支出項目表示順
				domain.getSisyutuItemSort().getValue(),
				// 更新可否フラグ
				domain.getEnableUpdateFlg().getValue());
	}
}
