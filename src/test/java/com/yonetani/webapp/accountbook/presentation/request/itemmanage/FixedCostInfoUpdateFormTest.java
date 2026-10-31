/**
 * FixedCostInfoUpdateFormの単体テストクラスです。
 *
 * <pre>
 * FixedCostInfoUpdateForm の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. isNeedCheckShiharaiTukiOptionalContext - 支払月その他任意(40)と支払月任意詳細の相関チェック
 * 2. isValidShiharaiKingakuForKubun - 固定費区分が予定支払い金額(2)の場合、支払金額0円不可の相関チェック
 * 3. isPaymentMethodCodeValid - 支払方法コードがシステム予約値の場合、買い物集計8項目専用固定費にのみ許容する相関チェック
 *
 * [テストシナリオ(isNeedCheckShiharaiTukiOptionalContext)]
 * ① 正常系：shiharaiTuki=40 + shiharaiTukiOptionalContext設定あり → true（チェックOK）
 * ② 正常系：shiharaiTuki=40以外 + shiharaiTukiOptionalContext設定なし → true（チェックOK）
 * ③ 異常系：shiharaiTuki=40 + shiharaiTukiOptionalContext空 → false（チェックNG）
 * ④ 異常系：shiharaiTuki=40以外 + shiharaiTukiOptionalContext設定あり → false（チェックNG）
 *
 * [テストシナリオ(isValidShiharaiKingakuForKubun)]
 * ⑤ 正常系：fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=1円以上 → true（チェックOK）
 * ⑥ 正常系：fixedCostKubun=1(支払い金額確定) + shiharaiKingaku=0円 → true（チェックOK。予定支払い金額以外は0円許容）
 * ⑦ 異常系：fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=0円 → false（チェックNG）
 * ⑧ 正常系：fixedCostKubun未入力(空文字) → true（@NotBlank側で検出するためここではチェック対象外）
 * ⑨ 正常系：fixedCostKubunが1,2以外の不正値 → true（tryFrom()がOptional.empty()を返すためここではチェック対象外）
 * ⑩ 正常系：fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=null → true（tryFrom()がOptional.empty()を返すためここではチェック対象外。@NotNull側で検出）
 * ⑪ 正常系：fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=マイナス値 → true（tryFrom()がOptional.empty()を返すためここではチェック対象外。@Min側で検出）
 *
 * [テストシナリオ(isPaymentMethodCodeValid)]
 * ⑫ 正常系：paymentMethodCode=通常値(999未満) → true（システム予約値以外は判定不要）
 * ⑬ 正常系：paymentMethodCode=999(システム予約値) + 買い物集計8項目に一致 → true（チェックOK）
 * ⑭ 異常系：paymentMethodCode=999(システム予約値) + 買い物集計8項目に不一致 → false（チェックNG）
 * ⑮ 正常系：paymentMethodCode/sisyutuItemCode/fixedCostNameのいずれかが未入力(空文字) → true（@NotBlank側で検出するためここではチェック対象外）
 * ⑯ 正常系：paymentMethodCodeが不正な形式値 → true（tryFrom()がOptional.empty()を返すためここではチェック対象外）
 * ⑰ 正常系：sisyutuItemCodeが不正な形式値（paymentMethodCode=999） → true（tryFrom()がOptional.empty()を返すためここではチェック対象外）
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/04/26 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/10/31 : 1.01.00  feature-1.03-dev1   isValidShiharaiKingakuForKubun()・isPaymentMethodCodeValid()のテストを追加(ドメイン層(FixedCostKubun/FixedCostPaymentAmount/FixedCostName/ExpenditureItemCode)移動対応)
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
 * @since 家計簿アプリ(1.00)
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

	// ========== isValidShiharaiKingakuForKubun ==========

	/**
	 *<pre>
	 * テスト⑤：正常系：fixedCostKubun=2 + shiharaiKingaku=1円以上 → true
	 *
	 * 【検証内容】
	 * ・固定費区分='2'(予定支払い金額)かつ支払金額が1円以上の場合、trueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=1 → true（チェックOK）")
	void testIsValidShiharaiKingakuForKubun_valid_estimate_withAmount() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("2");
		form.setShiharaiKingaku(1);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubun=2かつshiharaiKingaku=1はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑥：正常系：fixedCostKubun=1 + shiharaiKingaku=0円 → true
	 *
	 * 【検証内容】
	 * ・固定費区分='1'(支払い金額確定)の場合、支払金額が0円でもtrueを返すこと（バリデーションOK）
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ fixedCostKubun=1(支払い金額確定) + shiharaiKingaku=0 → true（チェックOK）")
	void testIsValidShiharaiKingakuForKubun_valid_fix_zeroAmount() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("1");
		form.setShiharaiKingaku(0);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubun=1かつshiharaiKingaku=0はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑦：異常系：fixedCostKubun=2 + shiharaiKingaku=0円 → false
	 *
	 * 【検証内容】
	 * ・固定費区分='2'(予定支払い金額)かつ支払金額が0円の場合、falseを返すこと（バリデーションNG）
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=0 → false（チェックNG）")
	void testIsValidShiharaiKingakuForKubun_invalid_estimate_zeroAmount() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("2");
		form.setShiharaiKingaku(0);

		assertFalse(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubun=2かつshiharaiKingaku=0はfalseであること");
	}

	/**
	 *<pre>
	 * テスト⑧：正常系：fixedCostKubun未入力(空文字) → true
	 *
	 * 【検証内容】
	 * ・fixedCostKubunが未入力(空文字)の場合、ここではチェックせずtrueを返すこと(@NotBlank側で検出するため)
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ fixedCostKubun未入力(空文字) → true（@NotBlank側で検出するためチェック対象外）")
	void testIsValidShiharaiKingakuForKubun_valid_blank() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("");
		form.setShiharaiKingaku(0);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubunが未入力の場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑨：正常系：fixedCostKubunが1,2以外の不正値 → true
	 *
	 * 【検証内容】
	 * ・fixedCostKubunが1,2以外の不正値の場合、tryFrom()がOptional.empty()を返すため、
	 *   ここではチェックせずtrueを返すこと(例外を発生させて500エラーにしないこと)
	 *</pre>
	 */
	@Test
	@DisplayName("⑨ fixedCostKubunが1,2以外の不正値 → true（tryFrom()でOptional.empty()のためチェック対象外）")
	void testIsValidShiharaiKingakuForKubun_valid_invalidValue() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("9");
		form.setShiharaiKingaku(0);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubunが不正値の場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑩：正常系：fixedCostKubun=2 + shiharaiKingaku=null → true
	 *
	 * 【検証内容】
	 * ・固定費区分='2'(予定支払い金額)かつ支払金額がnullの場合、FixedCostPaymentAmount.tryFrom()が
	 *   Optional.empty()を返すため、ここではチェックせずtrueを返すこと(@NotNull側で検出するため)
	 *</pre>
	 */
	@Test
	@DisplayName("⑩ fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=null → true（@NotNull側で検出するためチェック対象外）")
	void testIsValidShiharaiKingakuForKubun_valid_nullAmount() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("2");
		form.setShiharaiKingaku(null);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubun=2かつshiharaiKingaku=nullはtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑪：正常系：fixedCostKubun=2 + shiharaiKingaku=マイナス値 → true
	 *
	 * 【検証内容】
	 * ・固定費区分='2'(予定支払い金額)かつ支払金額がマイナス値の場合、FixedCostPaymentAmount.tryFrom()が
	 *   Optional.empty()を返すため、ここではチェックせずtrueを返すこと(@Min側で検出するため)
	 *</pre>
	 */
	@Test
	@DisplayName("⑪ fixedCostKubun=2(予定支払い金額) + shiharaiKingaku=マイナス値 → true（@Min側で検出するためチェック対象外）")
	void testIsValidShiharaiKingakuForKubun_valid_negativeAmount() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setFixedCostKubun("2");
		form.setShiharaiKingaku(-1);

		assertTrue(form.isValidShiharaiKingakuForKubun(),
				"fixedCostKubun=2かつshiharaiKingaku=マイナス値はtrueであること");
	}

	// ========== isPaymentMethodCodeValid ==========

	/**
	 *<pre>
	 * テスト⑫：正常系：paymentMethodCode=通常値(999未満) → true
	 *
	 * 【検証内容】
	 * ・支払方法コードがシステム予約値(990〜999)未満の通常値の場合、他の項目に関わらずtrueを返すこと
	 *</pre>
	 */
	@Test
	@DisplayName("⑫ paymentMethodCode=通常値(001) → true（システム予約値以外は判定不要）")
	void testIsPaymentMethodCodeValid_valid_normalCode() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("001");
		form.setSisyutuItemCode("0001");
		form.setFixedCostName("家賃");

		assertTrue(form.isPaymentMethodCodeValid(),
				"paymentMethodCodeが通常値の場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑬：正常系：paymentMethodCode=999(システム予約値) + 買い物集計8項目に一致 → true
	 *
	 * 【検証内容】
	 * ・支払方法コードがシステム予約値(999)かつ支出項目コード+固定費名から導出される支出区分の組み合わせが
	 *   買い物集計8項目(食費(0051)×無駄遣いなし)に一致する場合、trueを返すこと
	 *</pre>
	 */
	@Test
	@DisplayName("⑬ paymentMethodCode=999 + 買い物集計8項目に一致(食費) → true（チェックOK）")
	void testIsPaymentMethodCodeValid_valid_reservedAndSatisfied() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("999");
		form.setSisyutuItemCode("0051");
		form.setFixedCostName("食費概算");

		assertTrue(form.isPaymentMethodCodeValid(),
				"買い物集計8項目に一致する場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑭：異常系：paymentMethodCode=999(システム予約値) + 買い物集計8項目に不一致 → false
	 *
	 * 【検証内容】
	 * ・支払方法コードがシステム予約値(999)かつ支出項目コード+固定費名から導出される支出区分の組み合わせが
	 *   買い物集計8項目に一致しない場合、falseを返すこと
	 *</pre>
	 */
	@Test
	@DisplayName("⑭ paymentMethodCode=999 + 買い物集計8項目に不一致(家賃) → false（チェックNG）")
	void testIsPaymentMethodCodeValid_invalid_reservedAndNotSatisfied() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("999");
		form.setSisyutuItemCode("0001");
		form.setFixedCostName("家賃");

		assertFalse(form.isPaymentMethodCodeValid(),
				"買い物集計8項目に一致しない場合はfalseであること");
	}

	/**
	 *<pre>
	 * テスト⑮：正常系：paymentMethodCode/sisyutuItemCode/fixedCostNameのいずれかが未入力(空文字) → true
	 *
	 * 【検証内容】
	 * ・3項目のいずれかが未入力(空文字)の場合、ここではチェックせずtrueを返すこと(@NotBlank側で検出するため)
	 *</pre>
	 */
	@Test
	@DisplayName("⑮ paymentMethodCode未入力(空文字) → true（@NotBlank側で検出するためチェック対象外）")
	void testIsPaymentMethodCodeValid_valid_blank() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("");
		form.setSisyutuItemCode("0051");
		form.setFixedCostName("食費概算");

		assertTrue(form.isPaymentMethodCodeValid(),
				"paymentMethodCodeが未入力の場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑯：正常系：paymentMethodCodeが不正な形式値 → true
	 *
	 * 【検証内容】
	 * ・paymentMethodCodeが不正な形式値(3桁でない)の場合、PaymentMethodCode.tryFrom()が
	 *   Optional.empty()を返すため、ここではチェックせずtrueを返すこと(例外を発生させて500エラーにしないこと)
	 *</pre>
	 */
	@Test
	@DisplayName("⑯ paymentMethodCodeが不正な形式値(2桁) → true（tryFrom()でOptional.empty()のためチェック対象外）")
	void testIsPaymentMethodCodeValid_valid_invalidPaymentMethodCode() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("12");
		form.setSisyutuItemCode("0051");
		form.setFixedCostName("食費概算");

		assertTrue(form.isPaymentMethodCodeValid(),
				"paymentMethodCodeが不正な形式値の場合はtrueであること");
	}

	/**
	 *<pre>
	 * テスト⑰：正常系：sisyutuItemCodeが不正な形式値（paymentMethodCode=999） → true
	 *
	 * 【検証内容】
	 * ・paymentMethodCodeがシステム予約値でも、sisyutuItemCodeが不正な形式値(4桁でない)の場合、
	 *   ExpenditureItemCode.tryFrom()がOptional.empty()を返すため、
	 *   ここではチェックせずtrueを返すこと(例外を発生させて500エラーにしないこと)
	 *</pre>
	 */
	@Test
	@DisplayName("⑰ paymentMethodCode=999 + sisyutuItemCodeが不正な形式値(3桁) → true（tryFrom()でOptional.empty()のためチェック対象外）")
	void testIsPaymentMethodCodeValid_valid_invalidSisyutuItemCode() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setPaymentMethodCode("999");
		form.setSisyutuItemCode("005");
		form.setFixedCostName("食費概算");

		assertTrue(form.isPaymentMethodCodeValid(),
				"sisyutuItemCodeが不正な形式値の場合はtrueであること");
	}
}
