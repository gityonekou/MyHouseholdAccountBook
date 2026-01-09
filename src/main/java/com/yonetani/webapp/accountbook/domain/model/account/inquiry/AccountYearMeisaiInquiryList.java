/**
 * 指定年度の年間収支(明細)情報のリストの値を表すドメインモデルです。
 * 各明細のリスト情報と合計値をラッピングしています。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.BalanceTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.InsyokuNitiyouhinKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.IruiJyuukyoSetubiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.JigyouKeihiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiHikazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiKazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.MinorWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.RegularIncomeTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SevereWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyumiGotakuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WasteExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 指定年度の年間収支(明細)情報のリストの値を表すドメインモデルです。
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
public class AccountYearMeisaiInquiryList {
	/**
	 *<pre>
	 * 年間収支(明細)情報(ドメインモデル)です
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
	public static class MeisaiInquiryListItem {
		// 対象月
		private final TargetMonth month;
		// 収入金額(積立金取崩金額以外の収入金額)
		private final RegularIncomeAmount regularIncomeAmount;
		// 積立金取崩金額
		private final WithdrawingAmount withdrawingAmount;
		// 事業経費
		private final JigyouKeihiKingaku jigyouKeihiKingaku;
		// 固定(非課税)
		private final KoteiHikazeiKingaku koteiHikazeiKingaku;
		// 固定(課税)
		private final KoteiKazeiKingaku koteiKazeiKingaku;
		// 衣類住居設備
		private final IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingaku;
		// 飲食日用品
		private final InsyokuNitiyouhinKingaku insyokuNitiyouhinKingaku;
		// 趣味娯楽
		private final SyumiGotakuKingaku syumiGotakuKingaku;
		// 無駄遣い合計支出金額
		private final TotalWasteExpenditure totalWasteExpenditure;
		// 支出金額
		private final ExpenditureAmount expenditureAmount;
		// 収支金額
		private final BalanceAmount balanceAmount;
		
		/**
		 *<pre>
		 * 引数の値から年間収支(明細)情報のドメインモデルを生成して返します。
		 *</pre>
		 * @param month 対象月
		 * @param regularIncomeAmount 収入金額(積立金取崩金額以外の収入金額)
		 * @param withdrawingAmount 積立金取崩金額
		 * @param jigyouKeihiKingaku 事業経費
		 * @param koteiHikazeiKingaku 固定(非課税)
		 * @param koteiKazeiKingaku 固定(課税)
		 * @param iruiJyuukyoSetubiKingaku 衣類住居設備
		 * @param insyokuNitiyouhinKingaku 飲食日用品
		 * @param syumiGotakuKingaku 趣味娯楽
		 * @param minorWasteExpenditure 無駄遣い（軽度）支出金額
		 * @param severeWasteExpenditure 無駄遣い（重度）支出金額
		 * @param expenditureAmount 支出金額
		 * @param balanceAmount 収支金額
		 * @return 年間収支(明細)情報のドメインモデル
		 *
		 */
		public static MeisaiInquiryListItem from(
				String month,
				BigDecimal regularIncomeAmount,
				BigDecimal withdrawingAmount,
				BigDecimal jigyouKeihiKingaku,
				BigDecimal koteiHikazeiKingaku,
				BigDecimal koteiKazeiKingaku,
				BigDecimal iruiJyuukyoSetubiKingaku,
				BigDecimal insyokuNitiyouhinKingaku,
				BigDecimal syumiGotakuKingaku,
				BigDecimal minorWasteExpenditure,
				BigDecimal severeWasteExpenditure,
				BigDecimal expenditureAmount,
				BigDecimal balanceAmount
				) {
			return new MeisaiInquiryListItem(
					TargetMonth.from(month),
					RegularIncomeAmount.from(regularIncomeAmount),
					WithdrawingAmount.from(withdrawingAmount),
					JigyouKeihiKingaku.from(jigyouKeihiKingaku),
					KoteiHikazeiKingaku.from(koteiHikazeiKingaku),
					KoteiKazeiKingaku.from(koteiKazeiKingaku),
					IruiJyuukyoSetubiKingaku.from(iruiJyuukyoSetubiKingaku),
					InsyokuNitiyouhinKingaku.from(insyokuNitiyouhinKingaku),
					SyumiGotakuKingaku.from(syumiGotakuKingaku),
					TotalWasteExpenditure.from(MinorWasteExpenditure.from(minorWasteExpenditure), SevereWasteExpenditure.from(severeWasteExpenditure)),
					ExpenditureAmount.from(expenditureAmount),
					BalanceAmount.from(balanceAmount));
		}
	}
	
	// 年間収支(明細)情報のリスト
	private final List<MeisaiInquiryListItem> values;
	// 収入金額合計(積立金取崩金額合計以外の収入金額合計)
	private final RegularIncomeTotalAmount regularIncomeTotalAmount;
	// 積立金取崩金額合計
	private final WithdrawingTotalAmount withdrawingTotalAmount;
	// 事業経費合計
	private final JigyouKeihiKingaku jigyouKeihiKingakuGoukei;
	// 固定(非課税)合計
	private final KoteiHikazeiKingaku koteiHikazeiKingakuGoukei;
	// 固定(課税)合計
	private final KoteiKazeiKingaku koteiKazeiKingakuGoukei;
	// 衣類住居設備合計
	private final IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingakuGoukei;
	// 飲食日用品合計
	private final InsyokuNitiyouhinKingaku insyokuNitiyouhinKingakuGoukei;
	// 趣味娯楽合計
	private final SyumiGotakuKingaku syumiGotakuKingakuGoukei;
	// 無駄遣い合計支出金額の合計金額
	private final WasteExpenditureTotalAmount wasteExpenditureTotalAmount;
	// 支出金額合計
	private final ExpenditureTotalAmount expenditureTotalAmount;
	// 収支金額合計
	private final BalanceTotalAmount balanceTotalAmount;
	
	/**
	 *<pre>
	 * 年間収支(明細)情報(ドメインモデル)のリストからAccountYearMeisaiInquiryListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 年間収支(明細)情報(ドメインモデル)のリスト
	 * @return AccountYearMeisaiInquiryListのドメインモデル
	 *
	 */
	public static AccountYearMeisaiInquiryList from(List<MeisaiInquiryListItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AccountYearMeisaiInquiryList(
					Collections.emptyList(),
					RegularIncomeTotalAmount.ZERO,
					WithdrawingTotalAmount.NULL,
					JigyouKeihiKingaku.from(BigDecimal.ZERO),
					KoteiHikazeiKingaku.from(BigDecimal.ZERO),
					KoteiKazeiKingaku.from(BigDecimal.ZERO),
					IruiJyuukyoSetubiKingaku.from(BigDecimal.ZERO),
					InsyokuNitiyouhinKingaku.from(BigDecimal.ZERO),
					SyumiGotakuKingaku.from(BigDecimal.ZERO),
					WasteExpenditureTotalAmount.ZERO,
					ExpenditureTotalAmount.ZERO,
					BalanceTotalAmount.ZERO
					);
		} else {
			/* 各種合計値を計算 */
			// 収入金額合計(積立金取崩金額合計以外の収入金額合計)
			RegularIncomeTotalAmount regularIncomeAmountGoukei = RegularIncomeTotalAmount.ZERO;
			// 積立金取崩金額合計
			WithdrawingTotalAmount withdrawingAmountGoukei = WithdrawingTotalAmount.NULL;
			// 事業経費合計
			BigDecimal jigyouKeihiKingakuGoukei = BigDecimal.ZERO;
			// 固定(非課税)合計
			BigDecimal koteiHikazeiKingakuGoukei = BigDecimal.ZERO;
			// 固定(課税)合計
			BigDecimal koteiKazeiKingakuGoukei = BigDecimal.ZERO;
			// 衣類住居設備合計
			BigDecimal iruiJyuukyoSetubiKingakuGoukei = BigDecimal.ZERO;
			// 飲食日用品合計
			BigDecimal insyokuNitiyouhinKingakuGoukei = BigDecimal.ZERO;
			// 趣味娯楽合計
			BigDecimal syumiGotakuKingakuGoukei = BigDecimal.ZERO;
			// 無駄遣い合計支出金額の合計金額
			WasteExpenditureTotalAmount wasteExpenditureTotalAmountGoukei = WasteExpenditureTotalAmount.ZERO;
			// 支出金額合計
			ExpenditureTotalAmount expenditureAmountGoukei = ExpenditureTotalAmount.ZERO;
			// 収支金額合計
			BalanceTotalAmount balanceAmountGoukei = BalanceTotalAmount.ZERO;
			
			for(MeisaiInquiryListItem item : values) {
				regularIncomeAmountGoukei = regularIncomeAmountGoukei.add(item.getRegularIncomeAmount());
				withdrawingAmountGoukei = withdrawingAmountGoukei.add(item.getWithdrawingAmount());
				jigyouKeihiKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(jigyouKeihiKingakuGoukei, item.getJigyouKeihiKingaku().getValue());
				koteiHikazeiKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(koteiHikazeiKingakuGoukei, item.getKoteiHikazeiKingaku().getValue());
				koteiKazeiKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(koteiKazeiKingakuGoukei, item.getKoteiKazeiKingaku().getValue());
				iruiJyuukyoSetubiKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(iruiJyuukyoSetubiKingakuGoukei, item.getIruiJyuukyoSetubiKingaku().getValue());
				insyokuNitiyouhinKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(insyokuNitiyouhinKingakuGoukei, item.getInsyokuNitiyouhinKingaku().getValue());
				syumiGotakuKingakuGoukei = DomainCommonUtils.addBigDecimalNullSafe(syumiGotakuKingakuGoukei, item.getSyumiGotakuKingaku().getValue());
				wasteExpenditureTotalAmountGoukei = wasteExpenditureTotalAmountGoukei.add(item.getTotalWasteExpenditure());
				expenditureAmountGoukei = expenditureAmountGoukei.add(item.getExpenditureAmount());
				balanceAmountGoukei = balanceAmountGoukei.add(item.getBalanceAmount());
			}
			return new AccountYearMeisaiInquiryList(
					values,
					regularIncomeAmountGoukei,
					withdrawingAmountGoukei,
					JigyouKeihiKingaku.from(jigyouKeihiKingakuGoukei),
					KoteiHikazeiKingaku.from(koteiHikazeiKingakuGoukei),
					KoteiKazeiKingaku.from(koteiKazeiKingakuGoukei),
					IruiJyuukyoSetubiKingaku.from(iruiJyuukyoSetubiKingakuGoukei),
					InsyokuNitiyouhinKingaku.from(insyokuNitiyouhinKingakuGoukei),
					SyumiGotakuKingaku.from(syumiGotakuKingakuGoukei),
					wasteExpenditureTotalAmountGoukei,
					expenditureAmountGoukei,
					balanceAmountGoukei
					);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 2) * 400);
			buff.append("年間収支(明細)情報:")
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
			.append(",jigyouKeihiKingakuGoukei:")
			.append(jigyouKeihiKingakuGoukei)
			.append(",koteiHikazeiKingakuGoukei:")
			.append(koteiHikazeiKingakuGoukei)
			.append(",koteiKazeiKingakuGoukei:")
			.append(koteiKazeiKingakuGoukei)
			.append(",iruiJyuukyoSetubiKingakuGoukei:")
			.append(iruiJyuukyoSetubiKingakuGoukei)
			.append(",insyokuNitiyouhinKingakuGoukei:")
			.append(insyokuNitiyouhinKingakuGoukei)
			.append(",syumiGotakuKingakuGoukei:")
			.append(syumiGotakuKingakuGoukei)
			.append(",wasteExpenditureTotalAmount:")
			.append(wasteExpenditureTotalAmount)
			.append(",expenditureTotalAmount:")
			.append(expenditureTotalAmount)
			.append(",balanceTotalAmount:")
			.append(balanceTotalAmount)
			.append("]]");
			return buff.toString();
		} else {
			return "年間収支(明細)情報:0件";
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
