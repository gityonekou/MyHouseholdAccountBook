/**
 * 情報管理(商品)画面の商品情報が格納されたフォームデータデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import lombok.Data;

/**
 *<pre>
 * 情報管理(商品)画面の商品情報が格納されたフォームデータデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ShoppingItemInfoUpdateForm {
	// アクション
	private String action;
}
