/**
 * ShoppingRegistCodeクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ShoppingRegistCodeクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("買い物登録コード(ShoppingRegistCode)のテスト")
class ShoppingRegistCodeTest {

	@Test
	@DisplayName("正常系：文字列から生成")
	void testFrom_正常系_文字列から生成() {
		ShoppingRegistCode code = ShoppingRegistCode.from("001");
		// 検証
		assertNotNull(code);
		assertEquals("001", code.getValue());
		assertEquals("001", code.toString());
	}

	@Test
	@DisplayName("正常系：数値(発番用)から生成")
	void testFrom_正常系_数値から生成() {
		ShoppingRegistCode code = ShoppingRegistCode.from(3);
		// 検証
		assertEquals("003", code.getValue());
		assertEquals("003", ShoppingRegistCode.getNewCode(3));
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from(""));
	}

	@Test
	@DisplayName("異常系：3桁でない場合に例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from("12"));
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from("0001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from("abc"));
	}

	@Test
	@DisplayName("異常系：発番用の数値が0以下の場合に例外が発生する")
	void testFrom_異常系_数値が0以下() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from(0));
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from(-1));
	}

	@Test
	@DisplayName("異常系：発番用の数値が1000以上の場合に例外が発生する")
	void testFrom_異常系_数値が1000以上() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ShoppingRegistCode.from(1000));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		ShoppingRegistCode code1 = ShoppingRegistCode.from("001");
		ShoppingRegistCode code2 = ShoppingRegistCode.from("001");
		// 検証
		assertEquals(code1, code2);
		assertEquals(code1.hashCode(), code2.hashCode());
	}
}
