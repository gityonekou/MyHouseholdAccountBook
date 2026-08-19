/**
 * ShoppingAggregateSpecificationクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;

/**
 *<pre>
 * ShoppingAggregateSpecificationクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("買い物集計8項目判定仕様(ShoppingAggregateSpecification)のテスト")
class ShoppingAggregateSpecificationTest {

	private final ShoppingAggregateSpecification spec = new ShoppingAggregateSpecification();

	/**
	 *<pre>
	 * 買い物集計8項目に一致する8パターンすべてでtrueとなり、要件定義書7.3の項目数(8件)と一致することを確認します。
	 *</pre>
	 */
	static Stream<Arguments> eightTargets() {
		return Stream.of(
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE, ExpenditureCategory.NON_WASTED, "食費(無駄遣いなし)"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE, ExpenditureCategory.WASTED_B, "食費(無駄遣いB)"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE, ExpenditureCategory.WASTED_C, "食費(無駄遣いC)"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE, ExpenditureCategory.NON_WASTED, "日用消耗品"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_HIFUKU_VALUE, ExpenditureCategory.NON_WASTED, "衣類・クリーニング・靴"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE, ExpenditureCategory.NON_WASTED, "住居設備"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_GAISYOKU_VALUE, ExpenditureCategory.NON_WASTED, "外食"),
				Arguments.of(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE, ExpenditureCategory.NON_WASTED, "仕事(流動経費)")
		);
	}

	@ParameterizedTest(name = "{2}はtrueになる")
	@MethodSource("eightTargets")
	@DisplayName("正常系：買い物集計8項目に一致する組み合わせはtrueになる")
	void testIsSatisfiedBy_正常系_対象8項目(String itemCode, ExpenditureCategory category, String label) {
		assertTrue(spec.isSatisfiedBy(ExpenditureItemCode.from(itemCode), category), label + "はtrueであること");
	}

	@Test
	@DisplayName("正常系：対象8組の件数が要件定義書7.3の8件と一致する")
	void testEightTargetsCount() {
		assertEquals(8, eightTargets().count());
	}

	@Test
	@DisplayName("異常系：食費以外の項目で無駄遣いB/Cを指定した場合はfalseになる")
	void testIsSatisfiedBy_異常系_食費以外の無駄遣い区分() {
		assertFalse(spec.isSatisfiedBy(
				ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE), ExpenditureCategory.WASTED_B));
		assertFalse(spec.isSatisfiedBy(
				ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_HIFUKU_VALUE), ExpenditureCategory.WASTED_C));
	}

	@Test
	@DisplayName("異常系：対象外の支出項目コードはfalseになる")
	void testIsSatisfiedBy_異常系_対象外の支出項目コード() {
		assertFalse(spec.isSatisfiedBy(ExpenditureItemCode.from("0001"), ExpenditureCategory.NON_WASTED));
	}

	@Test
	@DisplayName("異常系：itemCodeまたはcategoryがnullの場合はfalseになる(例外にしない)")
	void testIsSatisfiedBy_異常系_null値() {
		assertFalse(spec.isSatisfiedBy(null, ExpenditureCategory.NON_WASTED));
		assertFalse(spec.isSatisfiedBy(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE), null));
		assertFalse(spec.isSatisfiedBy(null, null));
	}
}
