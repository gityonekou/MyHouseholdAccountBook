/**
 * 支出登録操作ユースケースです。
 * ・支出の新規追加・更新・削除（セッション操作）
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/26 : 1.00.00  新規作成（リファクタリング対応 IncomeAndExpenditureRegistUseCaseからの分離）
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.application.usecase.common.ExpenditureItemInfoComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItem;
import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfo;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndEventCode;
import com.yonetani.webapp.accountbook.domain.repository.account.event.EventItemTableRepository;
import com.yonetani.webapp.accountbook.domain.service.account.regist.TemporaryCodeGenerator;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureSelectItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出登録操作ユースケースです。
 * ・支出の新規追加・更新・削除（セッション操作）
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
public class ExpenditureRegistUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 支出項目情報取得コンポーネント
	private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
	// 収支登録画面 収入・支出一覧情報生成コンポーネント
	private final IncomeAndExpenditureRegistListComponent registListComponent;
	// イベントテーブル:EVENT_ITEM_TABLEリポジトリー
	private final EventItemTableRepository eventRepository;

	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定し、選択した支出情報の値を支出情報入力フォームに設定します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param expenditureCode 支出情報入力フォームに表示する支出情報の支出コード
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readExpenditureUpdateSelect(
			LoginUserInfo user, String targetYearMonth, String expenditureCode,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readExpenditureUpdateSelect:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",expenditureCode=" + expenditureCode);

		// ユーザID
		UserId userId = UserId.from(user.getUserId());
		// 新規支出情報入力フォームを生成
		ExpenditureItemForm expenditureItemForm = null;
		for(ExpenditureRegistItem session : expenditureRegistItemList) {
			if(Objects.equals(expenditureCode, session.getExpenditureCode())) {

				// 支出入力フォームに更新対象の支出情報を設定
				expenditureItemForm = new ExpenditureItemForm();
				// アクション：更新
				expenditureItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
				// 支出コード
				expenditureItemForm.setExpenditureCode(session.getExpenditureCode());
				// 支出項目コード
				expenditureItemForm.setSisyutuItemCode(session.getExpenditureItemCode());
				// 支出項目名＋イベント名
				expenditureItemForm.setSisyutuItemName(getSisyutuItemNameStr(
						userId,
						ExpenditureItemCode.from(session.getExpenditureItemCode()),
						StringUtils.hasLength(session.getEventCode()) ? EventCode.from(session.getEventCode()) :  null));
				// イベントコード
				expenditureItemForm.setEventCode(session.getEventCode());
				// 支出名
				expenditureItemForm.setExpenditureName(session.getExpenditureName());
				// 支出区分
				expenditureItemForm.setExpenditureKubun(session.getExpenditureCategory());
				// 支出詳細
				expenditureItemForm.setExpenditureDetailContext(session.getExpenditureDetailContext());
				// 支払日
				if(StringUtils.hasLength(session.getSiharaiDate())) {
					expenditureItemForm.setSiharaiDate(LocalDate.parse(
							targetYearMonth + session.getSiharaiDate(), MyHouseholdAccountBookContent.DATE_TIME_FORMATTER));
				}
				// 支出金額
				expenditureItemForm.setExpenditureKingaku(DomainCommonUtils.convertInteger(session.getExpenditureKingaku()));
				// 支払金額の0円開始設定フラグ
				expenditureItemForm.setClearStartFlg(session.isClearStartFlg());
				// hitしたのでループを抜ける
				break;
			}
		}
		// 指定した支出コードに対応する支出情報がセッションにいない場合、エラー
		if(expenditureItemForm == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の支出情報がセッションに存在しません。管理者に問い合わせてください。[expenditureCode=" + expenditureCode + "]");
		}

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createExpenditureItemFormResponse(targetYearMonth, expenditureItemForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		registListComponent.setIncomeAndExpenditureInfoList(userId, incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 支出情報フォーム登録ボタン押下時の入力チェックエラーの場合の画面表示情報取得
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param inputForm 支出情報入力フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readExpenditureUpdateBindingErrorSetInfo(LoginUserInfo user,
			String targetYearMonth, ExpenditureItemForm inputForm,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readExpenditureUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",inputForm=" + inputForm);

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createExpenditureItemFormResponse(targetYearMonth, inputForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		registListComponent.setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 支出情報入力フォーム登録・削除ボタン押下時の収入情報追加・更新・削除
	 *
	 * 支出情報入力フォームの入力値に従い、アクション(登録 or 更新 or 削除)を実行します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param inputForm 支出情報入力フォームデータ
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse execExpenditureAction(LoginUserInfo user, String targetYearMonth,
			ExpenditureItemForm inputForm, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("execExpenditureAction:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",inputForm=" + inputForm);

		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			return execExpenditureAdd(targetYearMonth, inputForm, expenditureRegistItemList);
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			return execExpenditureUpdate(targetYearMonth, inputForm, expenditureRegistItemList);
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {
			return execExpenditureDelete(targetYearMonth, inputForm, expenditureRegistItemList);
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
	}

	/**
	 *<pre>
	 * 支出情報の新規仮登録処理を実行します。
	 *</pre>
	 */
	private IncomeAndExpenditureRegistResponse execExpenditureAdd(String targetYearMonth,
			ExpenditureItemForm inputForm, List<ExpenditureRegistItem> expenditureRegistItemList) {

		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);

		// 支出金額をbigdecimalに変換
		BigDecimal expenditureKingaku = DomainCommonUtils.convertKingakuBigDecimal(inputForm.getExpenditureKingaku());

		// 支出情報入力フォームに支出コードを設定：(仮登録用支出コード):yyyyMMddHHmmssSSS
		inputForm.setExpenditureCode(TemporaryCodeGenerator.generate());

		// 支出情報をセッションに登録(データタイプ：新規を指定)
		expenditureRegistItemList.add(createExpenditureRegistItem(MyHouseholdAccountBookContent.DATA_TYPE_NEW, inputForm));

		// セッション情報をレスポンスに設定
		response.setExpenditureRegistItemList(expenditureRegistItemList);

		// 完了メッセージを設定
		response.addMessage("支出情報を仮登録しました。[名称:" + inputForm.getExpenditureName() + "][金額:"
		+ DomainCommonUtils.formatKingakuAndYen(expenditureKingaku) + "]");

		// トランザクション完了
		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 支出情報の仮更新処理を実行します。
	 *</pre>
	 */
	private IncomeAndExpenditureRegistResponse execExpenditureUpdate(String targetYearMonth,
			ExpenditureItemForm inputForm, List<ExpenditureRegistItem> expenditureRegistItemList) {

		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);

		// 支出金額をbigdecimalに変換
		BigDecimal expenditureKingaku = DomainCommonUtils.convertKingakuBigDecimal(inputForm.getExpenditureKingaku());

		/* セッションに登録されている支出情報から更新対象のデータがあるかを判定し、一致するなら登録したデータで更新する */
		boolean putFlg = false;
		// セッションの支出情報の件数分繰り返し
		for(int i = 0; i < expenditureRegistItemList.size() && !putFlg; i++) {

			ExpenditureRegistItem session = expenditureRegistItemList.get(i);

			// 更新対象の支出情報の場合、値を更新
			if(Objects.equals(inputForm.getExpenditureCode(), session.getExpenditureCode())) {

				// フォームデータからセッションデータを作成し、対象データを更新
				expenditureRegistItemList.set(i, createExpenditureRegistItem(session.getDataType(), inputForm));

				// データありフラグを設定
				putFlg = true;
			}
		}

		// 対象データがない場合、エラー
		if(!putFlg) {
			throw new MyHouseholdAccountBookRuntimeException(
					"更新対象の支出情報がセッションに存在しません。管理者に問い合わせてください。[expenditureCode=" + inputForm.getExpenditureCode() + "]");
		}

		// セッション情報をレスポンスに設定
		response.setExpenditureRegistItemList(expenditureRegistItemList);

		// 完了メッセージ
		response.addMessage("支出情報を仮更新しました。[名称:" + inputForm.getExpenditureName() + "][金額:"
		+ DomainCommonUtils.formatKingakuAndYen(expenditureKingaku) + "]");

		// トランザクション完了
		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 支出情報の仮削除処理を実行します。
	 *</pre>
	 */
	private IncomeAndExpenditureRegistResponse execExpenditureDelete(String targetYearMonth,
			ExpenditureItemForm inputForm, List<ExpenditureRegistItem> expenditureRegistItemList) {

		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);

		// 支出金額をbigdecimalに変換
		BigDecimal expenditureKingaku = DomainCommonUtils.convertKingakuBigDecimal(inputForm.getExpenditureKingaku());

		// 新規のセッションに設定する支出情報リストを作成
		List<ExpenditureRegistItem> newExpenditureRegistItemList = new ArrayList<>();
		boolean deleteFlg = false;

		// セッションの支出情報の件数分繰り返し
		for(ExpenditureRegistItem session : expenditureRegistItemList) {
			// 更新対象の支出情報の場合、値を更新
			if(Objects.equals(inputForm.getExpenditureCode(), session.getExpenditureCode())) {

				// データタイプが新規追加(DB未登録)の場合、セッションからデータを削除(ここでは読み飛ばしで対応)
				// データタイプが(DBロード)の場合、セッションの対象データを削除に設定
				if(Objects.equals(session.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_LOAD)) {
					// 削除のセッションデータを登録
					// 注意：セッションデータを作り替えなので、createExpenditureRegistItemメソッドは使用しない
					newExpenditureRegistItemList.add(ExpenditureRegistItem.from(
							// データタイプ
							session.getDataType(),
							// アクション：削除
							MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
							// 支出コード
							session.getExpenditureCode(),
							// 支出項目コード
							session.getExpenditureItemCode(),
							// イベントコード
							session.getEventCode(),
							// 支出名
							session.getExpenditureName(),
							// 支出区分
							session.getExpenditureCategory(),
							// 支出詳細
							session.getExpenditureDetailContext(),
							// 支払日
							session.getSiharaiDate(),
							// 支払金額
							session.getExpenditureKingaku(),
							// 支払金額の0円開始設定フラグ
							session.isClearStartFlg()));
				}

				deleteFlg = true;

			} else {
				// データを新規のリストに追加
				newExpenditureRegistItemList.add(session);
			}
		}

		// 対象データがない場合、エラー
		if(!deleteFlg) {
			throw new MyHouseholdAccountBookRuntimeException(
					"更新対象の支出情報がセッションに存在しません。管理者に問い合わせてください。[expenditureCode=" + inputForm.getExpenditureCode() + "]");
		}

		// 新規のセッション情報をレスポンスに設定
		response.setExpenditureRegistItemList(newExpenditureRegistItemList);

		// 完了メッセージ
		response.addMessage("支出情報を仮削除しました。[名称:" + inputForm.getExpenditureName() + "][金額:"
		+ DomainCommonUtils.formatKingakuAndYen(expenditureKingaku) + "]");

		// トランザクション完了
		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 選択した支出項目コード・イベント情報をもとに新規の支出情報登録フォームを設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param inputForm 選択した支出項目・イベント情報フォームデータ
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readNewExpenditureItem(LoginUserInfo user, String targetYearMonth,
			ExpenditureSelectItemForm inputForm,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readNewExpenditureItem:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",inputForm=" + inputForm);

		// ユーザID
		UserId userId = UserId.from(user.getUserId());
		// イベント情報必須の場合、イベントコードが未設定の場合はここで予期しないエラーとして判定する
		if(inputForm.isEventCodeRequired() && !StringUtils.hasLength(inputForm.getEventCode())) {
			throw new MyHouseholdAccountBookRuntimeException(
					"イベント情報を必須選択になっていますが、対象のイベントコードが空です。管理者に問い合わせてください。[eventCode="
							+ inputForm.getEventCode() + "]");
		}
		// 選択した支出項目コードに対応する支出項目情報を取得(支出項目選択画面からの遷移の場合、nullチェックは不要とする)
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(userId, ExpenditureItemCode.from(inputForm.getSisyutuItemCode()));

		// 新規支出情報入力フォームを生成
		ExpenditureItemForm expenditureItemForm = new ExpenditureItemForm();
		// アクション：新規登録
		expenditureItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 支出項目コード
		expenditureItemForm.setSisyutuItemCode(inputForm.getSisyutuItemCode());
		// 支出項目名＋イベント名
		expenditureItemForm.setSisyutuItemName(getSisyutuItemNameStr(
				userId,
				ExpenditureItemCode.from(inputForm.getSisyutuItemCode()),
				StringUtils.hasLength(inputForm.getEventCode()) ? EventCode.from(inputForm.getEventCode()) :  null));
		// イベントコード
		expenditureItemForm.setEventCode(inputForm.getEventCode());
		// 支出名
		expenditureItemForm.setExpenditureName(expenditureItemInfo.getExpenditureItemName().getValue());
		// 支出詳細
		expenditureItemForm.setExpenditureDetailContext(expenditureItemInfo.getExpenditureItemDetailContext().getValue());
		// 支払金額の0円開始設定フラグ
		expenditureItemForm.setClearStartFlg(false);

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createExpenditureItemFormResponse(targetYearMonth, expenditureItemForm);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		registListComponent.setIncomeAndExpenditureInfoList(userId, incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 支出情報入力フォームを指定して収支登録画面の表示情報を生成します。
	 *</pre>
	 * @param targetYearMonth 収支を新規登録する対象年月の値
	 * @param expenditureItemForm 支出情報入力フォームデータ
	 * @return 収支登録画面の表示情報
	 *
	 */
	private IncomeAndExpenditureRegistResponse createExpenditureItemFormResponse(String targetYearMonth, ExpenditureItemForm expenditureItemForm) {

		// コードテーブル情報から支出区分選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> expenditureKubunList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_EXPENDITURE_KUBUN);
		// 支出区分選択ボックス表示情報をもとにレスポンスを生成
		if(expenditureKubunList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「支出区分情報："
					+ MyHouseholdAccountBookContent.CODE_DEFINES_INCOME_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(
				// 収支対象の年月(YYYMM)
				targetYearMonth,
				// 支出情報入力フォーム
				expenditureItemForm,
				// 収入区分選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
				expenditureKubunList.stream().map(pair ->
					OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toList()));

		return response;
	}

	/**
	 *<pre>
	 * 支出情報入力フォームの値からセッションに設定する支出登録情報を作成します。
	 *</pre>
	 * @param dataTypeNew データタイプ(新規 or DBロード)
	 * @param inputForm 支出情報入力フォームの値
	 * @return セッションに設定する支出登録情報
	 *
	 */
	private ExpenditureRegistItem createExpenditureRegistItem(String dataTypeNew, ExpenditureItemForm inputForm) {
		// セッションに設定する支出登録情報を作成し返却
		return ExpenditureRegistItem.from(
				// データタイプ
				dataTypeNew,
				// アクション
				inputForm.getAction(),
				// 支出コード
				inputForm.getExpenditureCode(),
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// イベントコード
				inputForm.getEventCode(),
				// 支出名
				inputForm.getExpenditureName(),
				// 支出区分
				inputForm.getExpenditureKubun(),
				// 支出詳細
				inputForm.getExpenditureDetailContext(),
				// 支払日
				DomainCommonUtils.getDateStr(inputForm.getSiharaiDate()),
				// 支払金額
				DomainCommonUtils.convertKingakuBigDecimal(inputForm.getExpenditureKingaku()),
				// 支払金額の0円開始設定フラグ
				inputForm.isClearStartFlg());
	}

	/**
	 *<pre>
	 * 画面表示する支出項目名(支出項目名(＞で区切った値)＋イベント名)の値を取得します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param expenditureItemCode 支出項目コード
	 * @param eventCode イベントコード
	 * @return 支出項目名(支出項目名＋イベント名)
	 *
	 */
	private String getSisyutuItemNameStr(UserId userId, ExpenditureItemCode expenditureItemCode, EventCode eventCode) {

		// 支出項目名を取得(＞で区切った値)
		StringBuilder sisyutuItemNameBuff = new StringBuilder();
		sisyutuItemNameBuff.append(expenditureItemInfoComponent.getExpenditureItemName(userId, expenditureItemCode));

		// イベントコードが指定されている場合、イベント名を設定
		if(eventCode != null) {
			// イベントコードに対応するイベント情報を取得
			EventItem eventItem = eventRepository.findByPrimaryKey(SearchQueryUserIdAndEventCode.from(
					userId, eventCode));
			// イベントコードに対応するイベント情報がない場合、エラー
			if(eventItem == null) {
				throw new MyHouseholdAccountBookRuntimeException("対象のイベント情報が存在しません。管理者に問い合わせてください。eventCode:" + eventCode);
			}
			sisyutuItemNameBuff.append("【");
			sisyutuItemNameBuff.append(eventItem.getEventName().getValue());
			sisyutuItemNameBuff.append("】");
		}
		return sisyutuItemNameBuff.toString();
	}
}
