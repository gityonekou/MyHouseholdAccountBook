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

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.AssertTrue;
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
	//@Pattern(regexp = "^[0-9]{2}", message = "支払日はDD形式で指定してください。")
	private String siharaiDate;
	// 支払金額
	@NotNull
	@Min(1)
	private Integer expenditureKingaku;
	
	/**
	 * 支払日チェック(支払日が設定されている場合、日付内であること)
	 * 
	 * @return その他任意を選択した場合で収入詳細の設定ありならtrue、空ならfalse
	 */
	@AssertTrue(message = "支払日はDD形式で指定してください。")
	public boolean isNeedCheckSiharaiDate() {
		// 支払日が設定されている場合、日付内であること
		if(StringUtils.hasLength(siharaiDate)) {
			try {
				int val = Integer.parseInt(siharaiDate);
				if(val < 1 || val > 31) {
					return false;
				}
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		// 上記以外はチェック結果OK:trueを返却
		return true;
	}
}
