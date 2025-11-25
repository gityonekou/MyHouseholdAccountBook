/**
 * IncomeId(収入コード)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * IncomeId(収入コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("収入コード(IncomeId)のテスト")
class IncomeIdTest {

	@Test
	@DisplayName("正常系：有効な収入コードで生成できる")
	void testFrom_正常系_有効な収入コード() {
		// 実行
		IncomeId incomeId = IncomeId.from("INC001");

		// 検証
		assertNotNull(incomeId);
		assertEquals("INC001", incomeId.getValue());
		assertEquals("INC001", incomeId.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeId.from(null)
		);
		assertTrue(exception.getMessage().contains("収入コード"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeId.from("")
		);
		assertTrue(exception.getMessage().contains("収入コード"));
		assertTrue(exception.getMessage().contains("空文字"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		IncomeId incomeId1 = IncomeId.from("INC001");
		IncomeId incomeId2 = IncomeId.from("INC001");
		IncomeId incomeId3 = IncomeId.from("INC002");

		// 検証
		assertEquals(incomeId1, incomeId2);
		assertNotEquals(incomeId1, incomeId3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		IncomeId incomeId1 = IncomeId.from("INC001");
		IncomeId incomeId2 = IncomeId.from("INC001");

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(incomeId1.hashCode(), incomeId2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		IncomeId incomeId = IncomeId.from("INC001");

		// 検証
		assertEquals("INC001", incomeId.toString());
	}
}
