/**
 * 「収入金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/15 : 1.00.00  新規作成
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
 * 「収入金額」項目の値を表すドメインタイプです
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
public class SyuunyuuKingaku {
	// 収入金額
	private final BigDecimal value;
	// 値が0の「収入金額」項目の値
	public static final SyuunyuuKingaku ZERO = SyuunyuuKingaku.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「収入金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param syuunyuuKingaku 収入金額
	 * @return 「収入金額」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuKingaku from(BigDecimal syuunyuuKingaku) {
		// ガード節(null)
		if(syuunyuuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(syuunyuuKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + syuunyuuKingaku.intValue() + "]");
		}
		// ガード節(スケール値が2以外)
		if(syuunyuuKingaku.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額」項目のスケール値が不正です。管理者に問い合わせてください。[scale=" + syuunyuuKingaku.scale() + "]");
		}
		
		// 「収入金額」項目の値を生成して返却
		return new SyuunyuuKingaku(syuunyuuKingaku);
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
	public static SyuunyuuKingaku from(IncomeRegistItem income) {
		// ガード節(null)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException("収支登録情報(セッション情報)の設定値がnullです。管理者に問い合わせてください。");
		}
		// 収入区分が「積立からの取崩し(3)」の場合、積立金取崩し金額の収支登録情報(セッション情報)となるので値0の収入金額を生成して返却
		if(Objects.equals(income.getIncomeKubun(), MyHouseholdAccountBookContent.INCOME_KUBUN_WITHDREW_SELECTED_VALUE)) {
			return ZERO;
		}
		// 収支登録情報(セッション情報)の収入金額から「収入金額」項目の値を生成して返却
		return new SyuunyuuKingaku(income.getIncomeKingaku());
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
