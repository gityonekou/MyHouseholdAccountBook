/**
 * 以下の照会条件の値を表すドメインモデルです。
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
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndPaymentMethodSortBetweenAB {
	// ユーザID
	private final UserId userId;
	// 支払方法表示順A
	private final PaymentMethodSort paymentMethodSortA;
	// 支払方法表示順B
	private final PaymentMethodSort paymentMethodSortB;

	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・支払方法表示順A
	 * ・支払方法表示順B
	 *</pre>
	 * @param userId ユーザID
	 * @param paymentMethodSortA 支払方法表示順A
	 * @param paymentMethodSortB 支払方法表示順B
	 * @return 検索条件(ユーザID, 支払方法表示順A, 支払方法表示順B)
	 *
	 */
	public static SearchQueryUserIdAndPaymentMethodSortBetweenAB from(
			UserId userId, PaymentMethodSort paymentMethodSortA, PaymentMethodSort paymentMethodSortB) {
		return new SearchQueryUserIdAndPaymentMethodSortBetweenAB(userId, paymentMethodSortA, paymentMethodSortB);
	}
}
