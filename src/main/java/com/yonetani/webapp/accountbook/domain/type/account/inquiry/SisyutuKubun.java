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

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

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
	
	/* コード定義:支出区分で無駄遣いなし(1)を選択時 */
	private static final String NON_WASTED_SELECTED_VALUE = "1";
	/* コード定義:支出区分で無駄遣いB(2)を選択時 */
	private static final String WASTED_B_SELECTED_VALUE = "2";
	/* コード定義:支出区分で無駄遣いC(3)を選択時 */
	private static final String WASTED_C_SELECTED_VALUE = "3";
	
	/** 支出区分(無駄使いなし) */
	public static final SisyutuKubun NON_WASTED = SisyutuKubun.from(NON_WASTED_SELECTED_VALUE);
	
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
		if(kubun.equals(NON_WASTED_SELECTED_VALUE)
				|| kubun.equals(WASTED_B_SELECTED_VALUE)
				|| kubun.equals(WASTED_C_SELECTED_VALUE)) {
			return new SisyutuKubun(kubun);
		} else {
			throw new MyHouseholdAccountBookRuntimeException("「支出区分」項目の設定値が不正です。管理者に問い合わせてください。[kubun=" + kubun + "]");
		}
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
		return kubun.getValue().equals(NON_WASTED_SELECTED_VALUE);
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
		return kubun.getValue().equals(WASTED_B_SELECTED_VALUE);
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
		return kubun.getValue().equals(WASTED_C_SELECTED_VALUE);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
