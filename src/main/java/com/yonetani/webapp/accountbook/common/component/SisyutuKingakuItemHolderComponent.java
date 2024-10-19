/**
 * SisyutuKingakuItemHolderを生成するコンポーネントです。
 * 指定した年月で支出金額テーブル情報を検索し、データを支出金額テーブル情報を格納したホルダークラス(SisyutuKingakuItemHolder)に格納します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/14 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.component;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemHolder;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * SisyutuKingakuItemHolderを生成するコンポーネントです。
 * 指定した年月で支出金額テーブル情報を検索し、データを支出金額テーブル情報を格納したホルダークラス(SisyutuKingakuItemHolder)に格納します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class SisyutuKingakuItemHolderComponent {
	
	// 支出金額テーブル:SISYUTU_KINGAKU_TABLEポジトリー
	private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	
	/**
	 *<pre>
	 * 指定した検索条件をもとに、ホルダークラス(SisyutuKingakuItemHolder)を生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月)
	 * @return 支出金額テーブル情報を格納したホルダークラス(SisyutuKingakuItemHolder)
	 *
	 */
	public SisyutuKingakuItemHolder build(SearchQueryUserIdAndYearMonth search) {
		log.debug("road:search="+ search);
		
		// 指定された検索条件をもとに支出金額テーブルを検索
		SisyutuKingakuItemInquiryList searchResult = sisyutuKingakuTableRepository.findById(search);
		// 検索結果をもとに支出金額テーブル情報を格納したホルダークラス(SisyutuKingakuItemHolder)を生成して返却
		return SisyutuKingakuItemHolder.from(searchResult.getValues(), sisyutuItemComponent);
	}
}
