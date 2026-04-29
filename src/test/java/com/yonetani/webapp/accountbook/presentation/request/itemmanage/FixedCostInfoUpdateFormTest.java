/**
 * FixedCostInfoUpdateFormの単体テストクラスです。
 *
 * <pre>
 * FixedCostInfoUpdateForm の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. isNeedCheckShiharaiTukiOptionalContext - 支払月その他任意(40)と支払月任意詳細の相関チェック
 *
 * [テストシナリオ]
 * ① 正常系：shiharaiTuki=40 + shiharaiTukiOptionalContext設定あり → true（チェックOK）
 * ② 正常系：shiharaiTuki=40以外 + shiharaiTukiOptionalContext設定なし → true（チェックOK）
 * ③ 異常系：shiharaiTuki=40 + shiharaiTukiOptionalContext空 → false（チェックNG）
 * ④ 異常系：shiharaiTuki=40以外 + shiharaiTukiOptionalContext設定あり → false（チェックNG）
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/26 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * FixedCostInfoUpdateFormの単体テストクラスです。
 * isNeedCheckShiharaiTukiOptionalContext()の相関バリデーションロジックを検証します。
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@DisplayName("FixedCostInfoUpdateForm 単体テスト")
class FixedCostInfoUpdateFormTest {

	// ========== isNeedCheckShiharaiTukiOptionalContext ==========

	/**
	 *<pre>
	 * テスト①：正常系：shiharaiTuki=40 + OptionalContext設定あり → true
	 *
	 * 【検証内容】
	 * ・支払月='40'(その他任意)かつ支払月任意詳細に値がある場合、trueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("① shiharaiTuki=40 + OptionalContext設定あり → true（チェックOK）")
	void testIsNeedCheck_valid_40_withContext() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setShiharaiTuki("40");
		form.setShiharaiTukiOptionalContext("不定期の支払");

		assertTrue(form.isNeedCheckShiharaiTukiOptionalContext(),
				"shiharaiTuki=40かつOptionalContext設定ありはtrueであること");
	}

	/**
	 *<pre>
	 * テスト②：正常系：shiharaiTuki=40以外 + OptionalContext設定なし → true
	 *
	 * 【検証内容】
	 * ・支払月='40'以外(毎月='00')かつ支払月任意詳細がnullの場合、trueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("② shiharaiTuki=40以外(00) + OptionalContext設定なし → true（チェックOK）")
	void testIsNeedCheck_valid_nonOptional_withoutContext() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setShiharaiTuki("00");
		form.setShiharaiTukiOptionalContext(null);

		assertTrue(form.isNeedCheckShiharaiTukiOptionalContext(),
				"shiharaiTuki=40以外かつOptionalContextなしはtrueであること");
	}

	/**
	 *<pre>
	 * テスト③：異常系：shiharaiTuki=40 + OptionalContext空 → false
	 *
	 * 【検証内容】
	 * ・支払月='40'(その他任意)かつ支払月任意詳細が空文字の場合、falseを返すこと（バリデーションNG）
	 * ・StringUtils.hasLength(空文字)=falseのため空として扱われること
	 *</pre>
	 */
	@Test
	@DisplayName("③ shiharaiTuki=40 + OptionalContext空(空文字) → false（チェックNG）")
	void testIsNeedCheck_invalid_40_withoutContext() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setShiharaiTuki("40");
		form.setShiharaiTukiOptionalContext("");

		assertFalse(form.isNeedCheckShiharaiTukiOptionalContext(),
				"shiharaiTuki=40かつOptionalContext空はfalseであること");
	}

	/**
	 *<pre>
	 * テスト④：異常系：shiharaiTuki=40以外 + OptionalContext設定あり → false
	 *
	 * 【検証内容】
	 * ・支払月='40'以外(毎月='00')かつ支払月任意詳細に値がある場合、falseを返すこと（バリデーションNG）
	 * ・支払月任意詳細はその他任意(40)選択時のみ入力可能なため、矛盾する組み合わせとして弾かれること
	 *</pre>
	 */
	@Test
	@DisplayName("④ shiharaiTuki=40以外(00) + OptionalContext設定あり → false（チェックNG）")
	void testIsNeedCheck_invalid_nonOptional_withContext() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setShiharaiTuki("00");
		form.setShiharaiTukiOptionalContext("不定期の支払");

		assertFalse(form.isNeedCheckShiharaiTukiOptionalContext(),
				"shiharaiTuki=40以外かつOptionalContext設定ありはfalseであること");
	}
}
