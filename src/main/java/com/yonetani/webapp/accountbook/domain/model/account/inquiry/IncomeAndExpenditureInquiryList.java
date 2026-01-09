/**
 * 指定年度の年間収支(マージ)情報のリストの値を表すドメインモデルです。
 * 各明細のリスト情報と合計値をラッピングしています。
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.BalanceTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.RegularIncomeTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingTotalAmount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 指定年度の年間収支(マージ)情報のリストの値を表すドメインモデルです。
 * 各明細のリスト情報と合計値をラッピングしています。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenditureInquiryList {
	
	// 年間収支(マージ)情報のリスト
	private final List<IncomeAndExpenditureItem> values;
	
	// 収入金額合計(積立金取崩金額以外の収入金額合計)
	private final RegularIncomeTotalAmount regularIncomeTotalAmount;
	// 積立金取崩金額合計
	private final WithdrawingTotalAmount withdrawingTotalAmount;
	// 支出予定金額合計
	private final ExpectedExpenditureTotalAmount expectedExpenditureTotalAmount;
	// 支出金額合計
	private final ExpenditureTotalAmount expenditureTotalAmount;
	// 収支金額合計
	private final BalanceTotalAmount balanceTotalAmount;
	
	/**
	 *<pre>
	 * 収支情報の値を表すドメインモデルのリストから収支情報の明細リストの値を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param values 収支情報の値を表すドメインモデルのリスト
	 * @return 明細リストの値を表すドメインモデル
	 *
	 */
	public static IncomeAndExpenditureInquiryList from(List<IncomeAndExpenditureItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new IncomeAndExpenditureInquiryList(
					Collections.emptyList(),
					RegularIncomeTotalAmount.ZERO,
					WithdrawingTotalAmount.NULL,
					ExpectedExpenditureTotalAmount.ZERO,
					ExpenditureTotalAmount.ZERO,
					BalanceTotalAmount.ZERO);
		} else {
			// 各種合計値を計算
			RegularIncomeTotalAmount regularIncomeAmountGoukei = RegularIncomeTotalAmount.ZERO;
			WithdrawingTotalAmount withdrewAmountGoukei = WithdrawingTotalAmount.NULL;
			ExpectedExpenditureTotalAmount sisyutuYoteiKingakuGoukei = ExpectedExpenditureTotalAmount.ZERO;
			ExpenditureTotalAmount expenditureAmountGoukei = ExpenditureTotalAmount.ZERO;
			BalanceTotalAmount balanceAmountGoukei = BalanceTotalAmount.ZERO;

			for(IncomeAndExpenditureItem item : values) {
				regularIncomeAmountGoukei = regularIncomeAmountGoukei.add(item.getRegularIncomeAmount());
				withdrewAmountGoukei = withdrewAmountGoukei.add(item.getWithdrawingAmount());
				sisyutuYoteiKingakuGoukei = sisyutuYoteiKingakuGoukei.add(item.getExpectedExpenditureAmount());
				expenditureAmountGoukei = expenditureAmountGoukei.add(item.getExpenditureAmount());
				balanceAmountGoukei = balanceAmountGoukei.add(item.getBalanceAmount());
			}
			return new IncomeAndExpenditureInquiryList(
					values,
					regularIncomeAmountGoukei,
					withdrewAmountGoukei,
					sisyutuYoteiKingakuGoukei,
					expenditureAmountGoukei,
					balanceAmountGoukei);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 2) * 130);
			buff.append("収支情報の明細リスト:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			buff.append("[[合計][regularIncomeTotalAmount:")
			.append(regularIncomeTotalAmount)
			.append(",withdrawingTotalAmount:")
			.append(withdrawingTotalAmount)
			.append(",sisyutuYoteiKingakuTotalAmount:")
			.append(expectedExpenditureTotalAmount)
			.append(",expenditureTotalAmount:")
			.append(expenditureTotalAmount)
			.append(",balanceTotalAmount:")
			.append(balanceTotalAmount)
			.append("]]");
			return buff.toString();
		} else {
			return "収支情報の明細リスト:0件";
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
