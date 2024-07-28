/**
 * 収支登録ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse.IncomeListItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
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
public class IncomeAndExpenditureRegistUseCase {
	
	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	
	/**
	 *<pre>
	 * 収支登録画面の表示情報取得(新規登録時)
	 * 
	 * 引数で指定した対象年月の収支情報を新規に作成し画面表示情報に設定します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支を新規登録する対象年月の値
	 * @param returnYearMonth 月度収支画面に戻るときに表示する対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readInitInfo(
			LoginUserInfo user, String targetYearMonth, String returnYearMonth) {
		log.debug("readInitInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",returnYearMonth=" + returnYearMonth);
		
		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = new IncomeItemForm();
		// アクション：新規登録
		incomeItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 収入区分名
		incomeItemForm.setIncomeKubunName("【新規追加】");
		
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(incomeItemForm);
		
		// TODO:固定費テーブルに登録されている固定費情報から対象年月に合致する固定費一覧を取得
		//★ここから
		// TODO:固定費一覧から画面表示する支出一覧とセッション保存の支出一覧を設定
		
		return response;
	}
	
	/**
	 *<pre>
	 * 収支登録画面の表示情報取得(更新時)
	 * 
	 * 引数で指定した対象年月の収支情報を取得し画面表示情報に設定します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支更新対象の対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readUpdateInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readUpdateInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// TODO 自動生成されたメソッド・スタブ
		
		return IncomeAndExpenditureRegistResponse.getInstance();
	}
	
	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeAndExpenditureInfoList(LoginUserInfo user,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeAndExpenditureInfoList:userid=" + user.getUserId());
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance();
		// セッション情報の各収支一覧情報を画面表示情報に設定
		setIncomeAndExpenditureInfoList(incomeRegistItemList, expenditureRegistItemList, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定し、新規追加モードで収入情報登録フォームを設定します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeAddSelect(LoginUserInfo user,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeAddSelect:userid=" + user.getUserId());
		
		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = new IncomeItemForm();
		// アクション：新規登録
		incomeItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 収入区分名
		incomeItemForm.setIncomeKubunName("【新規追加】");
		
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(incomeItemForm);
		
		// セッション情報の各収支一覧情報を画面表示情報に設定
		setIncomeAndExpenditureInfoList(incomeRegistItemList, expenditureRegistItemList, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定し、選択した収入情報の値を収入情報入力フォームに設定します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param incomeCode 収入情報入力フォームに表示する収入情報の収入コード
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeUpdateSelect(LoginUserInfo user, String incomeCode,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeUpdateSelect:userid=" + user.getUserId() + ",incomeCode=" + incomeCode);
		
		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = null;
		for(IncomeRegistItem session : incomeRegistItemList) {
			if(Objects.equals(incomeCode, session.getIncomeCode())) {
				// 収入入力フォームに更新対象の収入情報を設定
				incomeItemForm = new IncomeItemForm();
				// アクション：新規登録
				incomeItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
				// 収入コード
				incomeItemForm.setIncomeCode(session.getIncomeCode());
				// 収入区分名
				StringBuilder nameBuff = new StringBuilder();
				nameBuff.append("【")
						.append(codeTableItem.getCodeValue(
							// コード区分：収入区分
							MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN,
							// 収入区分
							session.getIncomeKubun()))
						.append("】");
				incomeItemForm.setIncomeKubunName(nameBuff.toString());
				// 収入区分
				incomeItemForm.setIncomeKubun(session.getIncomeKubun());
				// 収入詳細
				incomeItemForm.setIncomeDetailContext(session.getIncomeDetailContext());
				// 収入金額
				incomeItemForm.setIncomeKingaku(DomainCommonUtils.convertInteger(session.getIncomeKingaku()));
			}
		}
		// 指定した収入コードに対応する収入情報がセッションにいない場合、エラー
		if(incomeItemForm == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の収入情報がセッションに存在しません。管理者に問い合わせてください。[incomeCode=" + incomeCode + "]");
		}
		
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(incomeItemForm);
		
		// セッション情報の各収支一覧情報を画面表示情報に設定
		setIncomeAndExpenditureInfoList(incomeRegistItemList, expenditureRegistItemList, response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 収入情報フォーム登録ボタン押下時の入力チェックエラーの場合の画面表示情報取得
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 収入情報入力フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeUpdateBindingErrorSetInfo(LoginUserInfo user, IncomeItemForm inputForm,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(inputForm);
		
		// セッション情報の各収支一覧情報を画面表示情報に設定
		setIncomeAndExpenditureInfoList(incomeRegistItemList, expenditureRegistItemList, response);
		
		return response;
	}

	/**
	 *<pre>
	 * 収入情報入力フォーム登録・削除ボタン押下時の収入情報追加・更新・削除
	 * 
	 * 収入情報入力フォームの入力値に従い、アクション(登録 or 更新 or 削除)を実行します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 収入情報入力フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse execIncomeAction(LoginUserInfo user, IncomeItemForm inputForm,
			List<IncomeRegistItem> incomeRegistItemList) {
		log.debug("execIncomeAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance();
		
		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			
			// 収入金額をbigdecimalに変換
			BigDecimal incomeKingaku = DomainCommonUtils.convertBigDecimal(inputForm.getIncomeKingaku(), 0);
			// 収入情報をセッションに登録
			incomeRegistItemList.add(IncomeRegistItem.from(
					// データタイプ：新規
					MyHouseholdAccountBookContent.DATA_TYPE_NEW,
					// アクション：新規登録
					MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
					// 収入コード(仮登録用収入コード):yyyyMMddHHmmss
					DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()),
					// 収入区分
					inputForm.getIncomeKubun(),
					// 収入詳細
					inputForm.getIncomeDetailContext(),
					// 収入金額
					incomeKingaku));
			response.setIncomeRegistItemList(incomeRegistItemList);
			
			// 完了メッセージ用に収入区分名を取得
			String kubunName = codeTableItem.getCodeValue(
					// コード区分：収入区分
					MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN,
					// 収入区分
					inputForm.getIncomeKubun());
			// 完了メッセージを設定
			response.addMessage("収入情報を仮登録しました。[収入区分:" + kubunName + "][金額:"
			+ DomainCommonUtils.formatKingakuAndYen(incomeKingaku) + "]");	
			
		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
			// 収入金額をbigdecimalに変換
			BigDecimal incomeKingaku = DomainCommonUtils.convertBigDecimal(inputForm.getIncomeKingaku(), 0);
			
			/* セッションに登録されている収入情報から更新対象のデータがあるかを判定し、一致するなら登録したデータで更新する */
			boolean putFlg = false;
			// セッションの収入情報の件数分繰り返し
			for(int i = 0; i < incomeRegistItemList.size() && !putFlg; i++) {
				IncomeRegistItem session = incomeRegistItemList.get(i);
				// 更新対象の収入情報の場合、値を更新
				if(Objects.equals(inputForm.getIncomeCode(), session.getIncomeCode())) {
					// フォームデータからセッションデータを作成
					IncomeRegistItem updData = IncomeRegistItem.from(
							// データタイプ
							session.getDataType(),
							// アクション：更新
							MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
							// 収入コード(仮登録用収入コード):yyyyMMddHHmmss
							inputForm.getIncomeCode(),
							// 収入区分
							inputForm.getIncomeKubun(),
							// 収入詳細
							inputForm.getIncomeDetailContext(),
							// 収入金額
							incomeKingaku);
					// 対象データを更新
					incomeRegistItemList.set(i, updData);
					// データありフラグを設定
					putFlg = true;
				}
			}
			// 対象データがない場合、エラー
			if(!putFlg) {
				throw new MyHouseholdAccountBookRuntimeException(
						"更新対象の収入情報がセッションに存在しません。管理者に問い合わせてください。[incomeCode=" + inputForm.getIncomeCode() + "]");
			}
			
			// レスポンスにセッションの収入情報を設定
			response.setIncomeRegistItemList(incomeRegistItemList);
			
			// 完了メッセージ用に収入区分名を取得
			String kubunName = codeTableItem.getCodeValue(
					// コード区分：収入区分
					MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN,
					// 収入区分
					inputForm.getIncomeKubun());
			
			// 完了メッセージ
			response.addMessage("収入情報を仮更新しました。[収入区分:" + kubunName + "][金額:"
			+ DomainCommonUtils.formatKingakuAndYen(incomeKingaku) + "]");	
			
		// 削除の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {
			
			// 新規のセッションに設定する収支情報リストを作成
			List<IncomeRegistItem> newIncomeRegistItemList = new ArrayList<>();
			boolean deleteFlg = false;
			// セッションの収入情報の件数分繰り返し
			for(IncomeRegistItem session : incomeRegistItemList) {
				// 更新対象の収入情報の場合、値を更新
				if(Objects.equals(inputForm.getIncomeCode(), session.getIncomeCode())) {
					
					// データタイプが新規追加(DB未登録)の場合、セッションからデータを削除(ここでは読み飛ばしで対応)
					// データタイプが(DBロード)の場合、セッションの対象データを削除に設定
					if(Objects.equals(session.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_LOAD)) {
						
						// 削除のセッションデータを登録
						newIncomeRegistItemList.add(IncomeRegistItem.from(
								// データタイプ
								session.getDataType(),
								// アクション：更新
								MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
								// 収入コード(仮登録用収入コード):yyyyMMddHHmmss
								session.getIncomeCode(),
								// 収入区分
								session.getIncomeKubun(),
								// 収入詳細
								session.getIncomeDetailContext(),
								// 収入金額
								session.getIncomeKingaku()));
					}
					
					deleteFlg = true;
				} else {
					// データを新規のリストに追加
					newIncomeRegistItemList.add(session);
				}
			}
			
			// 対象データがない場合、エラー
			if(!deleteFlg) {
				throw new MyHouseholdAccountBookRuntimeException(
						"更新対象の収入情報がセッションに存在しません。管理者に問い合わせてください。[incomeCode=" + inputForm.getIncomeCode() + "]");
			}
			
			// レスポンスにセッションの収入情報を設定
			response.setIncomeRegistItemList(newIncomeRegistItemList);
			
			// 完了メッセージ用に収入金額をbigdecimalに変換
			BigDecimal incomeKingaku = DomainCommonUtils.convertBigDecimal(inputForm.getIncomeKingaku(), 0);
			// 完了メッセージ用に収入区分名を取得
			String kubunName = codeTableItem.getCodeValue(
					// コード区分：収入区分
					MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN,
					// 収入区分
					inputForm.getIncomeKubun());
			
			// 完了メッセージ
			response.addMessage("収入情報を仮削除しました。[収入区分:" + kubunName + "][金額:"
			+ DomainCommonUtils.formatKingakuAndYen(incomeKingaku) + "]");	
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		return response;
	}
	
	/**
	 *<pre>
	 * 収入情報入力フォームを指定して収支登録画面の表示情報を生成します。
	 *</pre>
	 * @param incomeItemForm 収入情報入力フォームデータ
	 * @return 収支登録画面の表示情報
	 *
	 */
	private IncomeAndExpenditureRegistResponse createIncomeItemFormResponse(IncomeItemForm incomeItemForm) {
		
		// コードテーブル情報から収入区分選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> incomeKubunList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN);
		// 収入区分選択ボックス表示情報をもとにレスポンスを生成
		if(incomeKubunList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「収入区分情報：" 
					+ MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(
				// 収入情報入力フォーム
				incomeItemForm,
				// 収入区分選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
				incomeKubunList.stream().map(pair ->
					OptionItem.from(pair.getCode().toString(), pair.getCodeValue().toString())).collect(Collectors.toList()));
		
		return response;
	}
	
	/**
	 *<pre>
	 * セッションの収入情報、支出情報の各一覧情報を画面情報に設定します。
	 *</pre>
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @param response 収支登録画面の表示情報
	 *
	 */
	private void setIncomeAndExpenditureInfoList(
			List<IncomeRegistItem> incomeRegistItemList,
			List<ExpenditureRegistItem> expenditureRegistItemList,
			IncomeAndExpenditureRegistResponse response) {
		// セッションに登録されている収入情報のリストがある場合
		if(!CollectionUtils.isEmpty(incomeRegistItemList)) {
			/* セッションに登録されている収入情報のリストを画面表示情報の収入一覧情報に変換し合計金額を設定 */
			// 画面表示情報
			List<IncomeListItem> incomeList = new ArrayList<>();
			BigDecimal incomeKingakuGoukei = BigDecimal.ZERO;
			// セッションに登録されている収入情報のリスト件数分繰り返す
			for(IncomeRegistItem session : incomeRegistItemList) {
				// アクションタイプに削除が設定されている場合は読み飛ばし
				if(Objects.equals(session.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {
					continue;
				}
				// 画面表示情報にセッションの収入情報から画面表示データを作成
				incomeList.add(IncomeListItem.from(
						// 収入コード(仮登録用収入コード)
						session.getIncomeCode(),
						// 収入区分名
						codeTableItem.getCodeValue(
								// コード区分：収入区分
								MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN,
								// 収入区分
								session.getIncomeKubun()),
						// 収入詳細
						session.getIncomeDetailContext(),
						// 収入金額
						DomainCommonUtils.formatKingakuAndYen(session.getIncomeKingaku())
				));
				// 収入合計金額を加算
				incomeKingakuGoukei = incomeKingakuGoukei.add(session.getIncomeKingaku());
				
			}
			// 画面表示情報の収入一覧情報をレスポンスに設定(読み取り専用に変更)
			response.addIncomeListInfo(incomeList.stream().collect(Collectors.toUnmodifiableList()));
			// 収入一覧情報が1件以上の場合、合計金額を設定
			if(incomeList.size() > 0) {
				response.setIncomeSumKingaku(DomainCommonUtils.formatKingakuAndYen(incomeKingakuGoukei));
			}
		}
		if(!CollectionUtils.isEmpty(expenditureRegistItemList)) {
			response.addExpenditureListInfo(null);
		}
	}
}
