/**
 * 収支登録画面の収入・支出一覧情報を生成するコンポーネントです。
 * セッションに設定されている収入登録情報・支出登録情報をもとに、
 * 画面表示用の収入一覧・支出一覧を生成し、レスポンス情報に設定します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.ExpenditureListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractIncomeAndExpenditureRegistResponse.IncomeListItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録画面の収入・支出一覧情報を生成するコンポーネントです。
 * セッションに設定されている収入登録情報・支出登録情報をもとに、
 * 画面表示用の収入一覧・支出一覧を生成し、レスポンス情報に設定します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class IncomeAndExpenditureRegistListComponent {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;

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
	public void setIncomeAndExpenditureInfoList(
			UserId userId,
			List<IncomeRegistItem> incomeRegistItemList,
			List<ExpenditureRegistItem> expenditureRegistItemList,
			AbstractIncomeAndExpenditureRegistResponse response) {
		log.debug("setIncomeAndExpenditureInfoList:userId=" + userId);

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
								session.getIncomeCategory()),
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
				ExpenditureCategory checkSisyutuKubun = ExpenditureCategory.from(session.getExpenditureCategory());
				if(checkSisyutuKubun.isWastedBOrC()) {
					// 支出区分の値をコード変換し、【支出区分コード値】の形式で設定
					expenditureNameBuff.append("【");
					expenditureNameBuff.append(codeTableItem.getCodeValue(
							// コード区分：支出区分
							MyHouseholdAccountBookContent.CODE_DEFINES_EXPENDITURE_KUBUN,
							// 支出区分
							session.getExpenditureCategory()));
					expenditureNameBuff.append("】");
				}
				// 支出名を設定
				expenditureNameBuff.append(session.getExpenditureName());

				// 支出項目コードに対応する支出項目情報を取得
				SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(userId, ExpenditureItemCode.from(session.getExpenditureItemCode()));
				if(sisyutuItem == null) {
					throw new MyHouseholdAccountBookRuntimeException(
							"支出項目コードに対応する支出項目情報が存在しません。管理者に問い合わせてください。[sisyutuItemCode=" + session.getExpenditureItemCode() + "]");
				}

				/* セッションの支出情報から画面表示データを作成 */
				expenditureList.add(ExpenditureListItem.from(
						// 支出項目名(＞で区切らない値)を設定
						sisyutuItem.getExpenditureItemName().getValue(),
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
