/**
 * 固定費一覧情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/07 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/05/07 : 1.01.01  合計フィールド廃止・calculateMonthlyTotal()メソッド追加
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentDay;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonthOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 固定費一覧情報の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostInquiryList {

	/**
	 *<pre>
	 * 固定費一覧明細情報(ドメイン)です
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
	public static class FixedCostInquiryItem {
		// 固定費コード
		private final FixedCostCode fixedCostCode;
		// 固定費名(支払名)
		private final FixedCostName fixedCostName;
		// 固定費内容詳細(支払内容詳細)
		private final FixedCostDetailContext fixedCostDetailContext;
		// 支出項目名
		private final ExpenditureItemName expenditureItemName;
		// 固定費支払月(支払月)
		private final FixedCostTargetPaymentMonth fixedCostTargetPaymentMonth;
		// 固定費支払月任意詳細
		private final FixedCostTargetPaymentMonthOptionalContext fixedCostTargetPaymentMonthOptionalContext;
		// 固定費支払日(支払日)
		private final FixedCostPaymentDay fixedCostPaymentDay;
		// 支払金額
		private final FixedCostPaymentAmount fixedCostPaymentAmount;

		/**
		 *<pre>
		 * 引数の値から固定費一覧明細情報を表すドメインモデルを生成して返します。
		 *</pre>
		 * @param fixedCostCode 固定費コード
		 * @param fixedCostName 固定費名(支払名)
		 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
		 * @param expenditureItemName 支出項目名
		 * @param fixedCostTargetPaymentMonth 固定費支払月(支払月)
		 * @param fixedCostTargetPaymentMonthOptionalContext 固定費支払月任意詳細
		 * @param fixedCostPaymentDay 固定費支払日(支払日)
		 * @param fixedCostPaymentAmount 支払金額
		 * @return 固定費一覧明細情報を表すドメインモデル
		 *
		 */
		public static FixedCostInquiryItem from(
				String fixedCostCode,
				String fixedCostName,
				String fixedCostDetailContext,
				String expenditureItemName,
				String fixedCostTargetPaymentMonth,
				String fixedCostTargetPaymentMonthOptionalContext,
				String fixedCostPaymentDay,
				BigDecimal fixedCostPaymentAmount
				) {
			return new FixedCostInquiryItem(
					FixedCostCode.from(fixedCostCode),
					FixedCostName.from(fixedCostName),
					FixedCostDetailContext.from(fixedCostDetailContext),
					ExpenditureItemName.from(expenditureItemName),
					FixedCostTargetPaymentMonth.from(fixedCostTargetPaymentMonth),
					FixedCostTargetPaymentMonthOptionalContext.from(fixedCostTargetPaymentMonthOptionalContext),
					FixedCostPaymentDay.from(fixedCostPaymentDay),
					FixedCostPaymentAmount.from(fixedCostPaymentAmount));
		}
	}

	// 固定費一覧明細情報のリスト
	private final List<FixedCostInquiryItem> values;

	/**
	 *<pre>
	 * 引数の値から固定費一覧情報の値を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param values 固定費一覧明細リスト情報のリスト
	 * @return 固定費一覧情報を表すドメインモデル
	 *
	 */
	public static FixedCostInquiryList from(List<FixedCostInquiryItem> values) {
		if (CollectionUtils.isEmpty(values)) {
			return new FixedCostInquiryList(Collections.emptyList());
		} else {
			return new FixedCostInquiryList(values);
		}
	}

	/**
	 *<pre>
	 * 指定した対象月における固定費支払金額の合計を計算して返します。
	 * 固定費支払月の設定値に応じて、対象月に支払いが発生するかどうかを判定して合計を算出します。
	 *</pre>
	 * @param targetMonth 合計を算出する対象月
	 * @return 指定した対象月の固定費支払金額合計
	 *
	 */
	public FixedCostPaymentTotalAmount calculateMonthlyTotal(TargetYearMonth targetMonth) {
		if (CollectionUtils.isEmpty(values)) {
			return FixedCostPaymentTotalAmount.ZERO;
		}
		int monthValue = Integer.parseInt(targetMonth.getMonth());
		FixedCostPaymentTotalAmount total = FixedCostPaymentTotalAmount.ZERO;
		for (FixedCostInquiryItem item : values) {
			if (shouldAdd(item.getFixedCostTargetPaymentMonth().getValue(), monthValue)) {
				total = total.add(item.getFixedCostPaymentAmount());
			}
		}
		return total;
	}

	/**
	 * 固定費支払月コードと対象月の値から、その月に支払いが発生するかどうかを返します。
	 */
	private static boolean shouldAdd(String shiharaiTukiCode, int monthValue) {
		switch (shiharaiTukiCode) {
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE:    // "00" 毎月
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE: // "40" その他任意
				return true;
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE:      // "20" 奇数月
				return monthValue % 2 == 1;
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE:  // "30" 偶数月
				return monthValue % 2 == 0;
			default:
				// "01"〜"12"：特定月指定
				return Integer.parseInt(shiharaiTukiCode) == monthValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 385);
			buff.append("固定費一覧:")
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
			return "固定費一覧:0件";
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
