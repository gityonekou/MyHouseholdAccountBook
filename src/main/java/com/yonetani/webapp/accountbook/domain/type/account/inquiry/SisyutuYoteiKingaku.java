/**
 * 「支出予定金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/15 : 1.00.00  新規作成
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
 * 「支出予定金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SisyutuYoteiKingaku extends Money {
	
	// 値が0の「支出予定金額」項目の値
	public static final SisyutuYoteiKingaku ZERO = SisyutuYoteiKingaku.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 * コンストラクタ
	 * @param value 支出予定金額
	 */
	private SisyutuYoteiKingaku(BigDecimal value) {
		super(value);
	}
	
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
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(sisyutuYoteiKingaku, "支出予定金額");
		// ガード節(マイナス値) - 支出予定金額は0以上である必要がある
		if(BigDecimal.ZERO.compareTo(sisyutuYoteiKingaku) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「支出予定金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=%d]",
					sisyutuYoteiKingaku.intValue()));
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
		return new SisyutuYoteiKingaku(super.add(addValue));
	}
}
