/**
 * ExpenditureId(支出コード)のテストクラスです。
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
 * ExpenditureId(支出コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("支出コード(ExpenditureId)のテスト")
class ExpenditureIdTest {

	@Test
	@DisplayName("正常系：有効な支出コードで生成できる")
	void testFrom_正常系_有効な支出コード() {
		// 実行
		ExpenditureId expenditureId = ExpenditureId.from("EXP001");

		// 検証
		assertNotNull(expenditureId);
		assertEquals("EXP001", expenditureId.getValue());
		assertEquals("EXP001", expenditureId.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ExpenditureId.from(null)
		);
		assertTrue(exception.getMessage().contains("支出コード"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ExpenditureId.from("")
		);
		assertTrue(exception.getMessage().contains("支出コード"));
		assertTrue(exception.getMessage().contains("空文字"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		ExpenditureId expenditureId1 = ExpenditureId.from("EXP001");
		ExpenditureId expenditureId2 = ExpenditureId.from("EXP001");
		ExpenditureId expenditureId3 = ExpenditureId.from("EXP002");

		// 検証
		assertEquals(expenditureId1, expenditureId2);
		assertNotEquals(expenditureId1, expenditureId3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		ExpenditureId expenditureId1 = ExpenditureId.from("EXP001");
		ExpenditureId expenditureId2 = ExpenditureId.from("EXP001");

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(expenditureId1.hashCode(), expenditureId2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		ExpenditureId expenditureId = ExpenditureId.from("EXP001");

		// 検証
		assertEquals("EXP001", expenditureId.toString());
	}
}
