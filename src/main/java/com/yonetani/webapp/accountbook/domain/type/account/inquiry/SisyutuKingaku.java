/**
 * 「支出金額」項目の値を表すドメインタイプです
 * 
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
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
 * 「支出金額」項目の値を表すドメインタイプです
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
public class SisyutuKingaku {
	// 支出金額
	private final BigDecimal value;
	
	// 値が0の「支出金額」項目の値
	public static final SisyutuKingaku ZERO = SisyutuKingaku.from(BigDecimal.ZERO.setScale(2));
	
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
		// ガード節(null)
		if(sisyutuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(sisyutuKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + sisyutuKingaku.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(sisyutuKingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + sisyutuKingaku.scale() + "]");
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
		return new SisyutuKingaku(this.value.add(addValue.getValue()));
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
		return new SisyutuKingaku(this.value.subtract(subtractValue.getValue()));
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
