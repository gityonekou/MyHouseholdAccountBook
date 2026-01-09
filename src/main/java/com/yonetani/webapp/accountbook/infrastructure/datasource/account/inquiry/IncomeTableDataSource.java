/**
 * IncomeTableRepository(収入テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalAvailableFunds;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * IncomeTableRepository(収入テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class IncomeTableDataSource implements IncomeTableRepository {
	
	// マッパー
	private final IncomeTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(IncomeItem data) {
		// 収入情報を収入テーブルに出力
		return mapper.insert(IncomeReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(IncomeItem data) {
		// 収入テーブル:INCOME_TABLEを更新
		return mapper.update(IncomeReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(IncomeItem data) {
		// 収入テーブル:INCOME_TABLEから指定の収入情報を論理削除
		return mapper.delete(IncomeReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeItemInquiryList findById(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		List<IncomeReadWriteDto> searchResult = mapper.findById(UserIdAndYearMonthSearchQueryDto.from(searchQuery));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return IncomeItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return IncomeItemInquiryList.from(searchResult.stream().map(dto -> createIncomeItem(dto))
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
	public TotalAvailableFunds sumIncomeKingaku(SearchQueryUserIdAndYearMonth searchQuery) {
		// ユーザID、対象年月に対応する利用可能資金合計を取得し合計金額(ドメイン)に変換して返します。
		return TotalAvailableFunds.from(mapper.sumIncomeKingaku(UserIdAndYearMonthSearchQueryDto.from(searchQuery)));
	}
	
	/**
	 *<pre>
	 * 引数で指定した収入テーブル:INCOME_TABLE読込・出力情報から収入テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 収入テーブル:INCOME_TABLE読込・出力情報
	 * @return 収入テーブル情報ドメインモデル
	 *
	 */
	private IncomeItem createIncomeItem(IncomeReadWriteDto dto) {
		return IncomeItem.from(
				// ユーザID
				dto.getUserId(),
				// 対象年
				dto.getTargetYear(),
				// 対象月
				dto.getTargetMonth(),
				// 収入コード
				dto.getSyuunyuuCode(),
				// 収入区分
				dto.getSyuunyuuKubun(),
				// 収入詳細
				dto.getSyuunyuuDetailContext(),
				// 収入金額
				dto.getSyuunyuuKingaku(),
				// 削除フラグ
				dto.isDeleteFlg());
	}
}
