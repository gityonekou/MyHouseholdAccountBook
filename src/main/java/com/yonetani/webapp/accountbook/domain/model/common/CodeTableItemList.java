/**
 * コード定義テーブル情報の取得結果(リスト情報)の値を表すドメインモデルです
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * コード定義テーブル情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CodeTableItemList {
	// コード定義テーブル情報のリスト
	private final List<CodeTableItem> values;
	
	/**
	 *<pre>
	 * コード定義テーブル情報(ドメインモデル)のリストからCodeTableItemListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values コード定義テーブル情報(ドメインモデル)のリスト
	 * @return CodeTableItemListのドメインモデル
	 *
	 */
	public static CodeTableItemList from(List<CodeTableItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new CodeTableItemList(Collections.emptyList());
		} else {
			return new CodeTableItemList(values);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 300);
			buff.append("コード定義テーブル情報:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "コード定義テーブル情報:0件";
		}
	}
}
