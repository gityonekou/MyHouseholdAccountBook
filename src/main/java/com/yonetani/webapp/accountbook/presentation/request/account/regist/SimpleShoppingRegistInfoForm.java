/**
 * 買い物登録(簡易タイプ)画面の買い物情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/04 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応(追加リファクタリングで、クーポン金額の入力を0以上に変更、バリデーション追加)
 * 2026/11/01 : 1.01.01  feature-1.03-dev1   相関チェック(@AssertTrue)の判定ロジックをドメイン層(TargetYearMonth/ShoppingDate/ShopKubunCode/ShoppingExpenditureAmount/ShoppingTaxExpenses)に移動。isCheckedShopKubunCodeMedicalshop()/Barbershop()はisCheckedShopKubunCodeConsumerGoods()に統合、isPaymentMethodCodeValid()に処理内容のコメントを追加
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.regist;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 買い物登録(簡易タイプ)画面の買い物情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Data
public class SimpleShoppingRegistInfoForm {
	// アクション
	private String action;
	// 対象年月
	private String targetYearMonth;
	// 買い物登録コード
	private String shoppingRegistCode;
	
	// 店舗区分
	@NotBlank
	private String shopKubunCode;
	// 店舗コード
	@NotBlank
	private String shopCode;
	// 支払方法コード
	@NotBlank(message = "支払方法を選択してください。")
	private String paymentMethodCode;
	// 買い物日
	@NotNull(message = "買い物日を選択してください。")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate shoppingDate;

	// 食料品(必須)
	@Digits(integer = 6, fraction = 0, message = "食料品(必須)の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品(必須)の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodExpenses;
	// 食料品B(無駄遣い)
	@Digits(integer = 6, fraction = 0, message = "食料品B(無駄遣い)の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品B(無駄遣い)の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodBExpenses;
	// 食料品C(お酒類)
	@Digits(integer = 6, fraction = 0, message = "食料品C(お酒類)の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品C(お酒類)の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodCExpenses;
	// 外食
	@Digits(integer = 6, fraction = 0, message = "外食の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "外食の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingDineOutExpenses;
	// 日用品
	@Digits(integer = 6, fraction = 0, message = "日用品の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "日用品の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingConsumerGoodsExpenses;
	// 衣料品(私服)
	@Digits(integer = 6, fraction = 0, message = "衣料品(私服)の購入金額入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "衣料品(私服)の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingClothesExpenses;
	// 仕事
	@Digits(integer = 7, fraction = 0, message = "仕事の購入金額入力値は7桁以内の整数で入力してください")
	@Min(value = 0, message = "仕事の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingWorkExpenses;
	// 住居設備
	@Digits(integer = 7, fraction = 0, message = "住居設備の購入金額入力値は7桁以内の整数で入力してください")
	@Min(value = 0, message = "住居設備の購入金額入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingHouseEquipmentExpenses;
	// クーポン
	@Digits(integer = 6, fraction = 0, message = "クーポン金額の入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "クーポン金額の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingCouponPrice;
	// 購入金額合計(disabled)
	private Integer totalPurchasePriceView;
	// 購入金額合計
	@NotNull(message = "購入金額合計が空です。買い物情報を登録してください。")
	@Min(value = 0, message = "購入金額合計が0円以下です。正しい値を入力してください。")
	private Integer totalPurchasePrice;
	// 消費税：食料品(必須)
	@Digits(integer = 6, fraction = 0, message = "食料品(必須)の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品(必須)の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodTaxExpenses;
	// 消費税：食料品B(無駄遣い)
	@Digits(integer = 6, fraction = 0, message = "食料品B(無駄遣い)の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品B(無駄遣い)の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodBTaxExpenses;
	// 消費税：食料品C(お酒類)
	@Digits(integer = 6, fraction = 0, message = "食料品C(お酒類)の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "食料品C(お酒類)の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodCTaxExpenses;
	// 消費税：外食
	@Digits(integer = 6, fraction = 0, message = "外食の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "外食の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingDineOutTaxExpenses;
	// 消費税：日用品
	@Digits(integer = 6, fraction = 0, message = "日用品の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "日用品の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingConsumerGoodsTaxExpenses;
	// 消費税：衣料品(私服)
	@Digits(integer = 6, fraction = 0, message = "衣料品(私服)の消費税入力値は6桁以内の整数で入力してください")
	@Min(value = 0, message = "衣料品(私服)の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingClothesTaxExpenses;
	// 消費税：仕事
	@Digits(integer = 7, fraction = 0, message = "仕事の消費税入力値は7桁以内の整数で入力してください")
	@Min(value = 0, message = "仕事の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingWorkTaxExpenses;
	// 消費税：住居設備
	@Digits(integer = 7, fraction = 0, message = "住居設備の消費税入力値は7桁以内の整数で入力してください")
	@Min(value = 0, message = "住居設備の消費税入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingHouseEquipmentTaxExpenses;
	// 消費税合計(disabled)
	private Integer taxTotalPurchasePriceView;
	// 消費税合計
	@Min(value = 0, message = "消費税合計の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer taxTotalPurchasePrice;
	// 備考
	@Size(max = 150, message = "備考入力欄の入力文字数は150文字以内にしてください。")
	private String shoppingRemarks;
	//買い物合計金額(disabled)
	private Integer shoppingTotalAmountView;
	// 買い物合計金額
	@NotNull(message = "買い物合計金額が空です。買い物情報を登録してください。")
	@Min(value = 0, message = "購入金額合計が0円以下です。正しい値を入力してください。")
	private Integer shoppingTotalAmount;
	
	/**
	 * 入力した買い物日の値が対象年月の範囲内かどうか
	 *
	 * 判定ロジックの実体はドメイン層の{@link ShoppingDate#from(LocalDate, TargetYearMonth)}のガード節に
	 * 集約されている。
	 *
	 * @return 入力した買い物日が対象年月の範囲内の場合:true、対象年月範囲内でない場合はfalse
	 */
	@AssertTrue(message = "買い物日の値が対象年月と一致しません。対象年月内の日付を選択してください。")
	public boolean isCheckedShoppingDate() {
		// 買い物日がnull(未入力)の場合は常にtrueを返す(入力必須のチェックは買い物日の@NotNullで行う)
		if(shoppingDate == null) {
			return true;
		}
		// targetYearMonthは画面の隠しフィールドで@NotBlank等の制約がないため、ここで形式不正を検出する
		// (tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない)
		Optional<TargetYearMonth> yearMonth = TargetYearMonth.tryFrom(targetYearMonth);
		if(yearMonth.isEmpty()) {
			return false;
		}
		// 買い物日が対象年月の範囲外の場合、ShoppingDate.tryFrom()がOptional.empty()を返す
		return ShoppingDate.tryFrom(shoppingDate, yearMonth.get()).isPresent();
	}
	
	/**
	 * 入力した店舗区分の値が、日用品への金額入力を必須とする区分(薬局/薬局複合店/病院、または理髪店)の場合、
	 * 日用品に値が設定されているかどうか
	 *
	 * 判定ロジックの実体はドメイン層の{@link ShopKubunCode#requiresConsumerGoodsAmount()}に集約されている。
	 * (当初は薬局系/理髪店で2つの別メソッドだったが、判定対象のコード値が違うだけで構造が同一のため統合した)
	 *
	 * @return 日用品に値が設定されている場合:true、設定されていない場合はfalse
	 */
	@AssertTrue(message = "薬局/薬局複合店/病院、または、理髪店を選択した場合、日用品に値を入力してください。")
	public boolean isCheckedShopKubunCodeConsumerGoods() {
		if(!StringUtils.hasLength(shopKubunCode)) {
			return true; // 未入力は@NotBlank側で検出
		}
		// tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない
		Optional<ShopKubunCode> kubunCode = ShopKubunCode.tryFrom(shopKubunCode);
		if(kubunCode.isEmpty() || !kubunCode.get().requiresConsumerGoodsAmount()) {
			return true; // 形式不正、または対象外の区分はここでは判定不要
		}
		// 日用品に値が設定されている場合:true、設定されていない場合はfalse
		return shoppingConsumerGoodsExpenses != null;
	}
	
	/**
	 * 消費税のみ入力されている場合、NGとする
	 *
	 * 判定ロジックの実体はドメイン層の{@link ShoppingTaxExpenses#hasValueWithoutAmount(ShoppingExpenditureAmount)}に
	 * 集約されている(8つの{@code ShoppingXxxExpenditureItem.from()}のガード節と共通)。
	 *
	 * @return 消費税のみ入力されている項目がある場合:false、入力OKの場合はtrue
	 */
	@AssertTrue(message = "消費税に対応する値を入力してください。")
	public boolean isCheckedTax() {
		return !isTaxWithoutAmount(shoppingFoodExpenses, shoppingFoodTaxExpenses)
				&& !isTaxWithoutAmount(shoppingFoodBExpenses, shoppingFoodBTaxExpenses)
				&& !isTaxWithoutAmount(shoppingFoodCExpenses, shoppingFoodCTaxExpenses)
				&& !isTaxWithoutAmount(shoppingDineOutExpenses, shoppingDineOutTaxExpenses)
				&& !isTaxWithoutAmount(shoppingConsumerGoodsExpenses, shoppingConsumerGoodsTaxExpenses)
				&& !isTaxWithoutAmount(shoppingClothesExpenses, shoppingClothesTaxExpenses)
				&& !isTaxWithoutAmount(shoppingWorkExpenses, shoppingWorkTaxExpenses)
				&& !isTaxWithoutAmount(shoppingHouseEquipmentExpenses, shoppingHouseEquipmentTaxExpenses);
	}

	/**
	 * 購入金額・消費税の組について、消費税のみ入力されている不正な組み合わせかどうかを判定します。
	 *
	 * @param amount 購入金額入力値
	 * @param tax 消費税入力値
	 * @return 消費税のみ入力されている場合:true、それ以外:false
	 */
	private boolean isTaxWithoutAmount(Integer amount, Integer tax) {
		// tryFrom()により、不正な形式値(マイナス値)でも例外が伝播せず500エラーにならない
		Optional<ShoppingExpenditureAmount> amountVO = ShoppingExpenditureAmount.tryFrom(amount);
		Optional<ShoppingTaxExpenses> taxVO = ShoppingTaxExpenses.tryFrom(tax);
		if(amountVO.isEmpty() || taxVO.isEmpty()) {
			return false; // 形式不正はここでは判定しない(@Min側で検出)
		}
		return taxVO.get().hasValueWithoutAmount(amountVO.get());
	}

	/**
	 * 支払方法コードにシステム予約値が指定されていないかどうか
	 * (tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない。突合レビュー指摘AA反映)
	 *
	 * @return システム予約値でない場合:true、システム予約値の場合はfalse
	 */
	@AssertTrue(message = "支払方法にシステム予約値は指定できません。")
	public boolean isPaymentMethodCodeValid() {
		if (!StringUtils.hasLength(paymentMethodCode)) {
			return true; // 未入力は@NotBlank側で検出
		}
		// tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない。
		// 形式不正、またはシステム予約値以外の場合はtrue、システム予約値の場合はfalse
		return PaymentMethodCode.tryFrom(paymentMethodCode)
				.map(code -> !code.isSystemReserved())
				.orElse(true);
	}
}
