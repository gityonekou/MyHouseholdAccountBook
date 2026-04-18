/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・支出項目コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/20 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・支出項目コード
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
public class SearchQueryUserIdAndExpenditureItemCode {
	// ユーザID
	private final UserId userId;
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・支出項目コード
	 *</pre>
	 * @param userId ユーザID
	 * @param expenditureItemCode 支出項目コード
	 * @return 検索条件(ユーザID, 支出項目コード)
	 *
	 */
	public static SearchQueryUserIdAndExpenditureItemCode from (UserId userId, ExpenditureItemCode expenditureItemCode) {
		return new SearchQueryUserIdAndExpenditureItemCode(userId, expenditureItemCode);
	}
}
