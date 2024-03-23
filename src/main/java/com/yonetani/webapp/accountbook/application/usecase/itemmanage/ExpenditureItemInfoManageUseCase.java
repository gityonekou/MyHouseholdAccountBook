/**
 * 支出項目情報管理ユースケースです。
 * ・支出項目一覧情報取得
 * ・指定の支出項目情報取得
 * ・支出項目情報の更新
 * ・支出項目情報の追加
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

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemNameList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ExpenditureItemInfoForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報管理ユースケースです。
 * ・支出項目一覧情報取得
 * ・指定の支出項目情報取得
 * ・支出項目情報の更新
 * ・支出項目情報の追加
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
public class ExpenditureItemInfoManageUseCase {

	// 支出項目テーブル:SISYUTU_ITEM_TABLE参照リポジトリー
	private final SisyutuItemTableRepository sisyutuItemRepository;
	
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(支出項目) 対象選択画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(支出項目) 対象選択画面の表示情報
	 *
	 */
	public ExpenditureItemInfoManageInitResponse readInitInfo(UserSession user) {
		log.debug("readExpenditureItemInfo:userid=" + user.getUserId());
		// レスポンス生成
		ExpenditureItemInfoManageInitResponse response = ExpenditureItemInfoManageInitResponse.getInstance();
		
		// ログインユーザの支出項目一覧情報を取得しレスポンスに設定
		setSisyutuItemInquiryList(user, response);
		
		return response;
	}

	/**
	 *<pre>
	 * 指定したユーザIDと支出項目コードに応じた情報管理(支出項目) 処理選択画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param sisyutuItemCode 表示対象の支出項目コード
	 * @return 情報管理(支出項目) 処理選択画面の表示情報
	 *
	 */
	public ExpenditureItemInfoManageActSelectResponse readActSelectItemInfo(UserSession user, String sisyutuItemCode) {
		log.debug("readExpenditureItemInfo:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		// 支出項目コードの入力チェック
		if(!StringUtils.hasLength(sisyutuItemCode)) {
			// 支出項目コードの入力チェックNGの場合、init画面を再表示させるためnullを設定
			ExpenditureItemInfoManageActSelectResponse response = ExpenditureItemInfoManageActSelectResponse.getInstance(null);
			response.addErrorMessage("予期しないエラーが発生しました。管理者に問い合わせてください。[key=sisyutuItemCode] is empty");
			return response;
		}
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合、init画面を再表示させるためnullを設定
			ExpenditureItemInfoManageActSelectResponse response = ExpenditureItemInfoManageActSelectResponse.getInstance(null);
			response.addErrorMessage("更新対象の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			return response;
			
		} else {
			/* 所属する親の支出項目名を設定 */
			// 更新対象の支出項目情報の支出項目レベルが1以外の場合、親の親支出項目情報を設定
			List<String> errorMsgList = new ArrayList<String>();
			String parentSisyutuItemName = getParentSisyutuItemName(user, sisyutuItem, errorMsgList);
			
			/* 支出項目情報からレスポンスを生成 */
			ExpenditureItemInfoManageActSelectResponse response
				= ExpenditureItemInfoManageActSelectResponse.getInstance(
					ExpenditureItemInfoManageActSelectResponse.SelectExpenditureItemInfo.from(
						// 支出項目コード
						sisyutuItem.getSisyutuItemCode().toString(),
						// 支出項目名
						sisyutuItem.getSisyutuItemName().toString(),
						// 支出項目詳細内容
						sisyutuItem.getSisyutuItemDetailContext().toString(),
						// 親の支出項目名称(各、親を＞区切りで表した文字列を設定)
						parentSisyutuItemName,
						// 支出項目レベル(1～5)
						sisyutuItem.getSisyutuItemLevel().getValue(),
						// 更新可否フラグ
						sisyutuItem.getEnableUpdateFlg().getValue()));
			// エラーメッセージをレスポンスに設定(遷移先は選択画面)
			errorMsgList.forEach(message -> response.addErrorMessage(message));
			
			/* 親の支出項目に属する支出項目一覧を設定 */
			// 支出項目レベルが2以上の場合に親の支出項目に属する支出項目一覧を取得する
			if(sisyutuItem.getSisyutuItemLevel().getValue() >= 2) {
				// 親の支出項目に属する支出項目一覧をを取得
				SisyutuItemNameList sisyutuItemNameList = sisyutuItemRepository.searchParentSisyutuItemMemberNameList(
						SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItem.getParentSisyutuItemCode().toString()));
				// 親の支出項目に属する支出項目一覧をレスポンスに設定
				if(!sisyutuItemNameList.isEmpty()) {
					response.addParentSisyutuItemMemberNameList(sisyutuItemNameList.getValues().stream().map(
							domain -> domain.toString()).collect(Collectors.toUnmodifiableList()));
				}
			}
			
			return response;
		}
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと支出項目コードに応じた情報管理(支出項目) 更新画面の表示情報取得処理です。
	 * 選択した支出項目に属する新たな支出項目を追加するための情報を取得して画面返却情報に設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param sisyutuItemCode 新規追加する支出項目が属する支出項目コード(親の支出項目)
	 * @return 情報管理(支出項目) 更新画面の表示情報(支出項目新規追加時)
	 *
	 */
	public ExpenditureItemInfoManageUpdateResponse readAddExpenditureItemInfo(UserSession user, String sisyutuItemCode) {
		log.debug("readAddExpenditureItemInfo:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 支出項目コードチェックと支出項目一覧情報設定
		ExpenditureItemInfoManageUpdateResponse response = ExpenditureItemInfoManageUpdateResponse.getInstance();
		if(!execCheckAndSetSisyutuItemInquiryList(user, sisyutuItemCode, response)) {
			return response;
		}
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーを設定
			response.addErrorMessage("選択した親の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			return response;
			
		} else {
			// 新規登録する支出項目情報を仮作成
			SisyutuItem addSisyutuItem = SisyutuItem.from(
					// ユーザID
					user.getUserId(),
					// 支出項目コード
					"新規発番",
					// 支出項目名
					null,
					// 支出項目詳細内容
					null,
					// 親支出項目コード
					sisyutuItem.getSisyutuItemCode().toString(),
					// 支出項目レベル(1～5)
					String.valueOf(sisyutuItem.getSisyutuItemLevel().getValue() + 1),
					// 支出項目表示順
					sisyutuItem.getSisyutuItemSort().toString(),
					// 更新可否フラグ
					true);
			
			// 作成した新規追加用フォームデータをレスポンスに設定
			response.setExpenditureItemInfoForm(
					createExpenditureItemInfoForm(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, addSisyutuItem));
			
			// 親の支出項目名を設定
			List<String> errorMsgList = new ArrayList<String>();
			response.setParentSisyutuItemName(getParentSisyutuItemName(user, addSisyutuItem, errorMsgList));
			// エラーメッセージをレスポンスに設定
			errorMsgList.forEach(message -> response.addErrorMessage(message));
			
			return response;
		}
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと支出項目コードに応じた情報管理(支出項目) 更新画面の表示情報取得処理です。
	 * 選択した支出項目を更新するための情報を取得して画面返却情報に設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param sisyutuItemCode 更新対象の支出項目コード
	 * @return 情報管理(支出項目) 更新画面の表示情報(支出項目更新時)
	 *
	 */
	public ExpenditureItemInfoManageUpdateResponse readUpdateExpenditureItemInfo(UserSession user, String sisyutuItemCode) {
		log.debug("readUpdateExpenditureItemInfo:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 支出項目コードチェックと支出項目一覧情報設定
		ExpenditureItemInfoManageUpdateResponse response = ExpenditureItemInfoManageUpdateResponse.getInstance();
		if(!execCheckAndSetSisyutuItemInquiryList(user, sisyutuItemCode, response)) {
			return response;
		}
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーを設定
			response.addErrorMessage("更新対象の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			return response;
			
		} else {
			// 作成した更新用フォームデータをレスポンスに設定
			response.setExpenditureItemInfoForm(
					createExpenditureItemInfoForm(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, sisyutuItem));
			
			// 親の支出項目名を設定
			List<String> errorMsgList = new ArrayList<String>();
			response.setParentSisyutuItemName(getParentSisyutuItemName(user, sisyutuItem, errorMsgList));
			// エラーメッセージをレスポンスに設定
			errorMsgList.forEach(message -> response.addErrorMessage(message));
			
			return response;
		}
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目) 更新画面の初期表示情報取得処理です。
	 * 支出項目一覧情報をレスポンスに設定して返します。
	 * 主に、バリデーションチェック結果でNGの場合に画面表示する支出項目一覧情報を設定するために呼び出します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(支出項目) 更新画面の表示情報
	 *
	 */
	public ExpenditureItemInfoManageUpdateResponse readUpdateInitInfo(UserSession user) {
		log.debug("readUpdateInitInfo:userid=" + user.getUserId());
		// ログインユーザの支出項目一覧情報を取得しレスポンスに設定
		ExpenditureItemInfoManageUpdateResponse response = ExpenditureItemInfoManageUpdateResponse.getInstance();
		setSisyutuItemInquiryList(user, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 支出項目入力フォームの入力値に従い、アクション(登録 or 更新)を実行します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param inputForm 支出項目入力フォームの入力値
	 * @return 情報管理(支出項目) 更新画面の表示情報(レスポンス)
	 *
	 */
	@Transactional
	public ExpenditureItemInfoManageUpdateResponse execAction(UserSession user, ExpenditureItemInfoForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンス
		ExpenditureItemInfoManageUpdateResponse response = ExpenditureItemInfoManageUpdateResponse.getInstance();
		
		// 新規登録の場合
		if(inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			/* 支出項目コードを自動採番して設定 */
			// 現在登録されている支出項目数を取得
			int count = sisyutuItemRepository.countById(SearchQueryUserId.from(user.getUserId()));
			// 支出項目コード番号発番
			count++;
			// 登録済み支出項目数が2000件より多い場合、エラー
			if(count > 2000) {
				response.addErrorMessage("支出項目は2000件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// +1した値を4桁で0パディングして支出項目コードに設定
			inputForm.setSisyutuItemCode(String.format("%04d", count));
			
			/* 支出項目表示順を設定 */
			//inputForm.setSisyutuItemSort("");
			
			/* 支出項目情報を新規追加する */ 
			// 新規追加する支出項目情報(ドメイン)を生成
			SisyutuItem addData = createSisyutuItem(user.getUserId().toString(), inputForm);
			// データを登録
			int addCount = sisyutuItemRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
		// 更新の場合
		} else if (inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
			// 更新する支出項目情報(ドメイン)を生成
			SisyutuItem updateData = createSisyutuItem(user.getUserId().toString(), inputForm);
			// データを登録
			int updateCount = 0;
			// 更新フラグの値により更新情報を変更
			if(inputForm.isEnableUpdateFlg()) {
				// 更新フラグ=trueの場合、すべてのデータを更新
				updateCount = sisyutuItemRepository.update(updateData);
			} else {
				// 更新フラグ=falseの場合、支出項目詳細内容を更新
				updateCount = sisyutuItemRepository.updateSisyutuItemDetailContext(updateData);
			}
			// 追加件数が1件以上の場合、業務エラー
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updateData + "]");
			}
			
		// 新規登録・更新アクション以外の場合
		} else {
			response.addErrorMessage("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
			return response;
			
		}
		
		// 処理結果OKを設定(getリダイレクトを行う)
		response.setTransactionSuccessFull();
		
		
		return response;
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目) 更新画面初期表示時の共通処理です。
	 * 支出項目コードの入力チェックとログインユーザの支出項目一覧情報取得を実行して結果を返します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報
	 * @param sisyutuItemCode 更新対象の支出項目コード
	 * @param response 情報管理(支出項目) 更新画面の表示情報(支出項目更新時)レスポンス
	 * @return チェック結果(OKの場合、true、NGの場合、false
	 *
	 */
	private boolean execCheckAndSetSisyutuItemInquiryList(UserSession user, String sisyutuItemCode, AbstractExpenditureItemInfoManageResponse response) {
		// 支出項目コードの入力チェック
		if(!StringUtils.hasLength(sisyutuItemCode)) {
			// 支出項目コードの入力チェックNGの場合、init画面を再表示させるためnullを設定
			response.addErrorMessage("予期しないエラーが発生しました。管理者に問い合わせてください。[key=sisyutuItemCode] is empty");
			return false;
		}
		
		// ログインユーザの支出項目一覧情報を取得しレスポンスに設定
		setSisyutuItemInquiryList(user, response);
		if(response.hasMessages()) {
			// 更新情報登録画面表示時、支出項目一覧は必ず存在するのでメッセージが設定されている場合はエラー
			response.addErrorMessage("予期しないエラーが発生しました。管理者に問い合わせてください。[支出項目一覧] is empty");
			return false;
		}
		// チェック結果OK
		return true;
	}
	
	/**
	 *<pre>
	 * ログインユーザの支出項目一覧情報を取得しレスポンスに設定します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報
	 * @param response 支出項目一覧を設定するレスポンス
	 *
	 */
	private void setSisyutuItemInquiryList(UserSession user, AbstractExpenditureItemInfoManageResponse response) {
		// ログインユーザの支出項目一覧情報を取得
		SisyutuItemInquiryList sisyutuItemSearchResult = sisyutuItemRepository.findById(SearchQueryUserId.from(user.getUserId()));
		if(sisyutuItemSearchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(SisyutuItem)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(sisyutuItemSearchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getSisyutuItemCode().toString(),
					domain.getSisyutuItemName().toString(),
					domain.getSisyutuItemDetailContext().toString(),
					domain.getParentSisyutuItemCode().toString(),
					domain.getSisyutuItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 *<pre>
	 * 親の支出項目の名称を＞区切りで連結した値で返します。
	 *</pre>
	 * @param user  支出項目一覧を取得するユーザ情報
	 * @param sisyutuItem  取得対象の支出項目情報
	 * @param errorMsgList エラーメッセージの格納先
	 * @return 親の支出項目の名称を＞区切りで連結した値
	 *
	 */
	private String getParentSisyutuItemName(UserSession user, SisyutuItem sisyutuItem, List<String> errorMsgList) {
		
		// 入力支出項目情報チェック(支出項目がレベル1の場合は、親が存在しないので空文字列を返す
		if(sisyutuItem.getSisyutuItemLevel().getValue() <= 1) {
			return "";
		}
		
		List<String> parentSisyutuItemNameList = new ArrayList<String>();
		// 支出項目コード
		String sisyutuItemCode = sisyutuItem.getSisyutuItemCode().toString();
		String parentSisyutuItemCode = sisyutuItem.getParentSisyutuItemCode().toString();
		// 親の支出項目レベル
		int parentSisyutuItemLevel = 0;
		
		// 項目レベル1の支出項目を取得するまで支出項目情報を取得を繰り返す。
		// また、DBデータ不正による無限ループになることを避けるため、データ格納件数が6件になった時点で
		// 繰り返しを終了する
		do {
			// 親支出項目コードに対応する支出項目情報を取得
			SisyutuItem parentSisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
					SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), parentSisyutuItemCode));
			if(parentSisyutuItem == null) {
				errorMsgList.add("更新対象の支出項目情報が属する親の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" 
						+ sisyutuItemCode + ", parentSisyutuItemCode:" + parentSisyutuItemCode);
				break;
			} else {
				parentSisyutuItemNameList.add(parentSisyutuItem.getSisyutuItemName().toString());
			}
			// 取得対象の親の支出項目コードを再設定
			parentSisyutuItemCode = parentSisyutuItem.getParentSisyutuItemCode().toString();
			// 親の支出項目レベルを設定
			parentSisyutuItemLevel = parentSisyutuItem.getSisyutuItemLevel().getValue();
			
		} while(parentSisyutuItemLevel > 1 && parentSisyutuItemNameList.size() < 6);
		
		// 親の項目名は逆順で取得されるため、順序を入れ替える
		List<String> wkList = new ArrayList<String>();
		for(int i = parentSisyutuItemNameList.size() - 1; i >= 0; i--) {
			wkList.add(parentSisyutuItemNameList.get(i));
		}
		
		// 予期しないエラー(いるかどうかは不明だけど、DBに不正データが格納される)
		if(parentSisyutuItemNameList.size() > 5) {
			errorMsgList.add("予期しないエラー(DBデータ不正による繰り返し不正。管理者に問い合わせてください。sisyutuItemCode:" 
					+ sisyutuItemCode + ", parentSisyutuItemCode:" + parentSisyutuItemCode);
		}
		
		// 親の支出項目名を＞区切りで設定
		return String.join("＞", wkList);
	}
	
	/**
	 *<pre>
	 * 引数の支出項目情報(ドメイン)からフォームデータを生成して返します。
	 *</pre>
	 * @param action 追加 or 更新
	 * @param sisyutuItem 支出項目情報(ドメイン)
	 * @return 支出項目情報フォームデータ
	 *
	 */
	private ExpenditureItemInfoForm createExpenditureItemInfoForm(String action, SisyutuItem sisyutuItem) {
		
		// 支出項目情報フォームデータ
		ExpenditureItemInfoForm form = new ExpenditureItemInfoForm();
		// アクション(追加/更新)
		form.setAction(action);
		// 支出項目コード
		form.setSisyutuItemCode(sisyutuItem.getSisyutuItemCode().toString());
		// 支出項目名
		form.setSisyutuItemName(sisyutuItem.getSisyutuItemName().toString());
		//　支出項目詳細内容
		form.setSisyutuItemDetailContext(sisyutuItem.getSisyutuItemDetailContext().toString());
		// 親の支出項目コード
		form.setParentSisyutuItemCode(sisyutuItem.getParentSisyutuItemCode().toString());
		// 支出項目レベル
		form.setSisyutuItemLevel(sisyutuItem.getSisyutuItemLevel().toString());
		// 支出項目表示順
		form.setSisyutuItemSort(sisyutuItem.getSisyutuItemSort().toString());
		// 更新可否フラグ
		form.setEnableUpdateFlg(sisyutuItem.getEnableUpdateFlg().getValue());
		
		return form;
	}
	
	/**
	 *<pre>
	 * 引数のフォームデータから支出項目情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID 
	 * @param inputForm フォームデータ
	 * @return 支出項目情報(ドメイン)
	 *
	 */
	private SisyutuItem createSisyutuItem(String userId, ExpenditureItemInfoForm inputForm) {
		return SisyutuItem.from(
				// ユーザID
				userId,
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// 支出項目名
				inputForm.getSisyutuItemName(),
				// 支出項目詳細内容
				inputForm.getSisyutuItemDetailContext(),
				// 親の支出項目コード
				inputForm.getParentSisyutuItemCode(),
				// 支出項目レベル
				inputForm.getSisyutuItemLevel(),
				// 支出項目表示順
				inputForm.getSisyutuItemSort(),
				// 更新可否フラグ
				inputForm.isEnableUpdateFlg());
	}
}
