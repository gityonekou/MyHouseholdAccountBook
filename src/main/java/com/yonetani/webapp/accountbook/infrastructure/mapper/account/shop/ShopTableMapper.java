/**
 * 店舗テーブル:SHOP_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.shop;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopKubunCodeListSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 店舗テーブル:SHOP_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ShopTableMapper {
	
	/**
	 *<pre>
	 * 店舗テーブル:SHOP_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 店舗テーブル:SHOP_TABLE出力情報
	 * @return 店舗テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/shop/ShopTableInsertSql01.sql")
	public int insert(@Param("dto") ShopReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定の店舗情報で店舗テーブル:SHOP_TABLEを更新します。
	 *</pre>
	 * @param writeDto 店舗テーブル:SHOP_TABLE出力情報
	 * @return 店舗テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/shop/ShopTableUpdateSql01.sql")
	public int update(@Param("dto") ShopReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定の店舗情報で店舗テーブル:SHOP_TABLEの表示順の値を更新します。
	 *</pre>
	 * @param writeDto 店舗テーブル:SHOP_TABLE出力情報
	 * @return 店舗テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/shop/ShopTableUpdateSql02.sql")
	public int updateShopSort(@Param("dto") ShopReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザIDを条件に店舗テーブル:SHOP_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 店舗テーブル:SHOP_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/shop/ShopTableSelectSql01.sql")
	public List<ShopReadWriteDto> findById(@Param("dto") UserIdSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、店舗コードを条件に店舗テーブル:SHOP_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、店舗コード
	 * @return 店舗テーブル:SHOP_TABLE参照結果
	 *
	 */
	@Select("sql/account/shop/ShopTableSelectSql02.sql")
	public ShopReadWriteDto findByIdAndShopCode(@Param("dto") UserIdAndShopCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDと指定した店舗表示順以降のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、店舗表示順
	 * @return 店舗テーブル:SHOP_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/shop/ShopTableSelectSql03.sql")
	public List<ShopReadWriteDto> findByIdAndShopSort(@Param("dto") UserIdAndShopSortSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDと指定した店舗表示順A～店舗表示順B間のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、店舗表示順A、店舗表示順B
	 * @return 店舗テーブル:SHOP_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/shop/ShopTableSelectSql04.sql")
	public List<ShopReadWriteDto> findByIdAndShopSortBetween(@Param("dto") UserIdAndShopSortBetweenABSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDと指定した店舗区分コードのリスト(in条件に指定する店舗区分コード)のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、店舗区分コードのリスト(in条件に指定する店舗区分コード)
	 * @return 店舗テーブル:SHOP_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/shop/ShopTableSelectSql05.sql")
	public List<ShopReadWriteDto> findByIdAndShopKubunCodeList(@Param("dto") UserIdAndShopKubunCodeListSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDに対応する店舗情報のうち、900番未満のものが何件あるかを取得します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/shop/ShopTableCountSql01.sql")
	public int countByIdAndLessThanNineHundred(@Param("dto") UserIdSearchQueryDto userId);
	
}
