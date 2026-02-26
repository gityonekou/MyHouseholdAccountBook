/**
 * 収支登録画面初期表示ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
 * ・収支登録画面の表示情報取得(更新時)
 * ・収支登録画面の収入・支出一覧再表示
 * ・内容確認ボタン押下時の入力チェックエラー処理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応（DDD適応：Phase4までの内容を反映)
 * 2026/02/26 : 1.02.00  Phase5リファクタリング：IncomeAndExpenditureRegistUseCaseからリネーム
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostShiharaiTukiList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostShiharaiTuki;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
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
 * 収支登録画面初期表示ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
 * ・収支登録画面の表示情報取得(更新時)
 * ・収支登録画面の収入・支出一覧再表示
 * ・内容確認ボタン押下時の入力チェックエラー処理
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
public class IncomeAndExpenditureInitUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	// 固定費テーブル:FIXED_COST_TABLEリポジトリー
	private final FixedCostTableRepository fixedCostRepository;
	// 収入テーブル:INCOME_TABLEリポジトリー
	private final IncomeTableRepository incomeRepository;
	// 支出テーブル:EXPENDITURE_TABLEリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	// 買い物登録時の支出項目に対応する支出テーブル情報と支出金額テーブル情報にアクセスするコンポーネント
	private final ShoppingRegistExpenditureAndSisyutuKingakuComponent checkComponent;

	/**
	 *<pre>
	 * 収支登録画面の表示情報取得(新規登録時)
	 *
	 * 引数で指定した対象年月の収支情報を新規に作成し画面表示情報に設定します。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonthStr 収支を新規登録する対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readInitInfo(LoginUserInfo user, String targetYearMonthStr) {
		log.debug("readInitInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonthStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:対象年月
		TargetYearMonth targetYearMonth = TargetYearMonth.from(targetYearMonthStr);

		/* 空の収入情報入力フォームをもとにレスポンスを生成 */
		// 新規収入情報入力フォームを生成
		IncomeItemForm incomeItemForm = new IncomeItemForm();
		// アクション：新規登録
		incomeItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 収入区分名
		incomeItemForm.setIncomeKubunName("【新規追加】");

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = createIncomeItemFormResponse(targetYearMonth.getValue(), incomeItemForm);

		/* 固定費テーブルに登録されている固定費情報から対象年月に合致する固定費一覧を取得 */
		// 対象の月の値を取得
		String month = targetYearMonth.getMonth();
		// 対象月が奇数月(20) or 偶数月(30)かを取得
		String oddEven = Integer.parseInt(month) % 2 == 1
				? MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE
						: MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE;
		List<String> shiharaiTukiList = new ArrayList<>();
		// 検索条件:毎月(00)
		shiharaiTukiList.add(MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE);
		// 検索条件:指定月
		shiharaiTukiList.add(month);
		// 検索条件:奇数月 or 偶数月
		shiharaiTukiList.add(oddEven);
		// 検索条件:任意月(40)
		shiharaiTukiList.add(MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE);

		// 指定月に一致する固定費情報を取得
		FixedCostList searchResult = fixedCostRepository.findByIdAndFixedCostShiharaiTukiList(
				SearchQueryUserIdAndFixedCostShiharaiTukiList.from(
						// ユーザID
						userId,
						// 固定費支払月(リスト値)
						shiharaiTukiList.stream().map(item -> FixedCostShiharaiTuki.from(item)).collect(Collectors.toUnmodifiableList())));

		if(searchResult.isEmpty()) {
			// 登録済み固定費情報が0件の場合、メッセージを設定
			response.addMessage("条件に一致する登録済み固定費が0件でした。");

		} else if (searchResult.getValues().size() > 999) {
			// 登録済み固定費情報が999件以上ある場合、登録不可としてメッセージを設定
			response.addMessage("条件に一致する登録済み固定費が999件以上登録されています。固定費管理画面で対象件数を999件以下にしてください。");

		} else {
			/* 登録済みの固定費一覧情報からセッションに設定する支出登録情報リストを作成 */
			/* 作成するセッションデータのリストは登録・更新・削除で変更される可能性があるので変更可のリストを指定して作成します */
			// 仮登録用支出コードの末尾3桁(999)のインクリメント用
			AtomicInteger count = new AtomicInteger(1);
			// 仮登録用支出コードの固定値部分(yyyyMMddHHmmss)
			String nowTimeStr = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

			List<ExpenditureRegistItem> expenditureRegistItemList = searchResult.getValues().stream().map(
					domain -> ExpenditureRegistItem.from(
							// データタイプ(新規)
							MyHouseholdAccountBookContent.DATA_TYPE_NEW,
							// アクション(新規追加)
							MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
							// 支出コード(仮登録用支出コード:yyyyMMddHHmmss999)
							String.format("%s%03d", nowTimeStr, count.getAndIncrement()),
							// 支出項目コード
							domain.getSisyutuItemCode().getValue(),
							// イベントコード:固定費からは登録不可:値設定が必要な場合は収支登録画面で設定する
							"",
							// 支出名
							domain.getFixedCostName().getValue(),
							// 支出区分:支出名から支出区分を取得
							SisyutuKubun.from(domain.getFixedCostName()).getValue(),
							// 支出詳細
							domain.getExpenditureDetailContext(),
							// 支払日
							domain.getFixedCostShiharaiDay().getShiharaiDayValue(targetYearMonth),
							// 支払金額
							domain.getShiharaiKingaku().getValue(),
							// 支払金額の0円開始設定フラグ
							domain.getFixedCostKubun().isClearStart()
						)).collect(Collectors.toList());

			// レスポンスにセッションの支出登録情報を設定
			response.setExpenditureRegistItemList(expenditureRegistItemList);
			// 買い物登録に必須の項目がセッションの支出登録情報に設定されているかをチェック
			checkComponent.checkExpenditureRegistItemList(expenditureRegistItemList).forEach(message -> {
				// エラーメッセージを追加
				response.addErrorMessage(message);
			});

			// セッションの支出登録情報をもとに、画面表示する支出一覧情報を設定(収入情報は未登録のためnullを設定)
			setIncomeAndExpenditureInfoList(userId, null, expenditureRegistItemList, response);
		}
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
	 * @param targetYearMonthStr 収支更新対象の対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readUpdateInfo(LoginUserInfo user, String targetYearMonthStr) {
		log.debug("readUpdateInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonthStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:対象年月
		TargetYearMonth targetYearMonth = TargetYearMonth.from(targetYearMonthStr);
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth.getValue());

		// 対象年月検索条件
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(userId, targetYearMonth);
		// 収入テーブルから対象年月の収入情報を取得
		IncomeItemInquiryList incomeList = incomeRepository.findById(search);
		// 収入情報が未登録の場合、予期しないエラー(登録必須のためエラーとする必要あり)
		if(incomeList.isEmpty()) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の収入情報が収入テーブルに存在しません。管理者に問い合わせてください。[search=" + search + "]");
		}
		// 収入テーブルから取得した収入情報をセッションの収入登録情報に変換
		// 注意：セッション情報のリストは編集可とする必要があるので、可変リストで作成する必要あり
		List<IncomeRegistItem> incomeRegistItemList = incomeList.getValues().stream().map(
				domain -> IncomeRegistItem.from(
						// データタイプ(DBロード)
						MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
						// アクション(データ更新なし)
						MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
						// 収入コード
						domain.getSyuunyuuCode().getValue(),
						// 収入区分
						domain.getSyuunyuuKubun().getValue(),
						// 収入詳細
						domain.getSyuunyuuDetailContext().getValue(),
						// 収入金額
						domain.getIncomeAmount().getValue())
				).collect(Collectors.toList());
		// レスポンスにセッションの収入登録情報を設定
		response.setIncomeRegistItemList(incomeRegistItemList);

		// 支出テーブルから対象年月の支出情報を取得
		// (過去データの場合、現在の必須データが未登録の場合ありのため、0件チェックを含めた整合性チェックは不要)
		ExpenditureItemInquiryList expenditureList = expenditureRepository.findById(search);
		// 支出テーブルから取得した支出情報をセッションの支出登録情報に変換
		// 注意：セッション情報のリストは編集可とする必要があるので、可変リストで作成する必要あり
		List<ExpenditureRegistItem> expenditureRegistItemList = expenditureList.getValues().stream().map(
				domain -> ExpenditureRegistItem.from(
						// データタイプ(DBロード)
						MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
						// アクション(データ更新なし)
						MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
						// 支出コード
						domain.getSisyutuCode().getValue(),
						// 支出項目コード
						domain.getSisyutuItemCode().getValue(),
						// イベントコード
						domain.getEventCode().getValue(),
						// 支出名
						domain.getSisyutuName().getValue(),
						// 支出区分
						domain.getSisyutuKubun().getValue(),
						// 支出詳細
						domain.getSisyutuDetailContext().getValue(),
						// 支払日(日付からDDの値を取得して設定):DBはnull可
						(domain.getPaymentDate().getValue() != null) ?
								// 値ありの場合、日付から日にちの値を取得(文字列で値を設定)
								String.format("%02d", domain.getPaymentDate().getValue().getDayOfMonth()) :
								// 値なしの場合、nullを設定
								null,
						// 支払金額
						domain.getExpenditureAmount().getValue(),
						// 支払金額の0円開始設定フラグ
						false)
				).collect(Collectors.toList());
		// レスポンスにセッションの支出登録情報を設定
		response.setExpenditureRegistItemList(expenditureRegistItemList);

		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(userId, incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * セッション情報の各収支一覧情報を画面表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readIncomeAndExpenditureInfoList(LoginUserInfo user, String targetYearMonth,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readIncomeAndExpenditureInfoList:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);
		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 内容確認ボタン押下時の入力チェックエラーの場合の画面表示情報取得
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月の値
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readRegistCheckErrorSetInfo(LoginUserInfo user, String targetYearMonth,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readRegistCheckErrorSetInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);

		// レスポンスを生成
		IncomeAndExpenditureRegistResponse response = IncomeAndExpenditureRegistResponse.getInstance(targetYearMonth);
		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);
		// メッセージを設定
		response.addMessage("収入情報が未登録です。");

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

}
