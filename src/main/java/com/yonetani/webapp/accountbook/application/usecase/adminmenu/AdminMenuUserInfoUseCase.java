/**
 * 管理者メニュー ユーザ情報管理のユースケースです。
 * ・ユーザ情報管理画面表示情報取得
 * ・ユーザ情報追加・更新
 * ・【仮】各種パッチを充てる場合に実行
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.adminmenu;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfo;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfoItemList;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBaseList;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBaseList;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.AdminMenuUserInfoRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.ShopBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.SisyutuItemBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUserInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuUserInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 管理者メニュー ユーザ情報管理のユースケースです。
 * ・ユーザ情報管理画面表示情報取得
 * ・ユーザ情報追加・更新
 * ・【仮】各種パッチを充てる場合に実行
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AdminMenuUserInfoUseCase {
	
	// ユーザ情報取得リポジトリー
	private final AdminMenuUserInfoRepository adminUserInfoRepository;
	// 家計簿利用ユーザ情報参照・更新リポジトリー
	private final AccountBookUserRepository accountBookUserRepository;
	// 支出項目テーブルベースデータを登録/全件取得するリポジトリー
	private final SisyutuItemBaseTableRepository sisyutuItemBaseTableRepository;
	// 店舗テーブルベースデータを登録/全件取得するリポジトリー
	private final ShopBaseTableRepository shopBaseTableRepository;
	// 支出項目テーブルデータを登録するリポジトリー
	private final SisyutuItemTableRepository sisyutuItemTableRepository;
	// 店舗テーブルデータを登録するリポジトリー
	private final ShopTableRepository shopTableRepository;
	/**
	 *<pre>
	 * ユーザ情報管理画面の表示情報を取得します。
	 * ・有効なユーザのユーザ一覧を取得し、画面情報に設定します。
	 *</pre>
	 * @return ユーザ情報管理画面の表示情報(レスポンス)
	 *
	 */
	public AdminMenuUserInfoResponse read() {
		log.debug("read:");
		
		// ユーザ情報の一覧をレスポンスに設定し返却
		return applyUserInfoList(AdminMenuUserInfoResponse.getInstance());
	}

	/**
	 *<pre>
	 * ユーザ情報管理画面の表示情報を取得します。
	 * ・有効なユーザのユーザ一覧を取得し、画面情報に設定します。
	 * ・指定されたユーザIDの情報を取得し、入力フォームに値を設定します。
	 *</pre>
	 * @param userId 入力フォームに設定するユーザのユーザID
	 * @return　ユーザ情報管理画面の表示情報(レスポンス)
	 *
	 */
	public AdminMenuUserInfoResponse read(String userId) {
		log.debug("read: userId=" + userId);
		
		// 入力チェック:ユーザID未設定の場合エラー
		if(!StringUtils.hasLength(userId)) {
			throw new MyHouseholdAccountBookRuntimeException("予期しないエラーが発生しました。管理者に問い合わせてください。[userid:" + userId + "]");
		}
		// ユーザ情報の一覧をレスポンスに設定
		AdminMenuUserInfoResponse response = applyUserInfoList(AdminMenuUserInfoResponse.getInstance());
		
		// ユーザIDでユーザ情報を検索し、レスポンスに設定
		AdminMenuUserInfo userInfo = adminUserInfoRepository.getUserInfo(SearchQueryUserId.from(userId));
		if(userInfo.isEmpty()) {
			// 検索結果なしの場合
			throw new MyHouseholdAccountBookRuntimeException("選択したユーザIDに対応するユーザ情報がありません。管理者に問い合わせてください。[userid:"
					+ userId + "]");
		} else {
			// 取得したユーザ情報をもとにFormデータを作成し、レスポンスに設定
			AdminMenuUserInfoForm userInfoForm = new AdminMenuUserInfoForm();
			userInfoForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
			userInfoForm.setUserId(userInfo.getUserId().toString());
			userInfoForm.setUserName(userInfo.getUserName().toString());
			userInfoForm.setUserStatus(userInfo.getUserStatus().toValueString());
			userInfoForm.setUserRole(userInfo.getUserRole().toValueStringList());
			userInfoForm.setTargetYearMonth(userInfo.getTargetYearMonth().getTargetYearMonth());
			response.setAdminMenuUserInfoForm(userInfoForm);
		}
		
		return response;
	}
	
	/**
	 *<pre>
	 * ユーザ情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param userInfoForm ユーザ情報入力フォームの入力値
	 * @return ユーザ情報管理画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public AdminMenuUserInfoResponse execAction(AdminMenuUserInfoForm userInfoForm) {
		log.debug("execAction: action=" + userInfoForm.getAction() + ", userForm=" + userInfoForm);
		
		// レスポンスを生成
		AdminMenuUserInfoResponse response = AdminMenuUserInfoResponse.getInstance();
		
		try {
			// 対象年月ドメインタイプを生成
			TargetYearMonth domainTypeYearMonth = TargetYearMonth.from(userInfoForm.getTargetYearMonth());
			
			// userinfo
			AdminMenuUserInfo userInfo = AdminMenuUserInfo.from(
					userInfoForm.getUserId(),
					userInfoForm.getUserName(),
					userInfoForm.getUserStatus(),
					userInfoForm.getUserRole(),
					userInfoForm.getUserPassword(),
					domainTypeYearMonth.getTargetYearMonth());
			
			// 家計簿利用ユーザ情報
			AccountBookUser accountBookUser = AccountBookUser.from(
					userInfoForm.getUserId(),
					domainTypeYearMonth.getYear(),
					domainTypeYearMonth.getMonth(),
					userInfoForm.getUserName());
			
			// 新規登録の場合
			if(Objects.equals(userInfoForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
				// ユーザテーブル、ユーザ権限テーブルに新規ユーザを追加
				adminUserInfoRepository.addUserInfo(userInfo);
				// 家計簿利用ユーザテーブルに新規ユーザを追加
				int addUserCount = accountBookUserRepository.add(accountBookUser);
				// 追加件数が1件以上の場合、業務エラー
				if(addUserCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("家計簿利用ユーザテーブルへの追加件数が不正でした。[add data:" + accountBookUser + "]");
				}
				
				// 支出項目テーブル(BASE)から新規ユーザの支出項目テーブルを出力
				SisyutuItemBaseList sisyutuItemBaseList = sisyutuItemBaseTableRepository.findAll();
				
				sisyutuItemBaseList.getValues().forEach(baseData -> {
						// 登録する支出項目テーブル情報を生成(更新不可フラグはデフォルトで不可:falseを設定)
						SisyutuItem addData = SisyutuItem.from(
							accountBookUser.getUserId().toString(),
							baseData.getSisyutuItemCode().toString(),
							baseData.getSisyutuItemName().toString(), 
							baseData.getSisyutuItemDetailContext().toString(),
							baseData.getParentSisyutuItemCode().toString(),
							baseData.getSisyutuItemLevel().toString(),
							baseData.getSisyutuItemSort().toString(),
							false);
						// データを登録
						int addCount = sisyutuItemTableRepository.add(addData);
						// 追加件数が1件以上の場合、業務エラー
						if(addCount != 1) {
							throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの追加件数が不正でした。[add data:" + addData + "]");
						}
					});
				
				// 店舗テーブル(BASE)から新規ユーザの店舗テーブルを出力
				// 店舗区分コード、店舗表示順は店舗コードと同じ値で出力する
				ShopBaseList shopBaseList = shopBaseTableRepository.findAll();
				shopBaseList.getValues().forEach(baseData -> {
						// 登録する店舗テーブル情報を生成
						Shop addData = Shop.from(
								accountBookUser.getUserId().toString(),
								baseData.getShopCode().toString(),
								baseData.getShopCode().toString(),
								baseData.getShopName().toString(),
								baseData.getShopCode().toString());
						// データを登録
						int addCount = shopTableRepository.add(addData);
						// 追加件数が1件以上の場合、業務エラー
						if(addCount != 1) {
							throw new MyHouseholdAccountBookRuntimeException("店舗テーブルへの追加件数が不正でした。[add data:" + addData + "]");
						}
					});
				
				// 完了メッセージ
				response.addMessage("ユーザを追加しました。[ユーザID:" + userInfo.getUserId() + "][ユーザ名:" + userInfo.getUserName() + "]");
				
			// 更新の場合
			} else if (Objects.equals(userInfoForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
				// ユーザテーブル、ユーザ権限テーブルのユーザ情報を更新
				adminUserInfoRepository.updateUserInfo(userInfo);
				// 家計簿利用ユーザテーブルに新規ユーザを情報を更新
				int updateCount = accountBookUserRepository.update(accountBookUser);
				// 更新件数が1件以上の場合、業務エラー
				if(updateCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("家計簿利用ユーザテーブルへの追加件数が不正でした。[add data:" + accountBookUser + "]");
				}

				// 完了メッセージ
				response.addMessage("ユーザを更新しました。[ユーザID:" + userInfo.getUserId() + "][ユーザ名:" + userInfo.getUserName() + "]");
				
				
			} else {
				throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + userInfoForm.getAction());
			}
			
			// 処理結果OKを設定(getリダイレクトを行う)
			response.setTransactionSuccessFull();
			
		} catch (MyHouseholdAccountBookException ex) {
			throw new MyHouseholdAccountBookRuntimeException("業務エラーが発生しました。管理者に問い合わせてください。[message:" + ex.getLocalizedMessage() + "]", ex);
		}
		
		return response;
	}
	
	/**
	 *<pre>
	 * 【メソッドの説明を入力してください】
	 * ★パッチ充て用に急遽作った処理：後で、パッチ充て処理として本格対応する（家計簿ベース完了後）
	 *</pre>
	 * @return
	 *
	 */
	@Transactional
	public AdminMenuUserInfoResponse customInfo() {
		log.debug("customInfo: ");
		// レスポンスを生成
		AdminMenuUserInfoResponse response = AdminMenuUserInfoResponse.getInstance();
		// 支出項目テーブル(BASE)から新規ユーザの支出項目テーブルを出力
		SisyutuItemBaseList sisyutuItemBaseList = sisyutuItemBaseTableRepository.findAll();
		
		sisyutuItemBaseList.getValues().forEach(baseData -> {
				// 登録する支出項目テーブル情報を生成(更新不可フラグはデフォルトで不可:falseを設定)
				SisyutuItem addData = SisyutuItem.from(
					"koukiyonetani",
					baseData.getSisyutuItemCode().toString(),
					baseData.getSisyutuItemName().toString(), 
					baseData.getSisyutuItemDetailContext().toString(),
					baseData.getParentSisyutuItemCode().toString(),
					baseData.getSisyutuItemLevel().toString(),
					baseData.getSisyutuItemSort().toString(),
					false);
				// データを登録
				int addCount = sisyutuItemTableRepository.add(addData);
				// 追加件数が1件以上の場合、業務エラー
				if(addCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの追加件数が不正でした。[add data:" + addData + "]");
				}
			});
		// 完了メッセージ
		response.addMessage("パッチ当て処理が完了しました。");
		
		// 処理結果OKを設定(getリダイレクトを行う)
		response.setTransactionSuccessFull();
		return response;
	}
	/**
	 *<pre>
	 * ユーザ情報の一覧(ドメインモデル)を取得し、結果レスポンスに設定して返します。
	 *</pre>
	 * @param response レスポンス
	 * @return 処理結果を設定後のレスポンス
	 *
	 */
	private AdminMenuUserInfoResponse applyUserInfoList(AdminMenuUserInfoResponse response) {
		
		// ユーザ情報の一覧(ドメインモデル)を取得
		AdminMenuUserInfoItemList userInfoList = adminUserInfoRepository.getUserInfoList();
		log.debug("検索結果(ユーザ情報一覧のリスト)=" + userInfoList);
		
		// ドメインモデル→レスポンスに変換
		if(userInfoList.isEmpty()) {
			// ユーザ情報が0件の場合、メッセージを設定
			response.addMessage("家計簿ユーザが0件です。");
		} else {
			response.addUserInfoListItems(userInfoList.getValues().stream().map(domain ->
			AdminMenuUserInfoResponse.UserInfoListItem.from(
					domain.getUserId().toString(),
					domain.getUserName().toString(),
					domain.getUserStatus().toString(),
					domain.getUserRole().toViewStringList(),
					domain.getTargetYearMonth().toString()
			)).collect(Collectors.toUnmodifiableList()));
		}
		return response;
	}
}
