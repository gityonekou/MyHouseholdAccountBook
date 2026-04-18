/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・固定費支払月のリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/05 : 1.00.00  新規作成
 * 2026/04/05 : 1.01.00  クラス名をSearchQueryUserIdAndFixedCostShiharaiTukiListからSearchQueryUserIdAndFixedCostTargetPaymentMonthListにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・固定費支払月のリスト
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
public class SearchQueryUserIdAndFixedCostTargetPaymentMonthList {
	// ユーザID
	private final UserId userId;
	// 固定費支払月のリスト
	private final List<FixedCostTargetPaymentMonth> fixedCostTargetPaymentMonthList;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・固定費支払月のリスト
	 *</pre>
	 * @param userId ユーザID
	 * @param fixedCostTargetPaymentMonthList 固定費支払月のリスト
	 * @return 検索条件(ユーザID, 固定費支払月のリスト(IN条件に指定する値)
	 *
	 */
	public static SearchQueryUserIdAndFixedCostTargetPaymentMonthList from(UserId userId, List<FixedCostTargetPaymentMonth> fixedCostTargetPaymentMonthList) {
		if(CollectionUtils.isEmpty(fixedCostTargetPaymentMonthList)) {
			// 固定費支払月のリストは必須
			throw new MyHouseholdAccountBookRuntimeException("固定費支払月のリストが未設定です");
		} else {
			return new SearchQueryUserIdAndFixedCostTargetPaymentMonthList(userId, fixedCostTargetPaymentMonthList);
		}
	}
}
