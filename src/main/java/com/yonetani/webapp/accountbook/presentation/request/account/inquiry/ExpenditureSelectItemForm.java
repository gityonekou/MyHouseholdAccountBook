/**
 * 収支登録の支出項目選択画面で選択した支出項目・イベント情報のフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

import lombok.Data;

/**
 *<pre>
 * 収支登録の支出項目選択画面で選択した支出項目・イベント情報のフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ExpenditureSelectItemForm {
	// 支出項目コード
	private String sisyutuItemCode;
	// イベントコード
	private String eventCode;
}
