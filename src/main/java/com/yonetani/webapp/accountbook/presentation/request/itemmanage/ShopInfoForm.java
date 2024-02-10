/**
 * 情報管理(お店)画面の店舗情報が格納されたformデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(お店)画面の店舗情報が格納されたformデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ShopInfoForm {
	// アクション
	private String action;
	// 店舗コード
	private String shopCode;
	// 表示順(更新比較用)
	private String shopSortBefore;
	
	// 店舗区分
	@NotNull
	private String shopKubun;
	// 店舗名
	@Size(min = 1, max = 50)
	private String shopName;
	// 表示順
	@Min(1)
	@Max(899)
	private Integer shopSort;
}
