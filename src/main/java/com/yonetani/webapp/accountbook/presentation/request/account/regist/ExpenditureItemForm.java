/**
 * 収支登録画面の支出情報入力フォームデータです。
 * 入力した支出情報をもとに、支出一覧に登録及び対象の支出を更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/07/07 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 * 2026/11/01 : 1.01.01  feature-1.03-dev1   支払方法・銀行口座管理追加対応(支出一覧に支払方法名・引落先口座名を追加)
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.regist;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;

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
 * @since 家計簿アプリ(1.00)
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
	// 支払方法コード
	@NotBlank
	private String paymentMethodCode;
	// 支払金額
	@NotNull
	@Min(1)
	private Integer expenditureKingaku;

	/**
	 *<pre>
	 * 支払方法にシステム予約値が指定されていないかを検証します。
	 * 新規追加(action=add)時のみ検証対象とします。新規追加画面の支払方法プルダウンには
	 * システム予約値がそもそも選択肢として出ないため、通常操作で検証に掛かることはありません。
	 *
	 * 更新・削除(action=update)の場合は検証対象から除外します。買い物集計8項目は固定費登録画面経由で
	 * 支払方法「支払方法がない」(999)を設定して登録する運用であり(設計書5.5節)、この値を持つ既存行を
	 * 収支登録画面で開くとdisabled表示＋隠しフィールド併記で現在値がそのまま送信されます。
	 * action=updateにも本検証を適用すると、この既存行の訂正・削除操作そのものが常にエラーになってしまうため
	 * (実機確認で発見した回帰)、action=addのみに限定します。
	 *
	 * 【action=update側の対応箇所】action=updateで元々システム予約値でなかった行がシステム予約値に
	 * 変更されようとするケース(リクエスト改ざん)は、本メソッドでは判定できない(更新前の値との比較が必要で、
	 * Formは更新前の値を持たないため)。この場合のガードは{@code ExpenditureRegistUseCase.execExpenditureUpdate()}側で
	 * 実装しており、本メソッドのような入力エラー(バリデーションエラー)としてではなく、
	 * 不正なデータ更新として例外をスローする(エラー画面遷移)方式にしている(ユーザー判断)。
	 * 同じ「システム予約値を拒否する」チェックなのに扱いが異なる点に注意。
	 *</pre>
	 * @return 検証結果
	 *
	 */
	@AssertTrue(message = "支払方法にシステム予約値は指定できません。")
	public boolean isPaymentMethodCodeValid() {
		// 未入力は@NotBlank側で検出
		if(!StringUtils.hasLength(paymentMethodCode)) {
			return true;
		}
		// 新規追加以外(更新・削除)は、disabled+hidden併記による既存値の再送信を許容するため対象外
		if(!MyHouseholdAccountBookContent.ACTION_TYPE_ADD.equals(action)) {
			return true;
		}
		// tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない。
		// 形式不正、またはシステム予約値以外の場合はtrue、システム予約値の場合はfalse
		return PaymentMethodCode.tryFrom(paymentMethodCode)
				.map(code -> !code.isSystemReserved())
				.orElse(true);
	}
}
