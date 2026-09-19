/**
 * FixedCostクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * FixedCostクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("固定費情報(FixedCost)のテスト")
class FixedCostTest {

	@Test
	@DisplayName("正常系：from(BigDecimal)で全項目指定して生成")
	void testFrom_BigDecimal_正常系_全項目指定() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", null, "27", "001",
				new BigDecimal("60000.00"));

		assertEquals("user01", fixedCost.getUserId().getValue());
		assertEquals("0001", fixedCost.getFixedCostCode().getValue());
		assertEquals("家賃", fixedCost.getFixedCostName().getValue());
		assertEquals("毎月27日引き落とし", fixedCost.getFixedCostDetailContext().getValue());
		assertEquals("0035", fixedCost.getExpenditureItemCode().getValue());
		assertEquals("1", fixedCost.getFixedCostKubun().getValue());
		assertEquals("00", fixedCost.getFixedCostTargetPaymentMonth().getValue());
		assertNull(fixedCost.getFixedCostTargetPaymentMonthOptionalContext().getValue());
		assertEquals("27", fixedCost.getFixedCostPaymentDay().getValue());
		assertEquals(new BigDecimal("60000.00"), fixedCost.getFixedCostPaymentAmount().getValue());
		assertEquals("001", fixedCost.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：from(Integer)で全項目指定して生成")
	void testFrom_Integer_正常系_全項目指定() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0002", "電気代概算", "概算で登録",
				"0037", "2", "00", null, "27", "001",
				Integer.valueOf(12000));

		assertEquals(0, new BigDecimal("12000.00").compareTo(fixedCost.getFixedCostPaymentAmount().getValue()));
		assertEquals("2", fixedCost.getFixedCostKubun().getValue());
		assertEquals("001", fixedCost.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：updateBulkUpdateItemで支払日と支払金額のみ更新され、他項目は元の値のまま保持される")
	void testUpdateBulkUpdateItem_正常系_支払日と支払金額のみ更新() {
		FixedCost before = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", null, "27", "001",
				new BigDecimal("60000.00"));

		FixedCost after = before.updateBulkUpdateItem("31", Integer.valueOf(65000));

		// 更新対象項目
		assertEquals("31", after.getFixedCostPaymentDay().getValue());
		assertEquals(0, new BigDecimal("65000.00").compareTo(after.getFixedCostPaymentAmount().getValue()));
		// 更新対象外項目はそのまま維持される
		assertEquals(before.getUserId(), after.getUserId());
		assertEquals(before.getFixedCostCode(), after.getFixedCostCode());
		assertEquals(before.getFixedCostName(), after.getFixedCostName());
		assertEquals(before.getFixedCostDetailContext(), after.getFixedCostDetailContext());
		assertEquals(before.getExpenditureItemCode(), after.getExpenditureItemCode());
		assertEquals(before.getFixedCostKubun(), after.getFixedCostKubun());
		assertEquals(before.getFixedCostTargetPaymentMonth(), after.getFixedCostTargetPaymentMonth());
		assertEquals(before.getFixedCostTargetPaymentMonthOptionalContext(), after.getFixedCostTargetPaymentMonthOptionalContext());
	}

	@Test
	@DisplayName("正常系：updateBulkUpdateItemでは支払方法コードは一括更新の対象外であり現在値のままコピーされる")
	void testUpdateBulkUpdateItem_正常系_支払方法コードは現在値のまま() {
		FixedCost before = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", null, "27", "001",
				new BigDecimal("60000.00"));

		FixedCost after = before.updateBulkUpdateItem("27", Integer.valueOf(60000));

		assertEquals(before.getPaymentMethodCode(), after.getPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：getExpenditureDetailContextは両項目とも未設定の場合は空文字を返す")
	void testGetExpenditureDetailContext_正常系_両方未設定() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0001", "家賃", "",
				"0035", "1", "00", "", "27", "001",
				new BigDecimal("60000.00"));

		assertEquals("", fixedCost.getExpenditureDetailContext());
	}

	@Test
	@DisplayName("正常系：getExpenditureDetailContextは固定費内容詳細のみ設定されている場合その値を返す")
	void testGetExpenditureDetailContext_正常系_詳細のみ設定() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", "", "27", "001",
				new BigDecimal("60000.00"));

		assertEquals("毎月27日引き落とし", fixedCost.getExpenditureDetailContext());
	}

	@Test
	@DisplayName("正常系：getExpenditureDetailContextは固定費支払月任意詳細のみ設定されている場合その値を返す")
	void testGetExpenditureDetailContext_正常系_任意詳細のみ設定() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0004", "その他任意テスト", "",
				"0038", "1", "40", "不定期の支払です", "27", "001",
				new BigDecimal("10000.00"));

		assertEquals("不定期の支払です", fixedCost.getExpenditureDetailContext());
	}

	@Test
	@DisplayName("正常系：getExpenditureDetailContextは両項目とも設定されている場合「/」区切りで連結した値を返す")
	void testGetExpenditureDetailContext_正常系_両方設定で連結() {
		FixedCost fixedCost = FixedCost.from(
				"user01", "0004", "その他任意テスト", "その他任意テスト詳細内容",
				"0038", "1", "40", "不定期の支払です", "27", "001",
				new BigDecimal("10000.00"));

		assertEquals("その他任意テスト詳細内容/不定期の支払です", fixedCost.getExpenditureDetailContext());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		FixedCost fixedCost1 = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", null, "27", "001",
				new BigDecimal("60000.00"));
		FixedCost fixedCost2 = FixedCost.from(
				"user01", "0001", "家賃", "毎月27日引き落とし",
				"0035", "1", "00", null, "27", "001",
				new BigDecimal("60000.00"));

		assertEquals(fixedCost1, fixedCost2);
		assertEquals(fixedCost1.hashCode(), fixedCost2.hashCode());
	}
}
