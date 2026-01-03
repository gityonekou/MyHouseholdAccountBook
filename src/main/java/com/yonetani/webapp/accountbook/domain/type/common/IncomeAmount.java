/**
 * 「収入金額」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入金額」項目の値を表すドメインタイプです。
 * 「収入金額」項目は「積立金取崩し金額」を含まない収入金額を表します。
 * 
 * 収支登録時に収入区分(004)の区分「1,2,4」で登録した金額が「収入金額」項目の値となります。
 *   ・給与(1)
 *   ・副業(2)
 *   ・積立からの取崩し(3) ← この収入区分は「積立金取崩金額」項目で登録されます。
 *   ・その他(4)
 *
 * [ビジネスルール]
 * ・収入金額は0以上の値である必要があります
 * ・マイナスの収入金額は許可されません
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class IncomeAmount extends Money {

	/** 値が0の「収入金額」項目の値 */
	public static final IncomeAmount ZERO = new IncomeAmount(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収入金額
	 *
	 */
	private IncomeAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「収入金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param value 収入金額
	 * @return 「収入金額」項目ドメインタイプ
	 *
	 */
	public static IncomeAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "収入金額");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「収入金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + value.intValue() + "]");
		}

		// 「収入金額」項目の値を生成して返却
		return new IncomeAmount(value);
	}
	
	/**
	 *<pre>
	 * 収支登録情報(セッション情報)の値から「収入金額」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・null値
	 *</pre>
	 * @param income 収支登録情報(セッション情報)
	 * @return 「収入金額」項目ドメインタイプ
	 *
	 */
	public static IncomeAmount from(IncomeRegistItem income) {
		// ガード節(null)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"収支登録情報(セッション情報)の設定値がnullです。管理者に問い合わせてください。");
		}
		
		// 収入区分が「積立からの取崩し(3)」の場合、
		// 積立金取崩し金額の収支登録情報(セッション情報)となるので値0の収入金額を生成して返却
		if(Objects.equals(income.getIncomeKubun(),
				MyHouseholdAccountBookContent.INCOME_KUBUN_WITHDREW_SELECTED_VALUE)) {
			return ZERO;
		}
		
		// 収支登録情報(セッション情報)の収入金額から「収入金額」項目の値を生成して返却
		return new IncomeAmount(income.getIncomeKingaku());
	}
	
	/**
	 *<pre>
	 * 収入金額の値を指定した収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収入金額の値
	 * @return 加算した収入金額の値(this + addValue)
	 *
	 */
	public IncomeAmount add(IncomeAmount addValue) {
		return IncomeAmount.from(super.add(addValue));
	}

	/**
	 *<pre>
	 * 収入金額の値を指定した収入金額の値で減算(this - subtractValue)した値を返します。
	 *
	 * 注意：結果がマイナスになる場合は例外をスローします。
	 *</pre>
	 * @param subtractValue 減算する収入金額の値
	 * @return 減算した収入金額の値(this - subtractValue)
	 *
	 */
	public IncomeAmount subtract(IncomeAmount subtractValue) {
		// 基底クラスの減算処理を実行
		BigDecimal result = super.subtract(subtractValue);

		// 減算結果がマイナスになる場合はエラー
		if(result.compareTo(BigDecimal.ZERO) < 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"収入金額の減算結果がマイナスになります。管理者に問い合わせてください。");
		}

		return IncomeAmount.from(result);
	}
}
