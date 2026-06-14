/**
 * AccountMonthInquiryExpenditureList ドメインモデルのユニットテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/06/13 : 1.02.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;

/**
 *<pre>
 * AccountMonthInquiryExpenditureList ドメインモデルのユニットテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.02)
 *
 */
@DisplayName("月次収支照会 支出別一覧ドメインモデルのユニットテスト")
class AccountMonthInquiryExpenditureListTest {

	/**
	 * テスト用の ExpenditureItem を生成します。
	 */
	private ExpenditureItem createItem(String code, String name, String category,
			LocalDate paymentDate, int amount, String detail) {
		return ExpenditureItem.from(
				"user01", "2025", "11", code, "0002", null,
				name, category, detail,
				paymentDate,
				BigDecimal.valueOf(amount).setScale(2),
				BigDecimal.valueOf(amount).setScale(2),
				false);
	}

	// ========================================
	// ① 空リストのテスト
	// ========================================

	@Test
	@DisplayName("①空リストで from() → isEmpty()=true、totalAmount=0")
	void testFrom_EmptyList() {
		// Given: 空の支出情報リスト
		ExpenditureItemInquiryList emptyList = ExpenditureItemInquiryList.from(Collections.emptyList());

		// When: ドメインモデルを生成
		AccountMonthInquiryExpenditureList result = AccountMonthInquiryExpenditureList.from(emptyList);

		// Then: 空リストかつ合計=0
		assertTrue(result.isEmpty());
		assertEquals("0円", result.getTotalAmount().toFormatString());
	}

	// ========================================
	// ② 1件（無駄遣いなし）のテスト
	// ========================================

	@Test
	@DisplayName("②1件（無駄遣いなし）で from() → isEmpty()=false、totalAmount 正しい")
	void testFrom_SingleItem_NonWasted() {
		// Given: 1件の支出情報（無駄遣いなし）
		ExpenditureItem item = createItem("001", "スーパー買い物", "1", LocalDate.of(2025, 11, 5), 80000, "食料品購入");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));

		// When: ドメインモデルを生成
		AccountMonthInquiryExpenditureList result = AccountMonthInquiryExpenditureList.from(list);

		// Then: 1件かつ合計 = 80,000円
		assertFalse(result.isEmpty());
		assertEquals(1, result.getValues().size());
		assertEquals("80,000円", result.getTotalAmount().toFormatString());
	}

	// ========================================
	// ③ 複数件の合計テスト
	// ========================================

	@Test
	@DisplayName("③複数件で from() → totalAmount は全件の合計")
	void testFrom_MultipleItems_TotalAmountIsSum() {
		// Given: 複数件の支出情報
		ExpenditureItem item1 = createItem("001", "スーパー買い物", "1", LocalDate.of(2025, 11, 5), 80000, "食料品");
		ExpenditureItem item2 = createItem("002", "レストラン", "1", LocalDate.of(2025, 11, 10), 30000, "外食");
		ExpenditureItem item3 = createItem("003", "映画館", "2", LocalDate.of(2025, 11, 20), 20000, "娯楽");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item1, item2, item3));

		// When: ドメインモデルを生成
		AccountMonthInquiryExpenditureList result = AccountMonthInquiryExpenditureList.from(list);

		// Then: 3件かつ合計 = 130,000円
		assertFalse(result.isEmpty());
		assertEquals(3, result.getValues().size());
		assertEquals("130,000円", result.getTotalAmount().toFormatString());
	}

	// ========================================
	// ④ ドメインモデル: 支出区分 WASTED_C のテスト
	// ========================================

	@Test
	@DisplayName("④支出区分が WASTED_C の場合、ExpenditureRow の expenditureCategory が WASTED_C")
	void testExpenditureRow_CategoryWastedC() {
		// Given: 無駄遣いC の支出情報
		ExpenditureItem item = createItem("001", "衝動買い", "3", LocalDate.of(2025, 11, 15), 5000, "");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));
		AccountMonthInquiryExpenditureList domain = AccountMonthInquiryExpenditureList.from(list);

		// When: ドメインモデルの ExpenditureRow を取得
		AccountMonthInquiryExpenditureList.ExpenditureRow row = domain.getValues().get(0);

		// Then: ExpenditureCategory が WASTED_C
		assertEquals(ExpenditureCategory.WASTED_C, row.getExpenditureCategory());
	}

	// ========================================
	// ⑤ ドメインモデル: 支出区分 WASTED_B のテスト
	// ========================================

	@Test
	@DisplayName("⑤支出区分が WASTED_B の場合、ExpenditureRow の expenditureCategory が WASTED_B")
	void testExpenditureRow_CategoryWastedB() {
		// Given: 無駄遣いB の支出情報
		ExpenditureItem item = createItem("001", "夏コミ参加費", "2", LocalDate.of(2025, 11, 13), 12450, "夏コミ(C104)");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));
		AccountMonthInquiryExpenditureList domain = AccountMonthInquiryExpenditureList.from(list);

		// When: ドメインモデルの ExpenditureRow を取得
		AccountMonthInquiryExpenditureList.ExpenditureRow row = domain.getValues().get(0);

		// Then: ExpenditureCategory が WASTED_B
		assertEquals(ExpenditureCategory.WASTED_B, row.getExpenditureCategory());
	}

	// ========================================
	// ⑥ ドメインモデル: 支出区分 NON_WASTED のテスト
	// ========================================

	@Test
	@DisplayName("⑥支出区分が NON_WASTED の場合、ExpenditureRow の expenditureCategory が NON_WASTED")
	void testExpenditureRow_CategoryNonWasted() {
		// Given: 無駄遣いなし の支出情報
		ExpenditureItem item = createItem("001", "国民年金", "1", LocalDate.of(2025, 11, 27), 16800, "");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));
		AccountMonthInquiryExpenditureList domain = AccountMonthInquiryExpenditureList.from(list);

		// When: ドメインモデルの ExpenditureRow を取得
		AccountMonthInquiryExpenditureList.ExpenditureRow row = domain.getValues().get(0);

		// Then: ExpenditureCategory が NON_WASTED
		assertEquals(ExpenditureCategory.NON_WASTED, row.getExpenditureCategory());
	}

	// ========================================
	// ⑦ ドメインモデル: 支払日あり → toDayString() で DD日
	// ========================================

	@Test
	@DisplayName("⑦支払日あり → paymentDate の toDayString() が DD日 形式")
	void testExpenditureRow_PaymentDatePresent_DayString() {
		// Given: 支払日あり（27日）の支出情報
		ExpenditureItem item = createItem("001", "健康保険", "1", LocalDate.of(2025, 11, 27), 47650, "");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));
		AccountMonthInquiryExpenditureList domain = AccountMonthInquiryExpenditureList.from(list);

		// When: ドメインモデルの ExpenditureRow を取得
		AccountMonthInquiryExpenditureList.ExpenditureRow row = domain.getValues().get(0);

		// Then: paymentDate.toDayString() が「27日」
		assertEquals("27日", row.getPaymentDate().toDayString());
	}

	// ========================================
	// ⑧ ドメインモデル: 支払日 null → toDayString() が空文字
	// ========================================

	@Test
	@DisplayName("⑧支払日 null → paymentDate の toDayString() が空文字")
	void testExpenditureRow_PaymentDateNull_EmptyString() {
		// Given: 支払日 null の支出情報
		ExpenditureItem item = createItem("001", "未定支出", "1", null, 10000, "");
		ExpenditureItemInquiryList list = ExpenditureItemInquiryList.from(List.of(item));
		AccountMonthInquiryExpenditureList domain = AccountMonthInquiryExpenditureList.from(list);

		// When: ドメインモデルの ExpenditureRow を取得
		AccountMonthInquiryExpenditureList.ExpenditureRow row = domain.getValues().get(0);

		// Then: paymentDate.toDayString() が空文字
		assertEquals("", row.getPaymentDate().toDayString());
	}
}
