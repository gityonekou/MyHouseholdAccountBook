/**
 * 「支払方法種別」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import java.util.Arrays;
import java.util.Optional;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.SafeDomainFactory;

/**
 *<pre>
 * 「支払方法種別」項目の値を表すドメインタイプです
 *
 * 種別ごとに異なる業務ルール(銀行口座の要否、集計開始日の要否)を、呼び出し側でのif分岐に書き散らさず
 * 述語メソッド(requiresAccount()/requiresClosingDay())として自身に持たせるenumです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public enum PaymentMethodKubun {
	/** 現金 */
	CASH("1", false, false),
	/** 口座振替 */
	BANK_TRANSFER("2", true, false),
	/** クレジットカード */
	CREDIT_CARD("3", true, true),
	/** デビットカード */
	DEBIT_CARD("4", true, false),
	/** 電子マネー(前払い式) */
	PREPAID_EMONEY("5", false, false);

	// コード値
	private final String value;
	// 銀行口座の紐づけが必須かどうか
	private final boolean requiresAccount;
	// 集計開始日の設定が必須かどうか
	private final boolean requiresClosingDay;

	/**
	 *<pre>
	 * コンストラクタ
	 *</pre>
	 * @param value コード値
	 * @param requiresAccount 銀行口座の紐づけが必須かどうか
	 * @param requiresClosingDay 集計開始日の設定が必須かどうか
	 *
	 */
	private PaymentMethodKubun(String value, boolean requiresAccount, boolean requiresClosingDay) {
		this.value = value;
		this.requiresAccount = requiresAccount;
		this.requiresClosingDay = requiresClosingDay;
	}

	/**
	 *<pre>
	 * コード値から「支払方法種別」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value コード値
	 * @return 「支払方法種別」項目ドメインタイプ
	 * @throws MyHouseholdAccountBookRuntimeException 未定義の値が指定された場合
	 *
	 */
	public static PaymentMethodKubun from(String value) {
		return Arrays.stream(values())
				.filter(kubun -> kubun.value.equals(value))
				.findFirst()
				.orElseThrow(() -> new MyHouseholdAccountBookRuntimeException(
						"「支払方法種別」項目の設定値が不正です。管理者に問い合わせてください。[paymentMethodKubun=" + value + "]"));
	}

	/**
	 *<pre>
	 * コード値から「支払方法種別」項目の値を表すドメインタイプを生成します。
	 * from()と異なり、未定義の値が渡された場合に例外を投げず、Optional.empty()を返します。
	 *</pre>
	 * @param value コード値
	 * @return 生成に成功した場合は値ありのOptional、失敗した場合はOptional.empty()
	 *
	 */
	public static Optional<PaymentMethodKubun> tryFrom(String value) {
		return SafeDomainFactory.tryCreate(() -> PaymentMethodKubun.from(value));
	}

	/**
	 *<pre>
	 * コード値を取得します。
	 *</pre>
	 * @return コード値
	 *
	 */
	public String getValue() {
		return value;
	}

	/**
	 *<pre>
	 * この支払方法種別が銀行口座の紐づけを必須とするかどうかを判定します。
	 *</pre>
	 * @return 銀行口座の紐づけが必須の場合：true、不要の場合：false
	 *
	 */
	public boolean requiresAccount() {
		return requiresAccount;
	}

	/**
	 *<pre>
	 * この支払方法種別が集計開始日の設定を必須とするかどうかを判定します。
	 *</pre>
	 * @return 集計開始日の設定が必須の場合：true、不要の場合：false
	 *
	 */
	public boolean requiresClosingDay() {
		return requiresClosingDay;
	}
}
