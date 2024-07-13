/**
 * 収支登録画面の収入情報入力フォームデータです。
 * 入力した収入情報をもとに、収入一覧に登録及び対象の収入を更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/07/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

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
 * 収支登録画面の収入情報入力フォームデータです。
 * 入力した収入情報をもとに、収入一覧に登録及び対象の収入を更新します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class IncomeItemForm {
	// アクション
	private String action;
	// 収入コード(仮登録用収入コード)
	private String incomeCode;
	// 収入区分名
	private String incomeKubunName;
	
	// 収入区分
	@NotBlank
	private String incomeKubun;
	// 収入詳細(任意項目：収入区分でその他任意選択の場合必須)
	@Size(max = 300)
	private String incomeDetailContext;
	// 収入金額
	@NotNull
	@Min(1)
	private Integer incomeKingaku;
	
	/**
	 * 相関チェック(収入区分でその他任意選択の場合必須、収入詳細は必須)
	 * 
	 * @return その他任意を選択した場合で収入詳細の設定ありならtrue、空ならfalse
	 */
	@AssertTrue(message = "その他任意を選択した場合、収入詳細は必須入力です。")
	public boolean isNeedCheckIncomeDetailContext() {
		// 収入区分の値がその他任意(9)で収入詳細が未入力の場合、false
		if(Objects.equals(incomeKubun, MyHouseholdAccountBookContent.INCOME_KUBUN_OPTIONAL_SELECTED_VALUE)
				&& !StringUtils.hasLength(incomeDetailContext)) {
			return false;
		}
		// 上記以外はチェック結果OK:trueを返却
		return true;
	}
	
}
