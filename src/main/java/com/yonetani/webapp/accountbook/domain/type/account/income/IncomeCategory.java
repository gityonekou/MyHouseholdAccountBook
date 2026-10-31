/**
 * 「収入区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/09/08 : 1.00.00                      新規作成
 * 2026/03/15 : 1.01.00  feature-1.00-dev00  リファクタリング対応 クラス名をSyuunyuuKubunからIncomeCategoryにリネーム
 * 2026/10/31 : 1.02.00  feature-1.03-dev1   isOptional()・isDetailRequired()追加(IncomeItemFormの相関チェックのドメイン層移動対応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.income;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「収入区分」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class IncomeCategory {

	// 収入区分
	private final String value;

	/**
	 *<pre>
	 * 「収入区分」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param category 収入区分
	 * @return 「収入区分」項目ドメインタイプ
	 *
	 */
	public static IncomeCategory from(String category) {
		return new IncomeCategory(category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}

	/**
	 *<pre>
	 * 収入区分の設定内容が「9(その他任意)」の場合、true、それ以外の場合はfalseを返します。
	 *</pre>
	 * @return 収入区分が「その他任意」の場合：true、それ以外：false
	 *
	 */
	public boolean isOptional() {
		return MyHouseholdAccountBookContent.INCOME_KUBUN_OPTIONAL_SELECTED_VALUE.equals(value);
	}

	/**
	 *<pre>
	 * 収入詳細の入力を必須とするかどうかを判定して返します。
	 *
	 * 収入登録・更新画面の入力チェック(収入詳細の入力を必須とするかどうか)の判定に用います。
	 * (isOptional()と条件は同じですが、収入区分の分類判定と画面入力チェックという別の業務目的を表すため、
	 *  意図を明確にする別メソッドとして用意しています)
	 *</pre>
	 * @return 収入区分が「その他任意」の場合、収入詳細の入力を必須とする:true、それ以外:false
	 *
	 */
	public boolean isDetailRequired() {
		return MyHouseholdAccountBookContent.INCOME_KUBUN_OPTIONAL_SELECTED_VALUE.equals(value);
	}
}
