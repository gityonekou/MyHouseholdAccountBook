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

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	// 支出コード(新規の場合、仮登録用支出コード:yyyyMMddHHmmssSSS)
	private String expenditureCode;
	// 支出項目コード
	private String sisyutuItemCode;
	// 支出項目名
	private String sisyutuItemName;
	// イベントコード
	private String eventCode;
	// 支払金額の0円開始設定フラグ
	private boolean clearStartFlg;
	
	// 支出名
	@NotBlank
	@Size(max = 100)
	private String expenditureName;
	// 支出区分
	@NotBlank
	private String expenditureKubun;
	// 支出詳細
	@Size(max = 300)
	private String expenditureDetailContext;
	// 支払日
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate siharaiDate;
	// 支払金額
	@NotNull
	@Min(1)
	private Integer expenditureKingaku;
	
}
