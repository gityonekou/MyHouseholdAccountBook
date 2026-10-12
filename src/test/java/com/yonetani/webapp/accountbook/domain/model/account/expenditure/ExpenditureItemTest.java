/**
 * ExpenditureItemクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.DeleteFlg;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;

/**
 *<pre>
 * ExpenditureItemクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支出テーブル情報(ExpenditureItem)のテスト")
class ExpenditureItemTest {

	@Test
	@DisplayName("正常系：from()で全項目指定して生成")
	void testFrom_正常系_全項目指定() {
		ExpenditureItem item = helper(
				"user01", "2025", "11", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", LocalDate.of(2025, 11, 30),
				new BigDecimal("12000.00"), new BigDecimal("10000.00"), "001", false);

		assertEquals("user01", item.getUserId().getValue());
		assertEquals("2025", item.getTargetYear().getValue());
		assertEquals("11", item.getTargetMonth().getValue());
		assertEquals("001", item.getExpenditureCode().getValue());
		assertEquals("0037", item.getExpenditureItemCode().getValue());
		assertEquals("0001", item.getExpenditureEventCode().getValue());
		assertEquals("電気代", item.getExpenditureName().getValue());
		assertEquals("1", item.getExpenditureCategory().getValue());
		assertEquals("電気代詳細", item.getExpenditureDetailContext().getValue());
		assertEquals(LocalDate.of(2025, 11, 30), item.getPaymentDate().getValue());
		assertEquals(new BigDecimal("12000.00"), item.getExpectedExpenditureAmount().getValue());
		assertEquals(new BigDecimal("10000.00"), item.getExpenditureAmount().getValue());
		assertEquals("001", item.getPaymentMethodCode().getValue());
		assertFalse(item.getDeleteFlg().getValue());
	}

	@Test
	@DisplayName("正常系：createExpenditureItemは初期登録時(initFlg=true)、支出金額を支出予定金額として設定する")
	void testCreateExpenditureItem_正常系_初期登録時は支出金額を支出予定金額に設定() {
		ExpenditureRegistItem session = ExpenditureRegistItem.from(
				"NEW", "add", "20260101120000001", "0037", "",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("12000"), false, "001");

		ExpenditureItem item = createExpenditureItem(
				true, UserId.from("user01"), TargetYearMonth.from("202511"),
				ExpenditureCode.from("001"), session);

		assertEquals(new BigDecimal("12000.00"), item.getExpectedExpenditureAmount().getValue());
		assertEquals(new BigDecimal("12000.00"), item.getExpenditureAmount().getValue());
		assertEquals("001", item.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：createExpenditureItemは更新時(initFlg=false)、支出予定金額に0を設定する")
	void testCreateExpenditureItem_正常系_更新時は支出予定金額に0を設定() {
		ExpenditureRegistItem session = ExpenditureRegistItem.from(
				"LOAD", "update", "001", "0037", "",
				"電気代", "1", "電気代詳細", "30",
				new BigDecimal("15000"), false, "001");

		ExpenditureItem item = createExpenditureItem(
				false, UserId.from("user01"), TargetYearMonth.from("202511"),
				ExpenditureCode.from("001"), session);

		assertEquals(0, BigDecimal.ZERO.compareTo(item.getExpectedExpenditureAmount().getValue()));
		assertEquals(new BigDecimal("15000.00"), item.getExpenditureAmount().getValue());
	}

	@Test
	@DisplayName("正常系：createExpenditureItemはclearStartFlg=trueの場合、支出金額を0に設定する")
	void testCreateExpenditureItem_正常系_clearStartFlgがtrueの場合支出金額0() {
		ExpenditureRegistItem session = ExpenditureRegistItem.from(
				"NEW", "add", "20260101120000001", "0051", "",
				"食費(無駄遣いなし)", "1", "食費詳細", "05",
				new BigDecimal("30000"), true, "999");

		ExpenditureItem item = createExpenditureItem(
				true, UserId.from("user01"), TargetYearMonth.from("202511"),
				ExpenditureCode.from("001"), session);

		assertEquals(new BigDecimal("30000.00"), item.getExpectedExpenditureAmount().getValue());
		assertEquals(0, BigDecimal.ZERO.compareTo(item.getExpenditureAmount().getValue()));
		// 買い物集計8項目は固定費から「支払方法がない(999)」を引き継ぐ
		assertEquals("999", item.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：addSisyutuKingakuは支出金額を加算し、支払方法コードは現在値のまま維持する(突合レビュー指摘A)")
	void testAddSisyutuKingaku_正常系_支払方法コードは維持() {
		ExpenditureItem before = helper(
				"user01", "2025", "11", "001", "0051", "",
				"食費(無駄遣いなし)", "1", "食費詳細", null,
				new BigDecimal("30000.00"), new BigDecimal("10000.00"), "999", false);

		ExpenditureItem after = before.addSisyutuKingaku(ExpenditureAmount.from(new BigDecimal("3000.00")));

		assertEquals(new BigDecimal("13000.00"), after.getExpenditureAmount().getValue());
		assertEquals("999", after.getPaymentMethodCode().getValue());
		assertEquals(before.getExpenditureName(), after.getExpenditureName());
	}

	@Test
	@DisplayName("正常系：subtractSisyutuKingakuは支出金額を減算し、支払方法コードは現在値のまま維持する(突合レビュー指摘A)")
	void testSubtractSisyutuKingaku_正常系_支払方法コードは維持() {
		ExpenditureItem before = helper(
				"user01", "2025", "11", "001", "0051", "",
				"食費(無駄遣いなし)", "1", "食費詳細", null,
				new BigDecimal("30000.00"), new BigDecimal("10000.00"), "999", false);

		ExpenditureItem after = before.subtractSisyutuKingaku(ExpenditureAmount.from(new BigDecimal("2000.00")));

		assertEquals(new BigDecimal("8000.00"), after.getExpenditureAmount().getValue());
		assertEquals("999", after.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		ExpenditureItem item1 = helper(
				"user01", "2025", "11", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", LocalDate.of(2025, 11, 30),
				new BigDecimal("12000.00"), new BigDecimal("10000.00"), "001", false);
		ExpenditureItem item2 = helper(
				"user01", "2025", "11", "001", "0037", "0001",
				"電気代", "1", "電気代詳細", LocalDate.of(2025, 11, 30),
				new BigDecimal("12000.00"), new BigDecimal("10000.00"), "001", false);

		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	/**
	 *<pre>
	 * ExpenditureItem生成を行うヘルパーです
	 *</pre>
	 * @param userId
	 * @param targetYear
	 * @param targetMonth
	 * @param expenditureCode
	 * @param expenditureItemCode
	 * @param expenditureEventCode
	 * @param expenditureName
	 * @param expenditureCategory
	 * @param expenditureDetailContext
	 * @param paymentDate
	 * @param expectedExpenditureAmount
	 * @param expenditureAmount
	 * @param paymentMethodCode
	 * @param deleteFlg
	 * @return
	 *
	 */
	private ExpenditureItem helper(String userId,
			String targetYear,
			String targetMonth,
			String expenditureCode,
			String expenditureItemCode,
			String expenditureEventCode,
			String expenditureName,
			String expenditureCategory,
			String expenditureDetailContext,
			LocalDate paymentDate,
			BigDecimal expectedExpenditureAmount,
			BigDecimal expenditureAmount,
			String paymentMethodCode,
			boolean deleteFlg) {
		return ExpenditureItem.from(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				ExpenditureCode.from(expenditureCode),
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureEventCode.from(expenditureEventCode),
				ExpenditureName.from(expenditureName),
				ExpenditureCategory.from(expenditureCategory),
				ExpenditureDetailContext.from(expenditureDetailContext),
				PaymentDate.from(paymentDate),
				ExpectedExpenditureAmount.from(expectedExpenditureAmount),
				ExpenditureAmount.from(expenditureAmount),
				PaymentMethodCode.from(paymentMethodCode),
				DeleteFlg.from(deleteFlg));
	}

	/**
	 *<pre>
	 * セッションの支出登録情報からExpenditureItem生成を行うヘルパーです
	 *</pre>
	 * @param initFlg 初期登録かどうかのフラグ
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @param expenditureCode 支出コード
	 * @param session 支出登録情報(セッション)
	 * @return
	 *
	 */
	private ExpenditureItem createExpenditureItem(boolean initFlg, UserId userId, TargetYearMonth targetYearMonth,
			ExpenditureCode expenditureCode, ExpenditureRegistItem session) {
		return ExpenditureItem.createExpenditureItem(
				initFlg,
				userId,
				targetYearMonth,
				expenditureCode,
				ExpenditureItemCode.from(session.getExpenditureItemCode()),
				ExpenditureEventCode.from(session.getEventCode()),
				ExpenditureName.from(session.getExpenditureName()),
				ExpenditureCategory.from(session.getExpenditureCategory()),
				ExpenditureDetailContext.from(session.getExpenditureDetailContext()),
				PaymentDate.from(targetYearMonth.getValue(), session.getSiharaiDate()),
				ExpenditureAmount.from(session.getExpenditureKingaku()),
				session.isClearStartFlg(),
				PaymentMethodCode.from(session.getPaymentMethodCode()));
	}
}
