/**
 * Shopクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * Shopクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("店舗(Shop)のテスト")
class ShopTest {

	@Test
	@DisplayName("正常系：全項目指定で生成")
	void testFrom_正常系_全項目指定() {
		Shop shop = Shop.from("user01", "001", "901", "テスト店舗", "001", "001");

		assertEquals("user01", shop.getUserId().getValue());
		assertEquals("001", shop.getShopCode().getValue());
		assertEquals("901", shop.getShopKubunCode().getValue());
		assertEquals("テスト店舗", shop.getShopName().getValue());
		assertEquals("001", shop.getShopSort().getValue());
		assertEquals("001", shop.getDefaultPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コードがnullでも生成できる(任意項目)")
	void testFrom_正常系_デフォルト支払方法コードnull() {
		Shop shop = Shop.from("user01", "001", "901", "テスト店舗", "001", null);

		assertNull(shop.getDefaultPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コードが空文字でもnullとして扱われる")
	void testFrom_正常系_デフォルト支払方法コード空文字() {
		Shop shop = Shop.from("user01", "001", "901", "テスト店舗", "001", "");

		assertNull(shop.getDefaultPaymentMethodCode());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		Shop shop1 = Shop.from("user01", "001", "901", "テスト店舗", "001", "001");
		Shop shop2 = Shop.from("user01", "001", "901", "テスト店舗", "001", "001");
		assertEquals(shop1, shop2);
		assertEquals(shop1.hashCode(), shop2.hashCode());
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コードの有無で等価にならない")
	void testEquals_正常系_デフォルト支払方法コードの有無で異なる() {
		Shop shopWith = Shop.from("user01", "001", "901", "テスト店舗", "001", "001");
		Shop shopWithout = Shop.from("user01", "001", "901", "テスト店舗", "001", null);
		assertNotEquals(shopWith, shopWithout);
	}
}
