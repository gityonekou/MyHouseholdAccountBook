/**
 * 支出項目テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo;

import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemSortOrder;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ParentExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.EnableUpdateFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出項目テーブル情報を表すドメインモデルです
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
public class ExpenditureItemInfo {
	// ユーザID
	private final UserId userId;
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
	// 更新可否フラグ
	private final EnableUpdateFlg enableUpdateFlg;
	
	/**
	 *<pre>
	 * 引数の値から支出項目テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureItemName 支出項目名
	 * @param expenditureItemDetailContext 支出項目詳細内容
	 * @param parentExpenditureItemCode 親支出項目コード
	 * @param expenditureItemLevel 支出項目レベル(1～5)
	 * @param expenditureItemSortOrder 支出項目表示順
	 * @param enableUpdateFlg 更新可否フラグ
	 * @return 支出項目テーブル情報を表すドメインモデル
	 *
	 */
	public static ExpenditureItemInfo from(
			String userId,
			String expenditureItemCode,
			String expenditureItemName,
			String expenditureItemDetailContext,
			String parentExpenditureItemCode,
			String expenditureItemLevel,
			String expenditureItemSortOrder,
			boolean enableUpdateFlg) {
		return new ExpenditureItemInfo(
				UserId.from(userId),
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureItemName.from(expenditureItemName),
				ExpenditureItemDetailContext.from(expenditureItemDetailContext),
				ParentExpenditureItemCode.from(parentExpenditureItemCode),
				ExpenditureItemLevel.from(expenditureItemLevel),
				ExpenditureItemSortOrder.from(expenditureItemSortOrder),
				EnableUpdateFlg.from(enableUpdateFlg));
		
	}
}
