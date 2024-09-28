/**
 * 「収支金額」項目の値を表すドメインタイプです
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
 * 「収支金額」項目の値を表すドメインタイプです
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
public class SyuusiKingaku {
	// 収支金額
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「収支金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード]
	 * ・マイナス値：収入＜支出の場合マイナスとなるのでマイナス値は許可する
	 * [ガード節]
	 * ・null値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param syuusiKingaku 収支金額
	 * @return 「収支金額」項目ドメインタイプ
	 *
	 */
	public static SyuusiKingaku from(BigDecimal syuusiKingaku) {
		
		// ガード節(null)
		if(syuusiKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「収支金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(スケール値が2以外)
		if(syuusiKingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「収支金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + syuusiKingaku.scale() + "]");
		}
		
		// 「収支金額」項目の値を生成して返却
		return new SyuusiKingaku(syuusiKingaku);
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
