/**
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/06/03 : 1.00.00                      新規作成
 * 2026/04/05 : 1.01.00  feature-1.00-dev00  リファクタリング対応 クラス名をFixedCostShiharaiTukiからFixedCostTargetPaymentMonthにリネーム
 * 2026/10/18 : 1.02.00  feature-1.03-dev1   支払月判定ロジック重複解消対応 appliesToMonth/isOptionalメソッド追加
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプです
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
public class FixedCostTargetPaymentMonth {
	// 固定費支払月(支払月)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費支払月(支払月)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 固定費支払月(支払月)
	 * @return 「固定費支払月(支払月)」項目ドメインタイプ
	 *
	 */
	public static FixedCostTargetPaymentMonth from(String value) {
		return new FixedCostTargetPaymentMonth(value);
	}

	/**
	 *<pre>
	 * 指定した月に支払いが発生するかどうかを判定して返します。
	 *</pre>
	 * @param monthValue 判定対象の月（1〜12）
	 * @return 指定した月に支払いが発生する場合true、そうでない場合false
	 *
	 */
	public boolean appliesToMonth(int monthValue) {

		// 固定費支払月の値に応じて、対象月に支払いが発生するかどうかを判定
		switch (value) {

			// "00" 毎月 と "40" その他任意 は常に支払いが発生(trueを返す)
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE:
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE:
				return true;

			// "20" 奇数月
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE:
				return monthValue % 2 == 1;

			// "30" 偶数月
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE:
				return monthValue % 2 == 0;

			// それ以外は "01"〜"12" の特定月指定と想定し、整数値に変換して比較
			default:
				return Integer.parseInt(value) == monthValue;
		}
	}

	/**
	 *<pre>
	 * 固定費支払月の値が「その他任意」かどうかを判定して返します。
	 *</pre>
	 * @return 「その他任意」の場合true、そうでない場合false
	 *
	 */
	public boolean isOptional() {
		return MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE.equals(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
