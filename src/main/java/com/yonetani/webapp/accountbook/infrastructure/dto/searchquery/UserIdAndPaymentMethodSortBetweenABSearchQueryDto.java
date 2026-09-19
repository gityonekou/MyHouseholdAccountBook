/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支払方法表示順A
 * ・支払方法表示順B
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSortBetweenAB;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支払方法表示順A
 * ・支払方法表示順B
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndPaymentMethodSortBetweenABSearchQueryDto {
	// ユーザID
	private final String userId;
	// 支払方法表示順A
	private final String paymentMethodSortA;
	// 支払方法表示順B
	private final String paymentMethodSortB;

	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndPaymentMethodSortBetweenABSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、支払方法表示順A、支払方法表示順B)
	 * @return テーブルの検索条件：ユーザID、支払方法表示順A、支払方法表示順B
	 *
	 */
	public static UserIdAndPaymentMethodSortBetweenABSearchQueryDto from(SearchQueryUserIdAndPaymentMethodSortBetweenAB search) {
		return new UserIdAndPaymentMethodSortBetweenABSearchQueryDto(
				search.getUserId().getValue(),
				search.getPaymentMethodSortA().getValue(),
				search.getPaymentMethodSortB().getValue());
	}
}
