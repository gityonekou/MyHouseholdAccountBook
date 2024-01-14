/**
 * 月毎の支出項目の取得結果(リスト情報)の値を表すドメインモデルです
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

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ClosingFlg;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SiharaiDate;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuB;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 月毎の支出項目の取得結果(リスト情報)の値を表すドメインモデルです
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
	 * 月毎の支出項目明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class ExpenditureItem {
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
		private final SiharaiDate siharaiDate;
		// 支払い済みフラグ
		private final ClosingFlg closingFlg;
		
		/**
		 *<pre>
		 * 引数の値から月毎の支出項目明細ドメインモデルを生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemLevel 支出項目レベル(1～5)
		 * @param sisyutuKingaku 支出金額
		 * @param sisyutuKingakuB 支出金額b
		 * @param siharaiDate 支払日
		 * @param closingFlg 支払い済みフラグ
		 * @return 月毎の支出項目明細
		 *
		 */
		public static ExpenditureItem from(
					String sisyutuItemCode,
					String sisyutuItemName,
					String sisyutuItemLevel,
					BigDecimal sisyutuKingaku,
					BigDecimal sisyutuKingakuB,
					LocalDate siharaiDate,
					boolean closingFlg
				) {
			return new ExpenditureItem(
					SisyutuItemCode.from(sisyutuItemCode),
					SisyutuItemName.from(sisyutuItemName),
					SisyutuItemLevel.from(sisyutuItemLevel),
					SisyutuKingaku.from(sisyutuKingaku),
					SisyutuKingakuB.from(sisyutuKingaku, sisyutuKingakuB),
					SiharaiDate.from(siharaiDate),
					ClosingFlg.from(closingFlg));
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuilder buff = new StringBuilder(200);
			buff.append("sisyutuItemCode:")
			.append(sisyutuItemCode)
			.append(",sisyutuItemName:")
			.append(sisyutuItemName)
			.append(",sisyutuItemLevel:")
			.append(sisyutuItemLevel)
			.append(",sisyutuKingaku:")
			.append(sisyutuKingaku)
			.append(",sisyutuKingakuB:")
			.append(sisyutuKingakuB)
			.append(",siharaiDate:")
			.append(siharaiDate)
			.append(",closingFlg:")
			.append(closingFlg);
			return buff.toString();
		}
	}
	
	// 月毎の支出項目明細のリスト
	private final List<ExpenditureItem> values;
	
	/**
	 *<pre>
	 * 月毎の支出項目明細(ドメインモデル)のリストからAccountMonthInquiryExpenditureItemListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 月毎の支出項目明細(ドメインモデル)のリスト
	 * @return AccountMonthInquiryExpenditureItemListのドメインモデル
	 *
	 */
	public static AccountMonthInquiryExpenditureItemList from(List<ExpenditureItem> values) {
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
			buff.append("月毎の支出項目リスト明細:")
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
			return "月毎の支出項目リスト明細:0件";
		}
	}
}
