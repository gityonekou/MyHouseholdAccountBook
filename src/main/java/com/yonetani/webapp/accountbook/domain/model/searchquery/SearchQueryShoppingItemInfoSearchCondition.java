/**
 * 商品テーブルを指定の検索条件で照会するための値を表すドメインモデルです。
 * ・ユーザID:必須
 *   ※以下は検索条件の値としてどれかが必須で設定
 * ・商品区分名：任意
 * ・商品名：任意
 * ・会社名：任意
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCompanyName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemKubunName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemName;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 商品テーブルを指定の検索条件で照会するための値を表すドメインモデルです。
 * ・ユーザID:必須
 *   ※以下は検索条件の値としてどれかが必須で設定
 * ・商品区分名：任意
 * ・商品名：任意
 * ・会社名：任意
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
public class SearchQueryShoppingItemInfoSearchCondition {
	// ユーザID
	private final UserId userId;
	// 商品区分名
	private final ShoppingItemKubunName shoppingItemKubunName;
	// 商品名
	private final ShoppingItemName shoppingItemName;
	// 会社名
	private final ShoppingItemCompanyName companyName;
	// 商品JANコード
	private final ShoppingItemJanCode shoppingItemJanCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID:必須
	 * ・商品区分名：任意(null可)
	 * ・商品名：任意(null可)
	 * ・会社名：任意(null可)
	 * ・商品JANコード：任意(null可)
	 * ※商品区分名、商品名、会社名のどれかは必須
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemKubunName 商品区分名
	 * @param shoppingItemName 商品名
	 * @param companyName 会社名
	 * @param shoppingItemJanCode 商品JANコード
	 * @return 検索条件(ユーザID, 商品区分名, 商品名, 会社名, 商品JANコード)
	 *
	 */
	public static SearchQueryShoppingItemInfoSearchCondition from(String userId, String shoppingItemKubunName, 
			String shoppingItemName, String companyName, String shoppingItemJanCode) {
		return new SearchQueryShoppingItemInfoSearchCondition(
						// ユーザID
						UserId.from(userId),
						// 商品区分名
						ShoppingItemKubunName.from(shoppingItemKubunName),
						// 商品名
						ShoppingItemName.from(shoppingItemName),
						// 会社名
						ShoppingItemCompanyName.from(companyName),
						// 商品JANコード
						ShoppingItemJanCode.from(shoppingItemJanCode));
	}
}
