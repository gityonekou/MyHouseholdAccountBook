/**
 * SisyutuItemTableRepository(支出項目テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.SisyutuItemTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * SisyutuItemTableRepository(支出項目テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class SisyutuItemTableDataSource implements SisyutuItemTableRepository {

	// マッパー
	private final SisyutuItemTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(SisyutuItem data) {
		mapper.insert(SisyutuItemReadWriteDto.from(
				data.getUserId().toString(),
				data.getSisyutuItemCode().toString(),
				data.getSisyutuItemName().toString(),
				data.getSisyutuItemDetailContext().toString(),
				data.getParentSisyutuItemCode().toString(),
				data.getSisyutuItemLevel().toString(),
				data.getSisyutuItemSort().toString()));

	}

}
