/**
 * 「買い物集計8項目」に該当するかどうかを判定する仕様(Specification)です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import java.util.Map;
import java.util.Set;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;

/**
 *<pre>
 * 「買い物集計8項目」（要件定義書 3.3・7.3。ドメイン用語集参照）に該当するかどうかを判定する仕様(Specification)です。
 * 判定は支出項目コード＋支出区分(無駄遣い区分)の組み合わせで行います。
 *
 * 本クラスはSpring管理外の単純な値オブジェクトです。DIコンテナ経由の注入は行わず、
 * 利用側で都度newしてください（状態を持たない・依存性がないため）。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public class ShoppingAggregateSpecification {

	// 支出項目コード＋支出区分(無駄遣い区分)の組み合わせ8組を1箇所に列挙する
	// (Map.Entryを組み合わせの保持に使用。プロジェクトにPair型の依存がないため専用型は新設せず標準APIで代替)
	private static final Set<Map.Entry<ExpenditureItemCode, ExpenditureCategory>> TARGETS = Set.of(
			// 食費(無駄遣いなし)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.NON_WASTED),
			// 食費(無駄遣いB)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.WASTED_B),
			// 食費(無駄遣いC)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.WASTED_C),
			// 日用消耗品
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE), ExpenditureCategory.NON_WASTED),
			// 衣類・クリーニング・靴(被服費)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_HIFUKU_VALUE), ExpenditureCategory.NON_WASTED),
			// 住居設備
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE), ExpenditureCategory.NON_WASTED),
			// 外食(一人プチ贅沢・外食)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_GAISYOKU_VALUE), ExpenditureCategory.NON_WASTED),
			// 仕事(流動経費)
			Map.entry(ExpenditureItemCode.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE), ExpenditureCategory.NON_WASTED)
	);

	/**
	 *<pre>
	 * 指定の支出項目コード・支出区分の組み合わせが買い物集計8項目に該当するかどうかを判定します。
	 *</pre>
	 * @param itemCode 支出項目コード
	 * @param category 支出区分(無駄遣い区分)
	 * @return 買い物集計8項目に該当する場合：true、該当しない場合：false
	 *
	 */
	public boolean isSatisfiedBy(ExpenditureItemCode itemCode, ExpenditureCategory category) {
		if(itemCode == null || category == null) {
			return false;
		}
		return TARGETS.contains(Map.entry(itemCode, category));
	}
}
