/**
 * お店情報管理ユースケースです。
 * ・お店一覧情報取得
 * ・指定のお店情報取得
 * ・お店情報の更新
 * ・お店情報の追加
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * お店情報管理ユースケースです。
 * ・お店一覧情報取得
 * ・指定のお店情報取得
 * ・お店情報の更新
 * ・お店情報の追加
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
public class ShopInfoManageUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(お店)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(お店)画面の表示情報
	 *
	 */
	public ShopInfoManageResponse readShopInfo(UserSession user) {
		log.debug("readShopInfo:userid=" + user.getUserId());
		
		// 店舗のコードテーブル情報を取得し、リストに設定
		codeTableItem.getCodeValues(MyHouseholdAccountBookContent.SHOP_KUBUN_CODE);
		
		// TODO 自動生成されたメソッド・スタブ
		return new ShopInfoManageResponse();
	}

	/**
	 *<pre>
	 * 指定したユーザIDと店舗に応じた情報管理(お店)画面の表示情報を取得します。
	 *</pre>
	 * @param user
	 * @param shopid
	 * @return
	 *
	 */
	public ShopInfoManageResponse readShopInfo(UserSession user, String shopid) {
		log.debug("readShopInfo:userid=" + user.getUserId() + ",shopid=" + shopid);
		// TODO 自動生成されたメソッド・スタブ
		return new ShopInfoManageResponse();
	}

}
