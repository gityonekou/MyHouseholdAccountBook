/**
 * FixedCostName(固定費名(支払名))のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/10/31 : 1.00.00  feature-1.03-dev1   新規作成
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
 * FixedCostName(固定費名(支払名))のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("固定費名(支払名)(FixedCostName)のテスト")
class FixedCostNameTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		FixedCostName name = FixedCostName.from("家賃");
		assertNotNull(name);
		assertEquals("家賃", name.getValue());
		assertEquals("家賃", name.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> FixedCostName.from(null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> FixedCostName.from(""));
	}

	@Test
	@DisplayName("正常系：tryFromは通常の文字列で値ありのOptionalを返す")
	void testTryFrom_正常系() {
		Optional<FixedCostName> name = FixedCostName.tryFrom("家賃");
		assertTrue(name.isPresent());
		assertEquals("家賃", name.get().getValue());
	}

	@Test
	@DisplayName("異常系：tryFromはnull値・空文字でOptional.empty()を返す")
	void testTryFrom_異常系() {
		assertTrue(FixedCostName.tryFrom(null).isEmpty());
		assertTrue(FixedCostName.tryFrom("").isEmpty());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		FixedCostName name1 = FixedCostName.from("家賃");
		FixedCostName name2 = FixedCostName.from("家賃");
		FixedCostName name3 = FixedCostName.from("電気代");
		assertEquals(name1, name2);
		assertNotEquals(name1, name3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		FixedCostName name1 = FixedCostName.from("家賃");
		FixedCostName name2 = FixedCostName.from("家賃");
		assertEquals(name1.hashCode(), name2.hashCode());
	}
}
