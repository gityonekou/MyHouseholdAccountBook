/**
 * 「支払方法コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import java.util.Optional;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;
import com.yonetani.webapp.accountbook.domain.type.common.SafeDomainFactory;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支払方法コード」項目の値を表すドメインタイプです
 *
 * 990〜999をシステム予約帯とし、「支払方法がない」を999固定値とする。
 * 予約帯の判定はisSystemReserved()/isNotApplicable()に集約する（990という値をコード中に直書きしない）。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class PaymentMethodCode extends Identifier {

	// システム予約帯の開始値
	private static final String RESERVED_FROM = "990";
	// 「支払方法がない」固定値
	private static final String NOT_APPLICABLE = "999";

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 支払方法コード
	 *
	 */
	private PaymentMethodCode(String value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「支払方法コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param paymentMethodCode 支払方法コード
	 * @return 「支払方法コード」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodCode from(String paymentMethodCode) {

		// 基本検証（null、空文字、長さが3桁でない）
		Identifier.validate(paymentMethodCode, 3, "支払方法コード");
		
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(paymentMethodCode);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法コード」項目の設定値が不正です。管理者に問い合わせてください。[paymentMethodCode=" + paymentMethodCode + "]");
		}
		
		return new PaymentMethodCode(paymentMethodCode);
	}

	/**
	 *<pre>
	 * 新規発番する支払方法コードの値(数値)をもとに、「支払方法コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する支払方法コードの値(数値)
	 * @return 「支払方法コード」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodCode from(int count) {
		return new PaymentMethodCode(String.format("%03d", count));
	}

	/**
	 *<pre>
	 * 新規発番する支払方法コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する支払方法コードの値(数値)
	 * @return 支払方法コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return PaymentMethodCode.from(count).getValue();
	}

	/**
	 *<pre>
	 * 「支払方法コード」項目の値を表すドメインタイプを生成します。
	 * from()と異なり、不正な形式の値が渡された場合に例外を投げず、Optional.empty()を返します。
	 * 値が不正かもしれない外部入力(Formのバリデーション等)を検証する場合に使用してください。
	 *</pre>
	 * @param value 支払方法コード
	 * @return 生成に成功した場合は値ありのOptional、失敗した場合はOptional.empty()
	 *
	 */
	public static Optional<PaymentMethodCode> tryFrom(String value) {
		return SafeDomainFactory.tryCreate(() -> PaymentMethodCode.from(value));
	}

	/**
	 *<pre>
	 * このコードがシステム予約帯（990～999）に該当するかどうかを判定します。
	 *</pre>
	 * @return システム予約帯に該当する場合：true、該当しない場合：false
	 *
	 */
	public boolean isSystemReserved() {
		return getValue().compareTo(RESERVED_FROM) >= 0;
	}

	/**
	 *<pre>
	 * このコードが「支払方法がない」を表す固定値かどうかを判定します。
	 *</pre>
	 * @return 「支払方法がない」固定値の場合：true、それ以外：false
	 *
	 */
	public boolean isNotApplicable() {
		return NOT_APPLICABLE.equals(getValue());
	}
}
