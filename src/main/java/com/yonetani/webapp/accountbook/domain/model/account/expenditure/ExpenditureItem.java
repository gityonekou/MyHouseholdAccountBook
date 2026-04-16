/**
 * 支出テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.DeleteFlg;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出テーブル情報を表すドメインモデルです
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
public class ExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 支出コード
	private final ExpenditureCode expenditureCode;
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	// イベントコード
	private final ExpenditureEventCode expenditureEventCode;
	// 支出名称
	private final ExpenditureName expenditureName;
	// 支出区分
	private final ExpenditureCategory expenditureCategory;
	// 支出詳細
	private final ExpenditureDetailContext expenditureDetailContext;
	// 支払日
	private final PaymentDate paymentDate;
	// 支出予定金額
	private final ExpectedExpenditureAmount expectedExpenditureAmount;
	// 支出金額
	private final ExpenditureAmount expenditureAmount;
	// 削除フラグ
	private final DeleteFlg deleteFlg;
	
	/**
	 *<pre>
	 * 引数の値から支出テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param expenditureCode 支出コード
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureEventCode イベントコード
	 * @param expenditureName 支出名称
	 * @param expenditureCategory 支出区分
	 * @param expenditureDetailContext 支出詳細
	 * @param paymentDate 支払日
	 * @param expectedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param deleteFlg 削除フラグ
	 * @return 支出テーブル情報を表すドメインモデル
	 *
	 */
	public static ExpenditureItem from(
			String userId,
			String targetYear,
			String targetMonth,
			String expenditureCode,
			String expenditureItemCode,
			String expenditureEventCode,
			String expenditureName,
			String expenditureCategory,
			String expenditureDetailContext,
			LocalDate paymentDate,
			BigDecimal expectedExpenditureAmount,
			BigDecimal expenditureAmount,
			boolean deleteFlg) {
		
		// 支出テーブル情報を表すドメインモデルを生成して返却
		return new ExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				ExpenditureCode.from(expenditureCode),
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureEventCode.from(expenditureEventCode),
				ExpenditureName.from(expenditureName),
				ExpenditureCategory.from(expenditureCategory),
				ExpenditureDetailContext.from(expenditureDetailContext),
				PaymentDate.from(paymentDate),
				ExpectedExpenditureAmount.from(expectedExpenditureAmount),
				ExpenditureAmount.from(expenditureAmount),
				DeleteFlg.from(deleteFlg));
		
	}
	
	/**
	 *<pre>
	 * 引数の支出登録情報(セッション)から支出テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param initFlg 初期登録かどうかのフラグ　(収支登録確認画面からの遷移:true／各月の収支画面の更新ボタン押下からの遷移：false)
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param expenditureCode 支出コード
	 * @param expenditureData 支出登録情報(セッション)
	 * @return 支出テーブル情報(ドメイン)
	 *
	 */
	public static ExpenditureItem createExpenditureItem(boolean initFlg, UserId userId, TargetYearMonth yearMonthDomain,
			ExpenditureCode expenditureCode, ExpenditureRegistItem expenditureData) {
		
		// 支出予定金額：対象月の新規登録時のみ、支出金額の設定値を支出予定金額として設定。対象月の更新時は新規追加時を含めすべて0を設定
		BigDecimal expectedExpenditureAmount = (initFlg) ? expenditureData.getExpenditureKingaku() : ExpectedExpenditureAmount.ZERO.getValue();
		// 支出金額：固定費の対象データが初期値0フラグが設定していある場合は支出金額は0を設定、それ以外は設定されている支出金額を設定
		BigDecimal sisyutuKingaku = (expenditureData.isClearStartFlg()) ? ExpenditureAmount.ZERO.getValue() : expenditureData.getExpenditureKingaku();
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				yearMonthDomain.getYear(),
				// 対象月
				yearMonthDomain.getMonth(),
				// 支出コード
				expenditureCode.getValue(),
				// 支出項目コード
				expenditureData.getExpenditureItemCode(),
				// イベントコード
				expenditureData.getEventCode(),
				// 支出名称
				expenditureData.getExpenditureName(),
				// 支出区分
				expenditureData.getExpenditureCategory(),
				// 支出詳細
				expenditureData.getExpenditureDetailContext(),
				// 支払日
				PaymentDate.from(yearMonthDomain.getValue(), expenditureData.getSiharaiDate()).getValue(),
				// 支出予定金額
				expectedExpenditureAmount,
				// 支出金額
				sisyutuKingaku,
				// 削除フラグ
				false);
	}
	
	/**
	 *<pre>
	 * 現在の支出テーブル情報(ドメイン)情報の支出金額の値に引数で指定した支出金額を加算した結果を返します。
	 *</pre>
	 * @param addValue　加算する支出金額の値
	 * @return 支出金額を加算した支出テーブル情報(ドメイン)情報
	 *
	 */
	public ExpenditureItem addSisyutuKingaku(ExpenditureAmount addValue) {
		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 支出コード
				expenditureCode.getValue(),
				// 支出項目コード
				expenditureItemCode.getValue(),
				// イベントコード
				expenditureEventCode.getValue(),
				// 支出名称
				expenditureName.getValue(),
				// 支出区分
				expenditureCategory.getValue(),
				// 支出詳細
				expenditureDetailContext.getValue(),
				// 支払日
				paymentDate.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				expenditureAmount.add(addValue).getValue(),
				// 削除フラグ
				deleteFlg.getValue());
		
	}
	
	/**
	 *<pre>
	 * 現在の支出テーブル情報(ドメイン)情報の支出金額の値に引数で指定した支出金額を減算した結果を返します。
	 *</pre>
	 * @param subtractValue　減算する支出金額の値
	 * @return 支出金額を減算した支出テーブル情報(ドメイン)情報
	 *
	 */
	public ExpenditureItem subtractSisyutuKingaku(ExpenditureAmount subtractValue) {
		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 支出コード
				expenditureCode.getValue(),
				// 支出項目コード
				expenditureItemCode.getValue(),
				// イベントコード
				expenditureEventCode.getValue(),
				// 支出名称
				expenditureName.getValue(),
				// 支出区分
				expenditureCategory.getValue(),
				// 支出詳細
				expenditureDetailContext.getValue(),
				// 支払日
				paymentDate.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				expenditureAmount.subtract(subtractValue).getValue(),
				// 削除フラグ
				deleteFlg.getValue());
		
	}
}
