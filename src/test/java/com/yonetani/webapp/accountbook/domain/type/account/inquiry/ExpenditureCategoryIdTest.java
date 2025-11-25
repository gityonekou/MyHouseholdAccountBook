/**
 * ExpenditureCategoryId(支出項目コード)のテストクラスです。
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
 * ExpenditureCategoryId(支出項目コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("支出項目コード(ExpenditureCategoryId)のテスト")
class ExpenditureCategoryIdTest {

	@Test
	@DisplayName("正常系：有効な支出項目コードで生成できる")
	void testFrom_正常系_有効な支出項目コード() {
		// 実行
		ExpenditureCategoryId categoryId = ExpenditureCategoryId.from("CAT001");

		// 検証
		assertNotNull(categoryId);
		assertEquals("CAT001", categoryId.getValue());
		assertEquals("CAT001", categoryId.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ExpenditureCategoryId.from(null)
		);
		assertTrue(exception.getMessage().contains("支出項目コード"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ExpenditureCategoryId.from("")
		);
		assertTrue(exception.getMessage().contains("支出項目コード"));
		assertTrue(exception.getMessage().contains("空文字"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		ExpenditureCategoryId categoryId1 = ExpenditureCategoryId.from("CAT001");
		ExpenditureCategoryId categoryId2 = ExpenditureCategoryId.from("CAT001");
		ExpenditureCategoryId categoryId3 = ExpenditureCategoryId.from("CAT002");

		// 検証
		assertEquals(categoryId1, categoryId2);
		assertNotEquals(categoryId1, categoryId3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		ExpenditureCategoryId categoryId1 = ExpenditureCategoryId.from("CAT001");
		ExpenditureCategoryId categoryId2 = ExpenditureCategoryId.from("CAT001");

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(categoryId1.hashCode(), categoryId2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		ExpenditureCategoryId categoryId = ExpenditureCategoryId.from("CAT001");

		// 検証
		assertEquals("CAT001", categoryId.toString());
	}
}
