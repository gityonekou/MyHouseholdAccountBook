/**
 * ExpenditureRegistItemクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * ExpenditureRegistItemクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支出登録情報(ExpenditureRegistItem)のテスト")
class ExpenditureRegistItemTest {

	@Test
	@DisplayName("正常系：from()で全項目指定して生成")
	void testFrom_正常系_全項目指定() {
		ExpenditureRegistItem item = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "001");

		assertEquals("LOAD", item.getDataType());
		assertEquals("update", item.getAction());
		assertEquals("001", item.getExpenditureCode());
		assertEquals("0037", item.getExpenditureItemCode());
		assertEquals("0001", item.getEventCode());
		assertEquals("電気代", item.getExpenditureName());
		assertEquals("1", item.getExpenditureCategory());
		assertEquals("電気代詳細", item.getExpenditureDetailContext());
		assertEquals("30", item.getSiharaiDate());
		assertEquals(new BigDecimal("12000.00"), item.getExpenditureKingaku());
		assertFalse(item.isClearStartFlg());
		assertEquals("001", item.getPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：from()は支払金額をスケール2で保持する")
	void testFrom_正常系_支払金額はスケール2() {
		ExpenditureRegistItem item = ExpenditureRegistItem.from(
				"NEW", "add", "20260101120000001", "0051", "",
				"食費", "1", "食費詳細", "00",
				new BigDecimal("30000"), true, "999");

		assertEquals(2, item.getExpenditureKingaku().scale());
		assertEquals(new BigDecimal("30000.00"), item.getExpenditureKingaku());
	}

	@Test
	@DisplayName("正常系：買い物集計8項目相当(支払方法がない=999・clearStartFlg=true)を保持できる")
	void testFrom_正常系_支払方法がないと0円開始フラグ() {
		ExpenditureRegistItem item = ExpenditureRegistItem.from(
				"NEW", "add", "20260101120000002", "0051", "",
				"食費(無駄遣いなし)", "1", "食費詳細", "00",
				new BigDecimal("30000"), true, "999");

		assertTrue(item.isClearStartFlg());
		assertEquals("999", item.getPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		ExpenditureRegistItem item1 = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "001");
		ExpenditureRegistItem item2 = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "001");

		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	@Test
	@DisplayName("正常系：支払方法コードの違いで等価にならない")
	void testEquals_正常系_支払方法コードの違いで異なる() {
		ExpenditureRegistItem item1 = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "001");
		ExpenditureRegistItem item2 = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "002");

		assertNotEquals(item1, item2);
	}
}
