/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・支払方法表示順
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
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
 * ・支払方法表示順
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndPaymentMethodSort {
	// ユーザID
	private final UserId userId;
	// 支払方法表示順
	private final PaymentMethodSort paymentMethodSort;

	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・支払方法表示順
	 *</pre>
	 * @param userId ユーザID
	 * @param paymentMethodSort 支払方法表示順
	 * @return 検索条件(ユーザID, 支払方法表示順)
	 *
	 */
	public static SearchQueryUserIdAndPaymentMethodSort from(UserId userId, PaymentMethodSort paymentMethodSort) {
		return new SearchQueryUserIdAndPaymentMethodSort(userId, paymentMethodSort);
	}
}
