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
	// アクション(登録)
	public static final String ACTION_TYPE_ADD = "add";
	// アクション(更新)
	public static final String ACTION_TYPE_UPDATE = "update";
	
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
	public static final String SHOP_KUBUN_CODE = "001";
	
	// 支出項目コード仮登録データ:9999
	public static final String SISYUTU_ITEM_CODE_TEMPORARY_VALUE = "9999";
	// 支出項目表示順のその他項目の値:99
	public static final String OTHER_SISYUTU_ITEM_SORT_VALUE = "99";
	// 支出項目(日用消耗品)の支出項目表示順の値
	public static final String SISYUTU_ITEM_NITIYOU_SYOUMOUHIN_SORT_VALUE = "0502000000";
	// 支出項目(食費)の支出項目表示順最大値
	public static final String SISYUTU_ITEM_INSYOKU_SORT_MAX_VALUE = "0503999999";
	
}
