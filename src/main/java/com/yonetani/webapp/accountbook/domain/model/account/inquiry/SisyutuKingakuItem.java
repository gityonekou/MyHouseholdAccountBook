/**
 * 支出金額テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/08 : 1.00.00  新規作成
 * 2025/12/28 : 1.00.00  SisyutuShiharaiDateをPaymentDateに置き換え
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.MinorWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ParentSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SevereWasteExpenditure;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出金額テーブル情報を表すドメインモデルです
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
public class SisyutuKingakuItem {

	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 親支出項目コード
	private final ParentSisyutuItemCode parentSisyutuItemCode;
	// 支出予定金額
	private final ExpectedExpenditureAmount expectedExpenditureAmount;
	// 支出金額
	private final ExpenditureAmount expenditureAmount;
	// 無駄遣い（軽度）支出金額
	private final MinorWasteExpenditure minorWasteExpenditure;
	// 無駄遣い（重度）支出金額
	private final SevereWasteExpenditure severeWasteExpenditure;
	// 支出支払日
	private final PaymentDate paymentDate;
	
	/**
	 *<pre>
	 * 引数の値から支出金額テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param sisyutuItemCode 支出項目コード
	 * @param parentSisyutuItemCode 親支出項目コード
	 * @param expectedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param minorWasteExpenditure 無駄遣い（軽度）支出金額
	 * @param severeWasteExpenditure 無駄遣い（重度）支出金額
	 * @param paymentDate 支出支払日
	 * @return 支出金額テーブル情報を表すドメインモデル
	 *
	 */
	public static SisyutuKingakuItem from(
			String userId,
			String targetYear,
			String targetMonth,
			String sisyutuItemCode,
			String parentSisyutuItemCode,
			BigDecimal expectedExpenditureAmount,
			BigDecimal expenditureAmount,
			BigDecimal minorWasteExpenditure,
			BigDecimal severeWasteExpenditure,
			LocalDate paymentDate) {
		// 支出金額テーブル情報ドメインモデルを生成して返却
		return new SisyutuKingakuItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				SisyutuItemCode.from(sisyutuItemCode),
				ParentSisyutuItemCode.from(parentSisyutuItemCode),
				ExpectedExpenditureAmount.from(expectedExpenditureAmount),
				ExpenditureAmount.from(expenditureAmount),
				MinorWasteExpenditure.from(minorWasteExpenditure),
				SevereWasteExpenditure.from(severeWasteExpenditure),
				PaymentDate.from(paymentDate));
		
	}
}
