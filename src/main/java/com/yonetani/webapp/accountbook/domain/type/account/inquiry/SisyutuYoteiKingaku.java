/**
 * 「支出予定金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/15 : 1.00.00  新規作成
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
 * 「支出予定金額」項目の値を表すドメインタイプです
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
public class SisyutuYoteiKingaku {
	// 支出予定金額
	private final BigDecimal value;
	// 値が0の「支出予定金額」項目の値
	public static final SisyutuYoteiKingaku ZERO = SisyutuYoteiKingaku.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「支出予定金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @return 「支出予定金額」項目ドメインタイプ
	 *
	 */
	public static SisyutuYoteiKingaku from(BigDecimal sisyutuYoteiKingaku) {
		// ガード節(null)
		if(sisyutuYoteiKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出予定金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(sisyutuYoteiKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出予定金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + sisyutuYoteiKingaku.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(sisyutuYoteiKingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出予定金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + sisyutuYoteiKingaku.scale() + "]");
		}
		
		// 「支出予定金額」項目の値を生成して返却
		return new SisyutuYoteiKingaku(sisyutuYoteiKingaku);
	}
	
	/**
	 *<pre>
	 * 支出予定金額の値を指定した支出予定金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出予定金額の値
	 * @return 加算した支出予定金額の値(this + addValue)
	 *
	 */
	public SisyutuYoteiKingaku add(SisyutuYoteiKingaku addValue) {
		return new SisyutuYoteiKingaku(this.value.add(addValue.getValue()));
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
