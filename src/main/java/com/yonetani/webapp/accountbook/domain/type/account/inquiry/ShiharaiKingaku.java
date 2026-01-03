/**
 * 「支払金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/03 : 1.00.00  新規作成
 * 2025/12/21 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支払金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShiharaiKingaku extends Money {
	
	/** 値が0の「支払金額」項目の値 */
	public static final ShiharaiKingaku ZERO = ShiharaiKingaku.from(Money.MONEY_ZERO);
	
	/**
	 * コンストラクタ
	 * @param value 支払金額
	 */
	private ShiharaiKingaku(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「支払金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param kingaku 支払金額
	 * @return 「支払金額」項目ドメインタイプ
	 *
	 */
	public static ShiharaiKingaku from(BigDecimal kingaku) {
		
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(kingaku, "支払金額");
		
		// ガード節(マイナス値) - 支払金額は0以上である必要がある
		if(BigDecimal.ZERO.compareTo(kingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「支払金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=%d]",
					kingaku.intValue()));
		}
		
		return new ShiharaiKingaku(kingaku);
	}
}
