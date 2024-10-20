/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・イベントコード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndEventCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・イベントコード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndEventCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// イベントコード
	private final String eventCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndEventCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、イベントコード)
	 * @return テーブルの検索条件：ユーザID、イベントコード
	 *
	 */
	public static UserIdAndEventCodeSearchQueryDto from(SearchQueryUserIdAndEventCode search) {
		return new UserIdAndEventCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:イベントコード
				search.getEventCode().getValue());
	}
}
