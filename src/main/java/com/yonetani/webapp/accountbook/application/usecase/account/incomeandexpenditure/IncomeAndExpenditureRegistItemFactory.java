/**
 * 収支登録画面のセッション登録情報(ExpenditureRegistItem/IncomeRegistItem)を生成するコンポーネントです。
 * Form・ドメインモデル・既存のセッション登録情報のいずれかを起点として、各UseCaseに分散していた
 * ExpenditureRegistItem.from()/IncomeRegistItem.from()の呼び出し(VOの生成を含む)をこのクラスに集約します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/11/08 : 1.00.00  feature-1.03-dev1   新規作成(セッションDTO生成処理の集約。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.model.account.income.IncomeItem;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeCategory;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録画面のセッション登録情報(ExpenditureRegistItem/IncomeRegistItem)を生成するコンポーネントです。
 * Form・ドメインモデル・既存のセッション登録情報のいずれかを起点として、各UseCaseに分散していた
 * ExpenditureRegistItem.from()/IncomeRegistItem.from()の呼び出し(VOの生成を含む)をこのクラスに集約します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Component
@Log4j2
public class IncomeAndExpenditureRegistItemFactory {

	/**
	 *<pre>
	 * 固定費情報(ドメインモデル)から、新規登録用の支出登録情報(セッション)を生成します。
	 * データタイプ・アクションは固定費からの新規引き継ぎ(新規／追加)に固定して設定します。
	 *</pre>
	 * @param targetYearMonth 収支の対象年月(支払日の算出に使用)
	 * @param expenditureCode 支出コード(仮登録用支出コード)
	 * @param fixedCost 固定費情報(ドメインモデル)
	 * @return 支出登録情報(セッション)
	 *
	 */
	public ExpenditureRegistItem createExpenditureRegistItemFromFixedCost(
			TargetYearMonth targetYearMonth, String expenditureCode, FixedCost fixedCost) {
		return ExpenditureRegistItem.from(
				// データタイプ(新規)
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				// アクション(新規追加)
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				// 支出コード(仮登録用支出コード:yyyyMMddHHmmss999)
				expenditureCode,
				// 支出項目コード
				fixedCost.getExpenditureItemCode(),
				// イベントコード:固定費からは登録不可:値設定が必要な場合は収支登録画面で設定する
				ExpenditureEventCode.NULL,
				// 支出名(固定費名を支出名として引き継ぐ。FixedCostNameとExpenditureNameは別のドメイン型のため変換が必要)
				ExpenditureName.from(fixedCost.getFixedCostName().getValue()),
				// 支出区分:固定費名から支出区分を取得
				ExpenditureCategory.from(fixedCost.getFixedCostName()),
				// 支出詳細(FixedCost.getExpenditureDetailContext()はString(合成値)を返すため変換が必要)
				ExpenditureDetailContext.from(fixedCost.getExpenditureDetailContext()),
				// 支払日
				fixedCost.getFixedCostPaymentDay().getShiharaiDayValue(targetYearMonth),
				// 支払方法コード(固定費から引き継ぐ。買い物集計8項目はここで「支払方法がない」が伝播する)
				fixedCost.getPaymentMethodCode(),
				// 支払金額(FixedCostPaymentAmountとExpenditureAmountは別のドメイン型のため変換が必要)
				ExpenditureAmount.from(fixedCost.getFixedCostPaymentAmount().getValue()),
				// 支払金額の0円開始設定フラグ
				fixedCost.getFixedCostKubun().isClearStart());
	}

	/**
	 *<pre>
	 * 支出テーブル情報(ドメインモデル)から、更新対象読込用の支出登録情報(セッション)を生成します。
	 * データタイプ・アクションはDBロード・更新なしに固定して設定します。
	 *</pre>
	 * @param expenditureItem 支出テーブル情報(ドメインモデル)
	 * @return 支出登録情報(セッション)
	 *
	 */
	public ExpenditureRegistItem createExpenditureRegistItemFromExpenditureItem(ExpenditureItem expenditureItem) {
		return ExpenditureRegistItem.from(
				// データタイプ(DBロード)
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				// アクション(データ更新なし)
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				// 支出コード
				expenditureItem.getExpenditureCode().getValue(),
				// 支出項目コード
				expenditureItem.getExpenditureItemCode(),
				// イベントコード
				expenditureItem.getExpenditureEventCode(),
				// 支出名
				expenditureItem.getExpenditureName(),
				// 支出区分
				expenditureItem.getExpenditureCategory(),
				// 支出詳細
				expenditureItem.getExpenditureDetailContext(),
				// 支払日(日付からDDの値を取得して設定):DBはnull可
				expenditureItem.getPaymentDate().toDayValue(),
				// 支払方法コード
				expenditureItem.getPaymentMethodCode(),
				// 支払金額
				expenditureItem.getExpenditureAmount(),
				// 支払金額の0円開始設定フラグ
				false);
	}

	/**
	 *<pre>
	 * 支出情報入力フォームの値から、セッションに設定する支出登録情報を生成します。
	 *</pre>
	 * @param dataType データタイプ(新規 or DBロード。呼び出し元の登録・更新文脈に応じて指定)
	 * @param inputForm 支出情報入力フォームの値
	 * @return 支出登録情報(セッション)
	 *
	 */
	public ExpenditureRegistItem createExpenditureRegistItemFromForm(String dataType, ExpenditureItemForm inputForm) {
		return ExpenditureRegistItem.from(
				// データタイプ
				dataType,
				// アクション
				inputForm.getAction(),
				// 支出コード
				inputForm.getExpenditureCode(),
				// 支出項目コード
				ExpenditureItemCode.from(inputForm.getSisyutuItemCode()),
				// イベントコード
				ExpenditureEventCode.from(inputForm.getEventCode()),
				// 支出名
				ExpenditureName.from(inputForm.getExpenditureName()),
				// 支出区分
				ExpenditureCategory.from(inputForm.getExpenditureKubun()),
				// 支出詳細
				ExpenditureDetailContext.from(inputForm.getExpenditureDetailContext()),
				// 支払日
				DomainCommonUtils.getDateStr(inputForm.getSiharaiDate()),
				// 支払方法コード
				PaymentMethodCode.from(inputForm.getPaymentMethodCode()),
				// 支払金額
				ExpenditureAmount.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getExpenditureKingaku())),
				// 支払金額の0円開始設定フラグ
				inputForm.isClearStartFlg());
	}

	/**
	 *<pre>
	 * 既存の支出登録情報(セッション)から、アクションを削除に変更した支出登録情報を生成します。
	 * データタイプ・支出コード以下の各項目値は引数の支出登録情報の設定値をそのまま引き継ぎます。
	 *</pre>
	 * @param session 削除対象の支出登録情報(セッション)
	 * @return アクションを削除に変更した支出登録情報(セッション)
	 *
	 */
	public ExpenditureRegistItem createDeletedExpenditureRegistItem(ExpenditureRegistItem session) {
		return ExpenditureRegistItem.from(
				// データタイプ
				session.getDataType(),
				// アクション：削除
				MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				// 支出コード
				session.getExpenditureCode(),
				// 支出項目コード
				ExpenditureItemCode.from(session.getExpenditureItemCode()),
				// イベントコード
				ExpenditureEventCode.from(session.getEventCode()),
				// 支出名
				ExpenditureName.from(session.getExpenditureName()),
				// 支出区分
				ExpenditureCategory.from(session.getExpenditureCategory()),
				// 支出詳細
				ExpenditureDetailContext.from(session.getExpenditureDetailContext()),
				// 支払日
				session.getSiharaiDate(),
				// 支払方法コード
				PaymentMethodCode.from(session.getPaymentMethodCode()),
				// 支払金額
				ExpenditureAmount.from(session.getExpenditureKingaku()),
				// 支払金額の0円開始設定フラグ
				session.isClearStartFlg());
	}

	/**
	 *<pre>
	 * 収入テーブル情報(ドメインモデル)から、更新対象読込用の収入登録情報(セッション)を生成します。
	 * データタイプ・アクションはDBロード・更新なしに固定して設定します。
	 *</pre>
	 * @param incomeItem 収入テーブル情報(ドメインモデル)
	 * @return 収入登録情報(セッション)
	 *
	 */
	public IncomeRegistItem createIncomeRegistItemFromIncomeItem(IncomeItem incomeItem) {
		return IncomeRegistItem.from(
				// データタイプ(DBロード)
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				// アクション(データ更新なし)
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				// 収入コード
				incomeItem.getIncomeCode().getValue(),
				// 収入区分
				incomeItem.getIncomeCategory(),
				// 収入詳細
				incomeItem.getIncomeDetailContext(),
				// 収入金額
				incomeItem.getIncomeAmount());
	}

	/**
	 *<pre>
	 * 収入情報入力フォームの値から、セッションに設定する収入登録情報を生成します。
	 *</pre>
	 * @param dataType データタイプ(新規 or DBロード。呼び出し元の登録・更新文脈に応じて指定)
	 * @param inputForm 収入情報入力フォームの値
	 * @return 収入登録情報(セッション)
	 *
	 */
	public IncomeRegistItem createIncomeRegistItemFromForm(String dataType, IncomeItemForm inputForm) {
		return IncomeRegistItem.from(
				// データタイプ
				dataType,
				// アクション
				inputForm.getAction(),
				// 収入コード
				inputForm.getIncomeCode(),
				// 収入区分
				IncomeCategory.from(inputForm.getIncomeKubun()),
				// 収入詳細
				IncomeDetailContext.from(inputForm.getIncomeDetailContext()),
				// 収入金額
				IncomeAmount.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getIncomeKingaku())));
	}

	/**
	 *<pre>
	 * 既存の収入登録情報(セッション)から、アクションを削除に変更した収入登録情報を生成します。
	 * データタイプ・収入コード以下の各項目値は引数の収入登録情報の設定値をそのまま引き継ぎます。
	 *</pre>
	 * @param session 削除対象の収入登録情報(セッション)
	 * @return アクションを削除に変更した収入登録情報(セッション)
	 *
	 */
	public IncomeRegistItem createDeletedIncomeRegistItem(IncomeRegistItem session) {
		return IncomeRegistItem.from(
				// データタイプ
				session.getDataType(),
				// アクション：削除
				MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				// 収入コード
				session.getIncomeCode(),
				// 収入区分
				IncomeCategory.from(session.getIncomeCategory()),
				// 収入詳細
				IncomeDetailContext.from(session.getIncomeDetailContext()),
				// 収入金額
				IncomeAmount.from(session.getIncomeKingaku()));
	}
}
