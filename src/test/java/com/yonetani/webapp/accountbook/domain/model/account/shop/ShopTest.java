/**
 * Shopクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/09/23 : 1.00.01  feature-1.03-dev1   追加リファクタリング対応
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopDefaultPaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

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
		Shop shop = helper("user01", "001", "901", "テスト店舗", "001", "001");

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
		Shop shop = helper("user01", "001", "901", "テスト店舗", null, "001");

		assertNull(shop.getDefaultPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コードが空文字でもnullとして扱われる")
	void testFrom_正常系_デフォルト支払方法コード空文字() {
		Shop shop = helper("user01", "001", "901", "テスト店舗", "", "001");

		assertNull(shop.getDefaultPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		Shop shop1 = helper("user01", "001", "901", "テスト店舗", "001", "001");
		Shop shop2 = helper("user01", "001", "901", "テスト店舗", "001", "001");
		assertEquals(shop1, shop2);
		assertEquals(shop1.hashCode(), shop2.hashCode());
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コードの有無で等価にならない")
	void testEquals_正常系_デフォルト支払方法コードの有無で異なる() {
		Shop shopWith = helper("user01", "001", "901", "テスト店舗", "001", "001");
		Shop shopWithout = helper("user01", "001", "901", "テスト店舗", null, "001");
		assertNotEquals(shopWith, shopWithout);
	}
	
	/**
	 *<pre>
	 * Shopを生成するヘルパーです。
	 *</pre>
	 * @param userId
	 * @param shopCode
	 * @param shopKubunName
	 * @param shopName
	 * @param paymentMethodCode
	 * @param shopSort
	 * @return
	 *
	 */
	private Shop helper(String userId, String shopCode, String shopKubunName, String shopName, String paymentMethodCode, String shopSort) {
		return Shop.from(
				UserId.from(userId),
				ShopCode.from(shopCode),
				ShopKubunCode.from(shopKubunName),
				ShopName.from(shopName),
				ShopDefaultPaymentMethodCode.from(paymentMethodCode),
				ShopSort.from(shopSort));
	}
}
