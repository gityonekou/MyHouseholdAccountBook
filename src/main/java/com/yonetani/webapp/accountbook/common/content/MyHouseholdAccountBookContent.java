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

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

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
	/** アクション(新規登録) */
	public static final String ACTION_TYPE_ADD = "add";
	/** アクション(更新) */
	public static final String ACTION_TYPE_UPDATE = "update";
	/** アクション(更新) */
	public static final String ACTION_TYPE_DELETE = "delete";
	/** アクションタイプ(データ変更なし) */
	public static final String ACTION_TYPE_NON_UPDATE = "non";
	/** データタイプ(新規) */
	public static final String DATA_TYPE_NEW = "new";
	/** データタイプ(DBロード) */
	public static final String DATA_TYPE_LOAD = "load";
	
	/** ユーザステータス(view：有効) */
	public static final String USER_STATUS_ENABLED_VIEW = "有効";
	/** ユーザステータス(value：有効) */
	public static final String USER_STATUS_ENABLED_VALUE = "userStatusEnabled";
	/** ユーザステータス(view：無効) */
	public static final String USER_STATUS_DISABLED_VIEW = "無効";
	/** ユーザステータス(value：無効) */
	public static final String USER_STATUS_DISABLED_VALUE = "userStatusDisabled";
	/** ユーザロール(view：管理者) */
	public static final String USER_ROLE_ADMIN_VIEW = "管理者";
	/** ユーザロール(value:管理者) */
	public static final String USER_ROLE_ADMIN_VALUE = "userRoleAdmin";
	/** ユーザロール(view：ユーザー) */
	public static final String USER_ROLE_USER_VIEW = "ユーザー";
	/** ユーザロール(value：ユーザー) */
	public static final String USER_ROLE_USER_VALUE = "userRoleUser";
	/** 条件判定：支出項目テーブル(ベース):SISYUTU_ITEM_BASE_TABLE */
	public static final String SISYUTU_ITEM_BASE_TABLE = "SISYUTU_ITEM_BASE_TABLE";
	/** 条件判定：店名テーブル(ベース):SHOP_BASE_TABLE */
	public static final String SHOP_BASE_TABLE = "SHOP_BASE_TABLE";
	// 条件判定：コードテーブル:CODE_TABLE
	//public static final String CODE_TABLE = "CODE_TABLE";
	
	/** コード定義区分(店舗区分:001) */
	public static final String CODE_DEFINES_SHOP_KUBUN = "001";
	/** コード定義:店舗区分で食品・日用品店舗(901)を選択時 */
	public static final String SHOP_KUBUN_GROCERIES_SELECTED_VALUE = "901";
	/** コード定義:店舗区分で薬局/薬局複合店/病院(905)を選択時 */
	public static final String SHOP_KUBUN_MEDICALSHOP_SELECTED_VALUE = "905";
	/** コード定義:店舗区分で理髪店(908)を選択時 */
	public static final String SHOP_KUBUN_BARBERSHOP_SELECTED_VALUE = "908";
	/** 商品登録画面で基準店舗選択ボックスに表示する店舗の店舗区分コード<br>
	      飲食日用品(901)、ホームセンター(902)、薬局/薬局複合店(905)、複合店舗(907) */
	public static final String[] STANDARD_SHOPSLIST_KUBUN_CODE = {"901","902","905","907"};
	/** コード定義区分(固定費支払月:002) */
	public static final String CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI = "002";
	/** コード定義:固定費支払月で毎月(00)を選択時 */
	public static final String SHIHARAI_TUKI_EVERY_SELECTED_VALUE = "00";
	/** コード定義:固定費支払月で奇数月(20)を選択時 */
	public static final String SHIHARAI_TUKI_ODD_SELECTED_VALUE = "20";
	/** コード定義:固定費支払月で偶数月(30)を選択時 */
	public static final String SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE = "30";
	/** コード定義:固定費支払月でその他任意(40)を選択時 */
	public static final String SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE = "40";
	/** コード定義区分(固定費支払日:003) */
	public static final String CODE_DEFINES_FIXED_COST_SHIHARAI_DAY = "003";
	/** コード定義区分(収入区分:004) */
	public static final String CODE_DEFINES_INCOME_KUBUN = "004";
	/** コード定義:収入区分でその他任意(9)を選択時 */
	public static final String INCOME_KUBUN_OPTIONAL_SELECTED_VALUE = "9";
	/** コード定義区分(商品内容量単位:005) */
	public static final String SHOPPING_ITEM_CAPACITY_UNIT = "005";
	/** コード定義区分(支出区分:006) */
	public static final String CODE_DEFINES_EXPENDITURE_KUBUN = "006";
	/** コード定義:支出区分で無駄遣いなし(1)を選択時 */
	public static final String NON_WASTED_SELECTED_VALUE = "1";
	/** コード定義:支出区分で無駄遣いB(2)を選択時 */
	public static final String WASTED_B_SELECTED_VALUE = "2";
	/** コード定義の値:無駄遣いBの文字列(無駄遣いB) */
	public static final String WASTED_B_VIEW_VALUE = "無駄遣いB";
	/** コード定義:支出区分で無駄遣いC(3)を選択時 */
	public static final String WASTED_C_SELECTED_VALUE = "3";
	/** コード定義の値:無駄遣いCの文字列(無駄遣いC) */
	public static final String WASTED_C_VIEW_VALUE = "無駄遣いC";
	/** コード定義区分(固定費区分:007) */
	public static final String CODE_DEFINES_FIXED_COST_KUBUN = "007";
	/** コード定義:固定費区分で支払い金額確定を選択時 */
	public static final String FIXED_COST_FIX_SELECTED_VALUE = "1";
	/** コード定義:固定費区分で予定支払い金額を選択時 */
	public static final String FIXED_COST_ESTIMATE_SELECTED_VALUE = "2";
	
	/** 支出項目コード仮登録データ:9999 */
	public static final String SISYUTU_ITEM_CODE_TEMPORARY_VALUE = "9999";
	/** 支出項目表示順のその他項目の値:99 */
	public static final String OTHER_SISYUTU_ITEM_SORT_VALUE = "99";
	/** 支出項目コード:住居設備(0045) */
	public static final String SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE = "0045";
	/** 支出項目コード:被服費(0046) */
	public static final String SISYUTU_ITEM_CODE_HIFUKU_VALUE = "0046";
	/** 支出項目コード:日用消耗品(0050) */
	public static final String SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE = "0050";
	/** 支出項目コード:飲食(0051) */
	public static final String SISYUTU_ITEM_CODE_INSYOKU_VALUE = "0051";
	/** 支出項目コード:一人プチ贅沢・外食(0052) */
	public static final String SISYUTU_ITEM_CODE_GAISYOKU_VALUE = "0052";
	/** 支出項目コード:流動経費(0007) */
	public static final String SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE = "0007";
	
	/** 支出項目(日用消耗品)の支出項目表示順の値 */
	public static final String SISYUTU_ITEM_NITIYOU_SYOUMOUHIN_SORT_VALUE = "0501000000";
	/** 支出項目(食費)の支出項目表示順最大値 */
	public static final String SISYUTU_ITEM_INSYOKU_SORT_MAX_VALUE = "0502999999";
	/** 支出項目(イベント)の支出項目表示順の値 */
	public static final String SISYUTU_ITEM_EVENT_SORT_VALUE = "0603000000";
	/** 支出項目(イベント)に属する支出項目表示順の最大値 */
	public static final String SISYUTU_ITEM_EVENT_SORT_MAX_VALUE = "0603999999";
	
	/** 商品情報を検索条件に商品を検索(セッション格納値) */
	public static final String ACT_SEARCH_SHOPPING_ITEM = "ActSearchShoppingItem";
	/** 支出項目情報を検索条件に商品を検索(セッション格納値) */
	public static final String ACT_SEARCH_SISYUTU_ITEM = "ActSearchSisyutuItem";
	/** 商品区分名を検索条件に商品を検索 */
	public static final String SEARCH_TARGET_SHOPPING_ITEM_KUBUN_NAME = "itemKubun";
	/** 商品名を検索条件に商品を検索 */
	public static final String SEARCH_TARGET_SHOPPING_ITEM_NAME = "itemName";
	/** 会社名を検索条件に商品を検索 */
	public static final String SEARCH_TARGET_COMPANY_NAME = "companyName";
	/** 商品JANコードを検索条件に商品を検索 */
	public static final String SEARCH_TARGET_SHOPPING_ITEM_JAN_CODE = "janCode";
	
	/** 日付チェック用のフォーマットです */
	public static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
	/** 日付チェック用のフォーマットです(うるう年でない年で29日を指定した場合、28日のLocalDateとして判断します */
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	/** 日付の厳密チェック用の日付フォーマットです */
	// うるう年の厳密なチェックを行う場合のフォーマッターです
	// (デフォルトだと、うるう年でない年の日付の値に2月29日を指定してparseを行っても2月28日のLocalDateとしてparse結果が返されるため)
	// 厳密なチェックを行う場合、年のフォーマットはyyyyではなくuuuuを指定する必要があるので注意
	public static final DateTimeFormatter STRICT_DATE_TIME_FORMATTERA = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);
	
}
