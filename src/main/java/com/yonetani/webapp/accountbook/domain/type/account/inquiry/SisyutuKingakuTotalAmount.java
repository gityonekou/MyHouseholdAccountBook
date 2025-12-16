/**
 * 「支出金額合計」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/22 : 1.00.00  新規作成
 * 2025/12/15 : 1.01.00  Money基底クラス継承に変更
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支出金額合計」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SisyutuKingakuTotalAmount extends Money {

	// 値が0の「支出金額合計」項目の値
	public static final SisyutuKingakuTotalAmount ZERO = SisyutuKingakuTotalAmount.from(BigDecimal.ZERO.setScale(2));

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 支出金額合計
	 *
	 */
	private SisyutuKingakuTotalAmount(BigDecimal value) {
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
	public static SisyutuKingakuTotalAmount from(BigDecimal totalAmount) {
		Money.validate(totalAmount, "支出金額合計");
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}
		// 「支出金額合計」項目の値を生成して返却
		return new SisyutuKingakuTotalAmount(totalAmount);
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
	public static SisyutuKingakuTotalAmount from(SisyutuKingaku expenditure) {
		// ガード節(支出金額がnull)
		if(expenditure == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額の設定値がnullです。管理者に問い合わせてください。");
		}

		// 支出金額から支出金額合計を生成して返却
		return new SisyutuKingakuTotalAmount(expenditure.getValue());
	}

	/**
	 *<pre>
	 * 支出金額合計の値を指定した支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額の値
	 * @return 加算した支出金額合計の値(this + addValue)
	 *
	 */
	public SisyutuKingakuTotalAmount add(SisyutuKingaku addValue) {
		return new SisyutuKingakuTotalAmount(super.add(addValue));
	}
}
