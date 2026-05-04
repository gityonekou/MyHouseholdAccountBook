/**
 * 固定費情報管理ユースケース（参照系）です。
 * ・情報管理(固定費)初期表示画面情報取得(デフォルト時)
 * ・情報管理(固定費)処理選択画面情報取得
 * ・指定した支出項目に属する固定費が登録済みかを判定する処理
 * ・情報管理(固定費)初期表示画面情報取得(指定した支出項目に属する固定費が登録済みの場合)
 * ・情報管理(固定費)更新画面情報取得(追加する固定費の支出項目情報を指定時)
 * ・情報管理(固定費)更新画面情報取得(更新時)
 * ・固定費情報追加・更新時のパラメータチェックエラー時処理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/19 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/04/19 : 1.01.01  リファクタリング対応(FixedCostInfoManageUseCaseから参照系の処理を分離し、クラス名をFixedCostInquiryUseCase にリネーム)
 * 2026/05/01 : 1.01.02  固定費一括更新機能追加に伴う処理追加
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.application.usecase.common.ExpenditureItemInfoComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostCode;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostBulkUpdateForm;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractFixedCostItemListResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractFixedCostItemListResponse.FixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostBulkUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostBulkUpdateResponse.BulkUpdateTargetItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse.SiblingFixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse.SisyutuItemCodeInfo;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 固定費情報管理ユースケース（参照系）です。
 * ・情報管理(固定費)初期表示画面情報取得(デフォルト時)
 * ・情報管理(固定費)処理選択画面情報取得
 * ・指定した支出項目に属する固定費が登録済みかを判定する処理
 * ・情報管理(固定費)初期表示画面情報取得(指定した支出項目に属する固定費が登録済みの場合)
 * ・情報管理(固定費)更新画面情報取得(追加する固定費の支出項目情報を指定時)
 * ・情報管理(固定費)更新画面情報取得(更新時)
 * ・固定費情報追加・更新時のパラメータチェックエラー時処理
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FixedCostInquiryUseCase {
	
	// 支出項目情報取得コンポーネント
	private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
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
	 * 選択した固定費に応じた固定費情報、及び、選択した固定費が属する支出項目コードに属する固定費情報を取得し、
	 * 画面表示情報に設定します。また、ユーザIDに応じた固定費一覧情報を取得し画面表示情報に設定します。
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
		FixedCost searchResult = fixedCostRepository.findByPrimaryKey(
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
				searchResult.getFixedCostTargetPaymentMonth().getValue());
		// 固定費支払月の値が「その他任意」の場合、支払月詳細の値に「"(" ＋ 「固定費支払月任意詳細」＋")"」を追加
		if(Objects.equals(
				// 固定費支払月の値
				searchResult.getFixedCostTargetPaymentMonth().getValue(),
				// コード区分：固定費支払月：その他任意(40)
				MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE
			)) {
			// 支払月詳細の値 =「固定費支払月の値をコード変換した値」＋"(" ＋ 「固定費支払月任意詳細」＋")"
			shiharaiTukiDetailContext += "(" + searchResult.getFixedCostTargetPaymentMonthOptionalContext().getValue() + ")";
		}
		/* 固定費支払日区分、固定費支払日の値をもとに支払日詳細を設定 */
		/* レスポンスの生成 */
		// 固定費情報をもとにレスポンスを生成
		FixedCostInfoManageActSelectResponse response = FixedCostInfoManageActSelectResponse.getInstance(
				FixedCostInfoManageActSelectResponse.SelectFixedCostInfo.from(
						// 固定費コード
						searchResult.getFixedCostCode().getValue(),
						// 支出項目名(＞で区切った値)
						expenditureItemInfoComponent.getExpenditureItemName(userId, searchResult.getExpenditureItemCode()),
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
								searchResult.getFixedCostPaymentDay().getValue()),
						// 支払金額
						searchResult.getFixedCostPaymentAmount().toFormatString()));
		// 固定費一覧をレスポンスに設定
		setFixedCostItemList(userId, response);
		
		/* 選択固定費の支出項目コードで同一支出項目に属する固定費情報を取得 */
		// 選択固定費の支出項目コードで同一支出項目に属する固定費の件数を取得
		int siblingCount = fixedCostRepository.countByExpenditureItemCode(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, searchResult.getExpenditureItemCode()));
		// 2件以上の場合、兄弟固定費一覧を取得してレスポンスに設定
		if (siblingCount >= 2) {
			FixedCostInquiryList siblingList = fixedCostRepository.findByExpenditureItemCode(
					SearchQueryUserIdAndExpenditureItemCode.from(userId, searchResult.getExpenditureItemCode()));
			response.addSiblingFixedCostItemList(createSiblingFixedCostItemList(siblingList));
		}

		return response;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目に属する固定費が登録済みかを判定する処理
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
		ExpenditureItemCode expenditureItemCode = ExpenditureItemCode.from(sisyutuItemCodeStr);
		
		// 支出項目コードに属する固定費の件数が0件の場合faseを返却、1件以上の場合はtrueを返却
		if(fixedCostRepository.countByExpenditureItemCode(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode)) == 0) {
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
		ExpenditureItemCode expenditureItemCode = ExpenditureItemCode.from(sisyutuItemCodeStr);
		
		// 情報管理(固定費)初期表示画面の表示情報を取得
		FixedCostInfoManageInitResponse response = getInitResponse(userId, true);
		
		// 選択した支出項目コード情報を設定
		response.setSisyutuItemCodeInfo(SisyutuItemCodeInfo.from(expenditureItemCode.getValue()));
		
		// 既に登録済みの支出項目の固定費一覧を取得
		FixedCostInquiryList searchResult = fixedCostRepository.findByExpenditureItemCode(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
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
		FixedCost searchResult = fixedCostRepository.findByPrimaryKey(
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
		updateForm.setSisyutuItemCode(searchResult.getExpenditureItemCode().getValue());
		// 固定費名(支払名)
		updateForm.setFixedCostName(searchResult.getFixedCostName().getValue());
		// 固定費内容詳細(支払内容詳細)
		updateForm.setFixedCostDetailContext(searchResult.getFixedCostDetailContext().getValue());
		// 固定費区分
		updateForm.setFixedCostKubun(searchResult.getFixedCostKubun().getValue());
		// 支払月
		updateForm.setShiharaiTuki(searchResult.getFixedCostTargetPaymentMonth().getValue());
		// 支払月任意詳細
		updateForm.setShiharaiTukiOptionalContext(searchResult.getFixedCostTargetPaymentMonthOptionalContext().getValue());
		// 支払日
		updateForm.setShiharaiDay(searchResult.getFixedCostPaymentDay().getValue());
		// 支払金額
		updateForm.setShiharaiKingaku(searchResult.getFixedCostPaymentAmount().toIntegerValue());
		// 支払い月選択ボックス、支出項目名をレスポンスに設定し返却
		return getUpdateResponse(userId, updateForm);
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
	 * 情報管理(固定費)一括更新画面情報取得（初回表示）
	 *
	 * 基準固定費コードをもとに一括更新画面の表示情報を取得します。
	 * フォームの初期値として基準固定費の支払日・支払金額を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param fixedCostCodeStr 基準固定費コード
	 * @return 情報管理(固定費)一括更新画面の表示情報
	 *
	 */
	public FixedCostBulkUpdateResponse readBulkUpdateInfo(LoginUserInfo user, String fixedCostCodeStr) {
		log.debug("readBulkUpdateInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCodeStr);

		UserId userId = UserId.from(user.getUserId());
		FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);

		// 基準となる固定費情報を取得（フォームの初期値として使用）
		FixedCost baseFixedCost = fixedCostRepository.findByPrimaryKey(
				SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
		if (baseFixedCost == null) {
			throw new MyHouseholdAccountBookRuntimeException(
					"選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。[fixedCostCode=" + fixedCostCode + "]");
		}

		// 固定費一括更新入力フォームを生成の初期値として基準固定費の支払日・支払金額を設定
		FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
		// 基準固定費コード:選択固定費の固定費コードを設定
		form.setBaseFixedCostCode(baseFixedCost.getFixedCostCode().getValue());
		// 支払日:選択固定費の支払日を設定
		form.setShiharaiDay(baseFixedCost.getFixedCostPaymentDay().getValue());
		// 支払金額:選択固定費の支払金額を設定
		form.setShiharaiKingaku(baseFixedCost.getFixedCostPaymentAmount().toIntegerValue());
		
		// 選択固定費の支出項目コード、一括更新入力フォーム情報を元に画面表示情報を生成し返却
		return getBulkUpdateResponse(userId, baseFixedCost.getExpenditureItemCode(), form);
	}

	/**
	 *<pre>
	 * 固定費一括更新時のパラメータチェックエラー時処理
	 *
	 * 情報管理(固定費)一括更新画面で更新実行時のバリデーションチェックNGとなった場合の各画面表示項目を取得します。
	 * 送信済みの inputForm をそのまま渡すことで、ユーザーの入力状態を保持します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 固定費一括更新フォームの入力値
	 * @return 情報管理(固定費)一括更新画面の表示情報
	 *
	 */
	public FixedCostBulkUpdateResponse readBulkUpdateBindingErrorSetInfo(LoginUserInfo user,
			FixedCostBulkUpdateForm inputForm) {
		log.debug("readBulkUpdateBindingErrorSetInfo:userid=" + user.getUserId());

		UserId userId = UserId.from(user.getUserId());
		FixedCostCode baseFixedCostCode = FixedCostCode.from(inputForm.getBaseFixedCostCode());

		// 支出項目コードを取得するため基準固定費を検索
		FixedCost baseFixedCost = fixedCostRepository.findByPrimaryKey(
				SearchQueryUserIdAndFixedCostCode.from(userId, baseFixedCostCode));
		if (baseFixedCost == null) {
			throw new MyHouseholdAccountBookRuntimeException(
					"選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。[fixedCostCode=" + baseFixedCostCode + "]");
		}

		// 送信済み inputForm(一括更新入力フォーム情報) をそのまま渡して画面表示情報を生成（チェック状態・入力値を保持）
		return getBulkUpdateResponse(userId, baseFixedCost.getExpenditureItemCode(), inputForm);
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
		expenditureItemInfoComponent.setSisyutuItemResponseList(userId, response);
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
		FixedCostInquiryList searchResult = fixedCostRepository.findByUserId(SearchQueryUserId.from(userId));
		if(searchResult.isEmpty()) {
			// 登録済み固定費情報が0件の場合、メッセージを設定
			response.addMessage("登録済み固定費情報が0件です。");
		} else {
			// 固定費一覧情報をレスポンスに設定
			response.addFixedCostItemList(createFixedCostItemList(searchResult));
			// 奇数月合計の値を設定
			response.setOddMonthGoukei(searchResult.getOddMonthGoukei().toFormatString());
			// 偶数月合計の値を設定
			response.setAnEvenMonthGoukei(searchResult.getAnEvenMonthGoukei().toFormatString());
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
		
		// 支出項目名(＞で区切った値)を取得しレスポンスに設定
		response.setSisyutuItemName(expenditureItemInfoComponent.getExpenditureItemName(userId, ExpenditureItemCode.from(inputForm.getSisyutuItemCode())));
		
		return response;
		
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
				domain.getExpenditureItemName().getValue(),
				// 支払名：固定費名を設定を設定
				domain.getFixedCostName().getValue(),
				// 支払月：固定費支払月の値をコード変換して設定
				codeTableItem.getCodeValue(
						// コード区分：固定費支払月
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
						// 固定費支払月
						domain.getFixedCostTargetPaymentMonth().getValue()),
				// 支払日
				codeTableItem.getCodeValue(
						// コード区分：固定費支払日
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
						// 固定費支払日
						domain.getFixedCostPaymentDay().getValue()),
				// 支払金額
				domain.getFixedCostPaymentAmount().toFormatString(),
				// その他任意詳細：固定費支払月任意詳細
				domain.getFixedCostTargetPaymentMonthOptionalContext().getValue())
		).collect(Collectors.toUnmodifiableList());
	}

	/**
	 *<pre>
	 * 情報管理(固定費)一括更新画面の表示情報を取得します。
	 *</pre>
	 * @param userId 取得対象のユーザID
	 * @param expenditureItemCode 支出項目コード
	 * @param form 入力フォーム
	 * @return 情報管理(固定費)一括更新画面表示情報
	 *
	 */
	private FixedCostBulkUpdateResponse getBulkUpdateResponse(
			UserId userId, ExpenditureItemCode expenditureItemCode, FixedCostBulkUpdateForm form) {

		// 支払日選択ボックス情報を取得
		List<CodeAndValuePair> shiharaiDayList = codeTableItem.getCodeValues(
				MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY);
		if (shiharaiDayList == null) {
			throw new MyHouseholdAccountBookRuntimeException(
					"コード定義ファイルに「固定費支払日情報："
					+ MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY + "」が登録されていません。管理者に問い合わせてください");
		}

		// レスポンスを生成
		FixedCostBulkUpdateResponse response = FixedCostBulkUpdateResponse.getInstance(
				form,
				shiharaiDayList.stream().map(pair ->
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue()))
					.collect(Collectors.toList()));

		// 支出項目名(＞で区切った値)を取得しレスポンスに設定
		response.setSisyutuItemName(
				expenditureItemInfoComponent.getExpenditureItemName(userId, expenditureItemCode));

		// 同一支出項目の全固定費一覧を取得して設定
		FixedCostInquiryList siblingList = fixedCostRepository.findByExpenditureItemCode(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
		response.addBulkUpdateTargetList(createBulkUpdateTargetList(siblingList));

		return response;
	}

	/**
	 *<pre>
	 * 引数の固定費一覧情報(ドメイン)から兄弟固定費明細情報のリストを生成して返します。
	 *</pre>
	 * @param searchResult 固定費一覧情報(ドメイン)
	 * @return 兄弟固定費明細情報のリスト
	 *
	 */
	private List<SiblingFixedCostItem> createSiblingFixedCostItemList(FixedCostInquiryList searchResult) {
		// 兄弟固定費明細情報のリストを生成して返却
		return searchResult.getValues().stream().map(domain -> {
			// 支払月任意詳細の値は固定費支払月の値の値に応じて以下値を設定
			// ・「その他任意」の場合は「固定費支払月任意詳細」の値を設定
			// ・「その他任意」以外の場合は空文字を設定
			String shiharaiTukiOptionalContext = Objects.equals(
					domain.getFixedCostTargetPaymentMonth().getValue(),
					MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)
					? domain.getFixedCostTargetPaymentMonthOptionalContext().getValue()
					: "";
			// 兄弟固定費明細情報を生成して返却
			return SiblingFixedCostItem.from(
					// 固定費コード
					domain.getFixedCostCode().getValue(),
					// 支払名
					domain.getFixedCostName().getValue(),
					// 支払月：固定費支払月の値をコード変換して設定
					codeTableItem.getCodeValue(
							MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
							domain.getFixedCostTargetPaymentMonth().getValue()),
					// 支払月任意詳細
					shiharaiTukiOptionalContext,
					// 支払日：固定費支払日の値をコード変換して設定
					codeTableItem.getCodeValue(
							MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
							domain.getFixedCostPaymentDay().getValue()),
					// 支払金額：固定費支払金額の値をフォーマットして設定
					domain.getFixedCostPaymentAmount().toFormatString());
		}).collect(Collectors.toUnmodifiableList());
	}

	/**
	 *<pre>
	 * 引数の固定費一覧情報(ドメイン)から一括更新対象固定費明細情報のリストを生成して返します。
	 *</pre>
	 * @param searchResult 固定費一覧情報(ドメイン)
	 * @return 一括更新対象固定費明細情報のリスト
	 *
	 */
	private List<BulkUpdateTargetItem> createBulkUpdateTargetList(FixedCostInquiryList searchResult) {
		// 一括更新対象固定費明細情報のリストを生成して返却
		return searchResult.getValues().stream().map(domain -> {
			// 支払月任意詳細の値は固定費支払月の値の値に応じて以下値を設定
			// ・「その他任意」の場合は「固定費支払月任意詳細」の値を設定
			// ・「その他任意」以外の場合は空文字を設定
			String shiharaiTukiOptionalContext = Objects.equals(
					domain.getFixedCostTargetPaymentMonth().getValue(),
					MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)
					? domain.getFixedCostTargetPaymentMonthOptionalContext().getValue()
					: "";
			// 一括更新対象固定費明細情報を生成して返却
			return BulkUpdateTargetItem.from(
					// 固定費コード
					domain.getFixedCostCode().getValue(),
					// 支払名
					domain.getFixedCostName().getValue(),
					// 支払月：固定費支払月の値をコード変換して設定
					codeTableItem.getCodeValue(
							MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
							domain.getFixedCostTargetPaymentMonth().getValue()),
					// 支払月任意詳細
					shiharaiTukiOptionalContext,
					// 支払日：固定費支払日の値をコード変換して設定
					codeTableItem.getCodeValue(
							MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
							domain.getFixedCostPaymentDay().getValue()),
					// 支払金額：固定費支払金額の値をフォーマットして設定
					domain.getFixedCostPaymentAmount().toFormatString());
		}).collect(Collectors.toUnmodifiableList());
	}
}
