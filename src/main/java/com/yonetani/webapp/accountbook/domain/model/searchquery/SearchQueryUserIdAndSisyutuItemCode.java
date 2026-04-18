/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・支出項目コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/20 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndSisyutuItemCode {
	// ユーザID
	private final UserId userId;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・支出項目コード
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemCode 支出項目コード
	 * @return 検索条件(ユーザID, 支出項目コード)
	 *
	 */
	public static SearchQueryUserIdAndSisyutuItemCode from (UserId userId, SisyutuItemCode sisyutuItemCode) {
		return new SearchQueryUserIdAndSisyutuItemCode(userId, sisyutuItemCode);
	}
}
