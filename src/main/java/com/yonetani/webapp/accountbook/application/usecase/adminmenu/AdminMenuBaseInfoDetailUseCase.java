/**
 * 管理者画面メニュー ベース情報詳細表示のユースケースです。
 * ・ベース情報詳細画面表示情報取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.adminmenu;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.ShopBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.SisyutuItemBaseTableRepository;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuBaseInfoDetailResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 管理者画面メニュー ベース情報詳細表示のユースケースです。
 * ・ベース情報詳細画面表示情報取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AdminMenuBaseInfoDetailUseCase {
	
	// 支出項目テーブルベースデータを登録/全件取得するリポジトリー
	private final SisyutuItemBaseTableRepository sisyutuItemBaseTableRepository;
	// 店舗テーブルベースデータを登録/全件取得するリポジトリー
	private final ShopBaseTableRepository shopBaseTableRepository;
	
	/**
	 *<pre>
	 * ベース情報詳細画面の表示情報を取得します。
	 *</pre>
	 * @param target 表示対象のテーブル
	 * @return ベース情報詳細画面の表示情報(レスポンス)
	 *
	 */
	public AdminMenuBaseInfoDetailResponse read(String target) {
		log.debug("read: target=" + target);
		AdminMenuBaseInfoDetailResponse response = AdminMenuBaseInfoDetailResponse.getInstance(target);
		// 指定のテーブル情報を取得
		if(target.equals(MyHouseholdAccountBookContent.SISYUTU_ITEM_BASE_TABLE)) {
			response.setTableItemsLine("支出項目コード, 支出項目名, 支出項目詳細内容, 親支出項目コード, 支出項目レベル, 支出項目表示順");
			response.addTableDataList(sisyutuItemBaseTableRepository.findAll().getValues().stream().map(
					domain -> domain.toLineString()).collect(Collectors.toUnmodifiableList())
			);
		} else if(target.equals(MyHouseholdAccountBookContent.SHOP_BASE_TABLE)) {
			response.setTableItemsLine("店舗コード, 店舗名");
			response.addTableDataList(shopBaseTableRepository.findAll().getValues().stream().map(
					domain -> domain.toLineString()).collect(Collectors.toUnmodifiableList())
			);
		} else {
			response.addMessage("予期しないエラーが発生しました。管理者に問い合わせてください。[targetの値が不正です][target=" + target + "]");
		}
		// 初期表示のレスポンスを返却
		return response;
	}
}
