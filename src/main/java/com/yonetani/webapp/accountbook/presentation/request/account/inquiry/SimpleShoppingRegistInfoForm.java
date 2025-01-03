/**
 * 買い物登録(簡易タイプ)画面の買い物情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.inquiry;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
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
 * @since 家計簿アプリ(1.00.A)
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
	// 買い物日
	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate shoppingDate;
	
	// 食料品(必須)
	@Min(value = 0, message = "食料品(必須)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodExpenses;
	// 食料品B(無駄遣い)
	@Min(value = 0, message = "食料品B(無駄遣い)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodBExpenses;
	// 食料品C(お酒類)
	@Min(value = 0, message = "食料品C(お酒類)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodCExpenses;
	// 外食
	@Min(value = 0, message = "外食の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingDineOutExpenses;
	// 日用品
	@Min(value = 0, message = "日用品の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingConsumerGoodsExpenses;
	// 衣料品(私服)
	@Min(value = 0, message = "衣料品(私服)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingClothesExpenses;
	// 仕事
	@Min(value = 0, message = "仕事の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingWorkExpenses;
	// 住居設備
	@Min(value = 0, message = "住居設備の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingHouseEquipmentExpenses;
	// クーポン
	@Max(value = 0, message = "クーポンの入力値が不正です。0円以下(マイナスの値)を入力してください。")
	private Integer shoppingCouponPrice;
	// 購入金額合計(disabled)
	private Integer totalPurchasePriceView;
	// 購入金額合計
	@NotNull(message = "購入金額合計が空です。買い物情報を登録してください。")
	@Min(value = 0, message = "購入金額合計が0円以下です。正しい値を入力してください。")
	private Integer totalPurchasePrice;
	// 消費税：食料品(必須)
	@Min(value = 0, message = "消費税：食料品(必須)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodTaxExpenses;
	// 消費税：食料品B(無駄遣い)
	@Min(value = 0, message = "消費税：食料品B(無駄遣い)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodBTaxExpenses;
	// 消費税：食料品C(お酒類)
	@Min(value = 0, message = "消費税：食料品C(お酒類)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingFoodCTaxExpenses;
	// 消費税：外食
	@Min(value = 0, message = "消費税：外食の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingDineOutTaxExpenses;
	// 消費税：日用品
	@Min(value = 0, message = "消費税：日用品の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingConsumerGoodsTaxExpenses;
	// 消費税：衣料品(私服)
	@Min(value = 0, message = "消費税：衣料品(私服)の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingClothesTaxExpenses;
	// 消費税：仕事
	@Min(value = 0, message = "消費税：仕事の入力値がマイナスです。0円以上の値を入力してください。")
	private Integer shoppingWorkTaxExpenses;
	// 消費税：住居設備
	@Min(value = 0, message = "消費税：住居設備の入力値がマイナスです。0円以上の値を入力してください。")
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
	 * @return 入力した買い物日が対象年月の範囲内の場合:true、対象年月範囲内でない場合はfalse
	 */
	@AssertTrue(message = "買い物日の値が対象年月と一致しません。対象年月内の日付を選択してください。")
	public boolean isCheckedShoppingDate() {
		// 買い物日がnull(未入力)の場合は常にtrueを返す(入力必須のチェックは買い物日のバリデーションチェックで行う
		if(shoppingDate == null) {
			return true;
		}
		// 入力した買い物日のyyyyMMの値を取得
		String yearMonth =shoppingDate.format(MyHouseholdAccountBookContent.YEAR_MONTH_FORMATTER);
		// 対象年月と入力値の年月が等しいかどうかをチェック(nullセーフメソッドを使用)
		return Objects.equals(targetYearMonth, yearMonth);
	}
	
	/**
	 * 入力した店舗区分の値が薬局/薬局複合店/病院の場合、日用品に値が設定されているかどうか
	 * 
	 * @return 日用品に値が設定されている場合:true、設定されていない場合はfalse
	 */
	@AssertTrue(message = "薬局/薬局複合店/病院を選択した場合、日用品に値を入力してください。")
	public boolean isCheckedShopKubunCodeMedicalshop() {
		// 店舗区分が薬局/薬局複合店/病院以外の場合は常にtrueを返す(入力必須のチェックは買い物日のバリデーションチェックで行う
		if(!StringUtils.hasLength(shopKubunCode) || !shopKubunCode.equals(MyHouseholdAccountBookContent.SHOP_KUBUN_MEDICALSHOP_SELECTED_VALUE)) {
			return true;
		}
		// 日用品に値が設定されている場合:true、設定されていない場合はfalse
		if(shoppingConsumerGoodsExpenses != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 入力した店舗区分の値が理髪店の場合、日用品に値が設定されているかどうか
	 * 
	 * @return 日用品に値が設定されている場合:true、設定されていない場合はfalse
	 */
	@AssertTrue(message = "理髪店を選択した場合、日用品に値を入力してください。")
	public boolean isCheckedShopKubunCodeBarbershop() {
		// 店舗区分が理髪店以外の場合は常にtrueを返す(入力必須のチェックは買い物日のバリデーションチェックで行う
		if(!StringUtils.hasLength(shopKubunCode) || !shopKubunCode.equals(MyHouseholdAccountBookContent.SHOP_KUBUN_BARBERSHOP_SELECTED_VALUE)) {
			return true;
		}
		// 日用品に値が設定されている場合:true、設定されていない場合はfalse
		if(shoppingConsumerGoodsExpenses != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 消費税のみ入力されている場合、NGとする
	 * 
	 * @return 消費税のみ入力されている項目がある場合:false、入力OKの場合はtrue
	 */
	@AssertTrue(message = "消費税に対応する値を入力してください。")
	public boolean isCheckedTax() {
		// 食料品(必須)の消費税のみ入力チェック
		if(shoppingFoodExpenses == null && shoppingFoodTaxExpenses != null) {
			return false;
		}
		// 食料品B(無駄遣い)の消費税のみ入力チェック
		if(shoppingFoodBExpenses == null && shoppingFoodBTaxExpenses != null) {
			return false;
		}
		// 食料品C(お酒類)の消費税のみ入力チェック
		if(shoppingFoodCExpenses == null && shoppingFoodCTaxExpenses != null) {
			return false;
		}
		// 外食の消費税のみ入力チェック
		if(shoppingDineOutExpenses == null && shoppingDineOutTaxExpenses != null) {
			return false;
		}
		// 日用品の消費税のみ入力チェック
		if(shoppingConsumerGoodsExpenses == null && shoppingConsumerGoodsTaxExpenses != null) {
			return false;
		}
		// 衣料品(私服)の消費税のみ入力チェック
		if(shoppingClothesExpenses == null && shoppingClothesTaxExpenses != null) {
			return false;
		}
		// 仕事の消費税のみ入力チェック
		if(shoppingWorkExpenses == null && shoppingWorkTaxExpenses != null) {
			return false;
		}
		// 住居設備の消費税のみ入力チェック
		if(shoppingHouseEquipmentExpenses == null && shoppingHouseEquipmentTaxExpenses != null) {
			return false;
		}
		return true;
	}
}
