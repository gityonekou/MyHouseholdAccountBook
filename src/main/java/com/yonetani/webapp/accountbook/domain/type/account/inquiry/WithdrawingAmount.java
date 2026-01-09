/**
 * 「積立金取崩金額」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(WithdrewKingaku → WithdrawingAmount)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  リファクタリング対応(DDD適応)により新規作成
 * 
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「積立金取崩金額」項目の値を表すドメインタイプです。
 * 
 * 収支登録時に収入区分(004)の区分「3」で登録した金額が「積立金取崩金額」項目の値となります。
 *   ・給与(1) ← この収入区分は「収入金額」項目で登録されます。
 *   ・副業(2) ← この収入区分は「収入金額」項目で登録されます。
 *   ・積立からの取崩し(3)
 *   ・その他(4) ← この収入区分は「収入金額」項目で登録されます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class WithdrawingAmount extends NullableMoney {
	
	/** 値が0の「積立金取崩金額」項目の値 */
	public static final WithdrawingAmount ZERO = WithdrawingAmount.from(NullableMoney.NULLABLE_MONEY_ZERO);
	/** 値がnullの「積立金取崩金額」項目の値 */
	public static final WithdrawingAmount NULL = WithdrawingAmount.from((BigDecimal)null);
	
	/**
	 * コンストラクタ
	 * @param value 積立金取崩金額
	 */
	private WithdrawingAmount(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「積立金取崩金額」項目の値を表すドメインタイプを生成します
	 *
	 * [非ガード節]
	 * ・null値
	 * [ガード節]
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param withdrewKingaku 積立金取崩金額
	 * @return 「積立金取崩金額」項目ドメインタイプ
	 *
	 */
	public static WithdrawingAmount from(BigDecimal withdrewKingaku) {
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(withdrewKingaku, "積立金取崩金額");
		
		// 「積立金取崩金額」項目の値を生成して返却
		return new WithdrawingAmount(withdrewKingaku);
	}

	/**
	 *<pre>
	 * 収支登録情報(セッション情報)の値から「積立金取崩金額」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・null値
	 *</pre>
	 * @param income 収支登録情報(セッション情報)
	 * @return 「積立金取崩金額」項目ドメインタイプ
	 *
	 */
	public static WithdrawingAmount from(IncomeRegistItem income) {
		// ガード節(null)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException("収支登録情報(セッション情報)の設定値がnullです。管理者に問い合わせてください。");
		}
		
		// 収入区分が「積立からの取崩し(3)」の場合、収支登録情報(セッション情報)の収入金額から「積立金取崩金額」項目の値を生成して返却
		if(Objects.equals(income.getIncomeKubun(), MyHouseholdAccountBookContent.INCOME_KUBUN_WITHDREW_SELECTED_VALUE)) {
			return new WithdrawingAmount(income.getIncomeKingaku());
		}
		
		// 収入区分が「積立からの取崩し(3)」以外の場合、収入金額の収支登録情報(セッション情報)となるので値nullの積立金取崩金額を生成して返却
		return NULL;
	}
	
	/**
	 *<pre>
	 * 積立金取崩金額の値を指定した積立金取崩金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する積立金取崩金額の値
	 * @return 加算した積立金取崩金額の値(this + addValue)
	 *
	 */
	public WithdrawingAmount add(WithdrawingAmount addValue) {
		return WithdrawingAmount.from(super.add(addValue));
	}
}
