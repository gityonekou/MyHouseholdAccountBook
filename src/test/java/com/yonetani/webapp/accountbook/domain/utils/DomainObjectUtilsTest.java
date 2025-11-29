/**
 * DomainObjectUtilsのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * DomainObjectUtilsのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class DomainObjectUtilsTest {

	// ========================================
	// 文字列関連のテスト
	// ========================================

	@Test
	@DisplayName("正常系：isEmpty - nullの場合true")
	void testIsEmpty_Null() {
		assertTrue(DomainObjectUtils.isEmpty(null));
	}

	@Test
	@DisplayName("正常系：isEmpty - 空文字列の場合true")
	void testIsEmpty_EmptyString() {
		assertTrue(DomainObjectUtils.isEmpty(""));
	}

	@Test
	@DisplayName("正常系：isEmpty - 空白のみの場合false")
	void testIsEmpty_Whitespace() {
		assertFalse(DomainObjectUtils.isEmpty(" "));
	}

	@Test
	@DisplayName("正常系：isEmpty - 通常の文字列の場合false")
	void testIsEmpty_NormalString() {
		assertFalse(DomainObjectUtils.isEmpty("test"));
	}

	@Test
	@DisplayName("正常系：isNotEmpty - nullの場合false")
	void testIsNotEmpty_Null() {
		assertFalse(DomainObjectUtils.isNotEmpty(null));
	}

	@Test
	@DisplayName("正常系：isNotEmpty - 通常の文字列の場合true")
	void testIsNotEmpty_NormalString() {
		assertTrue(DomainObjectUtils.isNotEmpty("test"));
	}

	@Test
	@DisplayName("正常系：isBlank - nullの場合true")
	void testIsBlank_Null() {
		assertTrue(DomainObjectUtils.isBlank(null));
	}

	@Test
	@DisplayName("正常系：isBlank - 空文字列の場合true")
	void testIsBlank_EmptyString() {
		assertTrue(DomainObjectUtils.isBlank(""));
	}

	@Test
	@DisplayName("正常系：isBlank - 空白のみの場合true")
	void testIsBlank_Whitespace() {
		assertTrue(DomainObjectUtils.isBlank("   "));
	}

	@Test
	@DisplayName("正常系：isBlank - 通常の文字列の場合false")
	void testIsBlank_NormalString() {
		assertFalse(DomainObjectUtils.isBlank("test"));
	}

	@Test
	@DisplayName("正常系：isNotBlank - nullの場合false")
	void testIsNotBlank_Null() {
		assertFalse(DomainObjectUtils.isNotBlank(null));
	}

	@Test
	@DisplayName("正常系：isNotBlank - 通常の文字列の場合true")
	void testIsNotBlank_NormalString() {
		assertTrue(DomainObjectUtils.isNotBlank("test"));
	}

	@Test
	@DisplayName("正常系：trim - nullの場合null")
	void testTrim_Null() {
		assertNull(DomainObjectUtils.trim(null));
	}

	@Test
	@DisplayName("正常系：trim - 前後に空白がある場合トリム")
	void testTrim_Whitespace() {
		assertEquals("test", DomainObjectUtils.trim("  test  "));
	}

	@Test
	@DisplayName("正常系：trimToNull - nullの場合null")
	void testTrimToNull_Null() {
		assertNull(DomainObjectUtils.trimToNull(null));
	}

	@Test
	@DisplayName("正常系：trimToNull - 空白のみの場合null")
	void testTrimToNull_Whitespace() {
		assertNull(DomainObjectUtils.trimToNull("   "));
	}

	@Test
	@DisplayName("正常系：trimToNull - 通常の文字列の場合トリム")
	void testTrimToNull_NormalString() {
		assertEquals("test", DomainObjectUtils.trimToNull("  test  "));
	}

	@Test
	@DisplayName("正常系：trimToEmpty - nullの場合空文字列")
	void testTrimToEmpty_Null() {
		assertEquals("", DomainObjectUtils.trimToEmpty(null));
	}

	@Test
	@DisplayName("正常系：trimToEmpty - 通常の文字列の場合トリム")
	void testTrimToEmpty_NormalString() {
		assertEquals("test", DomainObjectUtils.trimToEmpty("  test  "));
	}

	@Test
	@DisplayName("正常系：equals(String, String) - 両方nullの場合true")
	void testEquals_String_BothNull() {
		assertTrue(DomainObjectUtils.equals((String) null, (String) null));
	}

	@Test
	@DisplayName("正常系：equals(String, String) - 同じ文字列の場合true")
	void testEquals_String_Same() {
		assertTrue(DomainObjectUtils.equals("test", "test"));
	}

	@Test
	@DisplayName("正常系：equals(String, String) - 異なる文字列の場合false")
	void testEquals_String_Different() {
		assertFalse(DomainObjectUtils.equals("test1", "test2"));
	}

	// ========================================
	// コレクション関連のテスト
	// ========================================

	@Test
	@DisplayName("正常系：isEmptyCollection - nullの場合true")
	void testIsEmptyCollection_Null() {
		assertTrue(DomainObjectUtils.isEmptyCollection(null));
	}

	@Test
	@DisplayName("正常系：isEmptyCollection - 空の場合true")
	void testIsEmptyCollection_Empty() {
		assertTrue(DomainObjectUtils.isEmptyCollection(Collections.emptyList()));
	}

	@Test
	@DisplayName("正常系：isEmptyCollection - 要素がある場合false")
	void testIsEmptyCollection_NotEmpty() {
		assertFalse(DomainObjectUtils.isEmptyCollection(Arrays.asList("test")));
	}

	@Test
	@DisplayName("正常系：isNotEmptyCollection - nullの場合false")
	void testIsNotEmptyCollection_Null() {
		assertFalse(DomainObjectUtils.isNotEmptyCollection(null));
	}

	@Test
	@DisplayName("正常系：isNotEmptyCollection - 要素がある場合true")
	void testIsNotEmptyCollection_NotEmpty() {
		assertTrue(DomainObjectUtils.isNotEmptyCollection(Arrays.asList("test")));
	}

	// ========================================
	// オブジェクト関連のテスト
	// ========================================

	@Test
	@DisplayName("正常系：isNull - nullの場合true")
	void testIsNull_Null() {
		assertTrue(DomainObjectUtils.isNull(null));
	}

	@Test
	@DisplayName("正常系：isNull - オブジェクトがある場合false")
	void testIsNull_NotNull() {
		assertFalse(DomainObjectUtils.isNull(new Object()));
	}

	@Test
	@DisplayName("正常系：isNotNull - nullの場合false")
	void testIsNotNull_Null() {
		assertFalse(DomainObjectUtils.isNotNull(null));
	}

	@Test
	@DisplayName("正常系：isNotNull - オブジェクトがある場合true")
	void testIsNotNull_NotNull() {
		assertTrue(DomainObjectUtils.isNotNull(new Object()));
	}

	@Test
	@DisplayName("正常系：requireNonNull - オブジェクトがある場合そのまま返却")
	void testRequireNonNull_NotNull() {
		String value = "test";
		assertEquals(value, DomainObjectUtils.requireNonNull(value, "テスト値"));
	}

	@Test
	@DisplayName("異常系：requireNonNull - nullの場合例外")
	void testRequireNonNull_Null() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> DomainObjectUtils.requireNonNull(null, "テスト値")
		);
		assertTrue(exception.getMessage().contains("テスト値"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：equals(Object, Object) - 両方nullの場合true")
	void testEquals_Object_BothNull() {
		assertTrue(DomainObjectUtils.equals((Object) null, (Object) null));
	}

	@Test
	@DisplayName("正常系：equals(Object, Object) - 同じオブジェクトの場合true")
	void testEquals_Object_Same() {
		Object obj = new Object();
		assertTrue(DomainObjectUtils.equals(obj, obj));
	}

	@Test
	@DisplayName("正常系：equals(Object, Object) - 異なるオブジェクトの場合false")
	void testEquals_Object_Different() {
		assertFalse(DomainObjectUtils.equals(new Object(), new Object()));
	}

	// ========================================
	// デフォルト値関連のテスト
	// ========================================

	@Test
	@DisplayName("正常系：defaultIfNull - nullの場合デフォルト値")
	void testDefaultIfNull_Null() {
		assertEquals("default", DomainObjectUtils.defaultIfNull(null, "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfNull - オブジェクトがある場合そのまま")
	void testDefaultIfNull_NotNull() {
		assertEquals("value", DomainObjectUtils.defaultIfNull("value", "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfEmpty - nullの場合デフォルト値")
	void testDefaultIfEmpty_Null() {
		assertEquals("default", DomainObjectUtils.defaultIfEmpty(null, "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfEmpty - 空文字列の場合デフォルト値")
	void testDefaultIfEmpty_EmptyString() {
		assertEquals("default", DomainObjectUtils.defaultIfEmpty("", "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfEmpty - 通常の文字列の場合そのまま")
	void testDefaultIfEmpty_NormalString() {
		assertEquals("value", DomainObjectUtils.defaultIfEmpty("value", "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfBlank - nullの場合デフォルト値")
	void testDefaultIfBlank_Null() {
		assertEquals("default", DomainObjectUtils.defaultIfBlank(null, "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfBlank - 空白のみの場合デフォルト値")
	void testDefaultIfBlank_Whitespace() {
		assertEquals("default", DomainObjectUtils.defaultIfBlank("   ", "default"));
	}

	@Test
	@DisplayName("正常系：defaultIfBlank - 通常の文字列の場合そのまま")
	void testDefaultIfBlank_NormalString() {
		assertEquals("value", DomainObjectUtils.defaultIfBlank("value", "default"));
	}

	// ========================================
	// インスタンス化テスト
	// ========================================

	@Test
	@DisplayName("異常系：インスタンス化不可")
	void testConstructor_ThrowsException() {
		// 実行 & 検証
		assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
			// リフレクションでコンストラクタを呼び出そうとする
			java.lang.reflect.Constructor<DomainObjectUtils> constructor =
				DomainObjectUtils.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		});
	}

	// ========================================
	// 複合シナリオのテスト
	// ========================================

	@Test
	@DisplayName("正常系：複合シナリオ - 文字列検証チェーン")
	void testComplexScenario_StringValidationChain() {
		// 準備
		String value = "  test  ";

		// 実行 & 検証
		assertFalse(DomainObjectUtils.isEmpty(value));
		assertFalse(DomainObjectUtils.isBlank(value));
		assertTrue(DomainObjectUtils.isNotEmpty(value));
		assertTrue(DomainObjectUtils.isNotBlank(value));
		assertEquals("test", DomainObjectUtils.trim(value));
		assertEquals("test", DomainObjectUtils.trimToNull(value));
	}

	@Test
	@DisplayName("正常系：複合シナリオ - コレクション検証")
	void testComplexScenario_CollectionValidation() {
		// 準備
		List<String> emptyList = new ArrayList<>();
		List<String> filledList = Arrays.asList("item1", "item2");

		// 実行 & 検証
		assertTrue(DomainObjectUtils.isEmptyCollection(emptyList));
		assertFalse(DomainObjectUtils.isNotEmptyCollection(emptyList));

		assertFalse(DomainObjectUtils.isEmptyCollection(filledList));
		assertTrue(DomainObjectUtils.isNotEmptyCollection(filledList));
	}

	@Test
	@DisplayName("正常系：複合シナリオ - デフォルト値の連鎖")
	void testComplexScenario_DefaultValueChain() {
		// 実行 & 検証
		String result1 = DomainObjectUtils.defaultIfNull(null, "default1");
		assertEquals("default1", result1);

		String result2 = DomainObjectUtils.defaultIfEmpty("", "default2");
		assertEquals("default2", result2);

		String result3 = DomainObjectUtils.defaultIfBlank("   ", "default3");
		assertEquals("default3", result3);

		String result4 = DomainObjectUtils.defaultIfBlank("value", "default4");
		assertEquals("value", result4);
	}
}
