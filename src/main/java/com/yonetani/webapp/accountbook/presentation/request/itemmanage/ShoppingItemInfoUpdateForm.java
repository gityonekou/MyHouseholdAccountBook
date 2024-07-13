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
	
	/**
	 * 相関チェック(基準店舗コードを選択した場合、基準価格は必須)
	 * 
	 * @return 基準店舗を選択した場合で基準価格の設定ありならtrue、空ならfalse
	 */
	@AssertTrue(message = "基準店舗が未選択か基準価格が未入力です。")
	public boolean isNeedCheckStandardPrice() {
		// 基準店舗コードが未選択、基準価格が未入力の場合、true
		if(!StringUtils.hasLength(standardShopCode) && standardPrice == null) {
			return true;
		}
		// 基準店舗を選択した場合で基準価格の設定ありならtrue、空ならfalse 
		return (StringUtils.hasLength(standardShopCode) && standardPrice != null) ? true : false;
	}
}
