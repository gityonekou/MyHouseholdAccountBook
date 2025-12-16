/**
 * 収支（IncomeAndExpenditure）集約のルートエンティティです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.util.Objects;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支（IncomeAndExpenditure）集約のルートエンティティです。
 *
 * [責務]
 * ・収支情報の一貫性を保証
 * ・整合性検証のためのデータ提供
 * ・照会機能における収支情報の表現
 *
 * [設計方針]
 * ・不変性：すべてのフィールドをfinalにし、生成後は変更不可
 * ・自己完結性：収支金額の計算ロジックを内部に持つ（Phase 3以降で実装予定）
 * ・整合性保証：コンストラクタで不正な状態を拒否
 *
 * [Phase 2の責務範囲]
 * ・照会機能専用のドメインモデル
 * ・データベースから取得した値をそのまま保持
 * ・金額計算は行わない（既にDBで計算済みの値を使用）
 * ・整合性検証のためのヘルパーメソッドを提供
 *
 * [Phase 3以降の拡張計画]
 * ・登録・更新機能のサポート
 * ・金額計算ロジックの実装
 * ・IncomeAndExpenditureItemの統合
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class IncomeAndExpenditure {

	// ユーザID
	private final UserId userId;
	// 対象年月
	private final TargetYearMonth targetYearMonth;
	// 収入金額
	private final SyuunyuuKingaku incomeAmount;
	// 積立金取崩金額
	private final WithdrewKingaku withdrewAmount;
	// 支出予定金額
	private final SisyutuYoteiKingaku estimatedExpenditureAmount;
	// 支出金額
	private final SisyutuKingaku expenditureAmount;
	// 収支金額
	private final SyuusiKingaku balanceAmount;

	/**
	 *<pre>
	 * データベースから取得した収支データを再構成して集約を生成します。
	 *
	 * [使用箇所]
	 * ・照会機能でデータベースから収支情報を取得した際に使用
	 * ・リポジトリ層からドメイン層へのデータ変換
	 *
	 * [不変条件]
	 * ・userIdとtargetYearMonthは必須（null不可）
	 * ・金額項目はnullを許可（データなしの場合）
	 *
	 * [注意事項]
	 * ・Phase 2では金額計算は行わない
	 * ・データベースに保存されている値をそのまま設定
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @param incomeAmount 収入金額
	 * @param withdrewAmount 積立金取崩金額
	 * @param estimatedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param balanceAmount 収支金額
	 * @return 収支集約
	 *
	 */
	public static IncomeAndExpenditure reconstruct(
			UserId userId,
			TargetYearMonth targetYearMonth,
			SyuunyuuKingaku incomeAmount,
			WithdrewKingaku withdrewAmount,
			SisyutuYoteiKingaku estimatedExpenditureAmount,
			SisyutuKingaku expenditureAmount,
			SyuusiKingaku balanceAmount) {

		// 不変条件の検証
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(targetYearMonth, "targetYearMonth must not be null");

		return new IncomeAndExpenditure(
			userId,
			targetYearMonth,
			incomeAmount,
			withdrewAmount,
			estimatedExpenditureAmount,
			expenditureAmount,
			balanceAmount
		);
	}

	/**
	 *<pre>
	 * 空の収支集約を生成します。
	 *
	 * [使用箇所]
	 * ・指定年月のデータが存在しない場合
	 * ・データなし状態を表現する必要がある場合
	 *
	 * [注意事項]
	 * ・isEmpty()がtrueを返す状態
	 * ・すべての金額フィールドがnull
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @return 空の収支集約
	 *
	 */
	public static IncomeAndExpenditure empty(UserId userId, TargetYearMonth targetYearMonth) {
		// 不変条件の検証
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(targetYearMonth, "targetYearMonth must not be null");

		return new IncomeAndExpenditure(
			userId,
			targetYearMonth,
			null,  // incomeAmount
			null,  // withdrewAmount
			null,  // estimatedExpenditureAmount
			null,  // expenditureAmount
			null   // balanceAmount
		);
	}

	/**
	 *<pre>
	 * 収入合計金額を取得します（収入 + 積立取崩）。
	 *
	 * [使用箇所]
	 * ・整合性検証サービスで使用
	 * ・収入テーブルの合計金額との比較に使用
	 *
	 * [計算ロジック]
	 * ・収入金額 + 積立取崩金額
	 * ・nullの場合は0として扱う
	 *
	 * [Phase 2の仕様]
	 * ・データベースから取得した値を使用して計算
	 * ・整合性検証の期待値として使用
	 *</pre>
	 * @return 収入合計金額（収入 + 積立取崩）
	 *
	 */
	public SyuunyuuKingakuTotalAmount getTotalIncome() {
		SyuunyuuKingaku income = incomeAmount != null ? incomeAmount : SyuunyuuKingaku.ZERO;
		WithdrewKingaku withdrew = withdrewAmount != null ? withdrewAmount : WithdrewKingaku.ZERO;
		return SyuunyuuKingakuTotalAmount.from(income, withdrew);
	}

	/**
	 *<pre>
	 * データが存在するかどうかを判定します。
	 *
	 * [判定基準]
	 * ・収入金額がnullでない場合、データありと判定
	 *
	 * [使用箇所]
	 * ・ユースケース層でデータ存在チェックに使用
	 * ・画面表示の分岐判定に使用
	 *</pre>
	 * @return データが存在する場合はtrue、存在しない場合はfalse
	 *
	 */
	public boolean isDataExists() {
		return incomeAmount != null;
	}

	/**
	 *<pre>
	 * データが空かどうかを判定します。
	 *
	 * [判定基準]
	 * ・isDataExists()の否定
	 *
	 * [使用箇所]
	 * ・ユースケース層でデータなしチェックに使用
	 *</pre>
	 * @return データが空の場合はtrue、データがある場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return !isDataExists();
	}
}
