/**
 * 画面表示値、値の定数クラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.content;

/**
 *<pre>
 * 画面表示値、値の定数クラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class MyHouseholdAccountBookContent {
	// アクション(新規登録)
	public static final String ACTION_TYPE_ADD = "add";
	// アクション(更新)
	public static final String ACTION_TYPE_UPDATE = "update";
	// アクション(更新)
	public static final String ACTION_TYPE_DELETE = "delete";
	// データタイプ(新規)
	public static final String DATA_TYPE_NEW = "new";
	// データタイプ(DBロード)
	public static final String DATA_TYPE_LOAD = "load";
	
	// ユーザステータス(view：有効)
	public static final String USER_STATUS_ENABLED_VIEW = "有効";
	// ユーザステータス(value：有効)
	public static final String USER_STATUS_ENABLED_VALUE = "userStatusEnabled";
	// ユーザステータス(view：無効)
	public static final String USER_STATUS_DISABLED_VIEW = "無効";
	// ユーザステータス(value：無効)
	public static final String USER_STATUS_DISABLED_VALUE = "userStatusDisabled";
	// ユーザロール(view：管理者)
	public static final String USER_ROLE_ADMIN_VIEW = "管理者";
	// ユーザロール(value:管理者)
	public static final String USER_ROLE_ADMIN_VALUE = "userRoleAdmin";
	// ユーザロール(view：ユーザー)
	public static final String USER_ROLE_USER_VIEW = "ユーザー";
	// ユーザロール(value：ユーザー)
	public static final String USER_ROLE_USER_VALUE = "userRoleUser";
	// 条件判定：支出項目テーブル(ベース):SISYUTU_ITEM_BASE_TABLE
	public static final String SISYUTU_ITEM_BASE_TABLE = "SISYUTU_ITEM_BASE_TABLE";
	// 条件判定：店名テーブル(ベース):SHOP_BASE_TABLE
	public static final String SHOP_BASE_TABLE = "SHOP_BASE_TABLE";
	// 条件判定：コードテーブル:CODE_TABLE
	//public static final String CODE_TABLE = "CODE_TABLE";
	
	// コード定義区分(ショップ区分:001)
	public static final String CODE_DEFINES_SHOP_KUBUN = "001";
	// 商品登録画面で基準店舗選択ボックスに表示する店舗の店舗区分コード
	// 飲食日用品(901)、ホームセンター(902)、薬局/薬局複合店(905)、複合店舗(907)
	public static final String[] STANDARD_SHOPSLIST_KUBUN_CODE = {"901","902","905","907"};
	// コード定義区分(固定費支払月:002)
	public static final String CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI = "002";
	// コード定義:固定費支払月で毎月(00)を選択時
	public static final String SHIHARAI_TUKI_EVERY_SELECTED_VALUE = "00";
	// コード定義:固定費支払月で奇数月(20)を選択時
	public static final String SHIHARAI_TUKI_ODD_SELECTED_VALUE = "20";
	// コード定義:固定費支払月で偶数月(30)を選択時
	public static final String SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE = "30";
	// コード定義:固定費支払月でその他任意(40)を選択時
	public static final String SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE = "40";
	// コード定義区分(固定費支払日:003)
	public static final String CODE_DEFINES_FIXED_COST_SHIHARAI_DAY = "003";
	// コード定義区分(収入区分:004)
	public static final String CODE_DEFINES_INCOME_KUBUN = "004";
	// コード定義:収入区分でその他任意(9)を選択時
	public static final String INCOME_KUBUN_OPTIONAL_SELECTED_VALUE = "9";
	// コード定義区分(商品内容量単位:005)
	public static final String SHOPPING_ITEM_CAPACITY_UNIT = "005";
	
	// 支出項目コード仮登録データ:9999
	public static final String SISYUTU_ITEM_CODE_TEMPORARY_VALUE = "9999";
	// 支出項目表示順のその他項目の値:99
	public static final String OTHER_SISYUTU_ITEM_SORT_VALUE = "99";
	// 支出項目(日用消耗品)の支出項目表示順の値
	public static final String SISYUTU_ITEM_NITIYOU_SYOUMOUHIN_SORT_VALUE = "0502000000";
	// 支出項目(食費)の支出項目表示順最大値
	public static final String SISYUTU_ITEM_INSYOKU_SORT_MAX_VALUE = "0503999999";
	
	// 商品情報を検索条件に商品を検索(セッション格納値)
	public static final String ACT_SEARCH_SHOPPING_ITEM = "ActSearchShoppingItem";
	// 支出項目情報を検索条件に商品を検索(セッション格納値)
	public static final String ACT_SEARCH_SISYUTU_ITEM = "ActSearchSisyutuItem";
	// 商品区分名を検索条件に商品を検索 
	public static final String SEARCH_TARGET_SHOPPING_ITEM_KUBUN_NAME = "itemKubun";
	// 商品名を検索条件に商品を検索 
	public static final String SEARCH_TARGET_SHOPPING_ITEM_NAME = "itemName";
	// 会社名を検索条件に商品を検索
	public static final String SEARCH_TARGET_COMPANY_NAME = "companyName";
	// 商品JANコードを検索条件に商品を検索
	public static final String SEARCH_TARGET_SHOPPING_ITEM_JAN_CODE = "janCode";
	
}
