/**
 * 「デフォルト支払方法コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.NullableIdentifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 *  「デフォルト支払方法コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShopDefaultPaymentMethodCode extends NullableIdentifier {
	/** 値がnullの「デフォルト支払方法コード」項目 */
	public static ShopDefaultPaymentMethodCode NULL = ShopDefaultPaymentMethodCode.from(null);
	
	/**
	 *<pre>
	 * ShopDefaultPaymentMethodCodeクラスコンストラクターです。
	 *</pre>
	 * @param value
	 *
	 */
	private ShopDefaultPaymentMethodCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「デフォルト支払方法コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [非ガード節]
	 * ・null値
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param paymentMethodCode デフォルト支払方法コード
	 * @return 「デフォルト支払方法コード」項目ドメインタイプ
	 *
	 */
	public static ShopDefaultPaymentMethodCode from(String paymentMethodCode) {

		// 基本検証（null・空文字列は許可、空文字でない、長さが3桁でない）
		validate(paymentMethodCode, 3, "デフォルト支払方法コード");
		
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			if(StringUtils.hasLength(paymentMethodCode)) {
				Integer.parseInt(paymentMethodCode);
			}
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「デフォルト支払方法コード」項目の設定値が不正です。管理者に問い合わせてください。[paymentMethodCode=" + paymentMethodCode + "]");
		}
		
		return new ShopDefaultPaymentMethodCode(paymentMethodCode);
	}
}
