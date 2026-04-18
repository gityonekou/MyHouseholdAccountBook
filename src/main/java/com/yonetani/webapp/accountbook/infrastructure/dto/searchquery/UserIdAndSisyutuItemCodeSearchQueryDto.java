/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支出項目コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/23 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
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
public class UserIdAndSisyutuItemCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 支出項目コード
	private final String sisyutuItemCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndSisyutuItemCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、支出項目コード)
	 * @return テーブルの検索条件：ユーザID、支出項目コード
	 *
	 */
	public static UserIdAndSisyutuItemCodeSearchQueryDto from(SearchQueryUserIdAndExpenditureItemCode search) {
		return new UserIdAndSisyutuItemCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:支出項目コード
				search.getExpenditureItemCode().getValue());
	}
}
