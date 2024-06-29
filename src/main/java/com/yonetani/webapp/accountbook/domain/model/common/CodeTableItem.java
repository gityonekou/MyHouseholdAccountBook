/**
 * コード定義テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.common;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.common.CodeKubun;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * コード定義テーブル情報を表すドメインモデルです
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
public class CodeTableItem {
	// コード区分
	private final CodeKubun kubun;
	// コード、コード値のリスト
	private final List<CodeAndValuePair> keyValueList;
	
	/**
	 *<pre>
	 * 引数の値からコード定義テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param kubun コード区分
	 * @param keyValueList コード、コード値のリスト
	 * @return コード定義テーブル情報を表すドメインモデル
	 *
	 */
	public static CodeTableItem from(String kubun, List<CodeAndValuePair> keyValueList) {
		if(CollectionUtils.isEmpty(keyValueList)) {
			return new CodeTableItem(CodeKubun.from(kubun), Collections.emptyList());
		} else {
			return new CodeTableItem(CodeKubun.from(kubun), keyValueList);
		}
	}
}
