/**
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.shoppingregist;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist.ShoppingRegistReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist.SimpleShoppingRegistItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndShoppingRegistCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ShoppingRegistTableMapper {
	
	/**
	 *<pre>
	 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 買い物登録情報テーブル:SHOPPING_REGIST_TABLE情報
	 * @return 買い物登録情報テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/shoppingregist/ShoppingRegistTableInsertSql01.sql")
	public int insert(@Param("dto") ShoppingRegistReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEの情報を指定の買い物登録情報で更新します。
	 *</pre>
	 * @param writeDto 買い物登録情報テーブル:SHOPPING_REGIST_TABLE情報
	 * @return 買い物登録情報テーブルを更新した件数
	 *
	 */
	@Update("sql/account/shoppingregist/ShoppingRegistTableUpdateSql01.sql")
	public int update(@Param("dto") ShoppingRegistReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * ユニークキー(ユーザID、対象年、対象月、買い物登録コード)を条件に買い物登録情報テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月、買い物登録コード
	 * @return 買い物登録情報テーブル検索結果
	 *
	 */
	@Select("sql/account/shoppingregist/ShoppingRegistTableSelectSql01.sql")
	public ShoppingRegistReadWriteDto findByUniqueKey(@Param("dto") UserIdAndYearMonthAndShoppingRegistCodeSearchQueryDto dto);
	
	/**
	 *<pre>
	 * ユーザID、対象年、対象月を条件に簡易タイプ買い物登録画面に表示する買い物一覧情報を検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 簡易タイプ買い物登録画面に表示する買い物一覧情報検索結果のリスト
	 *
	 */
	@Select("sql/account/shoppingregist/ShoppingRegistTableSelectSql02.sql")
	public List<SimpleShoppingRegistItemReadDto> findById(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
	/**
	 *<pre>
	 * 検索条件に一致する買い物登録情報が何件あるかを取得します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 指定条件に一致するデータの件数
	 *
	 */
	@Select("sql/account/shoppingregist/ShoppingRegistTableCountSql01.sql")
	public int countById(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
}
