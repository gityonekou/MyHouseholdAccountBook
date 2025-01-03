/**
 * 「支出区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
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
 * 「支出区分」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuKubun {
	// 支出区分
	private final String value;
	
	/** 支出区分(無駄使いなし) */
	public static final SisyutuKubun NON_WASTED = SisyutuKubun.from(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE);
	/** 支出区分(無駄遣いB) */
	public static final SisyutuKubun WASTED_B = SisyutuKubun.from(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE);
	/** 支出区分(無駄遣いC) */
	public static final SisyutuKubun WASTED_C = SisyutuKubun.from(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE);
	
	/**
	 *<pre>
	 * 「支出区分」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・値が不正値(無駄遣いなし(1)、無駄遣いB(2)、無駄遣いC(3)のどれでもない)
	 * 
	 *</pre>
	 * @param kubun 支出区分
	 * @return 「支出区分」項目ドメインタイプ
	 *
	 */
	public static SisyutuKubun from(String kubun) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(kubun)) {
			throw new MyHouseholdAccountBookRuntimeException("「支出区分」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(値が不正値=無駄遣いなし(1)、無駄遣いB(2)、無駄遣いC(3)のどれでもない)
		if(kubun.equals(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE)
				|| kubun.equals(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE)
				|| kubun.equals(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE)) {
			return new SisyutuKubun(kubun);
		} else {
			throw new MyHouseholdAccountBookRuntimeException("「支出区分」項目の設定値が不正です。管理者に問い合わせてください。[kubun=" + kubun + "]");
		}
	}
	
	/**
	 *<pre>
	 * 「固定費名(支払名)」項目の値に「支出区分」項目の値の文字列が含まれる場合、該当する支出区分の「支出区分」項目ドメインタイプを生成します。
	 * ・「固定費名(支払名)」項目の値に文字列「無駄遣いB」が含まれる場合、「無駄遣いB」の支出区分を設定
	 * ・「固定費名(支払名)」項目の値に文字列「無駄遣いC」が含まれる場合、「無駄遣いC」の支出区分を設定
	 * ・無駄遣いなし、及び、どの文字列も含まない場合、「無駄遣いなし」の支出区分を設定
	 * 　
	 *</pre>
	 * @param name「固定費名(支払名)」項目の値
	 * @return 「支出区分」項目ドメインタイプ
	 *
	 */
	public static SisyutuKubun from(FixedCostName name) {
		if(name.getValue().indexOf(MyHouseholdAccountBookContent.WASTED_B_VIEW_VALUE) != -1) {
			return SisyutuKubun.WASTED_B;
		}
		if(name.getValue().indexOf(MyHouseholdAccountBookContent.WASTED_C_VIEW_VALUE) != -1) {
			return SisyutuKubun.WASTED_C;
		}
		return SisyutuKubun.NON_WASTED;
	}
	
	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣いなし」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣いなし」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isNonWasted(SisyutuKubun kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.NON_WASTED_SELECTED_VALUE);
	}
	
	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣いB」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣いB」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isWastedB(SisyutuKubun kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.WASTED_B_SELECTED_VALUE);
	}
	
	/**
	 *<pre>
	 * 指定した支出区分項目の値が「無駄遣いC」かどうかを判定します。
	 *</pre>
	 * @param kubun 支出区分項目
	 * @return 値が「無駄遣いC」の場合、true、それ以外はfalse
	 *
	 */
	public static boolean isWastedC(SisyutuKubun kubun) {
		return kubun.getValue().equals(MyHouseholdAccountBookContent.WASTED_C_SELECTED_VALUE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
