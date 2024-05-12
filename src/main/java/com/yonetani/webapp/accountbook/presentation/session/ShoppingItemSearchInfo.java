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
	// 検索対象(検索条件が商品検索条件で商品を検索の場合の検索項目)
	private final String searchTargetKubun;
	// 検索条件(検索条件が商品検索条件で商品を検索の場合の検索項目)
	private final String searchValue;
	// 支出項目コード(検索条件が支出項目コードで商品を検索の場合の検索項目)
	private final String sisyutuItemCode;
	/**
	 *<pre>
	 * 引数の値から商品検索条件情報を生成して返します。
	 *</pre>
	 * @param searchActType 検索タイプ
	 * @param searchTargetKubun 検索対象(検索条件が商品検索条件で商品を検索の場合の検索項目)
	 * @param searchValue 検索条件(検索条件が商品検索条件で商品を検索の場合の検索項目)
	 * @param sisyutuItemCode 支出項目コード(検索条件が支出項目コードで商品を検索の場合の検索項目)
	 * @return セッションに設定する商品検索条件
	 *
	 */
	public static ShoppingItemSearchInfo from(String searchActType, String searchTargetKubun, String searchValue, String sisyutuItemCode) {
		return new ShoppingItemSearchInfo(searchActType, searchTargetKubun, searchValue, sisyutuItemCode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(40);
		buff.append("searchActType:")
		.append(searchActType)
		.append(",searchTargetKubun:")
		.append(searchTargetKubun)
		.append(",searchValue:")
		.append(searchValue)
		.append(",sisyutuItemCode:")
		.append(sisyutuItemCode);
		return buff.toString();
	}
}
