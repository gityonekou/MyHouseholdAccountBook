/**
 * 収支登録確認・完了処理ユースケースです。
 * ・収支登録内容確認画面の表示
 * ・収支登録のキャンセル
 * ・収支情報のDB登録・更新（トランザクション完了処理）
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuKingakuItemHolderComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemHolder;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.ExpenditureListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.IncomeListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistCheckResponse;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録確認・完了処理ユースケースです。
 * ・収支登録内容確認画面の表示
 * ・収支登録のキャンセル
 * ・収支情報のDB登録・更新（トランザクション完了処理）
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
public class IncomeAndExpenditureRegistConfirmUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	// 収入テーブル:INCOME_TABLEリポジトリー
	private final IncomeTableRepository incomeRepository;
	// 支出テーブル:EXPENDITURE_TABLEリポジトリー
	private final ExpenditureTableRepository expenditureRepository;
	// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEリポジトリー
	private final IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;
	// 支出金額テーブル:SISYUTU_KINGAKU_TABLEポジトリー
	private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	// 支出金額テーブル情報保持ホルダー生成用コンポーネント
	private final SisyutuKingakuItemHolderComponent sisyutuKingakuItemHolderComponent;
	// 買い物登録時の支出項目に対応する支出テーブル情報と支出金額テーブル情報にアクセスするコンポーネント
	private final ShoppingRegistExpenditureAndSisyutuKingakuComponent checkComponent;

	/**
	 *<pre>
	 * 収入一覧・支出一覧をもとに、収支登録内容確認画面表示情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param incomeRegistItemList
	 * @param expenditureRegistItemList
	 * @return　収支登録内容確認画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistCheckResponse readRegistCheckInfo(LoginUserInfo user, String targetYearMonth,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("readRegistCheckInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);

		// レスポンスを生成
		IncomeAndExpenditureRegistCheckResponse response = IncomeAndExpenditureRegistCheckResponse.getInstance(targetYearMonth);
		// セッションの収入登録情報、支出登録情報をもとに、画面表示する収入一覧情報、支出一覧情報を設定
		setIncomeAndExpenditureInfoList(UserId.from(user.getUserId()), incomeRegistItemList, expenditureRegistItemList, response);

		return response;
	}

	/**
	 *<pre>
	 * 収支登録画面の現在の登録内容をキャンセルし、各月の収支参照画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支の対象年月
	 * @param returnYearMonth 月度収支画面に戻るときに表示する対象年月
	 * @return 収支登録内容確認画面の表示情報(各月の収支参照画面にリダイレクトを設定)
	 *
	 */
	public IncomeAndExpenditureRegistCheckResponse readRegistCancelInfo(LoginUserInfo user, String targetYearMonth,
			String returnYearMonth) {
		log.debug("readRegistCancelInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",returnYearMonth=" + returnYearMonth);

		// 戻り対象の対象年月の設定値をチェック
		TargetYearMonth.from(returnYearMonth);

		// レスポンスを生成
		IncomeAndExpenditureRegistCheckResponse response = IncomeAndExpenditureRegistCheckResponse.getInstance(returnYearMonth);
		response.addMessage(String.format("%s年%s月度の収支登録をキャンセルしました。", targetYearMonth.substring(0, 4), targetYearMonth.substring(4)));
		response.setTransactionSuccessFull();

		return response;
	}

	/**
	 *<pre>
	 * 収支情報のリスト、支出情報のリストをもとに収支を登録します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonthStr 収支の対象年月の値
	 * @param incomeRegistItemList セッションに設定されている収支情報のリスト
	 * @param expenditureRegistItemList セッションに設定されている支出情報のリスト
	 * @return 収支登録内容確認画面の表示情報(各月の収支参照画面にリダイレクトを設定)
	 *
	 */
	@Transactional
	public IncomeAndExpenditureRegistCheckResponse execRegistAction(LoginUserInfo user, String targetYearMonthStr,
			List<IncomeRegistItem> incomeRegistItemList, List<ExpenditureRegistItem> expenditureRegistItemList) {
		log.debug("execRegistAction:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonthStr);

		// ユーザID
		UserId userId = UserId.from(user.getUserId());
		// 対象年月ドメインタイプ
		TargetYearMonth targetYearMonth = TargetYearMonth.from(targetYearMonthStr);
		// レスポンスを生成
		IncomeAndExpenditureRegistCheckResponse response = IncomeAndExpenditureRegistCheckResponse.getInstance(targetYearMonth.getValue());

		// セッションに登録されている収入情報のリストがない場合、予期しないエラー
		if(CollectionUtils.isEmpty(incomeRegistItemList)) {
			throw new MyHouseholdAccountBookRuntimeException("セッションの収入情報がnullか空です。管理者に問い合わせてください。[targetYearMonth="
					+ targetYearMonth + "]");
		}
		// 検索条件(ユーザID、年月度(YYYYMM))
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(userId, targetYearMonth);
		// 現在の収入テーブル情報登録件数を取得
		int incomeDataCount = incomeRepository.countById(search);
		// 初期登録かどうかのフラグ　(収支登録確認画面からの遷移:true／各月の収支画面の更新ボタン押下からの遷移：false)
		// ・初期の場合は必ず収入テーブル情報登録件数が0件となるので、0件の場合は初期登録と判断
		boolean initFlg = (incomeDataCount == 0) ? true : false;

		// 現在の支出テーブル情報登録件数を取得
		int expenditureDataCount = expenditureRepository.countById(search);

		// 収入情報更新ありの場合、収入テーブルを更新
		boolean incomeUpdateFlg = false;
		// 収入金額の初期値=0
		RegularIncomeAmount incomeAmount = RegularIncomeAmount.ZERO;
		// 積立金取崩金額の初期値=null(値なしの場合、null値となるので初期値はnull)
		WithdrawingAmount withdrawingAmount = WithdrawingAmount.NULL;
		// 収入情報の件数分繰り返す
		for(IncomeRegistItem incomeRegistData : incomeRegistItemList) {

			// アクションが変更なしの場合、収入金額合計を加算するのみ(DBデータ変更なし)
			if(Objects.equals(incomeRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE)) {

				// 収入金額を加算
				incomeAmount = incomeAmount.add(RegularIncomeAmount.from(incomeRegistData));
				// 積立金取崩金額を加算
				withdrawingAmount = withdrawingAmount.add(WithdrawingAmount.from(incomeRegistData));

			// データタイプが新規追加の場合、収入テーブルに対象データを追加
			} else if (Objects.equals(incomeRegistData.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_NEW)) {

				// セッションの収支登録情報と発番した収入コードをもとに収入テーブル情報(ドメイン)を生成
				IncomeItem addIncomeData = IncomeItem.createIncomeItem(
						// ユーザID
						userId,
						// 対象年月
						targetYearMonth,
						// 収入コード(新規発番)
						SyuunyuuCode.from(++incomeDataCount),
						// 収支登録情報(セッション)
						incomeRegistData);
				// 収入テーブルに登録
				int addCount = incomeRepository.add(addIncomeData);
				// 追加件数が1件以上の場合、業務エラー
				if(addCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("収入テーブル:INCOME_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addIncomeData + "]");
				}
				// 収入金額を加算
				incomeAmount = incomeAmount.add(RegularIncomeAmount.from(incomeRegistData));
				// 積立金取崩金額を加算
				withdrawingAmount = withdrawingAmount.add(WithdrawingAmount.from(incomeRegistData));
				// 収入情報更新あり
				incomeUpdateFlg = true;

			// データタイプがDBロードの場合
			} else if (Objects.equals(incomeRegistData.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_LOAD)) {

				// アクションが更新の場合
				if (Objects.equals(incomeRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {

					// セッションの収支登録情報から収入テーブル情報(ドメイン)を生成
					IncomeItem updIncomeData = IncomeItem.createIncomeItem(
							// ユーザID
							userId,
							// 対象年月
							targetYearMonth,
							// 収入コード(セッション)
							SyuunyuuCode.from(incomeRegistData.getIncomeCode()),
							// 収支登録情報(セッション)
							incomeRegistData);
					// 収入テーブルを更新
					int updCount = incomeRepository.update(updIncomeData);
					// 更新件数が1件以上の場合、業務エラー
					if(updCount != 1) {
						throw new MyHouseholdAccountBookRuntimeException("収入テーブル:INCOME_TABLEへの更新件数が不正でした。[件数=" + updCount + "][update data:" + updIncomeData + "]");
					}
					// 収入金額を加算
					incomeAmount = incomeAmount.add(RegularIncomeAmount.from(incomeRegistData));
					// 積立金取崩金額を加算
					withdrawingAmount = withdrawingAmount.add(WithdrawingAmount.from(incomeRegistData));
					// 収入情報更新あり
					incomeUpdateFlg = true;

				// アクションが削除の場合
				} else if (Objects.equals(incomeRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {

					// セッションの収支登録情報から収入テーブル情報(ドメイン)を生成
					IncomeItem delIncomeData = IncomeItem.createIncomeItem(
							// ユーザID
							userId,
							// 対象年月
							targetYearMonth,
							// 収入コード(セッション)
							SyuunyuuCode.from(incomeRegistData.getIncomeCode()),
							// 収支登録情報(セッション)
							incomeRegistData);
					// 収入テーブルの対象データを論理削除
					int delCount = incomeRepository.delete(delIncomeData);
					// 削除件数が1件以上の場合、業務エラー
					if(delCount != 1) {
						throw new MyHouseholdAccountBookRuntimeException("収入テーブル:INCOME_TABLEへの削除件数が不正でした。[件数=" + delCount + "][delete data:" + delIncomeData + "]");
					}
					// 収入情報更新あり
					incomeUpdateFlg = true;
				} else {
					throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。"
							+ "dataType=" + incomeRegistData.getDataType() + "action=" + incomeRegistData.getAction());
				}
			} else {
				throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。"
						+ "dataType=" + incomeRegistData.getDataType() + "action=" + incomeRegistData.getAction());
			}
		}

		// 支出情報更新ありの場合、支出テーブルを更新
		boolean expenditureUpdateFlg = false;
		// 支出予定金額
		ExpectedExpenditureAmount expectedExpenditureAmount = ExpectedExpenditureAmount.ZERO;
		// 支出金額
		ExpenditureAmount expenditureAmount = ExpenditureAmount.ZERO;
		// 対象年月の支出金額テーブル情報を保持したホルダーを生成
		SisyutuKingakuItemHolder sisyutuKingakuItemHolder = sisyutuKingakuItemHolderComponent.build(search);

		// 支出情報の件数分繰り返す
		for(ExpenditureRegistItem expenditureRegistData : expenditureRegistItemList) {

			// アクションが変更なしの場合、支出金額合計を加算するのみ(DBデータ変更なし)
			if(Objects.equals(expenditureRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE)) {
				// 支出金額を加算
				expenditureAmount = expenditureAmount.add(ExpenditureAmount.from(expenditureRegistData.getExpenditureKingaku()));

			// データタイプが新規追加の場合、支出テーブルに対象データを追加
			} else if (Objects.equals(expenditureRegistData.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_NEW)) {

				// セッションの支出登録情報と新規発番した支出コードをもとに支出テーブル情報(ドメイン)を生成
				ExpenditureItem addExpenditureData = ExpenditureItem.createExpenditureItem(
						// 初期登録かどうかのフラグ
						initFlg,
						// ユーザID
						userId,
						// 対象年月
						targetYearMonth,
						// 支出コード(新規発番)
						SisyutuCode.from(++expenditureDataCount),
						// 支出登録情報(セッション)
						expenditureRegistData);
				// 支出テーブルに登録
				int addCount = expenditureRepository.add(addExpenditureData);
				// 追加件数が1件以上の場合、業務エラー
				if(addCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("支出テーブル：EXPENDITURE_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addExpenditureData + "]");
				}
				// 支出予定金額を加算
				expectedExpenditureAmount = expectedExpenditureAmount.add(addExpenditureData.getExpectedExpenditureAmount());
				// 支出金額を加算
				expenditureAmount = expenditureAmount.add(addExpenditureData.getExpenditureAmount());

				// 支出金額テーブル情報に追加
				sisyutuKingakuItemHolder.add(addExpenditureData);
				// 支出情報更新あり
				expenditureUpdateFlg = true;

			// データタイプがDBロードの場合
			} else if (Objects.equals(expenditureRegistData.getDataType(), MyHouseholdAccountBookContent.DATA_TYPE_LOAD)) {

				// アクションが更新の場合
				if (Objects.equals(expenditureRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {

					// 支出コード(セッション)の値をもとに、更新対象の支出コードのドメインタイプを生成
					SisyutuCode sisyutuCode = SisyutuCode.from(expenditureRegistData.getExpenditureCode());
					// 更新前の支出登録情報を取得(更新後-更新前の値を計算用)
					// 値が取れない場合は該当データの更新でエラーとなるのでここではnull判定しない
					ExpenditureItem beforeExpenditureData = expenditureRepository.findByUniqueKey(
							SearchQueryUserIdAndYearMonthAndSisyutuCode.from(userId, targetYearMonth, sisyutuCode));

					// セッションの支出登録情報から支出テーブル情報(ドメイン)を生成
					ExpenditureItem updExpenditureData = ExpenditureItem.createExpenditureItem(
							// 初期登録かどうかのフラグ
							initFlg,
							// ユーザID
							userId,
							// 対象年月
							targetYearMonth,
							// 支出コード
							sisyutuCode,
							// 支出登録情報(セッション)
							expenditureRegistData);

					// 支出テーブルを更新
					int updCount = expenditureRepository.update(updExpenditureData);
					// 更新件数が1件以上の場合、業務エラー
					if(updCount != 1) {
						throw new MyHouseholdAccountBookRuntimeException("支出テーブル：EXPENDITURE_TABLEへの更新件数が不正でした。[件数=" + updCount + "][update data:" + updExpenditureData + "]");
					}
					// 支出金額を加算
					expenditureAmount = expenditureAmount.add(updExpenditureData.getExpenditureAmount());

					// 更新前・更新後の支出情報をもとに支出金額テーブル情報の情報を更新
					sisyutuKingakuItemHolder.update(beforeExpenditureData, updExpenditureData);

					// 支出情報更新あり
					expenditureUpdateFlg = true;

				// アクションが削除の場合
				} else if (Objects.equals(expenditureRegistData.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_DELETE)) {

					// 支出コード(セッション)の値をもとに、削除対象の支出コードのドメインタイプを生成
					SisyutuCode sisyutuCode = SisyutuCode.from(expenditureRegistData.getExpenditureCode());
					// 削除前の支出登録情報を取得(更新後-更新前の値を計算用)
					// 値が取れない場合は該当データの論理削除でエラーとなるのでここではnull判定しない
					ExpenditureItem beforeExpenditureData = expenditureRepository.findByUniqueKey(
							SearchQueryUserIdAndYearMonthAndSisyutuCode.from(userId, targetYearMonth, sisyutuCode));

					// セッションの支出登録情報から支出テーブル情報(ドメイン)を生成
					ExpenditureItem delExpenditureData = ExpenditureItem.createExpenditureItem(
							// 初期登録かどうかのフラグ
							initFlg,
							// ユーザID
							userId,
							// 対象年月
							targetYearMonth,
							// 支出コード
							sisyutuCode,
							// 支出登録情報(セッション)
							expenditureRegistData);
					// 支出テーブルの対象データを論理削除
					int delCount = expenditureRepository.delete(delExpenditureData);
					// 削除件数が1件以上の場合、業務エラー
					if(delCount != 1) {
						throw new MyHouseholdAccountBookRuntimeException("支出テーブル：EXPENDITURE_TABLEへの削除件数が不正でした。[件数=" + delCount + "][delete data:" + delExpenditureData + "]");
					}

					// 削除前の支出情報をもとに、対象の支出金額テーブル情報の情報を更新
					sisyutuKingakuItemHolder.delete(beforeExpenditureData);

					// 支出情報更新あり
					expenditureUpdateFlg = true;

				} else {
					throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。"
							+ "dataType=" + expenditureRegistData.getDataType() + "action=" + expenditureRegistData.getAction());
				}

			} else {
				throw new MyHouseholdAccountBookRuntimeException("未定義のデータタイプが設定されています。管理者に問い合わせてください。"
						+ "dataType=" + expenditureRegistData.getDataType() + "action=" + expenditureRegistData.getAction());
			}
		}

		// 支出情報更新ありの場合、支出金額テーブルを更新
		if(expenditureUpdateFlg) {
			// ホルダーから新規追加データのリストを取得し対象件数分データを追加
			sisyutuKingakuItemHolder.getAddList().forEach(addData -> {
				// 支出金額テーブルに登録
				int addCount = sisyutuKingakuTableRepository.add(addData);
				// 追加件数が1件以上の場合、業務エラー
				if(addCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル:SISYUTU_KINGAKU_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
				}
			});
			// ホルダーから更新データのリストを取得し対象件数分データを更新
			sisyutuKingakuItemHolder.getUpdateList().forEach(updData -> {
				// 支出金額テーブルを更新
				int updCount = sisyutuKingakuTableRepository.update(updData);
				// 更新件数が1件以上の場合、業務エラー
				if(updCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("支出金額テーブル:SISYUTU_KINGAKU_TABLEへの更新件数が不正でした。[件数=" + updCount + "][add data:" + updData + "]");
				}
			});
		}

		// 収入情報、支出情報更新ありの場合、収支テーブルを更新
		if(incomeUpdateFlg || expenditureUpdateFlg) {

			// 初期登録:収支登録確認画面からの遷移の場合、対象年月の収支テーブル情報を追加
			if(initFlg) {

				// 収支テーブル情報を作成
				IncomeAndExpenditureItem addSyuusiData = IncomeAndExpenditureItem.createAddTypeIncomeAndExpenditureItem(
						// ユーザID
						userId,
						// 対象年月
						targetYearMonth,
						// 対象月の収入金額
						incomeAmount,
						// 対象月の積立金取崩金額
						withdrawingAmount,
						// 対象月の支出予定金額
						expectedExpenditureAmount,
						// 対象月の支出金額
						expenditureAmount);
				// 収支テーブルに登録
				int addCount = incomeAndExpenditureRepository.add(addSyuusiData);
				// 追加件数が1件以上の場合、業務エラー
				if(addCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("収支テーブル:INCOME_AND_EXPENDITURE_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addSyuusiData + "]");
				}

			// 収支更新:各月の収支画面の更新ボタン押下からの遷移の場合、収支テーブルを更新
			} else {

				// 収支テーブル情報を作成
				IncomeAndExpenditureItem updSyuusiData = IncomeAndExpenditureItem.createUpdTypeIncomeAndExpenditureItem(
						// ユーザID
						userId,
						// 対象年月
						targetYearMonth,
						// 対象月の収入金額
						incomeAmount,
						// 対象月の積立金取崩金額
						withdrawingAmount,
						// 対象月の支出金額
						expenditureAmount);
				// 収支テーブルを更新
				int updCount = incomeAndExpenditureRepository.update(updSyuusiData);
				// 更新件数が1件以上の場合、業務エラー
				if(updCount != 1) {
					throw new MyHouseholdAccountBookRuntimeException("収支テーブル:INCOME_AND_EXPENDITURE_TABLEへの更新件数が不正でした。[件数=" + updCount + "][update data:" + updSyuusiData + "]");
				}
			}

			// 買い物登録に必須の項目が支出テーブル、支出金額テーブルに登録されているかをチェック
			StringBuilder msb = new StringBuilder();
			checkComponent.checkExpenditureAndSisyutuKingaku(userId, targetYearMonth).forEach(message -> {
				// エラーメッセージを追加
				msb.append(message + "\n");
			});
			if(msb.length() != 0) {
				throw new MyHouseholdAccountBookRuntimeException(msb.toString());
			}

			response.addMessage(String.format("%s年%s月度の収支情報を登録しました。", targetYearMonth.getYear(), targetYearMonth.getMonth()));

		// 収支テーブル更新なしの場合、メッセージを設定
		} else {
			response.addMessage(String.format("【注意】%s年%s月度の収支情報の変更箇所がありませんでした。", targetYearMonth.getYear(), targetYearMonth.getMonth()));
		}

		response.setTransactionSuccessFull();

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
