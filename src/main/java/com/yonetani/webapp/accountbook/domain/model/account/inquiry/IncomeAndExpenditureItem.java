/**
 * 収支テーブル情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収支テーブル情報の値を表すドメインモデルです
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
public class IncomeAndExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 収入金額
	private final IncomeAmount syuunyuuKingaku;
	// 積立金取崩金額
	private final WithdrewKingaku withdrewKingaku;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final ExpenditureAmount sisyutuKingaku;
	// 収支金額
	private final BalanceAmount syuusiKingaku;
	
	/**
	 *<pre>
	 * 引数の値から収支テーブル情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param syuunyuuKingaku 収入金額
	 * @param withdrewKingaku 積立金取崩金額
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @param sisyutuKingaku 支出金額
	 * @param syuusiKingaku 収支金額
	 * @return 収支テーブル情報を表すドメインモデル
	 *
	 */
	public static IncomeAndExpenditureItem from(
			String userId,
			String targetYear,
			String targetMonth,
			BigDecimal syuunyuuKingaku,
			BigDecimal withdrewKingaku,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			BigDecimal syuusiKingaku) {
		return new IncomeAndExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				IncomeAmount.from(syuunyuuKingaku),
				WithdrewKingaku.from(withdrewKingaku),
				SisyutuYoteiKingaku.from(sisyutuYoteiKingaku),
				ExpenditureAmount.from(sisyutuKingaku),
				BalanceAmount.from(syuusiKingaku));
	}
	
	/**
	 *<pre>
	 * 値が空となるドメインモデルを生成して返します。
	 *</pre>
	 * @return
	 *
	 */
	public static IncomeAndExpenditureItem fromEmpty() {
		return new IncomeAndExpenditureItem(null, null, null, null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 引数の情報から対象月の収支テーブル情報を新規追加する場合の収支テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param incomeKingakuTotalAmount 対象月の収入金額合計
	 * @param withdrewKingakuTotalAmount 対象月の積立金取崩金額合計
	 * @param sisyutuYoteiKingakuTotalAmount 対象月の支出予定金額合計
	 * @param sisyutuKingakuTotalAmount 対象月の支出金額合計
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createAddTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonthDomain,
			SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
			WithdrewKingakuTotalAmount withdrewKingakuTotalAmount,
			SisyutuYoteiKingakuTotalAmount sisyutuYoteiKingakuTotalAmount,
			SisyutuKingakuTotalAmount sisyutuKingakuTotalAmount) {
		
		// 入り方金額 = 収入金額合計 + 積立金取崩金額合計
		BigDecimal incomingAmount = incomeKingakuTotalAmount.getValue().add(withdrewKingakuTotalAmount.getNullSafeValue());
		// 収支金額 = 入り方金額 - 支出金額合計
		BigDecimal syuusiKingaku = incomingAmount.subtract(sisyutuKingakuTotalAmount.getValue());
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				yearMonthDomain.getYear(),
				// 対象月
				yearMonthDomain.getMonth(),
				// 収入金額
				incomeKingakuTotalAmount.getValue(),
				// 積立金取崩金額
				withdrewKingakuTotalAmount.getValue(),
				// 支出予定金額
				sisyutuYoteiKingakuTotalAmount.getValue(),
				// 支出金額
				sisyutuKingakuTotalAmount.getValue(),
				// 収支金額
				syuusiKingaku);
	}
	
	/**
	 *<pre>
	 * 引数の情報から収支テーブルを更新する場合の収支テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param incomeKingakuTotalAmount 対象月の収入金額合計
	 * @param withdrewKingakuTotalAmount 対象月の積立金取崩金額合計
	 * @param expenditureKingakuTotalAmount 対象月の支出金額合計
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createUpdTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonthDomain,
			SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
			WithdrewKingakuTotalAmount withdrewKingakuTotalAmount,
			SisyutuKingakuTotalAmount expenditureKingakuTotalAmount) {
		
		// 入り方金額 = 収入金額合計 + 積立金取崩金額合計
		BigDecimal incomingAmount = incomeKingakuTotalAmount.getValue().add(withdrewKingakuTotalAmount.getNullSafeValue());
		// 収支金額 = 入り方金額 - 支出金額合計
		BigDecimal syuusiKingaku = incomingAmount.subtract(expenditureKingakuTotalAmount.getValue());
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				yearMonthDomain.getYear(),
				// 対象月
				yearMonthDomain.getMonth(),
				// 収入金額
				incomeKingakuTotalAmount.getValue(),
				// 積立金取崩金額
				withdrewKingakuTotalAmount.getValue(),
				// 支出予定金額
				SisyutuYoteiKingakuTotalAmount.ZERO.getValue(),
				// 支出金額
				expenditureKingakuTotalAmount.getValue(),
				// 収支金額
				syuusiKingaku);
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		if(userId == null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 *<pre>
	 * 現在の収支テーブル情報(ドメイン)の支出金額の値に引数で指定した支出金額を加算した結果を返します。
	 *</pre>
	 * @param addValue　加算する支出金額の値
	 * @return 支出金額を加算した収支テーブル情報(ドメイン)
	 *
	 */
	public IncomeAndExpenditureItem addSisyutuKingaku(ExpenditureAmount addValue) {

		// 新しい支出金額
		ExpenditureAmount addSisyutuKingaku = sisyutuKingaku.add(addValue);
		// 入り方金額 = 収入金額 + 積立金取崩金額
		BigDecimal incomingAmount = syuunyuuKingaku.getValue().add(withdrewKingaku.getNullSafeValue());
		// 新しい収支金額(入り方金額 - 支出金額合計)
		BigDecimal syuusiKingaku = incomingAmount.subtract(addSisyutuKingaku.getValue());
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 収入金額
				syuunyuuKingaku.getValue(),
				// 積立金取崩金額
				withdrewKingaku.getValue(),
				// 支出予定金額
				sisyutuYoteiKingaku.getValue(),
				// 支出金額
				addSisyutuKingaku.getValue(),
				// 収支金額
				syuusiKingaku);
	}
	
	/**
	 *<pre>
	 * 現在の収支テーブル情報(ドメイン)の支出金額の値に引数で指定した支出金額を減算した結果を返します。
	 *</pre>
	 * @param subtractValue　減算する支出金額の値
	 * @return 支出金額を減算した収支テーブル情報(ドメイン)
	 *
	 */
	public IncomeAndExpenditureItem subtractSisyutuKingaku(ExpenditureAmount subtractValue) {

		// 新しい支出金額
		ExpenditureAmount subtractSisyutuKingaku = sisyutuKingaku.subtract(subtractValue);
		// 入り方金額 = 収入金額 + 積立金取崩金額
		BigDecimal incomingAmount = syuunyuuKingaku.getValue().add(withdrewKingaku.getNullSafeValue());
		// 新しい収支金額(入り方金額 - 支出金額)
		BigDecimal syuusiKingaku = incomingAmount.subtract(subtractSisyutuKingaku.getValue());
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 収入金額
				syuunyuuKingaku.getValue(),
				// 積立金取崩金額
				withdrewKingaku.getValue(),
				// 支出予定金額
				sisyutuYoteiKingaku.getValue(),
				// 支出金額
				subtractSisyutuKingaku.getValue(),
				// 収支金額
				syuusiKingaku);
	}
}
