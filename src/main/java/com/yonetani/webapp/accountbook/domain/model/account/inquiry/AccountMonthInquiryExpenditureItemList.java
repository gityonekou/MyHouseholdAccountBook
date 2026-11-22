/**
 * 月毎の支出金額情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/09/30 : 1.00.00                      新規作成
 * 2025/12/28 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/09/23 : 1.02.00  feature-1.03-dev1   追加リファクタリング対応(ファクトリの引数をドメインタイプに変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.MinorWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.SevereWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 月毎の支出金額情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountMonthInquiryExpenditureItemList {
	
	/**
	 *<pre>
	 * 月毎の支出金額情報明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class ExpenditureListItem {
		// 支出項目コード
		private final ExpenditureItemCode expenditureItemCode;
		// 支出項目名
		private final ExpenditureItemName expenditureItemName;
		// 支出項目レベル(1～5)
		private final ExpenditureItemLevel expenditureItemLevel;
		// 支出金額
		private final ExpenditureAmount expenditureAmount;
		// 無駄遣い（軽度）支出金額
		private final MinorWasteExpenditureAmount minorWasteExpenditureAmount;
		// 無駄遣い（重度）支出金額
		private final SevereWasteExpenditureAmount severeWasteExpenditureAmount;
		// 無駄遣い合計支出金額
		private final TotalWasteExpenditureAmount totalWasteExpenditureAmount;
		// 支払日
		private final PaymentDate paymentDate;
		
		/**
		 *<pre>
		 * 引数の値から月毎の支出金額情報明細ドメインモデルを生成して返します。
		 *</pre>
		 * @param expenditureItemCode 支出項目コード
		 * @param expenditureItemName 支出項目名
		 * @param expenditureItemLevel 支出項目レベル(1～5)
		 * @param expenditureAmount 支出金額
		 * @param minorWasteExpenditureAmount 無駄遣い（軽度）支出金額
		 * @param severeWasteExpenditureAmount 無駄遣い（重度）支出金額
		 * @param paymentDate 支払日
		 * @return 月毎の支出項目明細
		 *
		 */
		public static ExpenditureListItem from(
					ExpenditureItemCode expenditureItemCode,
					ExpenditureItemName expenditureItemName,
					ExpenditureItemLevel expenditureItemLevel,
					ExpenditureAmount expenditureAmount,
					MinorWasteExpenditureAmount minorWasteExpenditureAmount,
					SevereWasteExpenditureAmount severeWasteExpenditureAmount,
					PaymentDate paymentDate
				) {
			
			// 月毎の支出項目明細ドメインモデルを生成して返す
			return new ExpenditureListItem(
					// 支出項目コード
					expenditureItemCode,
					// 支出項目名
					expenditureItemName,
					// 支出項目レベル(1～5)
					expenditureItemLevel,
					// 支出金額
					expenditureAmount,
					// 無駄遣い（軽度）支出金額
					minorWasteExpenditureAmount,
					// 無駄遣い（重度）支出金額
					severeWasteExpenditureAmount,
					// 無駄遣い合計支出金額
					TotalWasteExpenditureAmount.from(minorWasteExpenditureAmount, severeWasteExpenditureAmount),
					// 支払日
					paymentDate);
		}
	}
	
	// 月毎の支出項目明細のリスト
	private final List<ExpenditureListItem> values;
	
	/**
	 *<pre>
	 * 月毎の支出金額情報明細(ドメインモデル)のリストからAccountMonthInquiryExpenditureItemListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 月毎の支出金額情報明細(ドメインモデル)のリスト
	 * @return AccountMonthInquiryExpenditureItemListのドメインモデル
	 *
	 */
	public static AccountMonthInquiryExpenditureItemList from(List<ExpenditureListItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AccountMonthInquiryExpenditureItemList(Collections.emptyList());
		} else {
			return new AccountMonthInquiryExpenditureItemList(values);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 200);
			buff.append("月毎の支出金額情報リスト明細:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "月毎の支出金額情報リスト明細:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
