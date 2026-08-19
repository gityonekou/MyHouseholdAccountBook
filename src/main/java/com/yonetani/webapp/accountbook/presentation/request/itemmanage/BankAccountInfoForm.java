/**
 * 情報管理(銀行口座)画面の銀行口座情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(銀行口座)画面の銀行口座情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Data
public class BankAccountInfoForm {
	// アクション
	private String action;
	// 銀行口座コード
	private String bankAccountCode;
	// 表示順(更新比較用)
	private String bankAccountSortBefore;

	// 銀行名
	@NotBlank
	@Size(min = 1, max = 50)
	private String bankName;
	// 口座メモ(任意項目)
	@Size(max = 100)
	private String bankAccountMemo;
	// 表示順
	@Min(1)
	@Max(99)
	private Integer bankAccountSort;
	// 有効/無効フラグ(デフォルトtrue)
	private Boolean enableFlg = Boolean.TRUE;
}
