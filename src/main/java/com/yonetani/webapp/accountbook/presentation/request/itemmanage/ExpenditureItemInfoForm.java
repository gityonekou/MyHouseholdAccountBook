/**
 * 情報管理(支出項目)画面の支出項目情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(支出項目)画面の支出項目情報が格納されたフォームデータデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ExpenditureItemInfoForm {
	// アクション
	private String action;
	// 支出項目コード
	private String sisyutuItemCode;
	
	// 支出項目名
	@NotBlank
	@Size(min = 1, max = 15)
	private String sisyutuItemName;
	
	// 支出項目詳細内容
	@Size(max = 200)
	private String sisyutuItemDetailContext;
	
	// 親支出項目コード
	private String parentSisyutuItemCode;
	// 支出項目レベル(1～5)
	private String sisyutuItemLevel;
	// 支出項目表示順
	private String sisyutuItemSort;
	// 更新可否フラグ
	private boolean enableUpdateFlg;
}
