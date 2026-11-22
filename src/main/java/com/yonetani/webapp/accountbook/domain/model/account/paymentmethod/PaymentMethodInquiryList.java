/**
 * 支払方法テーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.paymentmethod;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支払方法テーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaymentMethodInquiryList {
	// 支払方法情報のリスト
	private final List<PaymentMethod> values;

	/**
	 *<pre>
	 * 引数の値から支払方法テーブル情報(リスト情報)を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param values 支払方法情報のリスト
	 * @return 支払方法テーブル情報(リスト情報)を表すドメインモデル
	 *
	 */
	public static PaymentMethodInquiryList from(List<PaymentMethod> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new PaymentMethodInquiryList(Collections.emptyList());
		} else {
			return new PaymentMethodInquiryList(values);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 130);
			buff.append("支払方法情報:")
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
			return "支払方法情報:0件";
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
