/**
 * 固定費情報管理ユースケースです。
 * ・情報管理(固定費)初期表示画面情報取得(デフォルト時)
 * ・情報管理(固定費)処理選択画面情報取得
 * ・指定した支出項目に属する固定費が登録済みかどうかの判定処理
 * ・情報管理(固定費)初期表示画面情報取得(指定した支出項目に属する固定費が登録済みの場合)
 * ・情報管理(固定費)更新画面情報取得(追加する固定費の支出項目情報を指定時)
 * ・情報管理(固定費)更新画面情報取得(更新時)
 * ・指定した固定費情報の削除処理
 * ・固定費情報追加・更新時のパラメータチェックエラー時処理
 * ・固定費情報追加・更新処理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractFixedCostItemListResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractFixedCostItemListResponse.FixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse.SisyutuItemCodeInfo;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 固定費情報管理ユースケースです。
 * ・情報管理(固定費)初期表示画面情報取得(デフォルト時)
 * ・情報管理(固定費)処理選択画面情報取得
 * ・指定した支出項目に属する固定費が登録済みかどうかの判定処理
 * ・情報管理(固定費)初期表示画面情報取得(指定した支出項目に属する固定費が登録済みの場合)
 * ・情報管理(固定費)更新画面情報取得(追加する固定費の支出項目情報を指定時)
 * ・情報管理(固定費)更新画面情報取得(更新時)
 * ・指定した固定費情報の削除処理
 * ・固定費情報追加・更新時のパラメータチェックエラー時処理
 * ・固定費情報追加・更新処理
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
public class FixedCostInfoManageUseCase {
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 固定費テーブル:FIXED_COST_TABLEリポジトリー
	private final FixedCostTableRepository fixedCostRepository;
	
	/**
	 *<pre>
	 * 情報管理(固定費)初期表示画面情報取得(デフォルト時)
	 * 
	 * 指定したユーザIDに応じた情報管理(固定費)初期表示画面の表示情報を取得します。
	 * ユーザIDをもとに支出項目一覧情報(事業経費・固定費(課税／非課税))と登録済みの固定費の一覧を取得し、
	 * 画面表示データに設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 情報管理(固定費)初期表示画面の表示情報
	 *
	 */
	public FixedCostInfoManageInitResponse readInitInfo(LoginUserInfo user) {
		log.debug("readInitInfo:userid=" + user.getUserId());
		// 情報管理(固定費)初期表示画面の表示情報を返却
		return getInitResponse(UserId.from(user.getUserId()), false);
	}
	
	/**
	 *<pre>
	 * 情報管理(固定費)処理選択画面情報取得
	 * 
	 * 指定したユーザID、固定費コードに応じた情報管理(固定費)処理選択画面の表示情報を取得します。
	 * 固定費コードをもとに固定費情報を取得し、画面表示情報に設定します。また、ユーザIDに応じた固定費一覧情報を
	 * 取得し画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param fixedCostCodeStr 表示対象の固定費コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	public FixedCostInfoManageActSelectResponse readActSelectItemInfo(LoginUserInfo user, String fixedCostCodeStr) {
		log.debug("readActSelectItemInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:固定費コード
		FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);
		
		/* 固定費コードに対応する固定費情報を取得 */
		FixedCost searchResult = fixedCostRepository.findByIdAndFixedCostCode(
				SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。管理者に問い合わせてください。[fixedCostCode=" + fixedCostCode + "]");
		}
		/* 固定費支払月の値を元に支払月詳細の値を設定 */
		// 固定費支払月をコード変換した値を支払月詳細の値に設定
		String shiharaiTukiDetailContext = codeTableItem.getCodeValue(
				// コード区分：固定費支払月
				MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
				// 固定費支払月
				searchResult.getFixedCostShiharaiTuki().getValue());
		// 固定費支払月の値が「その他任意」の場合、支払月詳細の値に「"(" ＋ 「固定費支払月任意詳細」＋")"」を追加
		if(Objects.equals(
				// 固定費支払月の値
				searchResult.getFixedCostShiharaiTuki().getValue(),
				// コード区分：固定費支払月：その他任意(40)
				MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE
			)) {
			// 支払月詳細の値 =「固定費支払月の値をコード変換した値」＋"(" ＋ 「固定費支払月任意詳細」＋")"
			//shiharaiTukiDetailContext += "(" + null + ")";
			shiharaiTukiDetailContext += "(" + searchResult.getFixedCostShiharaiTukiOptionalContext().getValue() + ")";
		}
		/* 固定費支払日区分、固定費支払日の値をもとに支払日詳細を設定 */
		/* レスポンスの生成 */
		// 固定費情報をもとにレスポンスを生成
		FixedCostInfoManageActSelectResponse response = FixedCostInfoManageActSelectResponse.getInstance(
				FixedCostInfoManageActSelectResponse.SelectFixedCostInfo.from(
						// 固定費コード
						searchResult.getFixedCostCode().getValue(),
						// 支出項目名(＞で区切った値)
						sisyutuItemComponent.getSisyutuItemName(userId, searchResult.getSisyutuItemCode()),
						// 支払名
						searchResult.getFixedCostName().getValue(),
						// 支払内容詳細
						searchResult.getFixedCostDetailContext().getValue(),
						// 支払月詳細
						shiharaiTukiDetailContext,
						// 支払日
						codeTableItem.getCodeValue(
								// コード区分：固定費支払日
								MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
								// 固定費支払月
								searchResult.getFixedCostShiharaiDay().getValue()),
						// 支払金額
						searchResult.getShiharaiKingaku().toString()));
		// 固定費一覧をレスポンスに設定
		setFixedCostItemList(userId, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目に属する固定費が登録済みかどうかの判定処理
	 * 
	 * 指定した支出項目に属する固定費が登録済みかどうかを判定して返します。
	 * 登録済みの場合、true、未登録の場合はfalseになります。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCodeStr 判定対象の支出項目コード
	 * @return 登録済みの場合、true、未登録の場合はfalse
	 *
	 */
	public boolean hasFixedCostInfoBySisyutuItem(LoginUserInfo user, String sisyutuItemCodeStr) {
		log.debug("hasFixedCostInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支出項目コード
		SisyutuItemCode sisyutuItemCode = SisyutuItemCode.from(sisyutuItemCodeStr);
		
		// 支出項目コードに属する固定費の件数が0件の場合faseを返却、1件以上の場合はtrueを返却
		if(fixedCostRepository.countBySisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(userId, sisyutuItemCode)) == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 *<pre>
	 * 情報管理(固定費)初期表示画面情報取得(指定した支出項目に属する固定費が登録済みの場合)
	 * 
	 * 指定した支出項目に属する固定費が登録済みであることのメッセージを追加した形で情報管理(固定費)初期表示画面の
	 * 表示情報を取得します。
	 * ユーザIDをもとに支出項目一覧情報(事業経費・固定費(課税／非課税))と登録済みの固定費の一覧を取得し、
	 * 画面表示データに設定します。
	 * 指定した支出項目に属する固定費の一覧情報を取得し画面表示データに設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCodeStr 支出項目コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	public FixedCostInfoManageInitResponse readRegisteredFixedCostInfoBySisyutuItem(LoginUserInfo user, String sisyutuItemCodeStr) {
		log.debug("readRegisteredFixedCostInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支出項目コード
		SisyutuItemCode sisyutuItemCode = SisyutuItemCode.from(sisyutuItemCodeStr);
		
		// 情報管理(固定費)初期表示画面の表示情報を取得
		FixedCostInfoManageInitResponse response = getInitResponse(userId, true);
		
		// 選択した支出項目コード情報を設定
		response.setSisyutuItemCodeInfo(SisyutuItemCodeInfo.from(sisyutuItemCode.getValue()));
		
		// 既に登録済みの支出項目の固定費一覧を取得
		FixedCostInquiryList searchResult = fixedCostRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(userId, sisyutuItemCode));
		if(searchResult.isEmpty()) {
			// 登録済み固定費情報が0件の場合、メッセージを設定(削除設定した場合、可能性はあるのでエラーとせず、
			// このまま処理続行(新規に固定費登録画面に遷移・固定費情報の登録可能）
			response.addMessage("既に登録済みの支出項目の固定費一覧が0件でした。");
		} else {
			// 登録済みの固定費一覧情報をレスポンスに設定
			response.addRegisteredFixedCostInfoList(createFixedCostItemList(searchResult));
		}
		
		return response;
	}
	
	/**
	 *<pre>
	 * 情報管理(固定費)更新画面情報取得(追加する固定費の支出項目情報を指定時)
	 * 
	 * 指定した支出項目の情報を取得し、追加する固定費が属する支出項目の情報として情報管理(固定費)更新画面情報の表示情報を取得し
	 * 画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCode 追加する固定費が所属する支出項目の支出項目コード
	 * @return 情報管理(固定費)更新画面の表示情報
	 *
	 */
	public FixedCostInfoManageUpdateResponse readAddFixedCostInfoBySisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("readAddFixedCostInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		/* 固定費情報入力フォームを生成しレスポンスに設定 */
		// 固定費情報入力フォームデータを生成
		FixedCostInfoUpdateForm addForm = new FixedCostInfoUpdateForm();
		// アクション：新規登録
		addForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 属する支出項目コード
		addForm.setSisyutuItemCode(sisyutuItemCode);
		// 固定費区分(デフォルト値:支払い金額確定)
		addForm.setFixedCostKubun(MyHouseholdAccountBookContent.FIXED_COST_FIX_SELECTED_VALUE);
		// 画面表示情報を取得
		FixedCostInfoManageUpdateResponse response = getUpdateResponse(UserId.from(user.getUserId()), addForm);
		
		return response;
	}

	/**
	 *<pre>
	 * 情報管理(固定費)更新画面情報取得(更新時)
	 * 
	 * 更新対象の固定費コードを指定して報管理(固定費)更新画面情報の表示情報を取得し画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param fixedCostCodeStr 更新対象の固定費コード
	 * @return 情報管理(固定費)更新画面の表示情報
	 *
	 */
	public FixedCostInfoManageUpdateResponse readUpdateFixedCostInfo(LoginUserInfo user, String fixedCostCodeStr) {
		log.debug("readUpdateFixedCostInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:固定費コード
		FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);
		
		// 固定費コードに対応する固定費情報を取得
		FixedCost searchResult = fixedCostRepository.findByIdAndFixedCostCode(
				SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。管理者に問い合わせてください。[fixedCostCode=" + fixedCostCode + "]");
		}
		
		// 固定費情報入力フォームデータを生成
		FixedCostInfoUpdateForm updateForm = new FixedCostInfoUpdateForm();
		// アクション：更新
		updateForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 固定費コード
		updateForm.setFixedCostCode(searchResult.getFixedCostCode().getValue());
		// 属する支出項目コード
		updateForm.setSisyutuItemCode(searchResult.getSisyutuItemCode().getValue());
		// 固定費名(支払名)
		updateForm.setFixedCostName(searchResult.getFixedCostName().getValue());
		// 固定費内容詳細(支払内容詳細)
		updateForm.setFixedCostDetailContext(searchResult.getFixedCostDetailContext().getValue());
		// 固定費区分
		updateForm.setFixedCostKubun(searchResult.getFixedCostKubun().getValue());
		// 支払月
		updateForm.setShiharaiTuki(searchResult.getFixedCostShiharaiTuki().getValue());
		// 支払月任意詳細
		updateForm.setShiharaiTukiOptionalContext(searchResult.getFixedCostShiharaiTukiOptionalContext().getValue());
		// 支払日
		updateForm.setShiharaiDay(searchResult.getFixedCostShiharaiDay().getValue());
		// 支払金額
		updateForm.setShiharaiKingaku(DomainCommonUtils.convertInteger(searchResult.getShiharaiKingaku().getValue()));
		
		// 支払い月選択ボックス、支出項目名をレスポンスに設定し返却
		return getUpdateResponse(userId, updateForm);
	}

	/**
	 *<pre>
	 * 指定した固定費情報の削除処理
	 * 
	 * 指定した固定費情報を削除します。削除は論理削除となります。
	 * 処理結果は情報管理(固定費)処理選択画面に設定し、完了時は情報管理(固定費)初期表示画面にリダイレクトを設定します。
	 * エラー時は情報管理(固定費)処理選択画面に遷移します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param fixedCostCodeStr 削除対象の固定費コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	@Transactional
	public FixedCostInfoManageActSelectResponse execDelete(LoginUserInfo user, String fixedCostCodeStr) {
		log.debug("execDelete:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:固定費コード
		FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);
		
		// 固定費コードに対応する固定費情報を取得
		FixedCost deleteData = fixedCostRepository.findByIdAndFixedCostCode(
				SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
		if(deleteData == null) {
			throw new MyHouseholdAccountBookRuntimeException("削除対象の固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。管理者に問い合わせてください。[fixedCostCode=" + fixedCostCode + "]");
		}
		
		// 削除処理を実行
		int deleteCount = fixedCostRepository.delete(deleteData);
		// 追加件数が1件以上の場合、業務エラー
		if(deleteCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException("固定費テーブル:FIXED_COST_TABLEへの削除件数が不正でした。[件数=" + deleteCount + "][delete data:" + deleteData + "]");
		}
		// レスポンスを生成(エラー時はエラー画面に遷移するので固定費情報は使用しない:nullを指定)
		FixedCostInfoManageActSelectResponse response = FixedCostInfoManageActSelectResponse.getInstance(null);
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		// 完了メッセージ
		response.addMessage("指定の固定費を削除しました。[code:" + deleteData.getFixedCostCode() + "]" + deleteData.getFixedCostName());
		
		return response;
	}
	
	/**
	 *<pre>
	 * 固定費情報追加・更新時のパラメータチェックエラー時処理
	 * 
	 * 情報管理(固定費)更新画面で登録実行時のバリデーションチェックNGとなった場合の各画面表示項目を取得します。
	 * バリデーションチェック結果でNGの場合に呼び出してください。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 固定費情報入力フォームの入力値
	 * @return 情報管理(固定費)更新画面の表示情報
	 *
	 */
	public FixedCostInfoManageUpdateResponse readUpdateBindingErrorSetInfo(LoginUserInfo user,
			FixedCostInfoUpdateForm inputForm) {
		log.debug("readUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		return getUpdateResponse(UserId.from(user.getUserId()), inputForm);
	}

	/**
	 *<pre>
	 * 固定費情報追加・更新処理
	 * 
	 * 固定費情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 固定費情報入力フォームの入力値
	 * @return 情報管理(固定費)更新画面の表示情報
	 *
	 */
	@Transactional
	public FixedCostInfoManageUpdateResponse execAction(LoginUserInfo user, FixedCostInfoUpdateForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		
		// レスポンスを取得
		FixedCostInfoManageUpdateResponse response = getUpdateResponse(userId, inputForm);
		
		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			
			// 新規採番する固定費コードの値を取得
			int count = fixedCostRepository.countById(SearchQueryUserId.from(userId));
			count++;
			if(count > 9999) {
				response.addErrorMessage("固定費情報は9999件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 固定費コードを入力フォームに設定
			inputForm.setFixedCostCode(FixedCostCode.getNewCode(count));
			
			// 追加する固定費情報
			FixedCost addData = createFixedCost(user.getUserId(), inputForm);
			
			// 固定費テーブルに登録
			int addCount = fixedCostRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("固定費テーブル:FIXED_COST_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("新規固定費を追加しました。[code:" + addData.getFixedCostCode() + "]" + addData.getFixedCostName());
			
		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
			// 更新する固定費情報
			FixedCost updateData = createFixedCost(user.getUserId(), inputForm);
			
			// 固定費テーブルに登録(支出項目コードは更新対象外項目なので注意)
			int updateCount = fixedCostRepository.update(updateData);
			// 更新件数が1件以上の場合、業務エラー
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("固定費テーブル:FIXED_COST_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updateData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("固定費を更新しました。[code:" + updateData.getFixedCostCode() + "]" + updateData.getFixedCostName());
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		return response;
	}

	
	/**
	 *<pre>
	 * 情報管理(固定費)初期表示画面の表示情報を取得します。
	 *</pre>
	 * @param userId 取得対象のユーザID
	 * @param registeredFlg 登録済み表示エリアを表示するかどうかのフラグ
	 * @return 情報管理(固定費)初期表示画面の表示情報
	 *
	 */
	private FixedCostInfoManageInitResponse getInitResponse(UserId userId, boolean registeredFlg) {
		// レスポンスを生成
		FixedCostInfoManageInitResponse response = FixedCostInfoManageInitResponse.getInstance(registeredFlg);
		// 支出項目一覧をレスポンスに設定
		sisyutuItemComponent.setSisyutuItemList(userId, response);
		// 固定費一覧をレスポンスに設定
		setFixedCostItemList(userId, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 固定費一覧情報を取得し、引数で渡されたレスポンス(固定費一覧画面情報)に設定します。
	 *</pre>
	 * @param userId 取得対象のユーザID
	 * @param response 固定費一覧画面情報
	 *
	 */
	private void setFixedCostItemList(UserId userId, AbstractFixedCostItemListResponse response) {
		// 固定費一覧を取得
		FixedCostInquiryList searchResult = fixedCostRepository.findById(SearchQueryUserId.from(userId));
		if(searchResult.isEmpty()) {
			// 登録済み固定費情報が0件の場合、メッセージを設定
			response.addMessage("登録済み固定費情報が0件です。");
		} else {
			// 固定費一覧情報をレスポンスに設定
			response.addFixedCostItemList(createFixedCostItemList(searchResult));
			// 奇数月合計の値を設定
			response.setOddMonthGoukei(searchResult.getOddMonthGoukei().toString());
			// 偶数月合計の値を設定
			response.setAnEvenMonthGoukei(searchResult.getAnEvenMonthGoukei().toString());
		}
	}
	
	/**
	 *<pre>
	 * 情報管理(固定費)更新画面の表示情報を取得します。
	 *</pre>
	 * @param userId 取得対象のユーザID
	 * @param inputForm 固定費情報が格納されたフォームデータ
	 * @return 情報管理(固定費)更新画面表示情報
	 *
	 */
	private FixedCostInfoManageUpdateResponse getUpdateResponse(UserId userId, FixedCostInfoUpdateForm inputForm) {
		
		// コードテーブル情報から固定費区分選択ボックスの表示情報を取得し、リストに設定addList
		List<CodeAndValuePair> fixedCostKubunList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_KUBUN);
		// 固定費区分選択ボックス表示情報がない場合、エラー
		if(fixedCostKubunList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「固定費区分情報：" 
					+ MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		
		// コードテーブル情報から支払月選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> shiharaiTukiList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI);
		// 支払月選択ボックス表示情報がない場合、エラー
		if(shiharaiTukiList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「固定費支払月情報：" 
					+ MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI + "」が登録されていません。管理者に問い合わせてください");
		}
		// コードテーブル情報から支払日選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> shiharaiDayList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY);
		// 支払月選択ボックス表示情報がない場合、エラー
		if(shiharaiDayList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「固定費支払日情報：" 
					+ MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY + "」が登録されていません。管理者に問い合わせてください");
		}
		
		// レスポンスを生成
		FixedCostInfoManageUpdateResponse response = FixedCostInfoManageUpdateResponse.getInstance(
				// 固定費情報入力フォーム
				inputForm,
				// 固定費区分の表示情報リスト(初期選択は「支払い金額確定」なので、不変リストを設定)
				fixedCostKubunList.stream().map(pair -> 
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toUnmodifiableList()),
				// 支払月選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
				shiharaiTukiList.stream().map(pair ->
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toList()),
				// 支払日選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
				shiharaiDayList.stream().map(pair ->
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toList()));
		
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(userId, SisyutuItemCode.from(inputForm.getSisyutuItemCode())));
		
		return response;
		
	}
	
	/**
	 *<pre>
	 * 引数のフォームデータから固定費情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param inputForm フォームデータ
	 * @return 固定費情報(ドメイン)
	 *
	 */
	private FixedCost createFixedCost(String userId,  FixedCostInfoUpdateForm inputForm) {
		return FixedCost.from(
				// ユーザID
				userId,
				// 固定費コード
				inputForm.getFixedCostCode(),
				// 固定費名(支払名)
				inputForm.getFixedCostName(),
				// 固定費内容詳細(支払内容詳細)
				inputForm.getFixedCostDetailContext(),
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// 固定費区分
				inputForm.getFixedCostKubun(),
				// 固定費支払月(支払月)
				inputForm.getShiharaiTuki(),
				// 固定費支払月任意詳細
				inputForm.getShiharaiTukiOptionalContext(),
				// 固定費支払日(支払日)
				inputForm.getShiharaiDay(),
				// 支払金額
				DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShiharaiKingaku()));
	}
	
	/**
	 *<pre>
	 * 引数の固定費一覧情報(ドメイン)から画面表示する固定費一覧明細情報のリストを生成して返します。
	 *</pre>
	 * @param searchResult 固定費一覧情報(ドメイン)
	 * @return 画面表示する固定費一覧明細情報のリスト
	 *
	 */
	private List<FixedCostItem> createFixedCostItemList(FixedCostInquiryList searchResult) {
		return searchResult.getValues().stream().map(domain ->
			AbstractFixedCostItemListResponse.FixedCostItem.from(
				// 固定費コード
				domain.getFixedCostCode().getValue(),
				// 支出項目名
				domain.getSisyutuItemName().getValue(),
				// 支払名：固定費名を設定を設定
				domain.getFixedCostName().getValue(),
				// 支払月：固定費支払月の値をコード変換して設定
				codeTableItem.getCodeValue(
						// コード区分：固定費支払月
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
						// 固定費支払月
						domain.getFixedCostShiharaiTuki().getValue()),
				// 支払日
				codeTableItem.getCodeValue(
						// コード区分：固定費支払日
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
						// 固定費支払月
						domain.getFixedCostShiharaiDay().getValue()),
				// 支払金額
				domain.getShiharaiKingaku().toString(),
				// その他任意詳細：固定費支払月任意詳細
				domain.getFixedCostShiharaiTukiOptionalContext().getValue())
		).collect(Collectors.toUnmodifiableList());
	}
}
