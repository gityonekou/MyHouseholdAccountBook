/**
 * 情報管理(商品)更新画面の商品情報が格納されたフォームデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(商品)更新画面の商品情報が格納されたフォームデータです。
 * 入力情報をもとに、商品情報を追加・更新します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class ShoppingItemInfoUpdateForm {
	// アクション
	private String action;
	
	// 商品コード
	private String shoppingItemCode;
	// 支出項目コード(商品が属する支出項目コード)
	@NotBlank
	private String sisyutuItemCode;
	
	// 商品区分名
	@NotBlank
	@Size(min = 1, max = 30)
	private String shoppingItemKubunName;
	// 商品名
	@NotBlank
	@Size(min = 1, max = 100)
	private String shoppingItemName;
	// 商品詳細
	@Size(max = 300)
	private String shoppingItemDetailContext;
	// 商品JANコード(13桁 or 8桁 or ISBNコード:10桁)
	@NotBlank
	@Size(min = 8, max = 13)
	private String shoppingItemJanCode;
	// 会社名
	@NotBlank
	@Size(min = 1, max = 100)
	private String companyName;
	// 基準店舗コード
	private String standardShopCode;
	// 基準価格
	@Min(1)
	@Max(99999)
	private Integer standardPrice;
	// 内容量
	@Min(1)
	@Max(65530)
	private Integer shoppingItemCapacity;
	// 内容量単位
	private String shoppingItemCapacityUnit;
	// カロリー
	@Min(0)
	@Max(999999)
	private Integer shoppingItemCalories;
	
	/**
	 * 相関チェック(基準店舗コードと基準価格)
	 * ・基準店舗コードを選択した場合、基準価格は必須
	 * ・基準価格を入力した場合、基準店舗コードは必須
	 * 
	 * @return 基準店舗コードと基準価格の相関チェック結果
	 */
	@AssertTrue(message = "基準店舗が未選択か基準価格が未入力です。")
	public boolean isNeedCheckStandardPrice() {
		// 基準店舗コードを選択した場合で基準価格が未入力の場合はfalse
		if(StringUtils.hasLength(standardShopCode) && standardPrice == null) {
			return false;
		}
		// 基準価格を入力した場合で基準店舗コードが未選択の場合はfalse
		if(standardPrice != null && !StringUtils.hasLength(standardShopCode)) {
			return false;
		}
		// 上記以外の場合はtrue
		return true;
	}
	
	/**
	 *<pre>
	 * 相関チェック(内容量)
	 * 内容量を入力した場合、内容量単位(mlなど)は必須選択、内容量単位を選択した場合、内容量は必須入力)
	 *</pre>
	 * @return 内容量相関チェック結果
	 *
	 */
	@AssertTrue(message = "内容量が未入力か内容量単位(mlなど)が未選択です。")
	public boolean isNeedCheckShoppingItemCapacity() {
		// 内容量が入力ありの場合で内容量単位が未選択の場合はfalse
		if(shoppingItemCapacity != null && !StringUtils.hasLength(shoppingItemCapacityUnit)) {
			return false;
		}
		// 内容量単位が選択ありの場合で内容量が未入力の場合はfalse
		if(StringUtils.hasLength(shoppingItemCapacityUnit) && shoppingItemCapacity == null) {
			return false;
		}
		// 上記以外の場合はtrue
		return true;
	}
}
