/**
 * 固定費一覧情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiDay;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTuki;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTukiOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ShiharaiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 固定費一覧情報の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCostInquiryList {
	
	/**
	 *<pre>
	 * 固定費一覧明細情報(ドメイン)です
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
	public static class FixedCostInquiryItem {
		// 固定費コード
		private final FixedCostCode fixedCostCode;
		// 固定費名(支払名)
		private final FixedCostName fixedCostName;
		// 固定費内容詳細(支払内容詳細)
		private final FixedCostDetailContext fixedCostDetailContext;
		// 支出項目名
		private final SisyutuItemName sisyutuItemName;
		// 固定費支払月(支払月)
		private final FixedCostShiharaiTuki fixedCostShiharaiTuki;
		// 固定費支払月任意詳細
		private final FixedCostShiharaiTukiOptionalContext fixedCostShiharaiTukiOptionalContext;
		// 固定費支払日(支払日)
		private final FixedCostShiharaiDay fixedCostShiharaiDay;
		// 支払金額
		private final ShiharaiKingaku shiharaiKingaku;
		
		/**
		 *<pre>
		 * 引数の値から固定費一覧明細情報を表すドメインモデルを生成して返します。
		 *</pre>
		 * @param fixedCostCode 固定費コード
		 * @param fixedCostName 固定費名(支払名)
		 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
		 * @param sisyutuItemName 支出項目名
		 * @param fixedCostShiharaiTuki 固定費支払月(支払月)
		 * @param fixedCostShiharaiTukiOptionalContext 固定費支払月任意詳細
		 * @param fixedCostShiharaiDay 固定費支払日(支払日)
		 * @param shiharaiKingaku 支払金額
		 * @return 固定費一覧明細情報を表すドメインモデル
		 *
		 */
		public static FixedCostInquiryItem from(
				String fixedCostCode,
				String fixedCostName,
				String fixedCostDetailContext,
				String sisyutuItemName,
				String fixedCostShiharaiTuki,
				String fixedCostShiharaiTukiOptionalContext,
				String fixedCostShiharaiDay,
				BigDecimal shiharaiKingaku
				) {
			return new FixedCostInquiryItem(
					FixedCostCode.from(fixedCostCode),
					FixedCostName.from(fixedCostName),
					FixedCostDetailContext.from(fixedCostDetailContext),
					SisyutuItemName.from(sisyutuItemName),
					FixedCostShiharaiTuki.from(fixedCostShiharaiTuki),
					FixedCostShiharaiTukiOptionalContext.from(fixedCostShiharaiTukiOptionalContext),
					FixedCostShiharaiDay.from(fixedCostShiharaiDay),
					ShiharaiKingaku.from(shiharaiKingaku));
		}
	}
	
	// 固定費一覧明細情報のリスト
	private final List<FixedCostInquiryItem> values;
	// 奇数月合計
	private final ShiharaiKingaku oddMonthGoukei;
	// 偶数月合計
	private final ShiharaiKingaku anEvenMonthGoukei;
	
	/**
	 *<pre>
	 * 引数の値から固定費一覧情報の値を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values 固定費一覧明細リスト情報のリスト
	 * @return 固定費一覧情報を表すドメインモデル
	 *
	 */
	public static FixedCostInquiryList from(List<FixedCostInquiryItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new FixedCostInquiryList(
					// 固定費一覧明細情報のリスト:空
					Collections.emptyList(),
					// 奇数月合計=0
					ShiharaiKingaku.from(BigDecimal.ZERO),
					// 偶数月合計=0
					ShiharaiKingaku.from(BigDecimal.ZERO));
		} else {
			/* 各種合計値を計算 */
			// 奇数月合計
			BigDecimal oddMonthGoukeiWk = BigDecimal.ZERO;
			// 偶数月合計
			BigDecimal anEvenMonthGoukeiWk = BigDecimal.ZERO;
			// 固定費支払月（毎月、奇数月、偶数月、任意）の値に応じて固定費一覧明細リスト情報のリストの件数分
			// 支払金額の値を奇数月合計、偶数月合計の値に加算
			for(FixedCostInquiryItem item : values) {
				
				// 支払月が毎月、またはその他任意の場合、奇数・偶数をそれぞれ加算
				if(Objects.equals(item.getFixedCostShiharaiTuki().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE)
					|| Objects.equals(item.getFixedCostShiharaiTuki().getValue(),
							MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)) {
					oddMonthGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(oddMonthGoukeiWk, item.getShiharaiKingaku().getValue());
					anEvenMonthGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(anEvenMonthGoukeiWk, item.getShiharaiKingaku().getValue());
					
				// 支払月が奇数月の場合、奇数月を加算
				} else if(Objects.equals(item.getFixedCostShiharaiTuki().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE)) {
					oddMonthGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(oddMonthGoukeiWk, item.getShiharaiKingaku().getValue());
					
				// 支払月が偶数月の場合、偶数月を加算
				} else if(Objects.equals(item.getFixedCostShiharaiTuki().getValue(),
						MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE)) {
					anEvenMonthGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(anEvenMonthGoukeiWk, item.getShiharaiKingaku().getValue());
				}
			}
			return new FixedCostInquiryList(
					// 固定費一覧明細情報のリスト
					values,
					// 奇数月合計
					ShiharaiKingaku.from(oddMonthGoukeiWk),
					// 偶数月合計
					ShiharaiKingaku.from(anEvenMonthGoukeiWk));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 385);
			buff.append("固定費一覧:")
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
			return "固定費一覧:0件";
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
