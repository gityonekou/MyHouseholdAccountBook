/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・イベントコード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
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
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndEventCode {
	// ユーザID
	private final UserId userId;
	// イベントコード
	private final EventCode eventCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・イベントコード
	 *</pre>
	 * @param userId ユーザID
	 * @param eventCode イベントコード
	 * @return 検索条件(ユーザID, イベントコード)
	 *
	 */
	public static SearchQueryUserIdAndEventCode from(UserId userId, EventCode eventCode) {
		return new SearchQueryUserIdAndEventCode(userId, eventCode);
	}
}
