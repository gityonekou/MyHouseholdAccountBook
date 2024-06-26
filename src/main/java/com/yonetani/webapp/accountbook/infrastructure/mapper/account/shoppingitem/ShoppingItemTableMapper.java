/**
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.shoppingitem;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem.ShoppingItemInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem.ShoppingItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.ShoppingItemInfoSearchConditionSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShoppingItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShoppingItemJanCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ShoppingItemTableMapper {
	/**
	 *<pre>
	 * 商品テーブル:SHOPPING_ITEM_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 商品テーブル:SHOPPING_ITEM_TABLE出力情報
	 * @return 商品テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/shoppingitem/ShoppingItemTableInsertSql01.sql")
	public int insert(@Param("dto") ShoppingItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 商品テーブル:SHOPPING_ITEM_TABLEの情報を指定の商品情報で更新します。
	 *</pre>
	 * @param writeDto 商品テーブル:SHOPPING_ITEM_TABLE出力情報
	 * @return 商品テーブルを更新した件数
	 *
	 */
	@Update("sql/account/shoppingitem/ShoppingItemTableUpdateSql01.sql")
	public int update(@Param("dto") ShoppingItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザID、商品コードを条件に商品テーブル:SHOPPING_ITEM_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、商品コード
	 * @return 商品テーブル:SHOPPING_ITEM_TABLE参照結果
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemTableSelectSql01.sql")
	public ShoppingItemReadWriteDto findByIdAndShoppingItemCode(@Param("dto") UserIdAndShoppingItemCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 商品テーブル:SHOPPING_ITEM_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLE、店舗テーブル:SHOP_TABLEから
	 * 指定のユーザID、支出項目コードを条件に商品情報を検索します。
	 * 
	 *</pre>
	 * @param search 検索条件:ユーザID、支出項目コード
	 * @return 商品情報参照結果のリスト
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemInquirySelectSql01.sql")
	public List<ShoppingItemInquiryReadDto> findByIdAndSisyutuItemCode(@Param("dto") UserIdAndSisyutuItemCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 商品テーブル:SHOPPING_ITEM_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLE、店舗テーブル:SHOP_TABLEから
	 * 指定の商品検索条件を条件に商品情報を検索します。
	 * 
	 *</pre>
	 * @param search 商品検索条件(ユーザID、商品区分名、商品名、会社名)
	 * @return 商品情報参照結果のリスト
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemInquirySelectSql02.sql")
	public List<ShoppingItemInquiryReadDto> selectShoppingItemInfoSearchCondition(@Param("dto") ShoppingItemInfoSearchConditionSearchQueryDto search);
	
	/**
	 *<pre>
	 * 商品テーブル:SHOPPING_ITEM_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLE、店舗テーブル:SHOP_TABLEから
	 * 指定のユーザID、商品JANコードを条件に商品情報を検索します。
	 *</pre>
	 * @param search 検索条件:ユーザID、商品JANコード
	 * @return 商品情報参照結果のリスト
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemInquirySelectSql03.sql")
	public List<ShoppingItemInquiryReadDto> findByIdAndShoppingItemJanCode(@Param("dto") UserIdAndShoppingItemJanCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDに対応する商品情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemTableCountSql01.sql")
	public int countById(@Param("dto") UserIdSearchQueryDto userId);
	
	/**
	 *<pre>
	 * 指定のユーザID、商品JANコードに対応する商品情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索条件:ユーザID、商品JANコード
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/shoppingitem/ShoppingItemTableCountSql02.sql")
	public int countByIdAndShoppingItemJanCode(@Param("dto") UserIdAndShoppingItemJanCodeSearchQueryDto search);
	
}
