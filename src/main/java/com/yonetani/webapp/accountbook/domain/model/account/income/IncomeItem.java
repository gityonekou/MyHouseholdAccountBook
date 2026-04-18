/**
 * 収入テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.income;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeCategory;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeCode;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeDetailContext;
import com.yonetani.webapp.accountbook.domain.type.common.DeleteFlg;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収入テーブル情報を表すドメインモデルです
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
public class IncomeItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 収入コード
	private final IncomeCode incomeCode;
	// 収入区分
	private final IncomeCategory incomeCategory;
	// 収入詳細
	private final IncomeDetailContext incomeDetailContext;
	// 収入金額
	private final IncomeAmount incomeAmount;
	// 削除フラグ
	private final DeleteFlg deleteFlg;
	
	/**
	 *<pre>
	 * 引数の値から収入テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param incomeCode 収入コード
	 * @param incomeCategory 収入区分
	 * @param incomeDetailContext 収入詳細
	 * @param incomeAmount 収入金額
	 * @param deleteFlg 削除フラグ
	 * @return 収入テーブル情報を表すドメインモデル
	 *
	 */
	public static IncomeItem from(
			String userId,
			String targetYear,
			String targetMonth,
			String incomeCode,
			String incomeCategory,
			String incomeDetailContext,
			BigDecimal incomeAmount,
			boolean deleteFlg) {
		return new IncomeItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				IncomeCode.from(incomeCode),
				IncomeCategory.from(incomeCategory),
				IncomeDetailContext.from(incomeDetailContext),
				IncomeAmount.from(incomeAmount),
				DeleteFlg.from(deleteFlg));
		
	}
	
	/**
	 *<pre>
	 * 引数の収支登録情報(セッション)から収入テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param incomeCode 収入コード
	 * @param incomeData 収支登録情報(セッション)
	 * @return 収入テーブル情報(ドメイン)
	 *
	 */
	public static IncomeItem createIncomeItem(UserId userId, TargetYearMonth yearMonthDomain, IncomeCode incomeCode, IncomeRegistItem incomeData) {
		return IncomeItem.from(
				// ユーザID
				userId.getValue(),
				// 対象年
				yearMonthDomain.getYear(),
				// 対象月
				yearMonthDomain.getMonth(),
				// 収入コード
				incomeCode.getValue(),
				// 収入区分
				incomeData.getIncomeCategory(),
				// 収入詳細
				incomeData.getIncomeDetailContext(),
				// 収入金額
				incomeData.getIncomeKingaku(),
				// 削除フラグ
				false);
	}
}
