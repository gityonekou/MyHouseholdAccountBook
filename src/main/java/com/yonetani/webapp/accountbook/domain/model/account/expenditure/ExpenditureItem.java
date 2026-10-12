/**
 * 支出テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/09/07 : 1.00.00                      新規作成
 * 2025/12/28 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 * 2026/10/12 : 1.02.01  feature-1.03-dev1   追加リファクタリング対応(witherパターン簡略化、createExpenditureItemのプレゼンテーション層依存解消)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.DeleteFlg;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出テーブル情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class ExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 支出コード
	private final ExpenditureCode expenditureCode;
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	// イベントコード
	private final ExpenditureEventCode expenditureEventCode;
	// 支出名称
	private final ExpenditureName expenditureName;
	// 支出区分
	private final ExpenditureCategory expenditureCategory;
	// 支出詳細
	private final ExpenditureDetailContext expenditureDetailContext;
	// 支払日
	private final PaymentDate paymentDate;
	// 支出予定金額
	private final ExpectedExpenditureAmount expectedExpenditureAmount;
	// 支出金額
	private final ExpenditureAmount expenditureAmount;
	// 支払方法コード
	private final PaymentMethodCode paymentMethodCode;
	// 削除フラグ
	private final DeleteFlg deleteFlg;

	/**
	 *<pre>
	 * 引数の値から支出テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param expenditureCode 支出コード
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureEventCode イベントコード
	 * @param expenditureName 支出名称
	 * @param expenditureCategory 支出区分
	 * @param expenditureDetailContext 支出詳細
	 * @param paymentDate 支払日
	 * @param expectedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param paymentMethodCode 支払方法コード
	 * @param deleteFlg 削除フラグ
	 * @return 支出テーブル情報を表すドメインモデル
	 *
	 */
	public static ExpenditureItem from(
			UserId userId,
			TargetYear targetYear,
			TargetMonth targetMonth,
			ExpenditureCode expenditureCode,
			ExpenditureItemCode expenditureItemCode,
			ExpenditureEventCode expenditureEventCode,
			ExpenditureName expenditureName,
			ExpenditureCategory expenditureCategory,
			ExpenditureDetailContext expenditureDetailContext,
			PaymentDate paymentDate,
			ExpectedExpenditureAmount expectedExpenditureAmount,
			ExpenditureAmount expenditureAmount,
			PaymentMethodCode paymentMethodCode,
			DeleteFlg deleteFlg) {

		// 支出テーブル情報を表すドメインモデルを生成して返却
		return new ExpenditureItem(
				// ユーザID
				userId,
				// 対象年
				targetYear,
				// 対象月
				targetMonth,
				// 支出コード
				expenditureCode,
				// 支出項目コード
				expenditureItemCode,
				// イベントコード
				expenditureEventCode,
				// 支出名称
				expenditureName,
				// 支出区分
				expenditureCategory,
				// 支出詳細
				expenditureDetailContext,
				// 支払日
				paymentDate,
				// 支出予定金額
				expectedExpenditureAmount,
				// 支出金額
				expenditureAmount,
				// 支払方法コード
				paymentMethodCode,
				// 削除フラグ
				deleteFlg);

	}
	
	/**
	 *<pre>
	 * 引数の値から支出テーブル情報(ドメイン)を生成して返します。
	 * 呼び出し元(UseCase)で、支出登録情報(セッション)から各項目をドメインタイプに変換してから渡してください
	 * (ドメイン層がプレゼンテーション層のセッションDTOに依存しないようにするため)。
	 *</pre>
	 * @param initFlg 初期登録かどうかのフラグ　(収支登録確認画面からの遷移:true／各月の収支画面の更新ボタン押下からの遷移：false)
	 * @param userId ユーザID
	 * @param yearMonthDomain 対象年月(ドメイン)
	 * @param expenditureCode 支出コード
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureEventCode イベントコード
	 * @param expenditureName 支出名称
	 * @param expenditureCategory 支出区分
	 * @param expenditureDetailContext 支出詳細
	 * @param paymentDate 支払日
	 * @param expenditureKingaku 支払金額(セッションの支出登録情報の設定値)
	 * @param clearStartFlg 支払金額の0円開始設定フラグ
	 * @param paymentMethodCode 支払方法コード
	 * @return 支出テーブル情報(ドメイン)
	 *
	 */
	public static ExpenditureItem createExpenditureItem(
			boolean initFlg,
			UserId userId,
			TargetYearMonth yearMonthDomain,
			ExpenditureCode expenditureCode,
			ExpenditureItemCode expenditureItemCode,
			ExpenditureEventCode expenditureEventCode,
			ExpenditureName expenditureName,
			ExpenditureCategory expenditureCategory,
			ExpenditureDetailContext expenditureDetailContext,
			PaymentDate paymentDate,
			ExpenditureAmount expenditureKingaku,
			boolean clearStartFlg,
			PaymentMethodCode paymentMethodCode) {

		// 支出予定金額：対象月の新規登録時のみ、支出金額の設定値を支出予定金額として設定。対象月の更新時は新規追加時を含めすべて0を設定
		ExpectedExpenditureAmount expectedExpenditureAmount = (initFlg)
				? ExpectedExpenditureAmount.from(expenditureKingaku.getValue()) : ExpectedExpenditureAmount.ZERO;
		// 支出金額：固定費の対象データが初期値0フラグが設定していある場合は支出金額は0を設定、それ以外は設定されている支出金額を設定
		ExpenditureAmount sisyutuKingaku = (clearStartFlg) ? ExpenditureAmount.ZERO : expenditureKingaku;

		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId,
				//対象年
				yearMonthDomain.getTargetYear(),
				// 対象月
				yearMonthDomain.getTargetMonth(),
				// 支出コード
				expenditureCode,
				// 支出項目コード
				expenditureItemCode,
				// イベントコード
				expenditureEventCode,
				// 支出名称
				expenditureName,
				// 支出区分
				expenditureCategory,
				// 支出詳細
				expenditureDetailContext,
				// 支払日
				paymentDate,
				// 支出予定金額
				expectedExpenditureAmount,
				// 支出金額
				sisyutuKingaku,
				// 支払方法コード
				paymentMethodCode,
				// 削除フラグ
				DeleteFlg.from(false));
	}
	
	/**
	 *<pre>
	 * 現在の支出テーブル情報(ドメイン)情報の支出金額の値に引数で指定した支出金額を加算した結果を返します。
	 *</pre>
	 * @param addValue　加算する支出金額の値
	 * @return 支出金額を加算した支出テーブル情報(ドメイン)情報
	 *
	 */
	public ExpenditureItem addSisyutuKingaku(ExpenditureAmount addValue) {
		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId,
				//対象年
				targetYear,
				// 対象月
				targetMonth,
				// 支出コード
				expenditureCode,
				// 支出項目コード
				expenditureItemCode,
				// イベントコード
				expenditureEventCode,
				// 支出名称
				expenditureName,
				// 支出区分
				expenditureCategory,
				// 支出詳細
				expenditureDetailContext,
				// 支払日
				paymentDate,
				// 支出予定金額
				expectedExpenditureAmount,
				// 支出金額
				expenditureAmount.add(addValue),
				// 支払方法コード(金額調整では変更しない。現在値のままコピー)
				paymentMethodCode,
				// 削除フラグ
				deleteFlg);

	}
	
	/**
	 *<pre>
	 * 現在の支出テーブル情報(ドメイン)情報の支出金額の値に引数で指定した支出金額を減算した結果を返します。
	 *</pre>
	 * @param subtractValue　減算する支出金額の値
	 * @return 支出金額を減算した支出テーブル情報(ドメイン)情報
	 *
	 */
	public ExpenditureItem subtractSisyutuKingaku(ExpenditureAmount subtractValue) {
		// 支出テーブル情報(ドメイン)を生成して返却
		return ExpenditureItem.from(
				// ユーザID
				userId,
				//対象年
				targetYear,
				// 対象月
				targetMonth,
				// 支出コード
				expenditureCode,
				// 支出項目コード
				expenditureItemCode,
				// イベントコード
				expenditureEventCode,
				// 支出名称
				expenditureName,
				// 支出区分
				expenditureCategory,
				// 支出詳細
				expenditureDetailContext,
				// 支払日
				paymentDate,
				// 支出予定金額
				expectedExpenditureAmount,
				// 支出金額
				expenditureAmount.subtract(subtractValue),
				// 支払方法コード(金額調整では変更しない。現在値のままコピー)
				paymentMethodCode,
				// 削除フラグ
				deleteFlg);

	}
}
