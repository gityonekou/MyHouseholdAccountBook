/**
 * 月毎の支出金額情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.MinorWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SevereWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalWasteExpenditure;
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
 * @since 家計簿アプリ(1.00.A)
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
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class ExpenditureListItem {
		// 支出項目コード
		private final SisyutuItemCode sisyutuItemCode;
		// 支出項目名
		private final SisyutuItemName sisyutuItemName;
		// 支出項目レベル(1～5)
		private final SisyutuItemLevel sisyutuItemLevel;
		// 支出金額
		private final ExpenditureAmount expenditureAmount;
		// 無駄遣い（軽度）支出金額
		private final MinorWasteExpenditure minorWasteExpenditure;
		// 無駄遣い（重度）支出金額
		private final SevereWasteExpenditure severeWasteExpenditure;
		// 無駄遣い合計支出金額
		private final TotalWasteExpenditure totalWasteExpenditure;
		// 支払日
		private final PaymentDate paymentDate;
		
		/**
		 *<pre>
		 * 引数の値から月毎の支出金額情報明細ドメインモデルを生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemLevel 支出項目レベル(1～5)
		 * @param expenditureAmount 支出金額
		 * @param minorWasteExpenditure 無駄遣い（軽度）支出金額
		 * @param severeWasteExpenditure 無駄遣い（重度）支出金額
		 * @param paymentDate 支払日
		 * @return 月毎の支出項目明細
		 *
		 */
		public static ExpenditureListItem from(
					String sisyutuItemCode,
					String sisyutuItemName,
					String sisyutuItemLevel,
					BigDecimal expenditureAmount,
					BigDecimal minorWasteExpenditure,
					BigDecimal severeWasteExpenditure,
					LocalDate paymentDate
				) {
			// 無駄遣い合計支出金額生成用に無駄遣い（軽度）支出金額のドメインオブジェクトを生成
			MinorWasteExpenditure minor = MinorWasteExpenditure.from(minorWasteExpenditure);
			// 無駄遣い合計支出金額生成用に無駄遣い（重度）支出金額のドメインオブジェクトを生成
			SevereWasteExpenditure severe = SevereWasteExpenditure.from(severeWasteExpenditure);
			// 月毎の支出項目明細ドメインモデルを生成して返す
			return new ExpenditureListItem(
					SisyutuItemCode.from(sisyutuItemCode),
					SisyutuItemName.from(sisyutuItemName),
					SisyutuItemLevel.from(sisyutuItemLevel),
					ExpenditureAmount.from(expenditureAmount),
					minor,
					severe,
					TotalWasteExpenditure.from(minor, severe),
					PaymentDate.from(paymentDate));
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
