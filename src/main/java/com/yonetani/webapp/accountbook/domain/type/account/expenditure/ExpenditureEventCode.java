/**
 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプです。
 * 支出テーブル情報の「イベントコード」項目は、NULLを許容する項目になります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/12 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditure;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.NullableIdentifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプです。
 * 支出テーブル情報の「イベントコード」項目は、NULLを許容する項目になります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpenditureEventCode extends NullableIdentifier {
	/** 値がnullの支出テーブル情報の「イベントコード」項目の値 */
	public static final ExpenditureEventCode NULL = ExpenditureEventCode.from(null);
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value イベントコード
	 *
	 */
	private ExpenditureEventCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプを生成します。
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
	public static ExpenditureEventCode from(String code) {
		
		// 基本検証（空文字）
		NullableIdentifier.validate(code, "イベントコード");
		
		// null値の場合は、null値の「イベントコード」項目ドメインタイプを返す
		if(code == null) {
			return new ExpenditureEventCode(null);
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
		
		return new ExpenditureEventCode(code);
	}

}
