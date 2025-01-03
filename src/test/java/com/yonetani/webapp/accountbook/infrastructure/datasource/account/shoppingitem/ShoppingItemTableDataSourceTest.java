/**
 * ShoppingItemTableRepositoryのテストクラスです。
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータ登録・更新・参照の各種メソッドテストを実施します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList.ShoppingItemInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.shoppingitem.ShoppingItemTableMapper;

/**
 *<pre>
 * ShoppingItemTableRepositoryのテストクラスです。
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータ登録・更新・参照の各種メソッドテストを実施します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@MybatisTest
class ShoppingItemTableDataSourceTest {
	
	// ShoppingItemTableRepository
	private ShoppingItemTableRepository repository;
	// ShoppingItemTable mapper
	@Autowired
	private ShoppingItemTableMapper mapper;
	// DBアクセス
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 *<pre>
	 * セットアップ時の処理
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
		// テスト対象のリポジトリーをテストごとに作成
		repository = new ShoppingItemTableDataSource(mapper);
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
		// TODO:テスト終了時の処理
	}
	
	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem)} のためのテスト・メソッド。
	 */
	@Test
	void testAdd() {
		/* 全項目の登録チェック */
		// テストデータ1を読み込み
		ShoppingItem expectedData = getTestShoppingItemData(1);
		// データ追加
		assertEquals(1, repository.add(expectedData), "登録データが1件であること");
		// 登録されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID=? AND SHOPPING_ITEM_CODE=?",
				// ユーザID
				expectedData.getUserId().toString(),
				// 商品コード
				expectedData.getShoppingItemCode().toString());
		// 登録データと等しいこと(基準価格のスケールはDBの該当項目のスケール2に合わせてデータ登録してあることを前提としています)
		assertEquals(expectedData.getUserId().toString(), actualDataMap.get("USER_ID"), "登録データのユーザID(USER_ID)が正しいこと");
		assertEquals(expectedData.getShoppingItemCode().toString(), actualDataMap.get("SHOPPING_ITEM_CODE"), "登録データの商品コード(SHOPPING_ITEM_CODE)が正しいこと");
		assertEquals(expectedData.getShoppingItemKubunName().toString(), actualDataMap.get("SHOPPING_ITEM_KUBUN_NAME"), "登録データの商品区分名(SHOPPING_ITEM_KUBUN_NAME)が正しいこと");
		assertEquals(expectedData.getShoppingItemName().toString(), actualDataMap.get("SHOPPING_ITEM_NAME"), "登録データの商品名(SHOPPING_ITEM_NAME)が正しいこと");
		assertEquals(expectedData.getShoppingItemDetailContext().toString(), actualDataMap.get("SHOPPING_ITEM_DETAIL_CONTEXT"), "登録データの商品詳細(SHOPPING_ITEM_DETAIL_CONTEXT)が正しいこと");
		assertEquals(expectedData.getShoppingItemJanCode().toString(), actualDataMap.get("SHOPPING_ITEM_JAN_CODE"), "登録データの商品JANコード(SHOPPING_ITEM_JAN_CODE)が正しいこと");
		assertEquals(expectedData.getSisyutuItemCode().toString(), actualDataMap.get("SISYUTU_ITEM_CODE"), "登録データの支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals(expectedData.getCompanyName().toString(), actualDataMap.get("COMPANY_NAME"), "登録データの会社名(COMPANY_NAME)が正しいこと");
		assertEquals(expectedData.getShopCode().toString(), actualDataMap.get("STANDARD_SHOP_CODE"), "登録データの基準店舗コード(STANDARD_SHOP_CODE)が正しいこと");
		assertEquals(expectedData.getStandardPrice().getValue(), actualDataMap.get("STANDARD_PRICE"), "登録データの基準価格(STANDARD_PRICE)が正しいこと");
		assertEquals(expectedData.getShoppingItemCapacity().getValue(), actualDataMap.get("CAPACITY"), "登録データの内容量(CAPACITY)が正しいこと");
		assertEquals(expectedData.getShoppingItemCapacityUnit().toString(), actualDataMap.get("CAPACITY_UNIT"), "登録データの内容量単位(CAPACITY_UNIT)が正しいこと");
		assertEquals(expectedData.getShoppingItemCalories().getValue(), actualDataMap.get("CALORIES"), "登録データのカロリー(CALORIES)が正しいこと");
		
		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(expectedData), "同じデータを登録した場合、一意制約違反となること");
		
		/* null可項目の登録チェック */
		// テストデータ5を読み込み
		ShoppingItem expectedNullData = getTestShoppingItemData(5);
		// データ追加
		assertEquals(1, repository.add(expectedNullData), "登録データが1件であること(null)");
		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID=? AND SHOPPING_ITEM_CODE=?",
				// ユーザID
				expectedNullData.getUserId().toString(),
				// 商品コード
				expectedNullData.getShoppingItemCode().toString());
		// 登録データと等しいこと(基準価格のスケールはDBの該当項目のスケール2に合わせてデータ登録してあることを前提としています)
		assertEquals(expectedNullData.getUserId().toString(), actualNullDataMap.get("USER_ID"), "登録データのユーザID(USER_ID)が正しいこと");
		assertEquals(expectedNullData.getShoppingItemCode().toString(), actualNullDataMap.get("SHOPPING_ITEM_CODE"), "登録データの商品コード(SHOPPING_ITEM_CODE)が正しいこと");
		assertEquals(expectedNullData.getShoppingItemKubunName().toString(), actualNullDataMap.get("SHOPPING_ITEM_KUBUN_NAME"), "登録データの商品区分名(SHOPPING_ITEM_KUBUN_NAME)が正しいこと");
		assertEquals(expectedNullData.getShoppingItemName().toString(), actualNullDataMap.get("SHOPPING_ITEM_NAME"), "登録データの商品名(SHOPPING_ITEM_NAME)が正しいこと");
		assertNull(actualNullDataMap.get("SHOPPING_ITEM_DETAIL_CONTEXT"), "登録データの商品詳細(SHOPPING_ITEM_DETAIL_CONTEXT)がNULLであること");
		assertEquals(expectedNullData.getShoppingItemJanCode().toString(), actualNullDataMap.get("SHOPPING_ITEM_JAN_CODE"), "登録データの商品JANコード(SHOPPING_ITEM_JAN_CODE)が正しいこと");
		assertEquals(expectedNullData.getSisyutuItemCode().toString(), actualNullDataMap.get("SISYUTU_ITEM_CODE"), "登録データの支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals(expectedNullData.getCompanyName().toString(), actualNullDataMap.get("COMPANY_NAME"), "登録データの会社名(COMPANY_NAME)が正しいこと");
		assertNull(actualNullDataMap.get("STANDARD_SHOP_CODE"), "登録データの基準店舗コード(STANDARD_SHOP_CODE)がNULLであること");
		assertNull(actualNullDataMap.get("STANDARD_PRICE"), "登録データの基準価格(STANDARD_PRICE)がNULLであること");
		assertNull(actualNullDataMap.get("CAPACITY"), "登録データの内容量(CAPACITY)がNULLであること");
		assertNull(actualNullDataMap.get("CAPACITY_UNIT"), "登録データの内容量単位(CAPACITY_UNIT)がNULLであること");
		assertNull(actualNullDataMap.get("CALORIES"), "登録データのカロリー(CALORIES)がNULLであること");
		
		/* null不可項目の登録チェック */
		assertThrows(DataIntegrityViolationException.class, () -> repository.add(getTestShoppingItemData(102)), "「商品区分名」項目のnull登録不可");
		assertThrows(DataIntegrityViolationException.class, () -> repository.add(getTestShoppingItemData(103)), "「商品名」項目のnull登録不可");
		assertThrows(DataIntegrityViolationException.class, () -> repository.add(getTestShoppingItemData(104)), "「商品JANコード」項目のnull登録不可");
		assertThrows(DataIntegrityViolationException.class, () -> repository.add(getTestShoppingItemData(105)), "「支出項目コード」項目のnull登録不可");
		assertThrows(DataIntegrityViolationException.class, () -> repository.add(getTestShoppingItemData(106)), "「会社名」項目のnull登録不可");
	}
	
	
	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ShoppingItemTableDataSourceUpdateTest.sql")
	void testUpdate() {
		/* 全項目の更新チェック */
		// テストデータ2を読み込み、ShoppingItemの各パラメータに設定
		ShoppingItem expectedData = getTestShoppingItemData(2);
		// 更新前の支出項目コードを取得
		String expectedSisyutuItemCode = jdbcTemplate.queryForObject(
				// 商品テーブル検索SQL(unique data)
				"SELECT SISYUTU_ITEM_CODE FROM SHOPPING_ITEM_TABLE WHERE USER_ID=? AND SHOPPING_ITEM_CODE=?",
				// 取得クラス
				String.class,
				// ユーザID
				expectedData.getUserId().toString(),
				// 商品コード
				expectedData.getShoppingItemCode().toString());
		
		// データ更新(対象データあり)
		assertEquals(1, repository.update(expectedData), "更新データが1件であること");
		// 登録されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID=? AND SHOPPING_ITEM_CODE=?",
				// ユーザID
				expectedData.getUserId().toString(),
				// 商品コード
				expectedData.getShoppingItemCode().toString());
		// 更新データと等しいこと：ユーザID
		assertEquals(expectedData.getUserId().toString(), actualDataMap.get("USER_ID"), "ユーザID(USER_ID)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品コード
		assertEquals(expectedData.getShoppingItemCode().toString(), actualDataMap.get("SHOPPING_ITEM_CODE"), "商品コード(SHOPPING_ITEM_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品区分名
		assertEquals(expectedData.getShoppingItemKubunName().toString(), actualDataMap.get("SHOPPING_ITEM_KUBUN_NAME"), "商品区分名(SHOPPING_ITEM_KUBUN_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品名
		assertEquals(expectedData.getShoppingItemName().toString(), actualDataMap.get("SHOPPING_ITEM_NAME"), "商品名(SHOPPING_ITEM_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品詳細
		assertEquals(expectedData.getShoppingItemDetailContext().toString(), actualDataMap.get("SHOPPING_ITEM_DETAIL_CONTEXT"), "商品詳細(SHOPPING_ITEM_DETAIL_CONTEXT)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品JANコード
		assertEquals(expectedData.getShoppingItemJanCode().toString(), actualDataMap.get("SHOPPING_ITEM_JAN_CODE"), "商品JANコード(SHOPPING_ITEM_JAN_CODE)の更新後の値が正しいこと");
		// 更新されないこと：支出項目コード
		assertEquals(expectedSisyutuItemCode, actualDataMap.get("SISYUTU_ITEM_CODE"), "更新データの支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		// 更新データと等しいこと：会社名
		assertEquals(expectedData.getCompanyName().toString(), actualDataMap.get("COMPANY_NAME"), "会社名(COMPANY_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：基準店舗コード
		assertEquals(expectedData.getShopCode().toString(), actualDataMap.get("STANDARD_SHOP_CODE"), "基準店舗コード(STANDARD_SHOP_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：基準価格
		assertEquals(expectedData.getStandardPrice().getValue(), actualDataMap.get("STANDARD_PRICE"), "基準価格(STANDARD_PRICE)の更新後の値が正しいこと");
		// 更新データと等しいこと：内容量
		assertEquals(expectedData.getShoppingItemCapacity().getValue(), actualDataMap.get("CAPACITY"), "内容量(CAPACITY)の更新後の値が正しいこと");
		// 更新データと等しいこと：内容量単位
		assertEquals(expectedData.getShoppingItemCapacityUnit().toString(), actualDataMap.get("CAPACITY_UNIT"), "内容量単位(CAPACITY_UNIT)の更新後の値が正しいこと");
		// 更新データと等しいこと：カロリー
		assertEquals(expectedData.getShoppingItemCalories().getValue(), actualDataMap.get("CALORIES"), "カロリー(CALORIES)の更新後の値が正しいこと");
		
		// 更新データと等しいこと：内容量単位
		// 更新データと等しいこと：カロリー
		/* データ更新(対象データなし) */
		assertEquals(0, repository.update(getTestShoppingItemData(4)), "更新データが0件(対象なし)であること");
		
		/* null可項目の登録チェック */
		// テストデータ101を読み込み
		ShoppingItem expectedNullData = getTestShoppingItemData(101);
		// データ追加
		assertEquals(1, repository.update(expectedNullData), "更新データが1件であること(null)");
		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID=? AND SHOPPING_ITEM_CODE=?",
				// ユーザID
				expectedNullData.getUserId().toString(),
				// 商品コード
				expectedNullData.getShoppingItemCode().toString());
		// 更新データと等しいこと：ユーザID
		assertEquals(expectedNullData.getUserId().toString(), actualNullDataMap.get("USER_ID"), "ユーザID(USER_ID)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品コード
		assertEquals(expectedNullData.getShoppingItemCode().toString(), actualNullDataMap.get("SHOPPING_ITEM_CODE"), "商品コード(SHOPPING_ITEM_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品区分名
		assertEquals(expectedNullData.getShoppingItemKubunName().toString(), actualNullDataMap.get("SHOPPING_ITEM_KUBUN_NAME"), "商品区分名(SHOPPING_ITEM_KUBUN_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：商品名
		assertEquals(expectedNullData.getShoppingItemName().toString(), actualNullDataMap.get("SHOPPING_ITEM_NAME"), "商品名(SHOPPING_ITEM_NAME)の更新後の値が正しいこと");
		// 商品詳細の値がnullであること
		assertNull(actualNullDataMap.get("SHOPPING_ITEM_DETAIL_CONTEXT"), "商品詳細(SHOPPING_ITEM_DETAIL_CONTEXT)の更新後の値がnullであること");
		// 更新データと等しいこと：商品JANコード
		assertEquals(expectedNullData.getShoppingItemJanCode().toString(), actualNullDataMap.get("SHOPPING_ITEM_JAN_CODE"), "商品JANコード(SHOPPING_ITEM_JAN_CODE)の更新後の値が正しいこと");
		// 更新されないこと：支出項目コード
		assertEquals(expectedSisyutuItemCode, actualNullDataMap.get("SISYUTU_ITEM_CODE"), "更新データの支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		// 更新データと等しいこと：会社名
		assertEquals(expectedNullData.getCompanyName().toString(), actualNullDataMap.get("COMPANY_NAME"), "会社名(COMPANY_NAME)の更新後の値が正しいこと");
		// 基準店舗コードの値がnullであること
		assertNull(actualNullDataMap.get("STANDARD_SHOP_CODE"), "基準店舗コード(STANDARD_SHOP_CODE)の更新後の値がnullであること");
		// 基準価格の値がnullであること
		assertNull(actualNullDataMap.get("STANDARD_PRICE"), "基準価格(STANDARD_PRICE)の更新後の値がnullであること");
		// 内容量の値がnullであること
		assertNull(actualNullDataMap.get("CAPACITY"), "内容量(CAPACITY)の更新後の値がnullであること");
		// 内容量単位の値がnullであること
		assertNull(actualNullDataMap.get("CAPACITY_UNIT"), "内容量単位(CAPACITY_UNIT)の更新後の値がnullであること");
		// カロリーの値がnullであること
		assertNull(actualNullDataMap.get("CALORIES"), "カロリー(CALORIES)の更新後の値がnullであること");
	}
	
	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndShoppingItemCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ShoppingItemTableDataSourceFindByIdAndShoppingItemCodeTest.sql")
	void testFindByIdAndShoppingItemCode() {
		/* 全項目の取得チェック */
		// 期待値
		ShoppingItem expectedData = getTestShoppingItemData(1);
		// データ取得
		ShoppingItem actualData = repository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(expectedData.getUserId(), expectedData.getShoppingItemCode()));
		
		// 取得したデータが期待通りであること(全項目)
		assertEquals(expectedData.getUserId(), actualData.getUserId(), "ユーザIDが等しいこと");
		assertEquals(expectedData.getShoppingItemCode(), actualData.getShoppingItemCode(), "商品コードが等しいこと");
		assertEquals(expectedData.getShoppingItemKubunName(), actualData.getShoppingItemKubunName(), "商品区分名が等しいこと");
		assertEquals(expectedData.getShoppingItemName(), actualData.getShoppingItemName(), "商品名が等しいこと");
		assertEquals(expectedData.getShoppingItemDetailContext(), actualData.getShoppingItemDetailContext(), "商品詳細が等しいこと");
		assertEquals(expectedData.getShoppingItemJanCode(), actualData.getShoppingItemJanCode(), "商品JANコードが等しいこと");
		assertEquals(expectedData.getSisyutuItemCode(), actualData.getSisyutuItemCode(), "支出項目コードが等しいこと");
		assertEquals(expectedData.getCompanyName(), actualData.getCompanyName(), "会社名が等しいこと");
		assertEquals(expectedData.getShopCode(), actualData.getShopCode(), "基準店舗コードが等しいこと");
		assertEquals(expectedData.getStandardPrice(), actualData.getStandardPrice(), "基準価格が等しいこと");
		assertEquals(expectedData.getShoppingItemCapacity(), actualData.getShoppingItemCapacity(), "内容量が等しいこと");
		assertEquals(expectedData.getShoppingItemCapacityUnit(), actualData.getShoppingItemCapacityUnit(), "内容量単位が等しいこと");
		assertEquals(expectedData.getShoppingItemCalories(), actualData.getShoppingItemCalories(), "カロリーが等しいこと");
		
		// toStringチェック
		assertEquals(getToStringStr(1), actualData.toString(), "toStringチェック");
		
		/* 対象データなしの場合、nullとなること(商品コード) */
		assertNull(repository.findByIdAndShoppingItemCode(createSearchQueryUserIdAndShoppingItemCode("TEST-USER-ID", "00004")),
				"対象データなしの場合nullとなること(商品コード)");
		/* 対象データなしの場合、nullとなること(ユーザID) */
		assertNull(repository.findByIdAndShoppingItemCode(createSearchQueryUserIdAndShoppingItemCode("TEST-USER-ID_NOT_FOUND", "00001")),
				"対象データなしの場合nullとなること(ユーザID)");
		/* null項目の取得チェック */
		// 期待値
		ShoppingItem expectedNullData = getTestShoppingItemData(101);
		// データ取得
		ShoppingItem actualNullData = repository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(expectedNullData.getUserId(), expectedNullData.getShoppingItemCode()));
		
		// 取得したデータが期待通りであること(Null値項目あり)
		assertEquals(expectedNullData.getUserId(), actualNullData.getUserId(), "ユーザIDが等しいこと");
		assertEquals(expectedNullData.getShoppingItemCode(), actualNullData.getShoppingItemCode(), "商品コードが等しいこと");
		assertEquals(expectedNullData.getShoppingItemKubunName(), actualNullData.getShoppingItemKubunName(), "商品区分名が等しいこと");
		assertEquals(expectedNullData.getShoppingItemName(), actualNullData.getShoppingItemName(), "商品名が等しいこと");
		assertNull(actualNullData.getShoppingItemDetailContext().toString(), "商品詳細の値がnullであること");
		assertEquals(expectedNullData.getShoppingItemJanCode(), actualNullData.getShoppingItemJanCode(), "商品JANコードが等しいこと");
		assertEquals(expectedNullData.getSisyutuItemCode(), actualNullData.getSisyutuItemCode(), "支出項目コードが等しいこと");
		assertEquals(expectedNullData.getCompanyName(), actualNullData.getCompanyName(), "会社名が等しいこと");
		assertNull(actualNullData.getShopCode().toString(), "基準店舗コードの値がnullであること");
		assertNull(actualNullData.getStandardPrice().getValue(), "基準価格の値がnullであること");
		assertNull(actualNullData.getShoppingItemCapacity().getValue(), "内容量の値がnullであること");
		assertNull(actualNullData.getShoppingItemCapacityUnit().toString(), "内容量単位の値がnullであること");
		assertNull(actualNullData.getShoppingItemCalories().getValue(), "カロリーの値がnullであること");
		
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndSisyutuItemCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ShoppingItemTableDataSourceFindByIdAndSisyutuItemCodeTest.sql")
	void testFindByIdAndSisyutuItemCode() {
		/* 対象データなしの場合、0件となること(支出項目コード) */
		ShoppingItemInquiryList actualNotFound = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0040"));
		assertTrue(actualNotFound.isEmpty(), "対象データなしの場合0件となること(支出項目コード)");
		assertEquals(0, actualNotFound.getValues().size(), "対象データなしの場合0件となること(支出項目コード)");
		assertEquals("商品検索結果:0件", actualNotFound.toString(), "対象データなし(toStringチェック)");
		/* 対象データなしの場合、0件となること(ユーザID) */
		assertTrue(repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID_NOT_FOUND", "0010")).isEmpty(),
				"対象データなしの場合0件となること(ユーザID)");
		/* 対象データなしの場合、0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:ユーザID)) */
		assertTrue(repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID2", "0010")).isEmpty(),
				"対象データなしの場合0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:ユーザID))");
		/* 対象データなしの場合、0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:支出項目コード)) */
		assertTrue(repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0030")).isEmpty(),
				"対象データなしの場合0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:支出項目コード))");
		/* 対象データ1件(店舗コードに対応する店舗名なし) */
		ShoppingItemInquiryList actual1 = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0011"));
		assertEquals(1, actual1.getValues().size(), "対象データ1件(店舗コードに対応する店舗名なし)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(1), actual1.getValues(), "検索結果が正しいこと:対象データ1件(店舗コードに対応する店舗名なし)");
		/* 対象データ1件(NULL値項目) */
		ShoppingItemInquiryList actual2 = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0020"));
		assertEquals(1, actual2.getValues().size(), "対象データ1件(NULL値項目)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(2), actual2.getValues(), "検索結果が正しいこと:対象データ1件(NULL値項目)");
		/* 対象データ1件(全項目) */
		ShoppingItemInquiryList actual3 = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0017"));
		assertEquals(1, actual3.getValues().size(), "対象データ1件(全項目)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(3), actual3.getValues(), "検索結果が正しいこと:対象データ1件(全項目)");
		assertEquals(getToStringStr(2), actual3.toString(), "対象データ1件(全項目)");
		/* 対象データ2件(店舗コードに対応する店舗名なし(店舗名なし商品コード:00001、全項目商品コード:00005) 商品コードの降順で表示 */
		ShoppingItemInquiryList actual4 = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0010"));
		assertEquals(2, actual4.getValues().size(), "対象データ2件(店舗コードに対応する店舗名なし(1件)、全項目1件)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(4), actual4.getValues(), "検索結果とソート結果が正しいこと:対象データ2件(店舗コードに対応する店舗名なし(1件)、全項目1件)");
		/* 対象データ2件(全項目2件) 商品コードの降順で表示 */
		ShoppingItemInquiryList actual5 = repository.findByIdAndSisyutuItemCode(createSearchQueryUserIdAndSisyutuItemCode("TEST-USER-ID", "0023"));
		assertEquals(2, actual5.getValues().size(), "対象データ2件(全項目2件)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(5), actual5.getValues(), "検索結果とソート結果が正しいこと:対象データ2件(全項目2件)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#findByIdAndShoppingItemJanCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ShoppingItemTableDataSourceFindByIdAndShoppingItemJanCodeTest.sql")
	void testFindByIdAndShoppingItemJanCode() {
		/* 対象データなしの場合、0件となること(商品JANコード) */
		assertTrue(repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "9999999999999")).isEmpty(),
				"対象データなしの場合0件となること(商品JANコード)");
		/* 対象データなしの場合、0件となること(ユーザID) */
		assertTrue(repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID_NOT_FOUND", "1234567890100")).isEmpty(),
				"対象データなしの場合0件となること(ユーザID)");
		/* 対象データなしの場合、0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:ユーザID)) */
		assertTrue(repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID2", "1234567890100")).isEmpty(),
				"対象データなしの場合0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:ユーザID))");
		/* 対象データなしの場合、0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:支出項目コード)) */
		assertTrue(repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "0030")).isEmpty(),
				"対象データなしの場合0件となること(支出項目コードに対応する支出項目名なし(支出項目テーブル:支出項目コード))");
		/* 対象データ1件(店舗コードに対応する店舗名なし) */
		ShoppingItemInquiryList actual1 = repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890400"));
		assertEquals(1, actual1.getValues().size(), "対象データ1件(店舗コードに対応する店舗名なし)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(101), actual1.getValues(), "検索結果が正しいこと:対象データ1件(店舗コードに対応する店舗名なし)");
		/* 対象データ1件(NULL値項目) */
		ShoppingItemInquiryList actual2 = repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890600"));
		assertEquals(1, actual2.getValues().size(), "対象データ1件(NULL値項目)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(102), actual2.getValues(), "検索結果が正しいこと:対象データ1件(NULL値項目)");
		/* 対象データ1件(全項目) */
		ShoppingItemInquiryList actual3 = repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890700"));
		assertEquals(1, actual3.getValues().size(), "対象データ1件(全項目)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(103), actual3.getValues(), "検索結果が正しいこと:対象データ1件(全項目)");
		/* 対象データ2件(店舗コードに対応する店舗名なし(店舗名なし商品コード:00008、全項目商品コード:00009)) 商品コードの降順で表示 */
		ShoppingItemInquiryList actual4 = repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890800"));
		assertEquals(2, actual4.getValues().size(), "対象データ2件(店舗コードに対応する店舗名なし(1件)、全項目1件)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(104), actual4.getValues(), "検索結果とソート結果が正しいこと:対象データ2件(店舗コードに対応する店舗名なし(1件)、全項目1件)");
		/* 対象データ2件(全項目2件) 商品コードの降順で表示 */
		ShoppingItemInquiryList actual5 = repository.findByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890100"));
		assertEquals(2, actual5.getValues().size(), "対象データ2件(全項目2件)");
		assertIterableEquals(getTestShoppingItemInquiryItemListData(105), actual5.getValues(), "検索結果とソート結果が正しいこと:対象データ2件(全項目2件)");
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
	@Sql("ShoppingItemTableDataSourceCountByIdTest.sql")
	void testCountById() {
		/* 対象データなしの場合、0件となること */
		assertEquals(0, repository.countById(createSearchQueryUserId("TEST-USER-ID_NOT_FOUND")), "対象データなしの場合、0件となること");
		/* 登録済みデータ2件の場合、2件となること */
		assertEquals(2, repository.countById(createSearchQueryUserId("TEST-USER-ID")), "登録済みデータ2件の場合、2件となること");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem.ShoppingItemTableDataSource#countByIdAndShoppingItemJanCode(com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ShoppingItemTableDataSourceCountByIdAndShoppingItemJanCodeTest.sql")
	void testCountByIdAndShoppingItemJanCode() {
		/* 対象データなしの場合、0件となること(ユーザID) */
		assertEquals(0, repository.countByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID_NOT_FOUND", "1234567890100")), "対象データなしの場合、0件となること(ユーザID)");
		/* 対象データなしの場合、0件となること(商品JANコード) */
		assertEquals(0, repository.countByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "9999999999999")), "対象データなしの場合、0件となること(商品JANコード)");
		/* 登録済みデータ3件で対象データ1件の場合、1件となること */
		assertEquals(1, repository.countByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890100")), "登録済みデータ3件で対象データ1件の場合、1件となること");
		/* 登録済みデータ3件で対象データ2件の場合、2件となること */
		assertEquals(2, repository.countByIdAndShoppingItemJanCode(createSearchQueryUserIdAndShoppingItemJanCode("TEST-USER-ID", "1234567890600")), "登録済みデータ3件で対象データ2件の場合、2件となること");
	}
	
	/**
	 *<pre>
	 * ShoppingItemのテストデータを返します。
	 *</pre>
	 * @param type
	 * @return
	 *
	 */
	private ShoppingItem getTestShoppingItemData(int type) {
		// TODO:本来は、リソースに設定したデータファイルからデータを取得してドメインを作成したいところ。時間みながら、共通のUtilを作って値を取得するように修正する
		ShoppingItem data = null;
		switch (type) {
			case 1:
				// 基準価格(BigDecimal)はDBの該当項目の小数点以下桁数のスケールに合わせる
				data = ShoppingItem.from("TEST-USER-ID", "00001", "商品区分名１", "商品名１", "商品詳細１", "1234567890100", "0010", "会社名１", "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			case 2:
				// 基準価格(BigDecimal)はDBの該当項目の小数点以下桁数のスケールに合わせる
				data = ShoppingItem.from("TEST-USER-ID", "00002", "商品区分名２", "商品名２", "商品詳細２", "1234567890200", "0020", "会社名２", "020", DomainCommonUtils.convertKingakuBigDecimal(1280), 120, "02", 1200);
				break;
			case 3:
				// 基準価格(BigDecimal)はDBの該当項目の小数点以下桁数のスケールに合わせる
				data = ShoppingItem.from("TEST-USER-ID", "00003", "商品区分名３", "商品名３", "商品詳細３", "1234567890300", "0030", "会社名３", "030", DomainCommonUtils.convertKingakuBigDecimal(1380), 130, "03", 1300);
				break;
			case 4:
				// 基準価格(BigDecimal)はDBの該当項目の小数点以下桁数のスケールに合わせる
				data = ShoppingItem.from("TEST-USER-ID", "00004", "商品区分名４", "商品名４", "商品詳細４", "1234567890400", "0040", "会社名４", "040", DomainCommonUtils.convertKingakuBigDecimal(1480), 140, "04", 1400);
				break;
			case 5:
				// 新規登録用null可データ
				data = ShoppingItem.from("TEST-USER-ID", "00005", "商品区分名NULL", "商品名NULL", null, "1234567890500", "0050", "会社名NULL", null, null, null, null, null);
				break;
			case 101:
				// 2のデータをnull可データに更新用
				data = ShoppingItem.from("TEST-USER-ID", "00002", "商品区分名２NULL", "商品名２NULL", null, "1234567890600", "0060", "会社名２NULL", null, null, null, null, null);
				break;
			case 102:
				// null不可データ(商品区分名)
				data = ShoppingItem.from("TEST-USER-ID", "00999", null, "商品名１", "商品詳細１", "1234567890100", "0010", "会社名１", "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			case 103:
				// null不可データ(商品名)
				data = ShoppingItem.from("TEST-USER-ID", "00999", "商品区分名１", null, "商品詳細１", "1234567890100", "0010", "会社名１", "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			case 104:
				// null不可データ(商品JANコード)
				data = ShoppingItem.from("TEST-USER-ID", "00999", "商品区分名１", "商品名１", "商品詳細１", null, "0010", "会社名１", "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			case 105:
				// null不可データ(支出項目コード)
				data = ShoppingItem.from("TEST-USER-ID", "00999", "商品区分名１", "商品名１", "商品詳細１", "1234567890100", null, "会社名１", "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			case 106:
				// null不可データ(会社名)
				data = ShoppingItem.from("TEST-USER-ID", "00999", "商品区分名１", "商品名１", "商品詳細１", "1234567890100", "0010", null, "010", DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100);
				break;
			default:
		}
		
		return data;
	}
	
	/**
	 *<pre>
	 * ShoppingItemInquiryListのテストデータ(ShoppingItemInquiryItemのリスト)を返します。
	 *</pre>
	 * @param type
	 * @return
	 *
	 */
	private List<ShoppingItemInquiryItem> getTestShoppingItemInquiryItemListData(int type) {
		// TODO:本来は、リソースに設定したデータファイルからデータを取得してドメインを作成したいところ。時間みながら、共通のUtilを作って値を取得するように修正する
		List<ShoppingItemInquiryItem> dataList = new ArrayList<>();
		switch(type) {
			case 1:
				dataList.add(ShoppingItemInquiryItem.from("00006", "商品区分名６", "商品名６", "商品詳細６", "1334567890101", "税金支払い", "会社名６", null, DomainCommonUtils.convertKingakuBigDecimal(1183), 660, "06", 6600));
				break;
			case 2:
				dataList.add(ShoppingItemInquiryItem.from("00002", "商品区分名２NULL", "商品名２NULL", null, "1234567890200", "飲食日用品", "会社名２NULL", null, null, null, null, null));
				break;
			case 3:
				dataList.add(ShoppingItemInquiryItem.from("00007", "商品区分名７", "商品名７", "商品詳細７", "1234567890700", "イデコ", "会社名７", "テスト店舗２", DomainCommonUtils.convertKingakuBigDecimal(2470), 770, "07", 7700));
				break;
			case 4:
				// 商品コードの降順で表示
				dataList.add(ShoppingItemInquiryItem.from("00005", "商品区分名１", "商品名１－２", "商品詳細１－２", "2234567890100", "その他", "会社名１", "テスト店舗１０", DomainCommonUtils.convertKingakuBigDecimal(1182), 111, "05", 1110));
				dataList.add(ShoppingItemInquiryItem.from("00001", "商品区分名１", "商品名１－１", "商品詳細１－１", "1234567890100", "その他", "会社名１", null, DomainCommonUtils.convertKingakuBigDecimal(1180), 110, "01", 1100));
				break;
			case 5:
				// 商品コードの降順で表示
				dataList.add(ShoppingItemInquiryItem.from("00009", "商品区分名９", "商品名９", "商品詳細９", "1234567891900", "食費", "会社名９", "テスト店舗３０", DomainCommonUtils.convertKingakuBigDecimal(3090), 990, "99", 9900));
				dataList.add(ShoppingItemInquiryItem.from("00008", "商品区分名８", "商品名８", "商品詳細８", "1234567891800", "食費", "会社名８", "テスト店舗２", DomainCommonUtils.convertKingakuBigDecimal(3080), 880, "08", 8800));
				break;
			case 101:
				dataList.add(ShoppingItemInquiryItem.from("00004", "商品区分名４", "商品名４", "商品詳細４", "1234567890400", "税金支払い", "会社名４", null, DomainCommonUtils.convertKingakuBigDecimal(1480), 140, "04", 1400));
				break;
			case 102:
				dataList.add(ShoppingItemInquiryItem.from("00006", "商品区分名６NULL", "商品名６NULL", null, "1234567890600", "飲食日用品", "会社名６NULL", null, null, null, null, null));
				break;
			case 103:
				dataList.add(ShoppingItemInquiryItem.from("00007", "商品区分名７", "商品名７", "商品詳細７", "1234567890700", "イデコ", "会社名７", "テスト店舗２", DomainCommonUtils.convertKingakuBigDecimal(1780), 170, "07", 1700));
				break;
			case 104:
				// 商品コードの降順で表示
				dataList.add(ShoppingItemInquiryItem.from("00009", "商品区分名９", "商品名９", "商品詳細９", "1234567890800", "税金支払い", "会社名９", "テスト店舗３０", DomainCommonUtils.convertKingakuBigDecimal(1990), 190, "99", 1900));
				dataList.add(ShoppingItemInquiryItem.from("00008", "商品区分名８", "商品名８", "商品詳細８", "1234567890800", "食費", "会社名８", null, DomainCommonUtils.convertKingakuBigDecimal(1880), 180, "08", 1800));
				break;
			case 105:
				// 商品コードの降順で表示
				dataList.add(ShoppingItemInquiryItem.from("00005", "商品区分名１", "商品名１－２", "商品詳細１－２", "1234567890100", "その他", "会社名１", "テスト店舗１０", DomainCommonUtils.convertKingakuBigDecimal(1580), 115, "05", 1155));
				dataList.add(ShoppingItemInquiryItem.from("00001", "商品区分名１", "商品名１－１", "商品詳細１－１", "1234567890100", "その他", "会社名１", "テスト店舗２", DomainCommonUtils.convertKingakuBigDecimal(1180), 112, "02", 1122));
				break;
			default:
		}
		
		return dataList;
	}
	
	/**
	 *<pre>
	 * ShoppingItemのtoStringメソッドで返される文字列を固定文字列として返します。
	 *</pre>
	 * @return ShoppingItemの文字列の値
	 *
	 */
	private String getToStringStr(int type) {
		// TODO:本来は期待値はテキストファイルのデータを取得したいところだが、固定でも別にいいような気もするし、どうするか
		String str = null;
		switch(type) {
			case 1:
				str = "ShoppingItem(userId=TEST-USER-ID, shoppingItemCode=00001, shoppingItemKubunName=商品区分名１, shoppingItemName=商品名１, shoppingItemDetailContext=商品詳細１, shoppingItemJanCode=1234567890100, sisyutuItemCode=0010, companyName=会社名１, shopCode=010, standardPrice=1,180円, shoppingItemCapacity=110, shoppingItemCapacityUnit=01, shoppingItemCalories=1100)";
				break;
			case 2:
				str = "商品検索結果:1件:[[0][ShoppingItemInquiryList.ShoppingItemInquiryItem(shoppingItemCode=00007, shoppingItemKubunName=商品区分名７, shoppingItemName=商品名７, shoppingItemDetailContext=商品詳細７, shoppingItemJanCode=1234567890700, sisyutuItemName=イデコ, companyName=会社名７, standardShopName=テスト店舗２, standardPrice=2,470円, shoppingItemCapacity=770, shoppingItemCapacityUnit=07, shoppingItemCalories=7700)]]";
				break;
			default:
		}
		
		return str;
	}
	
	/**
	 *<pre>
	 * 引数の値からSearchQueryUserIdを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @return 検索条件ドメイン(SearchQueryUserId)
	 *
	 */
	private SearchQueryUserId createSearchQueryUserId(String userId) {
		return SearchQueryUserId.from(UserId.from(userId));
	}
	
	/**
	 *<pre>
	 * 引数の値からSearchQueryUserIdAndSisyutuItemCodeを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemCode 支出項目コード
	 * @return 検索条件ドメイン(SearchQueryUserIdAndSisyutuItemCode)
	 *
	 */
	private SearchQueryUserIdAndSisyutuItemCode createSearchQueryUserIdAndSisyutuItemCode(String userId, String sisyutuItemCode) {
		return SearchQueryUserIdAndSisyutuItemCode.from(UserId.from(userId), SisyutuItemCode.from(sisyutuItemCode));
	}
	
	/**
	 *<pre>
	 * 引数の値からSearchQueryUserIdAndShoppingItemCodeを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemCode 商品コード
	 * @return 検索条件ドメイン(SearchQueryUserIdAndShoppingItemCode)
	 *
	 */
	private SearchQueryUserIdAndShoppingItemCode createSearchQueryUserIdAndShoppingItemCode(String userId, String shoppingItemCode) {
		return SearchQueryUserIdAndShoppingItemCode.from(UserId.from(userId), ShoppingItemCode.from(shoppingItemCode));
	}
	
	/**
	 *<pre>
	 * 引数の値からSearchQueryUserIdAndShoppingItemJanCodeを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemJanCode 商品JANコード
	 * @return 検索条件ドメイン(SearchQueryUserIdAndShoppingItemJanCode)
	 *
	 */
	private SearchQueryUserIdAndShoppingItemJanCode createSearchQueryUserIdAndShoppingItemJanCode(String userId, String shoppingItemJanCode) {
		return SearchQueryUserIdAndShoppingItemJanCode.from(UserId.from(userId), ShoppingItemJanCode.from(shoppingItemJanCode));
	}
}
