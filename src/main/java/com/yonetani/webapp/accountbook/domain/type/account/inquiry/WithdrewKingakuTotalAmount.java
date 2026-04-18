/**
 * 「積立金取崩金額合計」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/03/13 : 1.02.00(A)  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「積立金取崩金額合計」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.02.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class WithdrewKingakuTotalAmount {
	// 積立金取崩金額合計
	private final BigDecimal value;
	
	// 値が0の「積立金取崩金額合計」項目の値
	public static final WithdrewKingakuTotalAmount ZERO = WithdrewKingakuTotalAmount.from(BigDecimal.ZERO.setScale(2));
	// 値がnullの「積立金取崩金額合計」項目の値
	public static final WithdrewKingakuTotalAmount NULL = WithdrewKingakuTotalAmount.from(null);
	
	/**
	 *<pre>
	 * 「積立金取崩金額合計」項目の値を表すドメインタイプを生成します。
	 * 
	 * [非ガード節]
	 * ・null値
	 * [ガード節]
	 * ・マイナス値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「積立金取崩金額合計」項目ドメインタイプ
	 *
	 */
	public static WithdrewKingakuTotalAmount from(BigDecimal totalAmount) {
		// 非ガード節(null)
		if(totalAmount == null) {
			// null値の「積立金取崩金額」項目の値を生成して返却(定数NULLを指定するとstatic生成時は定数NULLはnullを参照となるので使用不可)
			return new WithdrewKingakuTotalAmount(null);
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「積立金取崩金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(totalAmount.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「積立金取崩金額合計」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + totalAmount.scale() + "]");
		}
		
		// 「積立金取崩金額合計」項目の値を生成して返却
		return new WithdrewKingakuTotalAmount(totalAmount);
	}
	
	/**
	 *<pre>
	 * 積立金取崩金額合計の値を指定した積立金取崩金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収入金額の値
	 * @return 加算した積立金取崩金額合計の値(this + addValue)
	 *
	 */
	public WithdrewKingakuTotalAmount add(WithdrewKingaku addValue) {
		// 加算する収入金額の値がnullの場合、自分自身を返す
		if(addValue.getValue() == null) {
			return this;
		}
		// 自分自身のvalueがnullの場合、加算する収入金額の値で「積立金取崩金額合計」項目の値を生成
		if(this.value == null) {
			return new WithdrewKingakuTotalAmount(addValue.getValue());
		}
		// 加算した積立金取崩金額合計の値(this + addValue)
		return new WithdrewKingakuTotalAmount(this.value.add(addValue.getValue()));
	}
	
	/**
	 *<pre>
	 * 積立金取崩金額合計の値(BigDecimal)を返します。値がnullの場合、値0のBigDecimalを返します。
	 *</pre>
	 * @return 積立金取崩金額合計の値(BigDecimal)。値がnullの場合、値0のBigDecimal
	 *
	 */
	public BigDecimal getNullSafeValue() {
		// valueがnullの場合、値0の「積立金取崩金額合計」項目の値を返却
		if(value == null) {
			return WithdrewKingakuTotalAmount.ZERO.getValue();
		}
		return value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
}
