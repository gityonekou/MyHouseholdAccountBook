/**
 * 収入登録操作ユースケースです。
 * ・収入の新規追加・更新・削除（セッション操作）
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/26 : 1.00.00  新規作成
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
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.ExpenditureListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.IncomeListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収入登録操作ユースケースです。
 * ・収入の新規追加・更新・削除（セッション操作）
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
public class IncomeRegistUseCase {

	private final CodeTableItemComponent codeTableItem;
	private final SisyutuItemComponent sisyutuItemComponent;

	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定し、新規追加モードで収入情報登録フォームを設定します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeAddSelect(LoginUserInfo user, String targetYearMonth,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeAddSelect:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);

		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = new IncomeItemForm();
		// アクション：新規登録
		incomeItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 収入区分名
		incomeItemForm.setIncomeKubunName("【新規追加】");

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(targetYearMonth, incomeItemForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定し、選択した収入情報の値を収入情報入力フォームに設定します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param incomeCode 収入情報入力フォームに表示する収入情報の収入コード
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeUpdateSelect(LoginUserInfo user, String targetYearMonth, String incomeCode,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeUpdateSelect:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",incomeCode=" + incomeCode);

		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = null;
		for(IncomeRegistItem session : incomeRegistItemList) {
			if(Objects.equals(incomeCode, session.getIncomeCode())) {
				// 収入入力フォームに更新対象の収入情報を設定
				incomeItemForm = new IncomeItemForm();
				// アクション：更新
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

				// hitしたのでループを抜ける
				break;
			}
		}
		// 指定した収入コードに対応する収入情報がセッションにいない場合、エラー
		if(incomeItemForm == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の収入情報がセッションに存在しません。管理者に問い合わせてください。[incomeCode=" + incomeCode + "]");
		}

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(targetYearMonth, incomeItemForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 収入情報フォーム登録ボタン押下時の入力チェックエラーの場合の画面表示情報取得
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param inputForm 収入情報入力フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeUpdateBindingErrorSetInfo(LoginUserInfo user,
			String targetYearMonth, IncomeItemForm inputForm, List<IncomeRegistItem> incomeRegistItemList,
			List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",inputForm=" + inputForm);

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(targetYearMonth, inputForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 収入情報入力フォーム登録・削除ボタン押下時の収入情報追加・更新・削除
	 *
	 * 収入情報入力フォームの入力値に従い、アクション(登録 or 更新 or 削除)を実行します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param inputForm 収入情報入力フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse execIncomeAction(LoginUserInfo user, String targetYearMonth,
			IncomeItemForm inputForm, List<IncomeRegistItem> incomeRegistItemList) {
		log.debug("execIncomeAction:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",inputForm=" + inputForm);

		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);

		// 収入金額をbigdecimalに変換
		BigDecimal incomeKingaku = DomainCommonUtils.convertKingakuBigDecimal(inputForm.getIncomeKingaku());

		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {

			// 収入情報入力フォームに収入コードを設定：(仮登録用収入コード):yyyyMMddHHmmssSSS
			inputForm.setIncomeCode(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()));

			// 収入情報をセッションに登録(データタイプ：新規を指定)
			incomeRegistItemList.add(createIncomeRegistItem(MyHouseholdAccountBookContent.DATA_TYPE_NEW, inputForm));

			// セッション情報をレスポンスに設定
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

			/* セッションに登録されている収入情報から更新対象のデータがあるかを判定し、一致するなら登録したデータで更新する */
			boolean putFlg = false;

			// セッションの収入情報の件数分繰り返し
			for(int i = 0; i < incomeRegistItemList.size() && !putFlg; i++) {

				IncomeRegistItem session = incomeRegistItemList.get(i);

				// 更新対象の収入情報の場合、値を更新
				if(Objects.equals(inputForm.getIncomeCode(), session.getIncomeCode())) {

					// フォームデータからセッションデータを作成し、対象データを更新
					incomeRegistItemList.set(i, createIncomeRegistItem(session.getDataType(), inputForm));

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
						// 注意：セッションデータを作り替えなので、createIncomeRegistItemメソッドは使用しない
						newIncomeRegistItemList.add(IncomeRegistItem.from(
								// データタイプ
								session.getDataType(),
								// アクション：削除
								MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
								// 収入コード
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
	 * @param targetYearMonth 収支を新規登録する対象年月の値
	 * @param incomeItemForm 収入情報入力フォームデータ
	 * @return 収支登録画面の表示情報
	 *
	 */
	private IncomeAndExpenditureRegistResponse createIncomeItemFormResponse(String targetYearMonth, IncomeItemForm incomeItemForm) {

		// コードテーブル情報から収入区分選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> incomeKubunList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN);
		// 収入区分選択ボックス表示情報をもとにレスポンスを生成
		if(incomeKubunList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「収入区分情報："
					+ MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(
				// 収支対象の年月(YYYMM)
				targetYearMonth,
				// 収入情報入力フォーム
				incomeItemForm,
				// 収入区分選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
				incomeKubunList.stream().map(pair ->
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toList()));

		return response;
	}

	/**
	 *<pre>
	 * セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を生成し画面情報(レスポンス情報)に設定します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param incomeRegistItemList セッションに設定されている収入登録情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出登録情報のリスト
	 * @param response 収入一覧、支出一覧画面情報(レスポンス情報)
	 *
	 */
	private void setIncomeAndExpenditureInfoList(
			UserId userId,
			List<IncomeRegistItem> incomeRegistItemList,
			List<ExpenditureRegistItem> expenditureRegistItemList,
			AbstractIncomeAndExpenditureRegistResponse response) {

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
				// セッションの収入情報から画面表示データを作成
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

		// セッションに登録されている支出情報のリストがある場合
		if(!CollectionUtils.isEmpty(expenditureRegistItemList)) {
			/* セッションに登録されている支出情報のリストを画面表示情報の支出一覧情報に変換し合計金額を設定 */
			// 画面表示情報
			List<ExpenditureListItem> expenditureList = new ArrayList<>();
			// 支出金額合計
			BigDecimal expenditureKingakuGoukei = BigDecimal.ZERO;

			// セッションに登録されている支出情報のリスト件数分繰り返す
			for(ExpenditureRegistItem session : expenditureRegistItemList) {
				/* アクションタイプに削除が設定されている場合は読み飛ばし */
				if(Objects.equals(session.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {
					continue;
				}
				/* 支出区分が無駄遣いB(2)、無駄遣いC(3)の場合、支出名の先頭に支出区分の名称を追加 */
				StringBuilder expenditureNameBuff = new StringBuilder(session.getExpenditureName().length() + 15);
				// 支出区分が無駄遣いB(2)、無駄遣いC(3)と等しい場合
				SisyutuKubun checkSisyutuKubun = SisyutuKubun.from(session.getExpenditureKubun());
				if(SisyutuKubun.isWastedB(checkSisyutuKubun) || SisyutuKubun.isWastedC(checkSisyutuKubun)) {
					// 支出区分の値をコード変換し、【支出区分コード値】の形式で設定
					expenditureNameBuff.append("【");
					expenditureNameBuff.append(codeTableItem.getCodeValue(
							// コード区分：支出区分
							MyHouseholdAccountBookContent.CODE_DEFINES_EXPENDITURE_KUBUN,
							// 支出区分
							session.getExpenditureKubun()));
					expenditureNameBuff.append("】");
				}
				// 支出名を設定
				expenditureNameBuff.append(session.getExpenditureName());

				// 支出項目コードに対応する支出項目情報を取得
				SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(userId, SisyutuItemCode.from(session.getSisyutuItemCode()));
				if(sisyutuItem == null) {
					throw new MyHouseholdAccountBookRuntimeException(
							"支出項目コードに対応する支出項目情報が存在しません。管理者に問い合わせてください。[sisyutuItemCode=" + session.getSisyutuItemCode() + "]");
				}

				/* セッションの支出情報から画面表示データを作成 */
				expenditureList.add(ExpenditureListItem.from(
						// 支出項目名(＞で区切らない値)を設定
						sisyutuItem.getSisyutuItemName().getValue(),
						// 支出コード(仮登録用支出コード)
						session.getExpenditureCode(),
						// 支出名と支出区分
						expenditureNameBuff.toString(),
						// 支出詳細
						session.getExpenditureDetailContext(),
						// 支払日(支払日が設定されている場合、日を追加)
						(StringUtils.hasLength(session.getSiharaiDate())) ? session.getSiharaiDate() + "日" : "" ,
						// 支払金額
						DomainCommonUtils.formatKingakuAndYen(session.getExpenditureKingaku())
				));
				// 支出合計金額を加算
				expenditureKingakuGoukei = expenditureKingakuGoukei.add(session.getExpenditureKingaku());
			}
			// 画面表示情報の支出一覧情報をレスポンスに設定(読み取り専用に変更)
			response.addExpenditureListInfo(expenditureList.stream().collect(Collectors.toUnmodifiableList()));
			// 支出一覧情報が1件以上の場合、合計金額を設定
			if(expenditureList.size() > 0) {
				response.setExpenditureSumKingaku(DomainCommonUtils.formatKingakuAndYen(expenditureKingakuGoukei));
			}
		}
	}

	/**
	 *<pre>
	 * 収入情報入力フォームの値からセッションに設定する収支登録情報を作成します。
	 *</pre>
	 * @param dataTypeNew データタイプ(新規 or DBロード)
	 * @param inputForm 収入情報入力フォームの値
	 * @return セッションに設定する収支登録情報
	 *
	 */
	private IncomeRegistItem createIncomeRegistItem(String dataTypeNew, IncomeItemForm inputForm) {
		// セッションに設定する収支登録情報を作成し返却
		return IncomeRegistItem.from(
				// データタイプ：新規
				dataTypeNew,
				// アクション
				inputForm.getAction(),
				// 収入コード
				inputForm.getIncomeCode(),
				// 収入区分
				inputForm.getIncomeKubun(),
				// 収入詳細
				inputForm.getIncomeDetailContext(),
				// 収入金額
				DomainCommonUtils.convertKingakuBigDecimal(inputForm.getIncomeKingaku()));
	}
}
