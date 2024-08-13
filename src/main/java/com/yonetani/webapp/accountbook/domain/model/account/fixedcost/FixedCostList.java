/**
 * 固定費テーブル情報(リスト情報)を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 固定費テーブル情報(リスト情報)を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostList {
	
	// 固定費情報の一覧リスト
	private final List<FixedCost> values;
	
	/**
	 *<pre>
	 * 引数の値から固定費テーブル情報(リスト情報)を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values 固定費情報のリスト
	 * @return 固定費テーブル情報(リスト情報)を表すドメインモデル
	 *
	 */
	public static FixedCostList from(List<FixedCost> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new FixedCostList(Collections.emptyList());
		} else {
			return new FixedCostList(values);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 420);
			buff.append("固定費情報:")
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
			return "固定費情報:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
