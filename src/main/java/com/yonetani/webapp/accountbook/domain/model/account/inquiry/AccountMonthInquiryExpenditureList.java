/**
 * 月毎の支出情報(支出別一覧)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/06/13 : 1.02.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 月毎の支出情報(支出別一覧)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.02)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountMonthInquiryExpenditureList {

	/**
	 *<pre>
	 * 月毎の支出情報(支出別一覧)明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.02)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class ExpenditureRow {
		// 支出コード（訂正ボタンの POST パラメータ用）
		private final ExpenditureCode expenditureCode;
		// 支出名称
		private final ExpenditureName expenditureName;
		// 支出区分
		private final ExpenditureCategory expenditureCategory;
		// 支払日
		private final PaymentDate paymentDate;
		// 支出金額
		private final ExpenditureAmount expenditureAmount;
		// 支出詳細
		private final ExpenditureDetailContext expenditureDetailContext;

		/**
		 *<pre>
		 * 支出テーブル情報(ドメイン)から月毎の支出情報(支出別一覧)明細を生成して返します。
		 *</pre>
		 * @param item 支出テーブル情報(ドメイン)
		 * @return 月毎の支出情報(支出別一覧)明細
		 *
		 */
		public static ExpenditureRow from(ExpenditureItem item) {
			return new ExpenditureRow(
					item.getExpenditureCode(),
					item.getExpenditureName(),
					item.getExpenditureCategory(),
					item.getPaymentDate(),
					item.getExpenditureAmount(),
					item.getExpenditureDetailContext());
		}
	}

	// 支出別一覧の明細リスト
	private final List<ExpenditureRow> values;
	// 支出合計金額
	private final ExpenditureTotalAmount totalAmount;

	/**
	 *<pre>
	 * 支出テーブル情報リスト(ドメイン)から月毎の支出情報(支出別一覧)ドメインモデルを生成して返します。
	 * 支出合計金額はリスト内の各支出金額の合計として計算されます。
	 *</pre>
	 * @param list 支出テーブル情報リスト(ドメイン)
	 * @return 月毎の支出情報(支出別一覧)ドメインモデル
	 *
	 */
	public static AccountMonthInquiryExpenditureList from(ExpenditureItemInquiryList list) {
		if (CollectionUtils.isEmpty(list.getValues())) {
			return new AccountMonthInquiryExpenditureList(Collections.emptyList(), ExpenditureTotalAmount.ZERO);
		}
		// ExpenditureItem → ExpenditureRow に変換しつつ合計金額を計算
		ExpenditureTotalAmount total = ExpenditureTotalAmount.ZERO;
		List<ExpenditureRow> rows = new java.util.ArrayList<>();
		for (ExpenditureItem item : list.getValues()) {
			rows.add(ExpenditureRow.from(item));
			total = total.add(item.getExpenditureAmount());
		}
		return new AccountMonthInquiryExpenditureList(
				Collections.unmodifiableList(rows), total);
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
