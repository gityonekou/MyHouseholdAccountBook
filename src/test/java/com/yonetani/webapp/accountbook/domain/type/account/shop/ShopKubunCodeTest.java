/**
 * ShopKubunCode(店舗区分コード)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/11/01 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ShopKubunCode(店舗区分コード)のテストクラスです。
 *
 * [対象メソッド]
 * 1. from                      - 「店舗区分コード」項目の値を表すドメインタイプを生成する
 * 2. tryFrom                   - from()の生成を試み、不正な値の場合は例外を発生させずOptional.empty()を返す
 * 3. requiresConsumerGoodsAmount - 日用品への金額入力を必須とする区分(薬局/薬局複合店/病院、または理髪店)かどうかを判定する
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("店舗区分コード(ShopKubunCode)のテスト")
class ShopKubunCodeTest {

	@Test
	@DisplayName("正常系：有効な店舗区分コード(3桁)で生成できる")
	void testFrom_正常系_有効な値() {
		ShopKubunCode code = ShopKubunCode.from("901");
		assertNotNull(code);
		assertEquals("901", code.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShopKubunCode.from(null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShopKubunCode.from(""));
	}

	@Test
	@DisplayName("異常系：3桁でない(2桁)で例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShopKubunCode.from("90"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShopKubunCode.from("ABC"));
	}

	@Test
	@DisplayName("正常系：tryFromは有効な値で値ありのOptionalを返す")
	void testTryFrom_正常系() {
		Optional<ShopKubunCode> code = ShopKubunCode.tryFrom("901");
		assertTrue(code.isPresent());
		assertEquals("901", code.get().getValue());
	}

	@Test
	@DisplayName("異常系：tryFromはnull値・桁数不正・数値変換不可でOptional.empty()を返す")
	void testTryFrom_異常系() {
		assertTrue(ShopKubunCode.tryFrom(null).isEmpty());
		assertTrue(ShopKubunCode.tryFrom("90").isEmpty());
		assertTrue(ShopKubunCode.tryFrom("ABC").isEmpty());
	}

	@Test
	@DisplayName("正常系：requiresConsumerGoodsAmountは薬局/薬局複合店/病院(905)の場合trueを返す")
	void testRequiresConsumerGoodsAmount_正常系_医療系() {
		assertTrue(ShopKubunCode.from("905").requiresConsumerGoodsAmount());
	}

	@Test
	@DisplayName("正常系：requiresConsumerGoodsAmountは理髪店(908)の場合trueを返す")
	void testRequiresConsumerGoodsAmount_正常系_理髪店() {
		assertTrue(ShopKubunCode.from("908").requiresConsumerGoodsAmount());
	}

	@Test
	@DisplayName("正常系：requiresConsumerGoodsAmountは対象外の区分(901)の場合falseを返す")
	void testRequiresConsumerGoodsAmount_正常系_対象外() {
		assertFalse(ShopKubunCode.from("901").requiresConsumerGoodsAmount());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ShopKubunCode code1 = ShopKubunCode.from("901");
		ShopKubunCode code2 = ShopKubunCode.from("901");
		ShopKubunCode code3 = ShopKubunCode.from("905");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}
}
