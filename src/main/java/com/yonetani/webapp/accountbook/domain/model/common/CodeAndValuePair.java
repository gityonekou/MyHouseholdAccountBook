/**
 * コード値と対応する値を表すドメインモデルです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.common;

import com.yonetani.webapp.accountbook.domain.type.common.Code;
import com.yonetani.webapp.accountbook.domain.type.common.CodeValue;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * コード値と対応する値を表すドメインモデルです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class CodeAndValuePair {
	// コード
	private final Code code;
	// 値
	private final CodeValue codeValue;
	
	/**
	 *<pre>
	 * 引数の値からコード値とコード値に対応する値を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param code コード値
	 * @param codeValue コード値に対応する値
	 * @return コード値と対応する値を表すドメインモデル
	 *
	 */
	public static CodeAndValuePair from(String code, String codeValue) {
		return new CodeAndValuePair(Code.from(code), CodeValue.from(codeValue));
	}
}
