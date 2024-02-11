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
	 * 引数のパラメータ値をもとにSisyutuItemReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId   ユーザID
	 * @param sisyutuItemCode 支出項目コード
	 * @param sisyutuItemName 支出項目名
	 * @param sisyutuItemDetailContext 支出項目詳細内容
	 * @param parentSisyutuItemCode 親支出項目コード
	 * @param sisyutuItemLevel 支出項目レベル(1～5)
	 * @param sisyutuItemSort 支出項目表示順
	 * @param enableUpdateFlg 更新可否フラグ
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 *
	 */
	public static SisyutuItemReadWriteDto from(
			String userId,
			String sisyutuItemCode,
			String sisyutuItemName,
			String sisyutuItemDetailContext,
			String parentSisyutuItemCode,
			String sisyutuItemLevel,
			String sisyutuItemSort,
			boolean enableUpdateFlg) {
		return new SisyutuItemReadWriteDto(
				userId,
				sisyutuItemCode,
				sisyutuItemName,
				sisyutuItemDetailContext, 
				parentSisyutuItemCode,
				sisyutuItemLevel,
				sisyutuItemSort,
				enableUpdateFlg);
	}
}
