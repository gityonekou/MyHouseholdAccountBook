/**
 * 「店舗区分コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/01/28 : 1.00.00                      新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(Identifier継承に変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「店舗区分コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShopKubunCode extends Identifier {
	
	/**
	 *<pre>
	 * ShopKubunCodeクラスコンストラクターです。
	 *</pre>
	 * @param value 店舗区分コード
	 *
	 */
	private ShopKubunCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「店舗区分コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param code 店舗区分コード
	 * @return 「店舗区分コード」項目ドメインタイプ
	 *
	 */
	public static ShopKubunCode from(String code) {
		
		// 基本検証（null、空文字、長さが3桁でない）
		validate(code, 3, "店舗区分コード");
		
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「店舗区分コード」項目の設定値が不正です。管理者に問い合わせてください。[shopKubunCode=" + code + "]");
		}
		return new ShopKubunCode(code);
	}
}
