/**
 * 情報管理(固定費)一括更新画面の固定費情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/01 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *<pre>
 * 情報管理(固定費)一括更新画面の固定費情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@Data
public class FixedCostBulkUpdateForm {

	// 基準固定費コード（起点となった選択固定費のコード）
	private String baseFixedCostCode;

	// 一括適用する支払日（必須）
	@NotBlank
	private String shiharaiDay;

	// 一括適用する支払金額（必須、1以上）
	@NotNull
	@Min(1)
	private Integer shiharaiKingaku;

	// 更新対象の固定費コードリスト（チェックボックス選択値、1件以上必須）
	@NotEmpty
	private List<String> checkedFixedCostCodeList;
}
