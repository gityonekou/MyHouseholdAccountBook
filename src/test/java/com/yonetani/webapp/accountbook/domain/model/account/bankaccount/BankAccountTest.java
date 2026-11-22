/**
 * BankAccountクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   from()型安全性対応に伴うhelper()追加、toggleEnableFlg()のテスト追加
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.bankaccount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountMemo;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankName;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

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

	/**
	 *<pre>
	 * プリミティブ引数を受け取り値オブジェクトへ変換してからBankAccount.from()を呼び出すテスト用ヘルパーメソッドです。
	 * (06-コーディング規約.md §8.3。from()が値オブジェクト型を要求するため、テストコードから直接プリミティブ値で呼び出せない)
	 *</pre>
	 */
	private BankAccount helper(String userId, String bankAccountCode, String bankName, String bankAccountMemo,
			String bankAccountSort, boolean enableFlg) {
		return BankAccount.from(
				UserId.from(userId),
				BankAccountCode.from(bankAccountCode),
				BankName.from(bankName),
				BankAccountMemo.from(bankAccountMemo),
				BankAccountSort.from(bankAccountSort),
				EnableFlg.from(enableFlg));
	}

	@Test
	@DisplayName("正常系：全項目指定で生成")
	void testFrom_正常系_全項目指定() {
		BankAccount account = helper("user01", "01", "テスト銀行", "引き落とし用", "01", true);
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
		BankAccount account = helper("user01", "01", "テスト銀行", null, "01", false);
		// 検証
		assertNotNull(account);
		assertNull(account.getBankAccountMemo().getValue());
		assertFalse(account.getEnableFlg().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		BankAccount account1 = helper("user01", "01", "テスト銀行", "メモ", "01", true);
		BankAccount account2 = helper("user01", "01", "テスト銀行", "メモ", "01", true);
		// 検証
		assertEquals(account1, account2);
		assertEquals(account1.hashCode(), account2.hashCode());
	}

	@Test
	@DisplayName("正常系：toggleEnableFlgで有効→無効に反転し、他項目は変化しない")
	void testToggleEnableFlg_正常系_有効から無効() {
		BankAccount account = helper("user01", "01", "テスト銀行", "引き落とし用", "01", true);
		BankAccount toggled = account.toggleEnableFlg();
		// 検証
		assertFalse(toggled.getEnableFlg().getValue());
		assertEquals(account.getUserId(), toggled.getUserId());
		assertEquals(account.getBankAccountCode(), toggled.getBankAccountCode());
		assertEquals(account.getBankName(), toggled.getBankName());
		assertEquals(account.getBankAccountMemo(), toggled.getBankAccountMemo());
		assertEquals(account.getBankAccountSort(), toggled.getBankAccountSort());
	}

	@Test
	@DisplayName("正常系：toggleEnableFlgで無効→有効に反転する")
	void testToggleEnableFlg_正常系_無効から有効() {
		BankAccount account = helper("user01", "01", "テスト銀行", "引き落とし用", "01", false);
		BankAccount toggled = account.toggleEnableFlg();
		// 検証
		assertTrue(toggled.getEnableFlg().getValue());
	}
}
