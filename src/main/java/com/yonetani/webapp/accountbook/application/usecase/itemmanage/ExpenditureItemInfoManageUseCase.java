/**
 * 支出項目情報管理ユースケースです。
 * ・情報管理(支出項目) 対象選択画面の表示情報取得
 * ・情報管理(支出項目) 処理選択画面の表示情報取得
 * ・情報管理(支出項目) 更新画面の表示情報取得(追加時)
 * ・情報管理(支出項目) 更新画面の表示情報取得(更新時)
 * ・情報管理(支出項目) 更新画面の表示情報取得(バリデーションチェックNG時)
 * ・支出項目情報の追加・更新
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ExpenditureItemInfoForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報管理ユースケースです。
 * ・情報管理(支出項目) 対象選択画面の表示情報取得
 * ・情報管理(支出項目) 処理選択画面の表示情報取得
 * ・情報管理(支出項目) 更新画面の表示情報取得(追加時)
 * ・情報管理(支出項目) 更新画面の表示情報取得(更新時)
 * ・情報管理(支出項目) 更新画面の表示情報取得(バリデーションチェックNG時)
 * ・支出項目情報の追加・更新
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
		SisyutuItem sisyutuItem = sisyutuItemRepository.findById(
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
				// 親の支出項目に属する支出項目一覧を取得
				SisyutuItemInquiryList sisyutuItemNameList = sisyutuItemRepository.searchParentMemberSisyutuItemList(
						SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItem.getParentSisyutuItemCode().toString()));
				// 親の支出項目に属する支出項目情報から名称を取得し、名称一覧をレスポンスに設定
				if(!sisyutuItemNameList.isEmpty()) {
					response.addParentSisyutuItemMemberNameList(sisyutuItemNameList.getValues().stream().map(
							domain -> domain.getSisyutuItemName().toString()).collect(Collectors.toUnmodifiableList()));
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
		
		// 支出項目コードに対応する支出項目情報(親となる支出項目情報)を取得
		SisyutuItem parentSisyutuItem = sisyutuItemRepository.findById(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(parentSisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーを設定
			response.addErrorMessage("選択した親の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			return response;
			
		} else {
			// 新規登録する支出項目情報を仮作成
			SisyutuItem addSisyutuItem = SisyutuItem.from(
					// ユーザID
					user.getUserId(),
					// 支出項目コード:新規発番仮コード(9999)
					MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_TEMPORARY_VALUE,
					// 支出項目名
					null,
					// 支出項目詳細内容
					null,
					// 親支出項目コード:親の支出項目コードを設定
					parentSisyutuItem.getSisyutuItemCode().toString(),
					// 支出項目レベル(1～5):親の支出項目レベル+1を設定
					String.valueOf(parentSisyutuItem.getSisyutuItemLevel().getValue() + 1),
					// 支出項目表示順:親の支出項目表示順を設定
					parentSisyutuItem.getSisyutuItemSort().toString(),
					// 更新可否フラグ
					true);
			
			// 作成した新規追加用フォームデータをレスポンスに設定
			response.setExpenditureItemInfoForm(
					createExpenditureItemInfoForm(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, addSisyutuItem));
			
			// 親の支出項目名と表示順選択リストをレスポンスに設定
			setParentMembers(user, addSisyutuItem, response);
			
			// 表示順選択リストを取得
			List<OptionItem> optionList = getParentMembersItemList(user, addSisyutuItem);
			// 親に属する支出項目の件数が50件より多い場合、エラー
			if(optionList.size() > 50) {
				response.addErrorMessage("親に属する支出項目を50件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 表示順選択リストに新規項目を追加
			addNewParentMembersItem(optionList, addSisyutuItem);
			
			// 表示順選択リストをレスポンスに設定
			response.setParentMembersItemList(optionList);
			
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
		SisyutuItem sisyutuItem = sisyutuItemRepository.findById(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーを設定
			response.addErrorMessage("更新対象の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			return response;
			
		} else {
			// 作成した更新用フォームデータをレスポンスに設定
			response.setExpenditureItemInfoForm(
					createExpenditureItemInfoForm(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, sisyutuItem));
			
			// 親の支出項目名と表示順選択リストをレスポンスに設定
			setParentMembers(user, sisyutuItem, response);
			
			// レスポンスに表示順選択リストを設定
			response.setParentMembersItemList(getParentMembersItemList(user, sisyutuItem));
			
			return response;
		}
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目) 更新画面で更新実行時にバリデーションチェックNGとなった場合の各画面表示項目を取得します。
	 * バリデーションチェック結果でNGの場合に呼び出してください。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm 支出項目入力フォームの入力値
	 * @return 情報管理(支出項目) 更新画面の表示情報
	 *
	 */
	public ExpenditureItemInfoManageUpdateResponse readUpdateBindingErrorSetInfo(UserSession user, ExpenditureItemInfoForm inputForm) {
		log.debug("readUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// ログインユーザの支出項目一覧情報を取得しレスポンスに設定
		ExpenditureItemInfoManageUpdateResponse response = ExpenditureItemInfoManageUpdateResponse.getInstance();
		setSisyutuItemInquiryList(user, response);
		
		// フォームデータから支出項目情報を作成
		SisyutuItem sisyutuItem = createSisyutuItem(user.getUserId().toString(), inputForm);
		
		// 親の支出項目名をレスポンスに設定
		setParentMembers(user, sisyutuItem, response);
		
		// 表示順選択リストを取得
		List<OptionItem> optionList = getParentMembersItemList(user, sisyutuItem);
		// 新規追加の場合の表示順選択リストを設定
		if(inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			// 新規登録の場合、新規の表示順項目を追加
			addNewParentMembersItem(optionList, sisyutuItem);
			
		// 新規登録・更新アクション以外の場合
		} else if (!inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			response.addErrorMessage("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
			return response;
			
		}
		// 表示順選択リストをレスポンスに設定
		response.setParentMembersItemList(optionList);
		
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
		
		// 親の支出項目に属する支出項目一覧を取得
		SisyutuItemInquiryList parentMemberList = sisyutuItemRepository.searchParentMemberSisyutuItemList(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), inputForm.getParentSisyutuItemCode().toString()));
		
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
			
			/* 支出項目情報を新規追加する */ 
			// 新規追加する支出項目情報(ドメイン)を生成
			SisyutuItem addData = createSisyutuItem(user.getUserId().toString(), inputForm);
			// データを登録
			int addCount = sisyutuItemRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// 表示順変更対象のデータを生成
			if(!parentMemberList.isEmpty()) {
				// 対象のデータ抽出式(Predicateの合成述語を使えばもっときれいにかける。ここでは、割愛するか)
				Predicate<SisyutuItem> func = domain -> {
					// その他項目(99)は対象外
					if(isCheckOtherItem(domain.getSisyutuItemSort().toString(), domain.getSisyutuItemLevel().getValue())) {
						return false;
					}
					// 親に属する支出項目の表示順＞＝新規追加する項目の表示順の場合、表示順変更対象のデータに追加
					if(domain.getSisyutuItemSort().toString().compareTo(inputForm.getSisyutuItemSort()) >= 0) {
						return true;
					}
					// 上記以外はすべて対象外
					return false;
				};
				// 表示順更新対象の支出項目情報をすべて更新
				updateAllSisyutuItemSort(parentMemberList, func, +1);	
				
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
			
			// 表示順を更新可能、かつ、親に属する子のリストが存在する(更新の場合は必ず存在する)場合、必要があれば支出項目表示順の値を更新する
			if(inputForm.isEnableUpdateFlg() && !parentMemberList.isEmpty()) {
				// 前の支出項目表示順の値
				String sortBeforeValue = inputForm.getSisyutuItemSort();
				// 親に属する子のリスト設定数分繰り返す
				for(SisyutuItem item : parentMemberList.getValues()) {
					// DBの支出項目コードと入力フォームの支出項目コードが等しい場合、かつ、支出項目表示順の値の変更されている場合
					if(item.getSisyutuItemCode().toString().equals(inputForm.getSisyutuItemCode())) {
						if(!item.getSisyutuItemSort().toString().equals(inputForm.getSisyutuItemSort())) {
							// 前の支出項目表示順の値を設定
							sortBeforeValue = item.getSisyutuItemSort().toString();
							
							/* 変更後の支出項目に属する子のソート順を変更する */
							// 親の支出項目に属する支出項目一覧を取得
							SisyutuItemInquiryList upateItemParentMemberList = sisyutuItemRepository.searchParentMemberSisyutuItemList(
									SearchQueryUserIdAndSisyutuItemCode.from(updateData.getUserId().toString(), updateData.getSisyutuItemCode().toString()));
							// 親の支出項目に属する支出項目情報から名称を取得し、名称一覧をレスポンスに設定
							if(!upateItemParentMemberList.isEmpty()) {
								
								// 更新する支出項目の支出項目レベルの値
								int sisyutuItemLevel = updateData.getSisyutuItemLevel().getValue();
								// 支出項目表示順から指定の支出項目レベルの位置の値を取得した値
								String sortItemValue = getSortValueFromLevel(updateData.getSisyutuItemSort().toString(), updateData.getSisyutuItemLevel().getValue());
								
								// 指定した支出項目レベルの位置の表示順の値を更新する支出項目情報の表示順の値と同じ値に更新して新たな支出項目更新データを生成
								List<SisyutuItem> updateValueList = upateItemParentMemberList.getValues().stream().map(domain -> {
									// 新規登録する支出項目の表示順の値を作成
									String updSortVal = replaceSisyutuItemSort(domain.getSisyutuItemSort().toString(), sisyutuItemLevel, sortItemValue);
									// 表示順を更新した支出項目情報を設定
									return createSisyutuItemFromSisyutuItemSort(domain, updSortVal);
								}).toList();
								
								// 表示順更新対象の支出項目情報を更新
								updateValueList.forEach(parentUpdateItem -> updateSisyutuItemSort(
									// 表示順更新対象の支出項目情報
									parentUpdateItem,
									// 表示順更新対象の支出項目レベル
									sisyutuItemLevel,
									// 支出項目表示順から指定の支出項目レベルの位置の値を取得した値
									sortItemValue));
							}
						}
						
						break;
					}
				}
				/* 表示順を変更している場合、親に属する支出項目の表示順の値を更新 */
				// ラムダ式内で参照するため、変更前表示順の値を固定値とする
				final String unmodifiableSortBeforeValue = sortBeforeValue;
				// 変更前の値が小さい⇒変更後のほうが大きい場合
				int sortCompareToValue = sortBeforeValue.compareTo(inputForm.getSisyutuItemSort());
				if(sortCompareToValue < 0) {
					
					// 変更前の表示順の一つ後の項目から変更後の値の項目まで表示順を-1する
					// 対象のデータ抽出式
					Predicate<SisyutuItem> func = domain -> {
						// 変更前の表示順の次の表示順の項目から変更後の表示順の項目までが表示順変更対象のデータ
						if(domain.getSisyutuItemSort().toString().compareTo(unmodifiableSortBeforeValue) > 0
								&& domain.getSisyutuItemSort().toString().compareTo(inputForm.getSisyutuItemSort()) <= 0) {
							return true;
						}
						// 上記以外はすべて対象外
						return false;
					};
					updateAllSisyutuItemSort(parentMemberList, func, -1);		
						
				// 変更前の値が大きい⇒変更後の値が小さい場合
				} else if (sortCompareToValue > 0){
					// 変更後の表示順の値の項目から変更前の表示順の一つ前の項目までソート順を+1する
					// 対象のデータ抽出式
					Predicate<SisyutuItem> func = domain -> {
						// 変更後の表示順の値の項目から変更前の表示順の一つ前の項目までが表示順変更対象のデータ
						if(domain.getSisyutuItemSort().toString().compareTo(inputForm.getSisyutuItemSort()) >= 0
								&& domain.getSisyutuItemSort().toString().compareTo(unmodifiableSortBeforeValue) < 0) {
							return true;
						}
						// 上記以外はすべて対象外
						return false;
					};
					updateAllSisyutuItemSort(parentMemberList, func, +1);
				}
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
	 * 親の支出項目名をレスポンスに設定します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報
	 * @param sisyutuItem 取得対象の支出項目情報
	 * @param response 情報管理(支出項目) 更新画面の表示情報(レスポンス)
	 *
	 */
	private void setParentMembers(UserSession user, SisyutuItem sisyutuItem,
			ExpenditureItemInfoManageUpdateResponse response) {
		
		/* 親の支出項目名を設定 */
		List<String> errorMsgList = new ArrayList<String>();
		response.setParentSisyutuItemName(getParentSisyutuItemName(user, sisyutuItem, errorMsgList));
		
		// エラーメッセージをレスポンスに設定
		errorMsgList.forEach(message -> response.addErrorMessage(message));
	}
	
	/**
	 *<pre>
	 * 表示順選択リストを返します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報
	 * @param sisyutuItem 取得対象の支出項目情報
	 * @return 表示順選択リスト(可変配列)
	 *
	 */
	private List<OptionItem> getParentMembersItemList(UserSession user, SisyutuItem sisyutuItem) {
		/* 親に属する子のリストを作成し、表示順選択リストに設定 */
		// 支出項目レベルが2以上の場合に親の支出項目に属する支出項目一覧を取得する
		List<OptionItem> optionList = new ArrayList<>();
		if(sisyutuItem.getSisyutuItemLevel().getValue() >= 2) {
			// 親の支出項目に属する支出項目一覧を取得
			SisyutuItemInquiryList sisyutuItemNameList = sisyutuItemRepository.searchParentMemberSisyutuItemList(
					SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItem.getParentSisyutuItemCode().toString()));
			// 親の支出項目に属する支出項目一覧から表示順リストを作成
			if(!sisyutuItemNameList.isEmpty()) {
				// 新規追加の場合、最後に新規追加用の項目を追加するので変更不可のリストではなく変更可のリストを作成する
				sisyutuItemNameList.getValues().forEach(domain -> {
					
					// その他項目(99)の場合、選択不可項目として表示順リスト項目に追加
					if(isCheckOtherItem(domain.getSisyutuItemSort().toString(), domain.getSisyutuItemLevel().getValue())) {
						optionList.add(OptionItem.from(
								// 選択時の値:表示順
								domain.getSisyutuItemSort().toString(),
								// 選択ボックスの表示値
								domain.getSisyutuItemName().toString(),
								// 選択不可:disabled
								true));
					
					// その他項目以外の場合、移動可能項目として表示順リスト項目に追加
					} else {
						optionList.add(OptionItem.from(
							// 選択時の値:表示順
							domain.getSisyutuItemSort().toString(),
							// 選択ボックスの表示値:「支出項目名」と入れ替え"
							"「" + domain.getSisyutuItemName().toString() + "」の位置に移動"));
					}
				});
			}
		// 支出項目レベルが1の場合は自分自身の支出項目名と表示順を設定
		} else {
			optionList.add(OptionItem.from(sisyutuItem.getSisyutuItemSort().toString(), sisyutuItem.getSisyutuItemName().toString()));
		}
		
		return optionList;
		
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
			SisyutuItem parentSisyutuItem = sisyutuItemRepository.findById(
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
	
	/**
	 *<pre>
	 * 引数の支出項目情報(ドメイン)を引数の表示順の値で更新した新しい支出項目情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param sisyutuItem 元の支出項目情報
	 * @param sisyutuItemSort 新しい表示順の値
	 * @return 支出項目情報(ドメイン)
	 *
	 */
	private SisyutuItem createSisyutuItemFromSisyutuItemSort(SisyutuItem sisyutuItem, String sisyutuItemSort) {
		return SisyutuItem.from(
				// ユーザID
				sisyutuItem.getUserId().toString(),
				// 支出項目コード
				sisyutuItem.getSisyutuItemCode().toString(),
				// 支出項目名
				sisyutuItem.getSisyutuItemName().toString(),
				// 支出項目詳細内容
				sisyutuItem.getSisyutuItemDetailContext().toString(),
				// 親支出項目コード
				sisyutuItem.getParentSisyutuItemCode().toString(),
				// 支出項目レベル(1～5)
				sisyutuItem.getSisyutuItemLevel().toString(),
				// 支出項目表示順
				sisyutuItemSort,
				// 更新可否フラグ
				sisyutuItem.getEnableUpdateFlg().getValue());
	}
	
	/**
	 *<pre>
	 * 支出項目表示順の値を支出項目レベルに該当する位置の文字列を指定の値で変換します。
	 * 
	 * 「StringBuilder#replaceメソッドstartからend -1 までの文字列を指定の値に置換」
	 * レベル：start - end：置換位置
	 * 1：0-2：**00000000
	 * 2：2-4：00**000000
	 * 3：4-6：0000**0000
	 * 4：6-8：000000**00
	 * 5：8-10：00000000**
	 *</pre>
	 * @param sisyutuItemSort 支出項目表示順
	 * @param sisyutuItemLevel 支出項目レベル
	 * @param replaceStr 置換文字列
	 * @return 置換後の支出項目表示順
	 *
	 */
	private String replaceSisyutuItemSort(String sisyutuItemSort, int sisyutuItemLevel, String replaceStr) {
		StringBuilder wk = new StringBuilder(sisyutuItemSort);
		int end = sisyutuItemLevel * 2;
		int start = end - 2;
		return wk.replace(start, end, replaceStr).toString();
	}
	
	/**
	 *<pre>
	 * 支出項目表示順の値で支出項目レベルの位置の表示順がその他(99)かどうかを返します。
	 * 
	 * レベル：start - end：の取得位置
	 * 1：0-2：**00000000
	 * 2：2-4：00**000000
	 * 3：4-6：0000**0000
	 * 4：6-8：000000**00
	 * 5：8-10：00000000**
	 *</pre>
	 * @param sisyutuItemSort 支出項目表示順
	 * @param sisyutuItemLevel 支出項目レベル
	 * @return 支出項目レベルの位置の表示順がその他(99)である場合、ture、そうでない場合はfalse
	 *
	 */
	private boolean isCheckOtherItem(String sisyutuItemSort, int sisyutuItemLevel) {
		return getSortValueFromLevel(sisyutuItemSort, sisyutuItemLevel).equals(MyHouseholdAccountBookContent.OTHER_SISYUTU_ITEM_SORT_VALUE) ? true : false;
	}
	
	/**
	 *<pre>
	 * 支出項目レベルの位置の支出項目表示順の値を返します。
	 * 
	 * レベル：start - end：の取得位置(レベルの値に応じて、**の値を返します)
	 * 1：0-2：**00000000
	 * 2：2-4：00**000000
	 * 3：4-6：0000**0000
	 * 4：6-8：000000**00
	 * 5：8-10：00000000**
	 *</pre>
	 * @param sisyutuItemSort 支出項目表示順
	 * @param sisyutuItemLevel 支出項目レベル
	 * @return 支出項目レベルの位置の表示順の値
	 *
	 */
	private String getSortValueFromLevel(String sisyutuItemSort, int sisyutuItemLevel) {
		int end = sisyutuItemLevel * 2;
		int start = end - 2;
		return sisyutuItemSort.substring(start, end);
	}
	
	/**
	 *<pre>
	 * 表示順選択リストに新規追加する支出項目を追加します。
	 *</pre>
	 * @param optionList 表示順選択リスト
	 * @param addSisyutuItem 支出項目情報(ドメイン)
	 *
	 */
	private void addNewParentMembersItem(List<OptionItem> optionList, SisyutuItem addSisyutuItem) {
		
		/* 一番最後にその他項目(99)がある場合、項目を取り出し(リストからはいったん削除) */
		OptionItem otherItem = null;
		// 表示順選択リストが空でない、かつ、一番最後の項目がその他項目の場合
		if(optionList.size() != 0
				&& isCheckOtherItem(optionList.get(optionList.size() - 1).getValue(),
						addSisyutuItem.getSisyutuItemLevel().getValue())) {
			// 一番最後の項目を取り出し、表示順選択リストから削除する
			otherItem = optionList.remove(optionList.size() - 1);
		}
		// 新規登録する支出項目の表示順の値を作成
		String sortVal = replaceSisyutuItemSort(
				// 支出項目表示順
				addSisyutuItem.getSisyutuItemSort().toString(),
				// 支出項目レベル
				addSisyutuItem.getSisyutuItemLevel().getValue(),
				// レベルの位置の変換文字列=親に属する支出項目の件数+1(2桁の0パディングした値)
				String.format("%02d", optionList.size() + 1));
		
		// 新規登録する支出項目を表示順選択リストに追加
		optionList.add(OptionItem.from(sortVal, "▼：新規追加表示順"));
		
		// その他項目(99)がある場合、選択リストの一番最後に移動
		if(otherItem != null) {
			optionList.add(otherItem);
		}
	}
	
	/**
	 *<pre>
	 * 指定した支出項目情報の検索結果を指定の方法でデータ抽出を行い、指定した増減値で表示順を更新します。
	 *</pre>
	 * @param parentMemberList 表示順更新対象の支出項目情報の検索結果
	 * @param predicate データ抽出式
	 * @param addValue 表示順を更新時の増減値
	 *
	 */
	private void updateAllSisyutuItemSort(SisyutuItemInquiryList parentMemberList, Predicate<SisyutuItem> predicate, int addValue) {
		
		// 検索結果のリストに対して以下処理を行う
		List<SisyutuItem> updateSisyutuItem = parentMemberList.getValues().stream()
			// 対象のデータ抽出
			.filter(domain -> predicate.test(domain))
			// 新しい表示順でデータを生成
			.map(domain -> {
				// 現在の表示順の値
				String sisyutuItemSort = domain.getSisyutuItemSort().toString();
				// 現在の支出項目レベルの値
				int sisyutuItemLevel = domain.getSisyutuItemLevel().getValue();
				// 現在の表示順の値を指定した増減値の値で更新して新しい表示順に設定
				int newSortIntVal = Integer.parseInt(getSortValueFromLevel(sisyutuItemSort, sisyutuItemLevel)) + addValue;
				// 新規登録する支出項目の表示順の値を作成
				String updSortVal = replaceSisyutuItemSort(sisyutuItemSort, sisyutuItemLevel, String.format("%02d", newSortIntVal));
				
				// 表示順を更新した支出項目情報を設定
				return createSisyutuItemFromSisyutuItemSort(domain, updSortVal);}
			).toList();
			
		// 表示順更新対象の支出項目情報を更新
		updateSisyutuItem.forEach(updItem -> updateSisyutuItemSort(
			// 表示順更新対象の支出項目情報
			updItem,
			// 表示順更新対象の支出項目レベル
			updItem.getSisyutuItemLevel().getValue(),
			// 支出項目表示順から指定の支出項目レベルの位置の値を取得した値
			getSortValueFromLevel(updItem.getSisyutuItemSort().toString(), updItem.getSisyutuItemLevel().getValue())));
	}
	
	/**
	 *<pre>
	 * 表示順変更対象の支出項目を指定の表示順の値で更新します。
	 * 表示順の値は対象位置(支出項目レベル)と、支出項目レベルに該当する表示順の値(2桁の0パディングした値)を指定してください。
	 * 対象の出項目を親に持つ支出項目も再起呼び出しを行い更新します。
	 *</pre>
	 * @param updItem 表示順を更新する支出項目情報(ドメイン)
	 * @param sisyutuItemLevel 支出項目レベル
	 * @param value 支出項目レベルの位置の表示順の値
	 *
	 */
	private void updateSisyutuItemSort(SisyutuItem updItem, int sisyutuItemLevel, String value) {
		int updateCount = 0;
		updateCount = sisyutuItemRepository.updateSisyutuItemSort(updItem);
		// 追加件数が1件以上の場合、業務エラー
		if(updateCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル:SISYUTU_ITEM_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updItem + "]");
		}
		/* 支出項目レベルが5より小さい(親となっている支出項目の可能性がある場合、支出項目コードの値を親の支出項目コード
		   として検索し、親に属する子の支出項目情報の表示順の値を再起して更新する */
		// 支出項目レベルが5以下の場合、子の支出項目は存在しないので呼び出し元に戻る
		if(updItem.getSisyutuItemLevel().getValue() >= 5) {
			return;
		}
		
		// 親の支出項目に属する支出項目一覧を取得
		SisyutuItemInquiryList parentMemberList = sisyutuItemRepository.searchParentMemberSisyutuItemList(
				SearchQueryUserIdAndSisyutuItemCode.from(updItem.getUserId().toString(), updItem.getSisyutuItemCode().toString()));
		// 親の支出項目に属する支出項目情報から名称を取得し、名称一覧をレスポンスに設定
		if(!parentMemberList.isEmpty()) {
			// 指定した支出項目レベルの位置の表示順の値を更新して新たな支出項目更新データを生成
			List<SisyutuItem> updateValueList = parentMemberList.getValues().stream().map(domain -> {
				// 新規登録する支出項目の表示順の値を作成
				String updSortVal = replaceSisyutuItemSort(domain.getSisyutuItemSort().toString(), sisyutuItemLevel, value);
				// 表示順を更新した支出項目情報を設定
				return createSisyutuItemFromSisyutuItemSort(domain, updSortVal);
			}).toList();
			
			// 表示順更新対象の支出項目情報を更新
			updateValueList.forEach(parentUpdateItem -> updateSisyutuItemSort(
				// 表示順更新対象の支出項目情報
				parentUpdateItem,
				// 表示順更新対象の支出項目レベル
				sisyutuItemLevel,
				// 支出項目表示順から指定の支出項目レベルの位置の値を取得した値
				value));
		}
	}
}
