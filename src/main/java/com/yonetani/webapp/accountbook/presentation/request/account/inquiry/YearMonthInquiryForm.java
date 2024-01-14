/**
 * マイ家計簿
 * 家計簿照会画面の検索条件:年月の値が格納されたformデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * マイ家計簿
 * 家計簿照会画面の検索条件:年月の値が格納されたformデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class YearMonthInquiryForm {
	// 検索条件：年月
	@NotNull
	@Size(max = 6)
	private String targetYearMonth;

}
