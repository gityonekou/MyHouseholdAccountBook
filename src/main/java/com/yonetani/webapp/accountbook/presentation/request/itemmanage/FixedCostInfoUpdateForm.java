/**
 * 情報管理(固定費)更新画面の固定費情報が格納されたフォームデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/22 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import java.util.Objects;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(固定費)更新画面の固定費情報が格納されたフォームデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class FixedCostInfoUpdateForm {
	// アクション
	private String action;
	// 固定費コード
	private String fixedCostCode;
	// 支出項目コード(固定費が属する支出項目コード)
	@NotBlank
	private String sisyutuItemCode;

	// 固定費名(支払名)
	@NotBlank
	@Size(min = 1, max = 100)
	private String fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	@Size(max = 300)
	private String fixedCostDetailContext;
	// 支払月
	@NotBlank
	private String shiharaiTuki;
	// 支払月任意詳細
	@Size(max = 300)
	private String shiharaiTukiOptionalContext;
	// 支払金額
	@NotNull
	@Min(1)
	private Integer shiharaiKingaku;
	
	/**
	 * 相関チェック(支払月でその他任意を選択した場合、支払月任意詳細は必須
	 * 
	 * @return その他任意を選択した場合で支払月任意詳細の設定ありならtrue、空ならfalse
	 */
	@AssertTrue(message = "その他任意が未選択か支払月任意詳細が未入力です。")
	public boolean isNeedCheckShiharaiTukiOptionalContext() {
		// 支払月の値がその他任意(40)で支払月任意詳細が未入力の場合、false
		if(Objects.equals(shiharaiTuki, MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)
				&& !StringUtils.hasLength(shiharaiTukiOptionalContext)) {
			return false;
		}
		// 支払月任意詳細が入力ありで支払月の値がその他任意(40)以外の場合、false
		if(StringUtils.hasLength(shiharaiTukiOptionalContext)
				&& !Objects.equals(shiharaiTuki, MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)) {
			return false;
		}
		// 上記以外はチェック結果OK:trueを返却
		return true;
	}
}
