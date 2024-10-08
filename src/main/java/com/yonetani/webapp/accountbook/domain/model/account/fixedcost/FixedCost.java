/**
 * 固定費情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostKubun;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiDay;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTuki;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTukiOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ShiharaiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 固定費情報を表すドメインモデルです
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
public class FixedCost {
	// ユーザID
	private final UserId userId;
	// 固定費コード
	private final FixedCostCode fixedCostCode;
	// 固定費名(支払名)
	private final FixedCostName fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	private final FixedCostDetailContext fixedCostDetailContext;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 固定費区分
	private final FixedCostKubun fixedCostKubun;
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
	 * 引数の値から固定費情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param fixedCostCode 固定費コード
	 * @param fixedCostName 固定費名(支払名)
	 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
	 * @param sisyutuItemCode 支出項目コード
	 * @param fixedCostKubun 固定費区分
	 * @param fixedCostShiharaiTuki 固定費支払月(支払月)
	 * @param fixedCostShiharaiTukiOptionalContext 固定費支払月任意詳細
	 * @param fixedCostShiharaiDay 固定費支払日(支払日)
	 * @param shiharaiKingaku 支払金額
	 * @return 固定費情報を表すドメインモデル
	 *
	 */
	public static FixedCost from(
			String userId,
			String fixedCostCode,
			String fixedCostName,
			String fixedCostDetailContext,
			String sisyutuItemCode,
			String fixedCostKubun,
			String fixedCostShiharaiTuki,
			String fixedCostShiharaiTukiOptionalContext,
			String fixedCostShiharaiDay,
			BigDecimal shiharaiKingaku) {
		return new FixedCost(
				UserId.from(userId),
				FixedCostCode.from(fixedCostCode),
				FixedCostName.from(fixedCostName),
				FixedCostDetailContext.from(fixedCostDetailContext),
				SisyutuItemCode.from(sisyutuItemCode),
				FixedCostKubun.from(fixedCostKubun),
				FixedCostShiharaiTuki.from(fixedCostShiharaiTuki),
				FixedCostShiharaiTukiOptionalContext.from(fixedCostShiharaiTukiOptionalContext),
				FixedCostShiharaiDay.from(fixedCostShiharaiDay),
				ShiharaiKingaku.from(shiharaiKingaku));
	}
	
	/**
	 *<pre>
	 * 「固定費内容詳細(支払内容詳細)」項目と「固定費支払月任意詳細」項目の値から支出詳細の表示値を作成して返します。
	 * 
	 * 固定費内容詳細(支払内容詳細)項目、固定費支払月任意詳細のどちらかに値が設定されている場合、その値を返します。
	 * 固定費内容詳細(支払内容詳細)項目と固定費支払月任意詳細両方に値が設定されている場合、各値を「/」で連結した値を返します。
	 *</pre>
	 * @return 支出詳細の表示値
	 *
	 */
	public String getExpenditureDetailContext() {
		StringBuilder buff = new StringBuilder();
		// 固定費内容詳細(支払内容詳細)の値が設定されている場合、支出詳細の表示値に追加
		if(StringUtils.hasLength(fixedCostDetailContext.getValue())) {
			buff.append(fixedCostDetailContext.getValue());
		}
		// 固定費支払月任意詳細の値が設定されている場合、支出詳細の表示値に追加
		if(StringUtils.hasLength(fixedCostShiharaiTukiOptionalContext.getValue())) {
			// 固定費内容詳細(支払内容詳細)の値が設定済みの場合、区切り文字(/)を追加
			if(buff.length() > 0) {
				buff.append('/');
			}
			buff.append(fixedCostShiharaiTukiOptionalContext.getValue());
		}
		return buff.toString();
	}
}
