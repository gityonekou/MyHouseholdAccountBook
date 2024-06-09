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

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTuki;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTukiOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ShiharaiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
				BigDecimal shiharaiKingaku
				) {
			return new FixedCostInquiryItem(
					FixedCostCode.from(fixedCostCode),
					FixedCostName.from(fixedCostName),
					FixedCostDetailContext.from(fixedCostDetailContext),
					SisyutuItemName.from(sisyutuItemName),
					FixedCostShiharaiTuki.from(fixedCostShiharaiTuki),
					FixedCostShiharaiTukiOptionalContext.from(fixedCostShiharaiTukiOptionalContext),
					ShiharaiKingaku.from(shiharaiKingaku));
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuilder buff = new StringBuilder(370);
			buff.append("fixedCostCode:")
			.append(fixedCostCode)
			.append(",fixedCostName:")
			.append(fixedCostName)
			.append(",fixedCostDetailContext:")
			.append(fixedCostDetailContext)
			.append(",sisyutuItemName:")
			.append(sisyutuItemName)
			.append(",fixedCostShiharaiTuki:")
			.append(fixedCostShiharaiTuki)
			.append(",fixedCostShiharaiTukiOptionalContext:")
			.append(fixedCostShiharaiTukiOptionalContext)
			.append(",shiharaiKingaku:")
			.append(shiharaiKingaku);
			return buff.toString();
		}
	}
	
	// 固定費一覧明細情報のリスト
	private final List<FixedCostInquiryItem> values;
	
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
			return new FixedCostInquiryList(Collections.emptyList());
		} else {
			return new FixedCostInquiryList(values);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 370);
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
