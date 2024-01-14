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
package com.yonetani.webapp.accountbook.application.usecase.baseinfo.manage;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.baseinfo.manage.ShopInfoManageResponse;

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
public class ShopInfoManageUseCase {

	public ShopInfoManageResponse readShopInfo(UserSession user) {
		log.debug("readShopInfo:userid=" + user.getUserId());
		// TODO 自動生成されたメソッド・スタブ
		return new ShopInfoManageResponse();
	}

	public ShopInfoManageResponse readShopInfo(UserSession user, String shopid) {
		log.debug("readShopInfo:userid=" + user.getUserId() + ",shopid=" + shopid);
		// TODO 自動生成されたメソッド・スタブ
		return new ShopInfoManageResponse();
	}

}
