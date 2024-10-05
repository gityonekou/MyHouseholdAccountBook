/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・固定費支払月のリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTuki;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class SearchQueryUserIdAndFixedCostShiharaiTukiList {
	// ユーザID
	private final UserId userId;
	// 固定費支払月のリスト
	private final List<FixedCostShiharaiTuki> fixedCostShiharaiTukiList;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・固定費支払月のリスト
	 *</pre>
	 * @param userId ユーザID
	 * @param fixedCostShiharaiTukiList 固定費支払月のリスト
	 * @return 検索条件(ユーザID, 固定費支払月のリスト(IN条件に指定する値)
	 *
	 */
	public static SearchQueryUserIdAndFixedCostShiharaiTukiList from(UserId userId, List<FixedCostShiharaiTuki> fixedCostShiharaiTukiList) {
		if(CollectionUtils.isEmpty(fixedCostShiharaiTukiList)) {
			// 固定費支払月のリストは必須
			throw new MyHouseholdAccountBookRuntimeException("固定費支払月のリストが未設定です");
		} else {
			return new SearchQueryUserIdAndFixedCostShiharaiTukiList(userId, fixedCostShiharaiTukiList);
		}
	}
}
