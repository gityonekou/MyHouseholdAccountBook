/**
 * 収支登録画面の支出情報入力フォームデータです。
 * 入力した支出情報をもとに、支出一覧に登録及び対象の支出を更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/07/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

import lombok.Data;

/**
 *<pre>
 * 収支登録画面の支出情報入力フォームデータです。
 * 入力した支出情報をもとに、支出一覧に登録及び対象の支出を更新します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ExpenditureItemForm {
	// アクション
	private String action;
}
