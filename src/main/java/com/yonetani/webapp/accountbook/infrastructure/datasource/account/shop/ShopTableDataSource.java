/**
 * ShopTableRepository(店舗テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shop;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.shop.ShopTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShopTableRepository(店舗テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class ShopTableDataSource implements ShopTableRepository {

	// マッパー
	private final ShopTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Shop data) {
		mapper.insert(ShopReadWriteDto.from(
				data.getUserId().toString(),
				data.getShopCode().toString(),
				data.getShopKubunCode().toString(),
				data.getShopName().toString(),
				data.getShopSort().toString()));

	}

}
