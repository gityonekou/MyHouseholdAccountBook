/**
 * 「入り金額」項目の値を表すドメインタイプです。
 * 収支のうち、入ってきたお金の合計値「収入金額と積立金取崩金額の値の合算値」となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/03/15 : 1.02.00(A)  新規作成
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
 * 「入り金額」項目の値を表すドメインタイプです。
 * 収支のうち、入ってきたお金の合計値「収入金額と積立金取崩金額の値の合算値」となります。
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
public class IncomingAmount {
	// 入り金額
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「入り金額」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・収入金額項目、積立金取崩金額項目がnull値
	 * （注意：積立金取崩金額項目の値自体はnull値を許容しています。)
	 * 
	 *</pre>
	 * @param syuunyuuKingaku 収入金額項目の値
	 * @param withdrewKingaku 積立金取崩金額項目の値
	 * @return 「入り金額」項目ドメインタイプ
	 *
	 */
	public static IncomingAmount from(SyuunyuuKingaku syuunyuuKingaku, WithdrewKingaku withdrewKingaku) {
		// ガード節(収入金額項目=null)
		if(syuunyuuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「入り金額」項目の設定値がnullです。管理者に問い合わせてください。[syuunyuuKingaku=null]");
		}
		// ガード節(収入金額項目=null)
		if(withdrewKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「入り金額」項目の設定値がnullです。管理者に問い合わせてください。[withdrewKingaku=null]");
		}
		
		// 収入金額 + 積立金取崩金額の値で「入り金額」項目の値を生成して返却
		return new IncomingAmount(syuunyuuKingaku.getValue().add(withdrewKingaku.getNullSafeValue()));
	}
	
	/**
	 *<pre>
	 * 「入り金額」項目の値から「収入金額合計」項目の値を生成して返します。
	 *</pre>
	 * @return 「収入金額合計」項目ドメインタイプ
	 *
	 */
	public SyuunyuuKingakuTotalAmount getSyuunyuuKingakuTotalAmount() {
		return SyuunyuuKingakuTotalAmount.from(value);
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
