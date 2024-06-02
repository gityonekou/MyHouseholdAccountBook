/**
 * 情報管理(商品)の各画面で商品検索条件が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *<pre>
 * 情報管理(商品)の各画面で商品検索条件が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ShoppingItemInfoSearchForm {
	// 検索対象
	@NotBlank
	private String searchTargetKubun;
	// 検索条件
	@NotBlank
	private String searchValue;
}
