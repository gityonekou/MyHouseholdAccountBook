/**
 * 「イベントコード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「イベントコード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class EventCode extends Identifier {
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value イベントコード
	 *
	 */
	private EventCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「イベントコード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 * 
	 *</pre>
	 * @param code イベントコード
	 * @return 「イベントコード」項目ドメインタイプ
	 *
	 */
	public static EventCode from(String code) {
		
		// 基本検証（null、空文字）
		Identifier.validate(code, "イベントコード");
		
		// ガード節(長さが4桁でない)
		if(code.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「イベントコード」項目の設定値が不正です。管理者に問い合わせてください。[eventCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「イベントコード」項目の設定値が不正です。管理者に問い合わせてください。[eventCode=" + code + "]");
		}
		
		return new EventCode(code);
	}
	
	/**
	 *<pre>
	 * 新規発番するイベントコードの値(数値)をもとに、「イベントコード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番するイベントコードの値(数値)
	 * @return 「イベントコード」項目ドメインタイプ
	 *
	 */
	public static EventCode from(int count) {
		return EventCode.from(String.format("%04d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番するイベントコードの値を取得します。
	 *</pre>
	 * @param count 新規発番するイベントコードの値(数値)
	 * @return イベントコードの値
	 *
	 */
	public static String getNewCode(int count) {
		return EventCode.from(count).getValue();
	}
}
