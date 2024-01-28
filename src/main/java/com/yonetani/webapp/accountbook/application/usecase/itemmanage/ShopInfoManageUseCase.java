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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
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
		List<CodeAndValuePair> shopGroupList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.SHOP_KUBUN_CODE);
		// 店舗グループをもとにレスポンスを生成
		if(shopGroupList == null) {
			ShopInfoManageResponse response = ShopInfoManageResponse.getInstance(null);
			response.addMessage("コード定義ファイルに「店舗グループ情報：" + MyHouseholdAccountBookContent.SHOP_KUBUN_CODE + "」が登録されていません。管理者に問い合わせてください");
			return response;
		}
		// 店舗グループの選択ボックスは入力先でデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
		ShopInfoManageResponse response = ShopInfoManageResponse.getInstance(shopGroupList.stream().map(pair ->
		OptionItem.from(pair.getCode().toString(), pair.getCodeValue().toString())).collect(Collectors.toList()));
		
		
		return response;
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
		return ShopInfoManageResponse.getInstance(null);
	}

}
