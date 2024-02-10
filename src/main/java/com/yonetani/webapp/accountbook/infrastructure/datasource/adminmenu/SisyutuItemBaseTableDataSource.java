/**
 * SisyutuItemBaseTableRepository(支出項目テーブル(BASE):登録/全件取得)を実装したデータソースです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.adminmenu;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBaseList;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.SisyutuItemBaseTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.SisyutuItemBaseTableReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu.SisyutuItemBaseTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * SisyutuItemBaseTableRepository(支出項目テーブル(BASE):登録/全件取得)を実装したデータソースです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class SisyutuItemBaseTableDataSource implements SisyutuItemBaseTableRepository {

	// マッパー
	private final SisyutuItemBaseTableMapper sisyutuItemBaseTableMapper;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(SisyutuItemBase data) {
		// 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEにデータを追加
		return sisyutuItemBaseTableMapper.insert(SisyutuItemBaseTableReadWriteDto.from(
				data.getSisyutuItemCode().toString(),
				data.getSisyutuItemName().toString(),
				data.getSisyutuItemDetailContext().toString(),
				data.getParentSisyutuItemCode().toString(),
				data.getSisyutuItemLevel().toString(),
				data.getSisyutuItemSort().toString()
				));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuItemBaseList findAll() {
		List<SisyutuItemBaseTableReadWriteDto> readDataList = sisyutuItemBaseTableMapper.findAll();
		return SisyutuItemBaseList.from(readDataList.stream().map(dto ->
			SisyutuItemBase.from(
					dto.getSisyutuItemCode(),
					dto.getSisyutuItemName(),
					dto.getSisyutuItemDetailContext(),
					dto.getParentSisyutuItemCode(),
					dto.getSisyutuItemLevel(),
					dto.getSisyutuItemSort())).collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countAll() {
		return sisyutuItemBaseTableMapper.countAll();
	}

}
