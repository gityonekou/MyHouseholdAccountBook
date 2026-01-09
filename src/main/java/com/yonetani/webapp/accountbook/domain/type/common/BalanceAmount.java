/**
 * 「収支金額」項目の値を表すドメインタイプです。
 * 収支金額は利用可能資金合計(「通常収入金額」項目と「積立金取崩金額」項目の合算値) - 支出金額で計算されます。
 * ※利用可能資金合計は別途定義されたTotalAvailableFundsドメインタイプを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalAvailableFunds;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収支金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・収支金額は利用可能資金合計(「通常収入金額」項目と「積立金取崩金額」項目の合算値) - 支出金額で計算されます
 * ・マイナス値も許可されます（赤字の場合）
 *
 * ※利用可能資金合計は別途定義されたTotalAvailableFundsドメインタイプを使用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class BalanceAmount extends Money {

	/** 値が0の「収支金額」項目の値 */
	public static final BalanceAmount ZERO = new BalanceAmount(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収支金額
	 *
	 */
	private BalanceAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「収支金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・スケール値が2以外
	 *
	 * 注意：収支金額はマイナス値も許可します（赤字の場合）
	 *</pre>
	 * @param value 収支金額
	 * @return 「収支金額」項目ドメインタイプ
	 *
	 */
	public static BalanceAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "収支金額");

		// 収支金額はマイナス値も許可
		return new BalanceAmount(value);
	}
	
	/**
	 *<pre>
	 * 利用可能資金合計と支出金額から収支金額を計算します。
	 *
	 * 計算式：収支金額 = 利用可能資金合計 - 支出金額
	 *
	 *
	 * [設計意図]
	 * ・単一責任の原則：BalanceAmountは収支計算のみを責務とする
	 * ・情報エキスパート：利用可能資金の構成はTotalAvailableFundsが知っている
	 * ・拡張性：将来ローンなどが追加されてもこのメソッドのシグネチャは変更不要
	 *
	 *</pre>
	 * @param availableFunds 利用可能資金合計
	 * @param expenditureAmount 支出金額
	 * @return 計算された収支金額
	 *
	 */
	public static BalanceAmount calculate(TotalAvailableFunds availableFunds, ExpenditureAmount expenditureAmount) {
		// 収支金額 = 利用可能資金合計 - 支出金額
		BigDecimal balance = availableFunds.getValue().subtract(expenditureAmount.getValue());

		// 収支金額ドメインタイプを生成して返却
		return BalanceAmount.from(balance);
	}
	
	/**
	 *<pre>
	 * 収支金額が赤字（マイナス）かどうかを判定します。
	 *</pre>
	 * @return 赤字の場合true、黒字または0の場合false
	 *
	 */
	public boolean isDeficit() {
		return this.isNegative();
	}

	/**
	 *<pre>
	 * 収支金額が黒字（プラス）かどうかを判定します。
	 *</pre>
	 * @return 黒字の場合true、赤字または0の場合false
	 *
	 */
	public boolean isSurplus() {
		return this.isPositive();
	}
}
