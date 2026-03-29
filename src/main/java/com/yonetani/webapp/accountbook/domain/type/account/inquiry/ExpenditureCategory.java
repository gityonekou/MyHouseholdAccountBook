/**
 * 「支出区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 * 2026/02/28 : 1.01.00  isWastedBOrC()インスタンスメソッド追加
 * 2026/03/15 : 1.02.00  クラス名をSisyutuKubunからExpenditureCategoryにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出区分」項目の値を表すドメインタイプです。
 * 支出区分には以下の3種類があります。
 * ・無駄遣いなし
 * ・無駄遣い（軽度）
 * ・無駄遣い（重度）
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
public class ExpenditureCategory {
	// 支出区分
	private final String value;

	/** 支出区分：無駄使いなし */
	public static final ExpenditureCategory NON_WASTED = ExpenditureCategory.from(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE);
	/** 支出区分：無駄遣い（軽度） */
	public static final ExpenditureCategory WASTED_B = ExpenditureCategory.from(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE);
	/** 支出区分：無駄遣い（重度） */
	public static final ExpenditureCategory WASTED_C = ExpenditureCategory.from(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE);

	/**
	 *<pre>
	 * 「支出区分」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・値が不正値(無駄遣いなし(1)、無駄遣い（軽度）(2)、無駄遣い（重度）(3)のどれでもない)
	 *
	 *</pre>
	 * @param kubun 支出区分
	 * @return 「支出区分」項目ドメインタイプ
	 *
	 */
	public static ExpenditureCategory from(String kubun) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(kubun)) {
			throw new MyHouseholdAccountBookRuntimeException("「支出区分」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(値が不正値=無駄遣いなし、無駄遣い（軽度）、無駄遣い（重度）のどれでもない)
		if(kubun.equals(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE)
				|| kubun.equals(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE)
				|| kubun.equals(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE)) {
			return new ExpenditureCategory(kubun);
		} else {
			throw new MyHouseholdAccountBookRuntimeException("「支出区分」項目の設定値が不正です。管理者に問い合わせてください。[kubun=" + kubun + "]");
		}
	}

	/**
	 *<pre>
	 * 「固定費名(支払名)」項目の値に「支出区分」項目の値の文字列が含まれる場合、該当する支出区分の「支出区分」項目ドメインタイプを生成します。
	 * ・「固定費名(支払名)」項目の値に文字列「無駄遣いB」が含まれる場合、「無駄遣い（軽度）」の支出区分を設定
	 * ・「固定費名(支払名)」項目の値に文字列「無駄遣いC」が含まれる場合、「無駄遣い（重度）」の支出区分を設定
	 * ・無駄遣いなし、及び、どの文字列も含まない場合、「無駄遣いなし」の支出区分を設定
	 *
	 *</pre>
	 * @param name「固定費名(支払名)」項目の値
	 * @return 「支出区分」項目ドメインタイプ
	 *
	 */
	public static ExpenditureCategory from(FixedCostName name) {
		if(name.getValue().indexOf(MyHouseholdAccountBookContent.WASTED_B_VIEW_VALUE) != -1) {
			return ExpenditureCategory.WASTED_B;
		}
		if(name.getValue().indexOf(MyHouseholdAccountBookContent.WASTED_C_VIEW_VALUE) != -1) {
			return ExpenditureCategory.WASTED_C;
		}
		return ExpenditureCategory.NON_WASTED;
	}

	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣いなし」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣いなし」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isNonWasted(ExpenditureCategory kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE);
	}

	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣い（軽度）」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣い（軽度）」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isWastedB(ExpenditureCategory kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE);
	}

	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣い（重度）」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣い（重度）」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isWastedC(ExpenditureCategory kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE);
	}

	/**
	 *<pre>
	 * この支出区分が「無駄遣い（軽度）」または「無駄遣い（重度）」かどうかを判定します。
	 *</pre>
	 * @return 値が「無駄遣い（軽度）」または「無駄遣い（重度）」の場合、true、それ以外はfalse
	 *
	 */
	public boolean isWastedBOrC() {
		return ExpenditureCategory.isWastedB(this) || ExpenditureCategory.isWastedC(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
