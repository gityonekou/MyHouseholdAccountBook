/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗区分コードのリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
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
 * ・店舗区分コードのリスト
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
public class SearchQueryUserIdAndShopKubunCodeList {
	// ユーザID
	private final UserId userId;
	// 店舗区分コードのリスト
	private final List<ShopKubunCode> shopKubunCodeList;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗区分コードのリスト
	 *</pre>
	 * @param userId ユーザID
	 * @param shopKubunCodeList 店舗区分コードのリスト
	 * @return 検索条件(ユーザID, 店舗区分コードのリスト(IN条件に指定する値)
	 *
	 */
	public static SearchQueryUserIdAndShopKubunCodeList from(UserId userId, List<ShopKubunCode> shopKubunCodeList) {
		if(CollectionUtils.isEmpty(shopKubunCodeList)) {
			// 店舗区分コードのリストは必須
			throw new MyHouseholdAccountBookRuntimeException("店舗区分コードのリストが未設定です");
		} else {
			return new SearchQueryUserIdAndShopKubunCodeList(userId, shopKubunCodeList);
		}
	}
}
