/**
 * 固定費一覧情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/07 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentDay;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonthOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureItemName;

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
	// 奇数月支払金額合計
	private final FixedCostPaymentTotalAmount oddMonthGoukei;
	// 偶数月支払金額合計
	private final FixedCostPaymentTotalAmount anEvenMonthGoukei;
	
	/**
	 *<pre>
	 * 引数の値から固定費一覧情報の値を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values 固定費一覧明細リスト情報のリスト
	 * @return 固定費一覧情報を表すドメインモデル
	 *
	 */
	public static FixedCostInquiryList from(List<FixedCostInquiryItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new FixedCostInquiryList(
					// 固定費一覧明細情報のリスト:空
					Collections.emptyList(),
					// 奇数月合計=0
					FixedCostPaymentTotalAmount.ZERO,
					// 偶数月合計=0
					FixedCostPaymentTotalAmount.ZERO);
		} else {
			/* 各種合計値を計算 */
			// 奇数月合計
			FixedCostPaymentTotalAmount oddMonthGoukeiWk = FixedCostPaymentTotalAmount.ZERO;
			// 偶数月合計
			FixedCostPaymentTotalAmount anEvenMonthGoukeiWk = FixedCostPaymentTotalAmount.ZERO;
			
			// 固定費支払月（毎月、奇数月、偶数月、任意）の値に応じて固定費一覧明細リスト情報のリストの件数分
			// 支払金額の値を奇数月合計、偶数月合計の値に加算
			for(FixedCostInquiryItem item : values) {
				
				// 支払月が毎月、またはその他任意の場合、奇数・偶数をそれぞれ加算
				if(Objects.equals(item.getFixedCostTargetPaymentMonth().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE)
					|| Objects.equals(item.getFixedCostTargetPaymentMonth().getValue(),
							MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)) {
					oddMonthGoukeiWk = oddMonthGoukeiWk.add(item.getFixedCostPaymentAmount());
					anEvenMonthGoukeiWk = anEvenMonthGoukeiWk.add(item.getFixedCostPaymentAmount());
					
				// 支払月が奇数月の場合、奇数月を加算
				} else if(Objects.equals(item.getFixedCostTargetPaymentMonth().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE)) {
					oddMonthGoukeiWk = oddMonthGoukeiWk.add(item.getFixedCostPaymentAmount());
					
				// 支払月が偶数月の場合、偶数月を加算
				} else if(Objects.equals(item.getFixedCostTargetPaymentMonth().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE)) {
					anEvenMonthGoukeiWk = anEvenMonthGoukeiWk.add(item.getFixedCostPaymentAmount());
				}
			}
			return new FixedCostInquiryList(
					// 固定費一覧明細情報のリスト
					values,
					// 奇数月合計
					oddMonthGoukeiWk,
					// 偶数月合計
					anEvenMonthGoukeiWk);
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
