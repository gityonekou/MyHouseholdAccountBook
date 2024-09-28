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

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
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
	private final SyuunyuuKingaku syuunyuuKingaku;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final SisyutuKingaku sisyutuKingaku;
	// 収支金額
	private final SyuusiKingaku syuusiKingaku;
	
	/**
	 *<pre>
	 * 引数の値から収支テーブル情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param syuunyuuKingaku 収入金額
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
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			BigDecimal syuusiKingaku) {
		return new IncomeAndExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				SyuunyuuKingaku.from(syuunyuuKingaku),
				SisyutuYoteiKingaku.from(sisyutuYoteiKingaku),
				SisyutuKingaku.from(sisyutuKingaku),
				SyuusiKingaku.from(syuusiKingaku));
	}
	
	/**
	 *<pre>
	 * 値が空となるドメインモデルを生成して返します。
	 *</pre>
	 * @return
	 *
	 */
	public static IncomeAndExpenditureItem fromEmpty() {
		return new IncomeAndExpenditureItem(null, null, null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 引数の情報から対象月の収支テーブル情報を新規追加する場合の収支テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param incomeKingakuTotalAmount 対象月の収入金額合計
	 * @param sisyutuYoteiKingakuTotalAmount 対象月の支出予定金額合計
	 * @param sisyutuKingakuTotalAmount 対象月の支出金額合計
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createAddTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonthDomain,
			SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
			SisyutuYoteiKingakuTotalAmount sisyutuYoteiKingakuTotalAmount,
			SisyutuKingakuTotalAmount sisyutuKingakuTotalAmount) {
		
		// 収支金額(収入金額合計 - 支出金額合計)
		BigDecimal syuusiKingaku = incomeKingakuTotalAmount.getValue().subtract(sisyutuKingakuTotalAmount.getValue());
		
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
	 * @param expenditureKingakuTotalAmount 対象月の支出金額合計
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createUpdTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonthDomain,
			SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
			SisyutuKingakuTotalAmount expenditureKingakuTotalAmount) {
		
		// 収支金額(収入金額合計 - 支出金額合計)
		BigDecimal syuusiKingaku = incomeKingakuTotalAmount.getValue().subtract(expenditureKingakuTotalAmount.getValue());
		
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
}
