/**
 * BankAccountクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/18 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.bankaccount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * BankAccountクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("銀行口座(BankAccount)のテスト")
class BankAccountTest {

	@Test
	@DisplayName("正常系：全項目指定で生成")
	void testFrom_正常系_全項目指定() {
		BankAccount account = BankAccount.from("user01", "01", "テスト銀行", "引き落とし用", "01", true);
		// 検証
		assertNotNull(account);
		assertEquals("user01", account.getUserId().getValue());
		assertEquals("01", account.getBankAccountCode().getValue());
		assertEquals("テスト銀行", account.getBankName().getValue());
		assertEquals("引き落とし用", account.getBankAccountMemo().getValue());
		assertEquals("01", account.getBankAccountSort().getValue());
		assertTrue(account.getEnableFlg().getValue());
	}

	@Test
	@DisplayName("正常系：銀行口座メモがnullでも生成できる")
	void testFrom_正常系_メモnull() {
		BankAccount account = BankAccount.from("user01", "01", "テスト銀行", null, "01", false);
		// 検証
		assertNotNull(account);
		assertNull(account.getBankAccountMemo().getValue());
		assertFalse(account.getEnableFlg().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		BankAccount account1 = BankAccount.from("user01", "01", "テスト銀行", "メモ", "01", true);
		BankAccount account2 = BankAccount.from("user01", "01", "テスト銀行", "メモ", "01", true);
		// 検証
		assertEquals(account1, account2);
		assertEquals(account1.hashCode(), account2.hashCode());
	}
}
