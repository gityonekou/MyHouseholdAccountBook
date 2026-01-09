/**
 * 「支出金額合計」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(SisyutuKingakuTotalAmount → ExpenditureTotalAmount)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  リファクタリング対応(DDD適応)により新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支出金額合計」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpenditureTotalAmount extends Money {

	/** 値が0の「支出金額合計」項目の値 */
	public static final ExpenditureTotalAmount ZERO = ExpenditureTotalAmount.from(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 支出金額合計
	 *
	 */
	private ExpenditureTotalAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「支出金額合計」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「支出金額合計」項目ドメインタイプ
	 *
	 */
	public static ExpenditureTotalAmount from(BigDecimal totalAmount) {
		
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(totalAmount, "支出金額合計");
		
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}
		
		// 「支出金額合計」項目の値を生成して返却
		return new ExpenditureTotalAmount(totalAmount);
	}

	/**
	 *<pre>
	 * 支出金額から「支出金額合計」項目の値を表すドメインタイプを生成します。
	 * 単一の支出金額を合計値として扱う場合に使用します。
	 *
	 * [ガード節]
	 * ・支出金額がnull
	 *
	 *</pre>
	 * @param expenditure 支出金額
	 * @return 「支出金額合計」項目ドメインタイプ
	 *
	 */
	public static ExpenditureTotalAmount from(ExpenditureAmount expenditure) {
		// ガード節(支出金額がnull)
		if(expenditure == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額の設定値がnullです。管理者に問い合わせてください。");
		}

		// 支出金額から支出金額合計を生成して返却
		return new ExpenditureTotalAmount(expenditure.getValue());
	}

	/**
	 *<pre>
	 * 支出金額合計の値を指定した支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額の値
	 * @return 加算した支出金額合計の値(this + addValue)
	 *
	 */
	public ExpenditureTotalAmount add(ExpenditureAmount addValue) {
		return ExpenditureTotalAmount.from(super.add(addValue));
	}
}
