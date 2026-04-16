/**
 * 支出項目テーブル(BASE)情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemSortOrder;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ParentExpenditureItemCode;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出項目テーブル(BASE)情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class SisyutuItemBase {
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	// 支出項目名
	private final ExpenditureItemName expenditureItemName;
	// 支出項目詳細内容
	private final ExpenditureItemDetailContext expenditureItemDetailContext;
	// 親支出項目コード
	private final ParentExpenditureItemCode parentExpenditureItemCode;
	// 支出項目レベル(1～5)
	private final ExpenditureItemLevel expenditureItemLevel;
	// 支出項目表示順
	private final ExpenditureItemSortOrder expenditureItemSortOrder;
	
	/**
	 *<pre>
	 * 引数の値から支出項目テーブル(BASE)情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureItemName 支出項目名
	 * @param expenditureItemDetailContext 支出項目詳細内容
	 * @param parentExpenditureItemCode 親支出項目コード
	 * @param expenditureItemLevel 支出項目レベル(1～5)
	 * @param expenditureItemSortOrder 支出項目表示順
	 * @return 支出項目テーブル(BASE)情報ドメインモデル
	 *
	 */
	public static SisyutuItemBase from(
			String expenditureItemCode,
			String expenditureItemName,
			String expenditureItemDetailContext,
			String parentExpenditureItemCode,
			String expenditureItemLevel,
			String expenditureItemSortOrder) {
		return new SisyutuItemBase(
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureItemName.from(expenditureItemName),
				ExpenditureItemDetailContext.from(expenditureItemDetailContext),
				ParentExpenditureItemCode.from(parentExpenditureItemCode),
				ExpenditureItemLevel.from(expenditureItemLevel),
				ExpenditureItemSortOrder.from(expenditureItemSortOrder));
	}
	
	/**
	 *<pre>
	 * 各データの値をカンマ区切りの1行の文字列として返します。
	 *</pre>
	 * @return
	 *
	 */
	public String toLineString() {
		StringBuilder buff = new StringBuilder(150);
		buff.append(expenditureItemCode)
		.append(',')
		.append(expenditureItemName)
		.append(',')
		.append(expenditureItemDetailContext)
		.append(',')
		.append(parentExpenditureItemCode)
		.append(',')
		.append(expenditureItemLevel)
		.append(',')
		.append(expenditureItemSortOrder);
		return buff.toString();
	}
}
