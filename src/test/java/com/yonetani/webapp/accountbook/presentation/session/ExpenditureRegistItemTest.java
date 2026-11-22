/**
 * ExpenditureRegistItemクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/11/08 : 1.00.01  feature-1.03-dev1   from()の引数が値オブジェクト型になったことに伴うヘルパーメソッド追加(型安全性対応。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

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

	/**
	 *<pre>
	 * プリミティブ引数を値オブジェクトへ変換してからExpenditureRegistItem.from()を呼び出すヘルパーメソッドです。
	 * from()の引数が値オブジェクト型になったため(06_コーディング規約.md §8)、テストコードから
	 * 直接プリミティブ値で呼び出せるようにするためのものです(同規約§8.3参照)。
	 *</pre>
	 */
	private ExpenditureRegistItem helper(
			String dataType, String action, String expenditureCode, String expenditureItemCode,
			String eventCode, String expenditureName, String expenditureCategory, String expenditureDetailContext,
			String siharaiDate, String paymentMethodCode, BigDecimal expenditureKingaku, boolean clearStartFlg) {
		return ExpenditureRegistItem.from(
				dataType,
				action,
				expenditureCode,
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureEventCode.from(eventCode),
				ExpenditureName.from(expenditureName),
				ExpenditureCategory.from(expenditureCategory),
				ExpenditureDetailContext.from(expenditureDetailContext),
				siharaiDate,
				PaymentMethodCode.from(paymentMethodCode),
				ExpenditureAmount.from(expenditureKingaku.setScale(2)),
				clearStartFlg);
	}

	@Test
	@DisplayName("正常系：from()で全項目指定して生成")
	void testFrom_正常系_全項目指定() {
		ExpenditureRegistItem item = helper(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				"001", new BigDecimal("12000"), false);

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
		ExpenditureRegistItem item = helper(
				"NEW", "add", "20260101120000001", "0051", "",
				"食費", "1", "食費詳細", "00",
				"999", new BigDecimal("30000"), true);

		assertEquals(2, item.getExpenditureKingaku().scale());
		assertEquals(new BigDecimal("30000.00"), item.getExpenditureKingaku());
	}

	@Test
	@DisplayName("正常系：買い物集計8項目相当(支払方法がない=999・clearStartFlg=true)を保持できる")
	void testFrom_正常系_支払方法がないと0円開始フラグ() {
		ExpenditureRegistItem item = helper(
				"NEW", "add", "20260101120000002", "0051", "",
				"食費(無駄遣いなし)", "1", "食費詳細", "00",
				"999", new BigDecimal("30000"), true);

		assertTrue(item.isClearStartFlg());
		assertEquals("999", item.getPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		ExpenditureRegistItem item1 = helper(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				"001", new BigDecimal("12000"), false);
		ExpenditureRegistItem item2 = helper(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				"001", new BigDecimal("12000"), false);

		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	@Test
	@DisplayName("正常系：支払方法コードの違いで等価にならない")
	void testEquals_正常系_支払方法コードの違いで異なる() {
		ExpenditureRegistItem item1 = helper(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				"001", new BigDecimal("12000"), false);
		ExpenditureRegistItem item2 = helper(
				"LOAD", "update", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", "30",
				"002", new BigDecimal("12000"), false);

		assertNotEquals(item1, item2);
	}
}
