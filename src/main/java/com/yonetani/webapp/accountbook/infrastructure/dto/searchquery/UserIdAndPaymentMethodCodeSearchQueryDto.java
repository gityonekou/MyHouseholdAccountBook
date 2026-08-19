/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支払方法コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支払方法コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndPaymentMethodCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 支払方法コード
	private final String paymentMethodCode;

	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndPaymentMethodCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、支払方法コード)
	 * @return テーブルの検索条件：ユーザID、支払方法コード
	 *
	 */
	public static UserIdAndPaymentMethodCodeSearchQueryDto from(SearchQueryUserIdAndPaymentMethodCode search) {
		return new UserIdAndPaymentMethodCodeSearchQueryDto(
				search.getUserId().getValue(),
				search.getPaymentMethodCode().getValue());
	}
}
