/**
 * 月毎の支出金額情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ShiharaiDate;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuB;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 月毎の支出金額情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountMonthInquiryExpenditureItemList {
	
	/**
	 *<pre>
	 * 月毎の支出金額情報明細です
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
	public static class ExpenditureListItem {
		// 支出項目コード
		private final SisyutuItemCode sisyutuItemCode;
		// 支出項目名
		private final SisyutuItemName sisyutuItemName;
		// 支出項目レベル(1～5)
		private final SisyutuItemLevel sisyutuItemLevel;
		// 支出金額
		private final SisyutuKingaku sisyutuKingaku;
		// 支出金額b(支出金額B, 割合)
		private final SisyutuKingakuB sisyutuKingakuB;
		// 支払日
		private final ShiharaiDate shiharaiDate;
		
		/**
		 *<pre>
		 * 引数の値から月毎の支出金額情報明細ドメインモデルを生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemLevel 支出項目レベル(1～5)
		 * @param sisyutuKingaku 支出金額
		 * @param sisyutuKingakuB 支出金額b
		 * @param siharaiDate 支払日
		 * @return 月毎の支出項目明細
		 *
		 */
		public static ExpenditureListItem from(
					String sisyutuItemCode,
					String sisyutuItemName,
					String sisyutuItemLevel,
					BigDecimal sisyutuKingaku,
					BigDecimal sisyutuKingakuB,
					LocalDate siharaiDate
				) {
			return new ExpenditureListItem(
					SisyutuItemCode.from(sisyutuItemCode),
					SisyutuItemName.from(sisyutuItemName),
					SisyutuItemLevel.from(sisyutuItemLevel),
					SisyutuKingaku.from(sisyutuKingaku),
					SisyutuKingakuB.from(sisyutuKingaku, sisyutuKingakuB),
					ShiharaiDate.from(siharaiDate));
		}
	}
	
	// 月毎の支出項目明細のリスト
	private final List<ExpenditureListItem> values;
	
	/**
	 *<pre>
	 * 月毎の支出金額情報明細(ドメインモデル)のリストからAccountMonthInquiryExpenditureItemListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 月毎の支出金額情報明細(ドメインモデル)のリスト
	 * @return AccountMonthInquiryExpenditureItemListのドメインモデル
	 *
	 */
	public static AccountMonthInquiryExpenditureItemList from(List<ExpenditureListItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AccountMonthInquiryExpenditureItemList(Collections.emptyList());
		} else {
			return new AccountMonthInquiryExpenditureItemList(values);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 200);
			buff.append("月毎の支出金額情報リスト明細:")
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
			return "月毎の支出金額情報リスト明細:0件";
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
