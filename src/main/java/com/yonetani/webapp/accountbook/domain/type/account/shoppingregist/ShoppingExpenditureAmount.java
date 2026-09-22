/**
 * 買い物カテゴリごとの「購入金額」項目の値を表すドメインタイプです。
 * 「食料品(必須)」項目、「食料品B(無駄遣い)」項目など、買い物カテゴリごとの購入金額の値で使用します。
 * 
 * 注意：買い物した品物単品ではなく、上記カテゴリごとの購入金額総額の値が格納されるので、購入金額ではなく、(ExpenditureAmount:支出金額)がより正しいドメイン名となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/20 : 1.00.00  feature-1.03-dev1   追加リファクタリングにより新規作成（買い物登録の買い物カテゴリ毎にあった購入金額項目を統合）
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 買い物カテゴリごとの「購入金額」項目の値を表すドメインタイプです。
 * 「食料品(必須)」項目、「食料品B(無駄遣い)」項目など、買い物のカテゴリごとの購入金額の値で使用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShoppingExpenditureAmount extends NullableMoney {
	
	/** 値がnullの「購入金額」項目の値 */
	public static ShoppingExpenditureAmount NULL = ShoppingExpenditureAmount.from((BigDecimal)null);
	
	/**
	 *<pre>
	 * ShoppingExpenditureAmountクラスコンストラクターです。
	 *</pre>
	 * @param value 買い物購入金額
	 *
	 */
	private ShoppingExpenditureAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「買い物購入金額」項目の値を表すドメインタイプを生成します
	 *
	 * [非ガード節]
	 * ・買い物購入金額がnull値
	 * [ガード節]
	 * ・買い物購入金額がマイナス値
	 * ・買い物購入金額のスケール値が2以外
	 *
	 *</pre>
	 * @param amount 買い物購入金額
	 * @return 「買い物購入金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingExpenditureAmount from(BigDecimal amount) {
		
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(amount, "買い物購入金額");
		
		// 買い物購入金額項目ドメインタイプを生成
		return new ShoppingExpenditureAmount(amount);
	}
	
	/**
	 *<pre>
	 * Integer値から「買い物購入金額」項目の値を表すドメインタイプを生成します。
	 * フォームの入力値(Integer値)から「買い物購入金額」項目の値を生成する場合に使用します。
	 *
	 * [非ガード節]
	 * ・非ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 * [ガード節]
	 * ・ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 *
	 *</pre>
	 * @param intAmount 買い物購入金額入力値
	 * @return 「買い物購入金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingExpenditureAmount from(Integer intAmount) {
		// 買い物購入金額(変換値)
		BigDecimal amount = null;
		
		// 買い物購入金額入力値ありの場合、Integer値をBigDecimalに変換
		if(intAmount != null) {
			// 整数の値に指定したスケール分0を追加
			String numStr = intAmount.toString() + ".00";
			// 整数の値を文字列変換
			amount = new BigDecimal(numStr.toString());
			// スケールを2に設定、小数点以下は切り捨て（HALF_DOWN）で丸める(0.5以上は切り上げ、0.5未満は切り捨て)
			amount.setScale(2, RoundingMode.HALF_DOWN);
		}
		
		// 買い物購入金額項目ドメインタイプを生成
		return ShoppingExpenditureAmount.from(amount);
	}
	
	/**
	 *<pre>
	 * 買い物購入金額の値を指定した買い物購入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する買い物購入金額の値
	 *
	 * @return 加算した買い物購入金額の値(this + addValue)
	 *
	 */
	public ShoppingExpenditureAmount add(ShoppingExpenditureAmount addValue) {
		return ShoppingExpenditureAmount.from(super.add(addValue));
	}
}
