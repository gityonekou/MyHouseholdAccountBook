/**
 * ShoppingItemTableRepositoryのテストクラスです。
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータを登録・更新・参照の各種メソッドのテストを実施します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShoppingItemTableRepositoryのテストクラスです。
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータを登録・更新・参照の各種メソッドのテストを実施します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@MybatisTest
@RequiredArgsConstructor
class ShoppingItemTableDataSourceTest {

	// ShoppingItemTableRepository
	private final ShoppingItemTableRepository repository;
	
	/**
	 *<pre>
	 * セットアップ時の処理
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
		System.out.println("setup call.");
	}

	/**
	 *<pre>
	 * テスト終了時の処理(clean up)
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@AfterEach
	void tearDown() throws Exception {
		System.out.println("teardown call.");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem)} のためのテスト・メソッド。
	 */
	@Test
	void testAdd() {
		repository.add(ShoppingItem.from("test", "0001", null, null, null, null, null, null, null, null));
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem)} のためのテスト・メソッド。
	 */
	@Test
	void testUpdate() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndShoppingItemCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode)} のためのテスト・メソッド。
	 */
	@Test
	void testFindByIdAndShoppingItemCode() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndSisyutuItemCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode)} のためのテスト・メソッド。
	 */
	@Test
	void testFindByIdAndSisyutuItemCode() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndShoppingItemJanCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode)} のためのテスト・メソッド。
	 */
	@Test
	void testFindByIdAndShoppingItemJanCode() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#selectShoppingItemInfoSearchCondition(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryShoppingItemInfoSearchCondition)} のためのテスト・メソッド。
	 */
	@Test
	void testSelectShoppingItemInfoSearchCondition() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#countById(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId)} のためのテスト・メソッド。
	 */
	@Test
	void testCountById() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#countByIdAndShoppingItemJanCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode)} のためのテスト・メソッド。
	 */
	@Test
	void testCountByIdAndShoppingItemJanCode() {
		fail("まだ実装されていません");
	}

}
