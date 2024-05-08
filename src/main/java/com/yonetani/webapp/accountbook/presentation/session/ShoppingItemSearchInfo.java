/**
 * セッションに設定する商品検索条件です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * セッションに設定する商品検索条件です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItemSearchInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// 検索タイプ(ActSearchShoppingItem　or ActSearchSisyutuItem)
	private final String searchActType;
	// 商品区分名
	private final String shoppingItemKubunName;
	// 商品名
	private final String shoppingItemName;
	// 会社名
	private final String companyName;
	// 支出項目コード
	private final String sisyutuItemCode;
	/**
	 *<pre>
	 * 引数の値から商品検索条件情報を生成して返します。
	 *</pre>
	 * @param searchActType 検索タイプ
	 * @param shoppingItemKubunName 商品区分名
	 * @param shoppingItemName 商品名
	 * @param companyName 会社名
	 * @param sisyutuItemCode 支出項目コード
	 * @return セッションに設定する商品検索条件
	 *
	 */
	public static ShoppingItemSearchInfo from(String searchActType, String shoppingItemKubunName, String shoppingItemName, String companyName, String sisyutuItemCode) {
		return new ShoppingItemSearchInfo(searchActType, shoppingItemKubunName, shoppingItemName, companyName, sisyutuItemCode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(40);
		buff.append("searchActType:")
		.append(searchActType)
		.append(",shoppingItemKubunName:")
		.append(shoppingItemKubunName)
		.append(",shoppingItemName:")
		.append(shoppingItemName)
		.append(",companyName:")
		.append(companyName)
		.append(",sisyutuItemCode:")
		.append(sisyutuItemCode);
		return buff.toString();
	}
}
