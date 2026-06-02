/**
 * IncomeItemFormの単体テストクラスです。
 *
 * <pre>
 * IncomeItemForm の以下メソッドをテストします。
 *
 * [対象メソッド]相関チェック
 * 1. isNeedCheckIncomeDetailContext - 収入区分その他任意(9)と収入詳細の相関チェック
 *
 * [テストシナリオ]
 * ① 正常系：incomeKubun=9 + incomeDetailContext設定あり → true（チェックOK）
 * ② 正常系：incomeKubun=9以外 + incomeDetailContext設定なし → true（チェックOK）
 * ③ 異常系：incomeKubun=9 + incomeDetailContext設定なし → false（チェックNG）
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/28 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.regist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * IncomeItemFormの単体テストクラスです。
 * isNeedCheckIncomeDetailContext()の相関バリデーションロジックを検証します。
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@DisplayName("IncomeItemForm 単体テスト")
class IncomeItemFormTest {
	
	// ========== isNeedCheckIncomeDetailContext ==========
	
	/**
	 *<pre>
	 * テスト①：正常系：incomeKubun=9 + incomeDetailContext設定あり → true
	 *
	 * 【検証内容】
	 * ・収入区分='9'(その他任意)かつ収入詳細に値がある場合、trueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("① incomeKubun=9 + incomeDetailContext設定あり → true（チェックOK）")
	void testIsNeedCheck_valid_9_withContext() {
		IncomeItemForm form = new IncomeItemForm();
		form.setIncomeKubun("9");
		form.setIncomeDetailContext("不定期の支払");

		assertTrue(form.isNeedCheckIncomeDetailContext(),
				"incomeKubun=9かつincomeDetailContext設定ありはtrueであること");
	}
	
	/**
	 *<pre>
	 * テスト②：正常系：incomeKubun=9以外 + incomeDetailContext設定なし → true
	 *
	 * 【検証内容】
	 * ・収入区分='9'以外(給料=1)かつ収入詳細がnullの場合、trueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("① incomeKubun=9 + incomeDetailContext設定あり → true（チェックOK）")
	void testIsNeedCheck_nonOptional_withoutContext() {
		IncomeItemForm form = new IncomeItemForm();
		form.setIncomeKubun("1");
		// incomeDetailContext:収入詳細は収入区分がその他任意(9)以外の場合、nullでもOKのためnullをセット
		assertTrue(form.isNeedCheckIncomeDetailContext(),
				"incomeKubun=9以外かつincomeDetailContextなしはtrueであること");
	}

	/**
	 *<pre>
	 * テスト③：異常系：incomeKubun=9 + incomeDetailContext空 → false
	 *
	 * 【検証内容】
	 * ・収入区分='9'(その他任意)かつ収入詳細が空文字の場合、falseを返すこと（バリデーションNG）
	 * ・StringUtils.hasLength(空文字)=falseのため空として扱われること
	 *</pre>
	 */
	@Test
	@DisplayName("③ incomeKubun=9 + incomeDetailContext空(空文字) → false（チェックNG）")
	void testIsNeedCheck_invalid_9_withoutContext() {
		IncomeItemForm form = new IncomeItemForm();
		form.setIncomeKubun("9");
		form.setIncomeDetailContext("");

		assertFalse(form.isNeedCheckIncomeDetailContext(),
				"shiharaiTuki=9かつincomeDetailContext空はfalseであること");
	}	
}
