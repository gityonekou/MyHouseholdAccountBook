/**
 * BankAccountMemoクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * BankAccountMemoクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("銀行口座メモ(BankAccountMemo)のテスト")
class BankAccountMemoTest {

	@Test
	@DisplayName("正常系：値から生成")
	void testFrom_正常系_値から生成() {
		BankAccountMemo memo = BankAccountMemo.from("引き落とし用口座");
		// 検証
		assertNotNull(memo);
		assertEquals("引き落とし用口座", memo.getValue());
		assertEquals("引き落とし用口座", memo.toString());
	}

	@Test
	@DisplayName("正常系：nullを許容する")
	void testFrom_正常系_null許容() {
		BankAccountMemo memo = BankAccountMemo.from(null);
		// 検証
		assertNotNull(memo);
		assertNull(memo.getValue());
	}

	@Test
	@DisplayName("正常系：100文字ちょうどで生成できる")
	void testFrom_正常系_100文字ちょうど() {
		String value100 = "あ".repeat(100);
		BankAccountMemo memo = BankAccountMemo.from(value100);
		// 検証
		assertEquals(100, memo.getValue().length());
	}

	@Test
	@DisplayName("異常系：101文字で例外が発生する")
	void testFrom_異常系_101文字() {
		String value101 = "あ".repeat(101);
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountMemo.from(value101));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		BankAccountMemo memo1 = BankAccountMemo.from("メモ");
		BankAccountMemo memo2 = BankAccountMemo.from("メモ");
		// 検証
		assertEquals(memo1, memo2);
		assertEquals(memo1.hashCode(), memo2.hashCode());
	}
}
