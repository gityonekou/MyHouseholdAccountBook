/**
 * お店情報管理ユースケースです。
 * ・情報管理(お店)画面の表示情報取得(初期表示)
 * ・情報管理(お店)画面の表示情報取得(対象選択時)
 * ・お店情報の追加・更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * お店情報管理ユースケースです。
 * ・情報管理(お店)画面の表示情報取得(初期表示)
 * ・情報管理(お店)画面の表示情報取得(対象選択時)
 * ・お店情報の追加・更新
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
	
	// 店舗情報取得リポジトリー
	private final ShopTableRepository shopRepository;
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
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「店舗グループ情報：" + MyHouseholdAccountBookContent.SHOP_KUBUN_CODE + "」が登録されていません。管理者に問い合わせてください");
		}
		// 店舗グループの選択ボックスは入力先でデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
		ShopInfoManageResponse response = ShopInfoManageResponse.getInstance(shopGroupList.stream().map(pair ->
		OptionItem.from(pair.getCode().toString(), pair.getCodeValue().toString())).collect(Collectors.toList()));
		
		// ログインユーザの店舗情報を取得
		ShopInquiryList shopSearchResult = shopRepository.findById(SearchQueryUserId.from(user.getUserId()));
		if(shopSearchResult.isEmpty()) {
			// 店舗情報が0件の場合、メッセージを設定
			response.addMessage("店舗情報取得結果が0件です。");
		} else {
			// 店舗情報をレスポンスに設定
			response.addShopList(shopSearchResult.getValues().stream().map(domain ->
				ShopInfoManageResponse.ShopListItem.from(
						domain.getShopCode().toString(),
						domain.getShopName().toString(),
						codeTableItem.getCodeValue(MyHouseholdAccountBookContent.SHOP_KUBUN_CODE, domain.getShopKubunCode().toString()),
						domain.getShopSort().toString())
			).collect(Collectors.toUnmodifiableList()));
		}
		
		return response;
	}

	/**
	 *<pre>
	 * 指定したユーザIDと店舗に応じた情報管理(お店)画面の表示情報を取得します。
	 *</pre>
	 * @param user　表示対象のユーザID
	 * @param shopCode　表示対象の店舗コード
	 * @return 情報管理(お店)画面の表示情報
	 *
	 */
	public ShopInfoManageResponse readShopInfo(UserSession user, String shopCode) {
		log.debug("readShopInfo:userid=" + user.getUserId() + ",shopCode=" + shopCode);
		
		// 店舗一覧情報を取得
		ShopInfoManageResponse response = readShopInfo(user);
		// 店舗IDに対応する店舗情報を取得
		Shop shop = shopRepository.findByIdAndShopCode(SearchQueryUserIdAndShopCode.from(user.getUserId(), shopCode));
		if(shop == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の店舗情報が存在しません。管理者に問い合わせてください。shopCode:" + shopCode);
		} else {
			// 店舗情報のformデータ
			ShopInfoForm form = new ShopInfoForm();
			// アクション
			form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
			// 店舗コード
			form.setShopCode(shop.getShopCode().toString());
			// 店舗区分
			form.setShopKubun(shop.getShopKubunCode().toString());
			// 店舗名
			form.setShopName(shop.getShopName().toString());
			// 表示順
			form.setShopSort(Integer.parseInt(shop.getShopSort().toString()));
			// 表示順(更新比較用)
			form.setShopSortBefore(shop.getShopSort().toString());
			
			response.setShopInfoForm(form);
		}
		return response;
	}

	/**
	 *<pre>
	 * 店舗情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param shopForm 店舗情報入力フォームの入力値
	 * @return 情報管理(お店)画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public ShopInfoManageResponse execAction(UserSession user, ShopInfoForm shopForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",shopForm=" + shopForm);
		ShopInfoManageResponse response = ShopInfoManageResponse.getInstance(null);
		
		// 現在の900番未満のデータ件数を取得
		int count = shopRepository.countByIdAndLessThanNineHundred(SearchQueryUserId.from(user.getUserId()));
		
		// 既存データの表示順更新データ
		List<Shop> sortValueUpdateList = new ArrayList<>();
		
		// 新規登録の場合
		if(shopForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			/* 新規店舗情報を追加 */
			// 新規店舗コード番号発番
			count++;
			// 登録済み店舗数が900件より多い場合、エラー
			if(count >= 900) {
				response.addErrorMessage("店舗は900件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 表示順が空、または900番未満の件数より大きい数値の場合、店舗コードの値=表示順の値とする
			if(shopForm.getShopSort() == null || shopForm.getShopSort() > count) {
				shopForm.setShopSort(count);
				
			// 表示順<店舗コードの値の場合、既存の表示順を1ずらしていく
			} else if(shopForm.getShopSort() < count){
				// 指定した表示順より大きい表示順の値の店舗情報を取得
				ShopInquiryList sortList = shopRepository.findByIdAndShopSort(SearchQueryUserIdAndShopSort.from(
						user.getUserId(), String.format("%03d", shopForm.getShopSort())));
				if(!sortList.isEmpty()) {
					sortList.getValues().forEach(data -> sortValueUpdateList.add(createChopData(data, 1)));
				}
			}
			
			// 新規の店舗コードを設定し、店舗情報入力フォームの値をドメインに変換
			Shop shop = Shop.from(
					user.getUserId(),
					String.format("%03d", count),
					shopForm.getShopKubun(),
					shopForm.getShopName(), 
					String.format("%03d", shopForm.getShopSort()));
			
			// 新規店舗情報を追加
			int addCount = shopRepository.add(shop);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("店舗テーブルへの追加件数が不正でした。[件数=" + addCount + "][add data:" + shop + "]");
			}
			
			// 完了メッセージ
			response.addMessage("新規店舗を追加しました。[code:" + shop.getShopCode() + "]" + shop.getShopName());
			
		// 更新の場合
		} else if (shopForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			// 旧表示順の値をチェック
			if(!StringUtils.hasLength(shopForm.getShopSortBefore())) {
				throw new MyHouseholdAccountBookRuntimeException("旧表示順の値が不正です。管理者に問い合わせてください。ShopSortBefore=" + shopForm.getShopSortBefore());
				
			}
			
			// 新しい表示順の値 > (900番より小さい)データ件数の場合、新しい表示順の値を(900番より小さい)データ件数に変更
			if(shopForm.getShopSort() > count) {
				shopForm.setShopSort(count);
			}
			// 表示順が更新されている場合、既存データの新しい表示順の値を設定
			String newShopSortValue = String.format("%03d", shopForm.getShopSort());
			if(!newShopSortValue.equals(shopForm.getShopSortBefore())) {
				log.debug(" 既存データの表示順調整ありshopSort Before:" + shopForm.getShopSortBefore() + ",New=" + newShopSortValue);
				
				/* 新旧の表示順を比較し、既存データの表示順を変更するデータを取得 */
				// 旧表示順＞新表示順の場合
				if(shopForm.getShopSortBefore().compareTo(newShopSortValue) > 0) {
					/* 旧表示順＞新表示順の場合、新表示順～旧表示順 -1間のデータを取得し、表示順を＋１する */
					// 検索条件(between a and b)のbの値を設定　＝　旧表示順 - 1の値
					int searchBIntVal = Integer.parseInt(shopForm.getShopSortBefore()) - 1;
					// 新表示順～旧表示順 -1間のデータを取得
					ShopInquiryList sortList = shopRepository.findByIdAndShopSortBetween(SearchQueryUserIdAndShopSortBetweenAB.from(
							user.getUserId(), newShopSortValue, String.format("%03d", searchBIntVal)));
					if(!sortList.isEmpty()) {
						// 既存データの表示順= 表示順 + 1
						sortList.getValues().forEach(updShopData -> sortValueUpdateList.add(createChopData(updShopData, 1)));
					}
				// 旧表示順＜新表示順の場合(等しい条件は一つ上のif判定にて除外済みなのでelse文でOK
				} else {
					/* 旧表示順＜新表示順の場合、旧表示順+1 ～新表示順間のデータを取得し、表示順を-１する */
					// 検索条件(between a and b)のAの値を設定　＝　旧表示順 + 1の値
					int searchAIntVal = Integer.parseInt(shopForm.getShopSortBefore()) + 1;
					// 旧表示順+1 ～新表示順間のデータを取得
					ShopInquiryList sortList = shopRepository.findByIdAndShopSortBetween(SearchQueryUserIdAndShopSortBetweenAB.from(
							user.getUserId(), String.format("%03d", searchAIntVal), newShopSortValue));
					if(!sortList.isEmpty()) {
						// 既存データの表示順= 表示順 - 1
						sortList.getValues().forEach(updShopData -> sortValueUpdateList.add(createChopData(updShopData, -1)));
					}
				}
				
			}
			// 店舗情報入力フォームの値をドメインに変換
			Shop shop = Shop.from(
					user.getUserId(),
					shopForm.getShopCode(),
					shopForm.getShopKubun(),
					shopForm.getShopName(), 
					newShopSortValue);
			int updateCount = shopRepository.update(shop);
			// 更新件数が1件以上の場合、業務エラー
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("店舗テーブルへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + shop + "]");
			}
			
			// 完了メッセージ
			response.addMessage("店舗を更新しました。[code:" + shop.getShopCode() + "]" + shop.getShopName());
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + shopForm.getAction());
			
		}
		
		// 既存データのソート順を調整
		sortValueUpdateList.forEach(updateSortData -> {
			// 表示順の値を更新
			int updateSortDataCount = shopRepository.updateShopSort(updateSortData);
			// 更新件数が1件以上の場合、業務エラー
			if(updateSortDataCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("店舗テーブルへの更新件数が不正でした。[add data:" + updateSortData + "]");
			}
		});
		
		// 処理結果OKを設定(getリダイレクトを行う)
		response.setTransactionSuccessFull();
		
		return response;
	}

	/**
	 *<pre>
	 * 指定の店舗情報のうち、表示順の値を指定した増減分加算・減算した値で店舗情報を生成して返します。
	 *</pre>
	 * @param data 生成対象の店舗情報
	 * @param add 表示順の増減分する値(1 or -1)
	 * @return 表示順を増減した値で新たに生成した店舗情報
	 *
	 */
	private Shop createChopData(Shop data, int add) {
		return Shop.from(
				data.getUserId().toString(),
				data.getShopCode().toString(),
				data.getShopKubunCode().toString(),
				data.getShopName().toString(),
				String.format("%03d", Integer.parseInt(data.getShopSort().toString()) + add)
				);
	}
}
