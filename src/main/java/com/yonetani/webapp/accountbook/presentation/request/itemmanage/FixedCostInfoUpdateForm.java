/**
 * 情報管理(固定費)更新画面の固定費情報が格納されたフォームデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/05/22 : 1.00.00                      新規作成
 * 2026/06/14 : 1.01.00  feature-1.02-dev2   固定費0円対応: @Min(1)→@Min(0)、@AssertTrue isValidShiharaiKingakuForKubun()追加
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import java.util.Objects;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ShoppingAggregateSpecification;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.SafeDomainFactory;

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
 * @since 家計簿アプリ(1.00)
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
	// 固定費区分
	@NotBlank
	private String fixedCostKubun;
	// 支払月
	@NotBlank
	private String shiharaiTuki;
	// 支払月任意詳細
	@Size(max = 300)
	private String shiharaiTukiOptionalContext;
	// 支払日
	@NotBlank
	private String shiharaiDay;
	// 支払金額
	@NotNull
	@Min(0)
	private Integer shiharaiKingaku;
	// 支払方法コード(固定費登録画面のみ、買い物集計8項目に限りシステム予約値「支払方法がない」を許容する)
	@NotBlank
	private String paymentMethodCode;


	/**
	 * 相関チェック(支払月でその他任意を選択した場合、支払月任意詳細は必須)
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

	/**
	 * 相関チェック(固定費区分が「予定支払い金額」の場合、支払金額に0円は設定不可)
	 *
	 * @return 固定費区分が予定支払い金額以外の場合、または支払金額が1円以上の場合はtrue、
	 *         固定費区分が予定支払い金額かつ支払金額が0円の場合はfalse
	 */
	@AssertTrue(message = "固定費区分が「予定支払い金額」の場合、支払金額に0円は設定できません。")
	public boolean isValidShiharaiKingakuForKubun() {
		// 固定費区分が「予定支払い金額(2)」かつ支払金額が0円の場合、false
		if (Objects.equals(fixedCostKubun, MyHouseholdAccountBookContent.FIXED_COST_ESTIMATE_SELECTED_VALUE)
				&& shiharaiKingaku != null && shiharaiKingaku == 0) {
			return false;
		}
		// 上記以外はチェック結果OK:trueを返却
		return true;
	}

	/**
	 * 相関チェック(支払方法コードがシステム予約値の場合、買い物集計8項目に対応する固定費にのみ設定を許容する)
	 *
	 * 固定費登録画面は「支払方法がない」の入力経路として正当だが、対象8項目以外の通常の固定費に
	 * 誤って設定されると、月別収支照会での表示や口座別支払確認、dev5の逆変換で不整合が生じるため、
	 * 対象8項目（支出項目コード＋固定費名から導出される支出区分の組み合わせ）に一致する場合のみ許容する。
	 *
	 * @return 検証結果
	 */
	@AssertTrue(message = "「支払方法がない」は買い物集計8項目専用の固定費にのみ設定できます。")
	private boolean isPaymentMethodCodeValid() {
		// paymentMethodCode・sisyutuItemCode・fixedCostNameの3つとも、このメソッドが参照する可能性がある
		// (@AssertTrueは他フィールドの@NotBlank違反があっても実行されるため、3つとも明示的にガードする)
		if (!StringUtils.hasLength(paymentMethodCode)
				|| !StringUtils.hasLength(sisyutuItemCode)
				|| !StringUtils.hasLength(fixedCostName)) {
			return true; // 各フィールドの@NotBlank側で検出
		}
		// tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない
		Optional<PaymentMethodCode> code = PaymentMethodCode.tryFrom(paymentMethodCode);
		if (code.isEmpty() || !code.get().isSystemReserved()) {
			return true; // 形式不正、または通常の支払方法はここでは判定不要
		}
		// システム予約値が選択された場合のみ、対象8項目かどうかを判定する。
		// FixedCostName・ExpenditureItemCodeは既存(Feature1.03以前)の型でtryFrom()を持たないため、
		// SafeDomainFactory.tryCreate()で直接包む
		Optional<FixedCostName> name = SafeDomainFactory.tryCreate(() -> FixedCostName.from(fixedCostName));
		Optional<ExpenditureItemCode> itemCode = SafeDomainFactory.tryCreate(() -> ExpenditureItemCode.from(sisyutuItemCode));
		if (name.isEmpty() || itemCode.isEmpty()) {
			return true; // 形式不正はsisyutuItemCode/fixedCostNameそれぞれの既存チェックで弾かれる想定
		}
		ExpenditureCategory category = ExpenditureCategory.from(name.get());
		// ShoppingAggregateSpecificationはSpring管理外の単純な値オブジェクトのため、その場でnewする
		return new ShoppingAggregateSpecification().isSatisfiedBy(itemCode.get(), category);
	}
}
