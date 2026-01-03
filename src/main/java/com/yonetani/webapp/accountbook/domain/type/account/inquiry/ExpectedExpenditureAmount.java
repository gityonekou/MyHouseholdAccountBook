/**
 * 「支出予定金額」項目の値を表すドメインタイプです
 * リファクタリングにより、クラス名変更しました(SisyutuYoteiKingaku → ExpectedExpenditureAmount)
 * 
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
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支出予定金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpectedExpenditureAmount extends Money {
	
	/** 値が0の「支出予定金額」項目の値 */
	public static final ExpectedExpenditureAmount ZERO = ExpectedExpenditureAmount.from(Money.MONEY_ZERO);
	
	/**
	 * コンストラクタ
	 * @param value 支出予定金額
	 */
	private ExpectedExpenditureAmount(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「支出予定金額」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param amount 支出予定金額
	 * @return 「支出予定金額」項目ドメインタイプ
	 *
	 */
	public static ExpectedExpenditureAmount from(BigDecimal amount) {
		
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(amount, "支出予定金額");
		
		// ガード節(マイナス値) - 支出予定金額は0以上である必要がある
		if(BigDecimal.ZERO.compareTo(amount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「支出予定金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=%d]",
						amount.intValue()));
		}
		
		// 「支出予定金額」項目の値を生成して返却
		return new ExpectedExpenditureAmount(amount);
	}
	
	/**
	 *<pre>
	 * 支出予定金額の値を指定した支出予定金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出予定金額の値
	 * @return 加算した支出予定金額の値(this + addValue)
	 *
	 */
	public ExpectedExpenditureAmount add(ExpectedExpenditureAmount addValue) {
		return ExpectedExpenditureAmount.from(super.add(addValue));
	}
}
