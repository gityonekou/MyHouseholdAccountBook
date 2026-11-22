/**
 * 支払方法テーブル:PAYMENT_METHOD_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.paymentmethod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支払方法テーブル:PAYMENT_METHOD_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentMethodReadWriteDto {
	// ユーザID
	private final String userId;
	// 支払方法コード
	private final String paymentMethodCode;
	// 支払方法名
	private final String paymentMethodName;
	// 支払方法メモ
	private final String paymentMethodMemo;
	// 支払方法種別
	private final String paymentMethodKubun;
	// 銀行口座コード
	private final String bankAccountCode;
	// 集計開始日
	private final String closingDay;
	// 支払方法表示順
	private final String paymentMethodSort;
	// 有効/無効フラグ
	private final boolean enableFlg;
	// 更新可否フラグ
	private final boolean enableUpdateFlg;

	/**
	 *<pre>
	 * 引数のパラメータ値をもとにPaymentMethodReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param paymentMethodCode 支払方法コード
	 * @param paymentMethodName 支払方法名
	 * @param paymentMethodMemo 支払方法メモ
	 * @param paymentMethodKubun 支払方法種別
	 * @param bankAccountCode 銀行口座コード
	 * @param closingDay 集計開始日
	 * @param paymentMethodSort 支払方法表示順
	 * @param enableFlg 有効/無効フラグ
	 * @param enableUpdateFlg 更新可否フラグ
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE出力情報
	 *
	 */
	public static PaymentMethodReadWriteDto from(String userId, String paymentMethodCode, String paymentMethodName,
			String paymentMethodMemo, String paymentMethodKubun, String bankAccountCode, String closingDay,
			String paymentMethodSort, boolean enableFlg, boolean enableUpdateFlg) {
		return new PaymentMethodReadWriteDto(userId, paymentMethodCode, paymentMethodName, paymentMethodMemo,
				paymentMethodKubun, bankAccountCode, closingDay, paymentMethodSort, enableFlg, enableUpdateFlg);
	}
}
