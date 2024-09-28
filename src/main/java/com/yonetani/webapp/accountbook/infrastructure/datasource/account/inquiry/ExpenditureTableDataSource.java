/**
 * ExpenditureTableRepository(支出テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.ExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.ExpenditureTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ExpenditureTableRepository(支出テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class ExpenditureTableDataSource implements ExpenditureTableRepository {
	
	// マッパー
	private final ExpenditureTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(ExpenditureItem data) {
		// 支出情報を支出テーブルに出力
		return mapper.insert(createExpenditureReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(ExpenditureItem data) {
		// 支出テーブル:EXPENDITURE_TABLEを更新
		return mapper.update(createExpenditureReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(ExpenditureItem data) {
		// 支出テーブル:EXPENDITURE_TABLEから指定の支出情報を論理削除
		return mapper.delete(createExpenditureReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExpenditureItemInquiryList findById(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		List<ExpenditureReadWriteDto> searchResult = mapper.findById(UserIdAndYearMonthSearchQueryDto.from(searchQuery));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ExpenditureItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ExpenditureItemInquiryList.from(searchResult.stream().map(dto -> createExpenditureItem(dto))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserIdAndYearMonth searchQuery) {
		// ユーザID、対象年月に対応する収入情報の件数を返します。
		return mapper.countById(UserIdAndYearMonthSearchQueryDto.from(searchQuery));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuKingakuTotalAmount sumExpenditureKingaku(SearchQueryUserIdAndYearMonth searchQuery) {
		// ユーザID、対象年月に対応する支出金額合計値を取得し合計金額(ドメイン)に変換して返します。
		// 値がnull(対象月の支出情報なし)の場合、0の値を返します。
		BigDecimal totalAmount = mapper.sumExpenditureKingaku(UserIdAndYearMonthSearchQueryDto.from(searchQuery));
		if(totalAmount == null) {
			return SisyutuKingakuTotalAmount.ZERO;
		} else {
			return SisyutuKingakuTotalAmount.from(totalAmount);
		}
	}
	
	/**
	 *<pre>
	 * 引数で指定した支出テーブル:EXPENDITURE_TABLE読込・出力情報から支出テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 支出テーブル:EXPENDITURE_TABLE読込・出力情報
	 * @return 支出テーブル情報ドメインモデル
	 *
	 */
	private ExpenditureItem createExpenditureItem(ExpenditureReadWriteDto dto) {
		return ExpenditureItem.from(
				// ユーザID
				dto.getUserId(),
				// 対象年
				dto.getTargetYear(),
				// 対象月
				dto.getTargetMonth(),
				// 支出コード
				dto.getSisyutuCode(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// イベントコード
				dto.getEventCode(),
				// 支出名称
				dto.getSisyutuName(),
				// 支出区分
				dto.getSisyutuKubun(),
				// 支出詳細
				dto.getSisyutuDetailContext(),
				// 支払日
				dto.getShiharaiDate(),
				// 支出予定金額
				dto.getSisyutuYoteiKingaku(),
				// 支出金額
				dto.getSisyutuKingaku(),
				// 削除フラグ
				dto.isDeleteFlg());
	}
	
	/**
	 *<pre>
	 * 引数で指定した支出テーブル情報ドメインモデルから支出テーブル:EXPENDITURE_TABLE読込・出力情報を生成して返します。
	 *</pre>
	 * @param domain 支出テーブル情報ドメインモデル
	 * @return 支出テーブル:EXPENDITURE_TABLE読込・出力情報
	 *
	 */
	private ExpenditureReadWriteDto createExpenditureReadWriteDto(ExpenditureItem domain) {
		return ExpenditureReadWriteDto.from(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYear().getValue(),
				// 対象月
				domain.getTargetMonth().getValue(),
				// 支出コード
				domain.getSisyutuCode().getValue(),
				// 支出項目コード
				domain.getSisyutuItemCode().getValue(),
				// イベントコード
				domain.getEventCode().getValue(),
				// 支出名称
				domain.getSisyutuName().getValue(),
				// 支出区分
				domain.getSisyutuKubun().getValue(),
				// 支出詳細
				domain.getSisyutuDetailContext().getValue(),
				// 支払日
				domain.getShiharaiDate().getValue(),
				// 支出予定金額
				domain.getSisyutuYoteiKingaku().getValue(),
				// 支出金額
				domain.getSisyutuKingaku().getValue(),
				// 削除フラグ
				domain.getDeleteFlg().getValue());
	}
}
