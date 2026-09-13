/**
 * 銀行口座テーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.bankaccount;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 銀行口座テーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BankAccountInquiryList {
	// 銀行口座情報のリスト
	private final List<BankAccount> values;

	/**
	 *<pre>
	 * 引数の値から銀行口座テーブル情報(リスト情報)を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param values 銀行口座情報のリスト
	 * @return 銀行口座テーブル情報(リスト情報)を表すドメインモデル
	 *
	 */
	public static BankAccountInquiryList from(List<BankAccount> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new BankAccountInquiryList(Collections.emptyList());
		} else {
			return new BankAccountInquiryList(values);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 130);
			buff.append("銀行口座情報:")
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
			return "銀行口座情報:0件";
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
