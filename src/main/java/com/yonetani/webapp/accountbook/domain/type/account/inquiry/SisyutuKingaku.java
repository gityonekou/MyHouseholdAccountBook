/**
 * 「支出金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
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
 * 「支出金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SisyutuKingaku extends Money {

	// 値が0の「支出金額」項目の値
	public static final SisyutuKingaku ZERO = SisyutuKingaku.from(BigDecimal.ZERO.setScale(2));

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 支出金額
	 *
	 */
	private SisyutuKingaku(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「支出金額」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param sisyutuKingaku 支出金額
	 * @return 「支出金額」項目ドメインタイプ
	 *
	 */
	public static SisyutuKingaku from(BigDecimal sisyutuKingaku) {
		Money.validate(sisyutuKingaku, "支出金額");
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(sisyutuKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + sisyutuKingaku.intValue() + "]");
		}
		// 「支出金額」項目の値を生成して返却
		return new SisyutuKingaku(sisyutuKingaku);
	}
	
	/**
	 *<pre>
	 * 支出金額の値を指定した支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額の値
	 * @return 加算した支出金額の値(this + addValue)
	 *
	 */
	public SisyutuKingaku add(SisyutuKingaku addValue) {
		return new SisyutuKingaku(super.add(addValue));
	}

	/**
	 *<pre>
	 * 支出金額の値を指定した支出金額の値で減算(this - subtractValue)した値を返します。
	 *</pre>
	 * @param subtractValue 減算する支出金額の値
	 * @return 減算した支出金額の値(this - subtractValue)
	 *
	 */
	public SisyutuKingaku subtract(SisyutuKingaku subtractValue) {
		return new SisyutuKingaku(super.subtract(subtractValue));
	}
}
