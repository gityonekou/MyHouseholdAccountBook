/**
 * 「収入金額合計」項目の値を表すドメインタイプです。
 * 「収入金額」項目と「積立金取崩金額」項目の合算値が「収入金額合計」項目の値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/20 : 1.00.00  新規作成
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
 * 「収入金額合計」項目の値を表すドメインタイプです。
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
public class SyuunyuuKingakuTotalAmount {
	
	// 収入金額合計
	private final BigDecimal value;
	
	// 値が0の「収入金額合計」項目の値
	public static final SyuunyuuKingakuTotalAmount ZERO = SyuunyuuKingakuTotalAmount.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「収入金額合計」項目の値を表すドメインタイプを生成します。
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「収入金額合計」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuKingakuTotalAmount from(BigDecimal totalAmount) {
		// ガード節(null)
		if(totalAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額合計」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(totalAmount.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額合計」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + totalAmount.scale() + "]");
		}
		
		// 「収入金額合計」項目の値を生成して返却
		return new SyuunyuuKingakuTotalAmount(totalAmount);
	}
	
	/**
	 *<pre>
	 * 収入金額合計の値を指定した収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収入金額の値
	 * @return 加算した収入金額合計の値(this + addValue)
	 *
	 */
	public SyuunyuuKingakuTotalAmount add(SyuunyuuKingaku addValue) {
		return new SyuunyuuKingakuTotalAmount(this.value.add(addValue.getValue()));
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
