/**
 * 「イベントコード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「イベントコード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class EventCode {
	// イベントコード
	private final String value;
	
	/** null値のイベントコード */
	public static final EventCode NUL_EVENT_CODE = EventCode.forNullEventCode();
	
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
		
		// ガード節(空文字列)
		if(!StringUtils.hasLength(code)) {
			throw new MyHouseholdAccountBookRuntimeException("「イベントコード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
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
	 * null値のイベントコード項目を生成します。
	 *</pre>
	 * @return
	 *
	 */
	private static EventCode forNullEventCode() {
		return new EventCode(null);
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
		return new EventCode(String.format("%04d", count));
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
