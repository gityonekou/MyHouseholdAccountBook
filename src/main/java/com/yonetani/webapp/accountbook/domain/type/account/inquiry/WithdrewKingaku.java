/**
 * 「積立金取崩金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/03/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「積立金取崩金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class WithdrewKingaku {
	// 積立金取崩金額
	private final BigDecimal value;
	// 値が0の「積立金取崩金額」項目の値
	public static final WithdrewKingaku ZERO = WithdrewKingaku.from(BigDecimal.ZERO.setScale(2));
	// 値がnullの「積立金取崩金額」項目の値
	public static final WithdrewKingaku NULL = WithdrewKingaku.from((BigDecimal)null);
	
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
	public static WithdrewKingaku from(BigDecimal withdrewKingaku) {
		// 非ガード節(null)
		if(withdrewKingaku == null) {
			// null値の「積立金取崩金額」項目の値を生成して返却(定数NULLを指定するとstatic生成時は定数NULLはnullを参照となるので使用不可)
			return new WithdrewKingaku(null);
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(withdrewKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「積立金取崩金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + withdrewKingaku.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(withdrewKingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「積立金取崩金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + withdrewKingaku.scale() + "]");
		}
		
		// 「積立金取崩金額」項目の値を生成して返却
		return new WithdrewKingaku(withdrewKingaku);
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
	public static WithdrewKingaku from(IncomeRegistItem income) {
		// ガード節(null)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException("収支登録情報(セッション情報)の設定値がnullです。管理者に問い合わせてください。");
		}
		// 収入区分が「積立からの取崩し(3)」の場合、収支登録情報(セッション情報)の収入金額から「積立金取崩金額」項目の値を生成して返却
		if(Objects.equals(income.getIncomeKubun(), MyHouseholdAccountBookContent.INCOME_KUBUN_WITHDREW_SELECTED_VALUE)) {
			return new WithdrewKingaku(income.getIncomeKingaku());
		}
		// 収入区分が「積立からの取崩し(3)」以外の場合、収入金額の収支登録情報(セッション情報)となるので値nullの積立金取崩金額を生成して返却
		return NULL;
	}
	
	/**
	 *<pre>
	 * 積立金取崩金額の値(BigDecimal)を返します。値がnullの場合、値0のBigDecimalを返します。
	 *</pre>
	 * @return 積立金取崩金額の値(BigDecimal)。値がnullの場合、値0のBigDecimal
	 *
	 */
	public BigDecimal getNullSafeValue() {
		// valueがnullの場合、値0の「積立金取崩金額」項目の値を返却
		if(value == null) {
			return WithdrewKingaku.ZERO.getValue();
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却(nullの場合、空文字列を返却)
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
}
