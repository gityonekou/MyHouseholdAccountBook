/**
 * ShopBaseTableRepository(店舗テーブル(BASE):登録/全件取得)を実装したデータソースです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.adminmenu;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBaseList;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.ShopBaseTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.ShopBaseTableReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu.ShopBaseTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShopBaseTableRepository(店舗テーブル(BASE):登録/全件取得)を実装したデータソースです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class ShopBaseTableDataSource implements ShopBaseTableRepository {

	// マッパー
	private final ShopBaseTableMapper shopBaseTableMapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(ShopBase data) {
		// 店舗テーブル(BASE):SHOP_BASE_TABLEにデータを追加
		return shopBaseTableMapper.insert(ShopBaseTableReadWriteDto.from(
				data.getShopCode().toString(),
				data.getShopName().toString()
				));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopBaseList findAll() {
		List<ShopBaseTableReadWriteDto> readDataList = shopBaseTableMapper.findAll();
		return ShopBaseList.from(readDataList.stream().map(dto ->
			ShopBase.from(dto.getShopCode(), dto.getShopName())
		).collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countAll() {
		return shopBaseTableMapper.countAll();
	}

}
