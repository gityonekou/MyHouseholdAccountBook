/**
 * FixedCostKubun の単体テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/10/25 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * FixedCostKubun の単体テストクラスです。
 *
 * [対象メソッド]
 * 1. from              - 「固定費区分」項目の値を表すドメインタイプを生成する(1 or 2以外はガード節で例外)
 * 2. tryFrom           - from()の生成を試み、不正な値の場合は例外を発生させずOptional.empty()を返す
 * 3. isClearStart      - 固定費区分が「予定支払い金額(2)」かどうかを判定する
 * 4. requiresPositiveAmount - 固定費区分が「予定支払い金額(2)」かどうかを判定する(isClearStartと条件は同じだが別の業務目的を表す)
 *
 * [テストシナリオ]
 *   ① from: 1(支払い金額確定) → 生成できる
 *   ② from: 2(予定支払い金額) → 生成できる
 *   ③ from: 1,2以外 → 例外発生
 *   ④ tryFrom: 1,2 → 値ありのOptional
 *   ⑤ tryFrom: 1,2以外 → Optional.empty()
 *   ⑥ isClearStart: 2 → true、1 → false
 *   ⑦ requiresPositiveAmount: 2 → true、1 → false
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("FixedCostKubun の C1 網羅単体テスト")
class FixedCostKubunTest {

	/**
	 *<pre>
	 * テスト①：from: 1(支払い金額確定) → 生成できる
	 *</pre>
	 */
	@Test
	@DisplayName("① from: 1(支払い金額確定) → 生成できる")
	void testFrom_正常系_支払い金額確定() {
		FixedCostKubun kubun = FixedCostKubun.from("1");
		assertNotNull(kubun);
		assertEquals("1", kubun.getValue());
		assertEquals("1", kubun.toString());
	}

	/**
	 *<pre>
	 * テスト②：from: 2(予定支払い金額) → 生成できる
	 *</pre>
	 */
	@Test
	@DisplayName("② from: 2(予定支払い金額) → 生成できる")
	void testFrom_正常系_予定支払い金額() {
		FixedCostKubun kubun = FixedCostKubun.from("2");
		assertNotNull(kubun);
		assertEquals("2", kubun.getValue());
	}

	/**
	 *<pre>
	 * テスト③：from: 1,2以外 → 例外発生
	 *</pre>
	 */
	@Test
	@DisplayName("③ from: 1,2以外(不正値) → MyHouseholdAccountBookRuntimeExceptionが発生する")
	void testFrom_異常系_不正値() {
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> FixedCostKubun.from("9"));
		assertTrue(exception.getMessage().contains("固定費区分"));
	}

	/**
	 *<pre>
	 * テスト④：tryFrom: 1,2 → 値ありのOptional
	 *</pre>
	 */
	@Test
	@DisplayName("④ tryFrom: 1,2 → 値ありのOptionalを返す")
	void testTryFrom_正常系() {
		Optional<FixedCostKubun> kubun1 = FixedCostKubun.tryFrom("1");
		Optional<FixedCostKubun> kubun2 = FixedCostKubun.tryFrom("2");
		assertTrue(kubun1.isPresent());
		assertEquals("1", kubun1.get().getValue());
		assertTrue(kubun2.isPresent());
		assertEquals("2", kubun2.get().getValue());
	}

	/**
	 *<pre>
	 * テスト⑤：tryFrom: 1,2以外 → Optional.empty()
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ tryFrom: 1,2以外(不正値・null) → Optional.empty()を返す")
	void testTryFrom_異常系_不正値() {
		assertTrue(FixedCostKubun.tryFrom("9").isEmpty());
		assertTrue(FixedCostKubun.tryFrom(null).isEmpty());
	}

	/**
	 *<pre>
	 * テスト⑥：isClearStart: 2 → true、1 → false
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ isClearStart: 2(予定支払い金額)はtrue、1(支払い金額確定)はfalse")
	void testIsClearStart() {
		assertTrue(FixedCostKubun.from("2").isClearStart(), "予定支払い金額(2)はtrue");
		assertFalse(FixedCostKubun.from("1").isClearStart(), "支払い金額確定(1)はfalse");
	}

	/**
	 *<pre>
	 * テスト⑦：requiresPositiveAmount: 2 → true、1 → false
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ requiresPositiveAmount: 2(予定支払い金額)はtrue、1(支払い金額確定)はfalse")
	void testRequiresPositiveAmount() {
		assertTrue(FixedCostKubun.from("2").requiresPositiveAmount(), "予定支払い金額(2)はtrue");
		assertFalse(FixedCostKubun.from("1").requiresPositiveAmount(), "支払い金額確定(1)はfalse");
	}

	/**
	 *<pre>
	 * テスト⑧：equalsが正しく動作する
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ 正常系：equalsが正しく動作する")
	void testEquals() {
		FixedCostKubun kubun1 = FixedCostKubun.from("1");
		FixedCostKubun kubun2 = FixedCostKubun.from("1");
		FixedCostKubun kubun3 = FixedCostKubun.from("2");
		assertEquals(kubun1, kubun2);
		assertNotEquals(kubun1, kubun3);
	}
}
