/**
 * SafeDomainFactoryクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * SafeDomainFactoryクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("SafeDomainFactoryのテスト")
class SafeDomainFactoryTest {

	@Test
	@DisplayName("正常系：ファクトリが正常に値を返す場合、値ありのOptionalを返す")
	void testTryCreate_正常系_値あり() {
		Optional<String> result = SafeDomainFactory.tryCreate(() -> "OK");
		assertTrue(result.isPresent());
		assertEquals("OK", result.get());
	}

	@Test
	@DisplayName("異常系：MyHouseholdAccountBookRuntimeExceptionが発生した場合、Optional.emptyを返す")
	void testTryCreate_異常系_業務例外はempty() {
		Optional<String> result = SafeDomainFactory.tryCreate(() -> {
			throw new MyHouseholdAccountBookRuntimeException("テスト用例外");
		});
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("正常系：ファクトリがnullを返す場合、Optional.emptyを返す(NullPointerExceptionが伝播しない)")
	void testTryCreate_正常系_null返却はempty() {
		Optional<String> result = SafeDomainFactory.tryCreate(() -> null);
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("異常系：業務例外以外のRuntimeExceptionは伝播する")
	void testTryCreate_異常系_業務例外以外は伝播() {
		assertThrows(IllegalStateException.class, () -> SafeDomainFactory.tryCreate(() -> {
			throw new IllegalStateException("想定外の例外");
		}));
	}
}
