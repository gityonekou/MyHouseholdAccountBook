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

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse;
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
		
		return FixedCostInfoManageInitResponse.getInstance(false);
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
	 * @param fixedCostCode 表示対象固定費の固定費コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	public FixedCostInfoManageActSelectResponse readActSelectItemInfo(LoginUserInfo user, String fixedCostCode) {
		log.debug("readActSelectItemInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCode);
		
		return new FixedCostInfoManageActSelectResponse();
	}
	
	/**
	 *<pre>
	 * 指定した支出項目に属する固定費が登録済みかどうかの判定処理
	 * 
	 * 指定した支出項目に属する固定費が登録済みかどうかを判定して返します。
	 * 登録済みの場合、true、未登録の場合はfalseになります。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCode 判定対象の支出項目コード
	 * @return 登録済みの場合、true、未登録の場合はfalse
	 *
	 */
	public boolean hasFixedCostInfoBySisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("hasFixedCostInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		return false;
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
	 * @param sisyutuItemCode 支出項目コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	public FixedCostInfoManageInitResponse readRegisteredFixedCostInfoBySisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("readRegisteredFixedCostInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		return FixedCostInfoManageInitResponse.getInstance(true);
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
		
		return new FixedCostInfoManageUpdateResponse();
	}

	/**
	 *<pre>
	 * 情報管理(固定費)更新画面情報取得(更新時)
	 * 
	 * 更新対象の固定費コードを指定して報管理(固定費)更新画面情報の表示情報を取得し画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param fixedCostCode 更新対象の固定費の固定費コード
	 * @return 情報管理(固定費)更新画面の表示情報
	 *
	 */
	public FixedCostInfoManageUpdateResponse readUpdateFixedCostInfo(LoginUserInfo user, String fixedCostCode) {
		log.debug("readUpdateFixedCostInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCode);
		
		return new FixedCostInfoManageUpdateResponse();
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
	 * @param fixedCostCode 削除対象の固定費の固定費コード
	 * @return 情報管理(固定費)処理選択画面の表示情報
	 *
	 */
	public FixedCostInfoManageActSelectResponse execDelete(LoginUserInfo user, String fixedCostCode) {
		log.debug("execDelete:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCode);
		
		return new FixedCostInfoManageActSelectResponse();
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
		
		return new FixedCostInfoManageUpdateResponse();
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
	public FixedCostInfoManageUpdateResponse execAction(LoginUserInfo user, FixedCostInfoUpdateForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		return new FixedCostInfoManageUpdateResponse();
	}

}
