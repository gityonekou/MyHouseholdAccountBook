/**
 * 買い物登録(簡易タイプ)画面の買い物情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

import lombok.Data;

/**
 *<pre>
 * 買い物登録(簡易タイプ)画面の買い物情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class SimpleShoppingRegistInfoForm {
	// アクション
	private String action;
}
