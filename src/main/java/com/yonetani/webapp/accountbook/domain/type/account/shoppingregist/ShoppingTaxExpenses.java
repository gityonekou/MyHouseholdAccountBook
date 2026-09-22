/**
 * 買い物カテゴリごとの「消費税」項目の値を表すドメインタイプです。
 * 「食料品(必須)」項目、「食料品B(無駄遣い)」項目など、買い物カテゴリごとの消費税の値で使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/20 : 1.00.00  feature-1.03-dev1   追加リファクタリングにより新規作成（買い物登録の買い物カテゴリ毎にあった消費税項目を統合）
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 買い物カテゴリごとの「消費税」項目の値を表すドメインタイプです。
 * 「食料品(必須)」項目、「食料品B(無駄遣い)」項目など、買い物カテゴリごとの消費税の値で使用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShoppingTaxExpenses extends NullableMoney {

	/** 値がnullの「消費税」項目の値 */
	public static ShoppingTaxExpenses NULL = ShoppingTaxExpenses.from((BigDecimal)null);
	
	/**
	 *<pre>
	 * ShoppingTaxExpensesクラスコンストラクターです。
	 *</pre>
	 * @param value 消費税
	 *
	 */
	private ShoppingTaxExpenses(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「消費税」項目の値を表すドメインタイプを生成します
	 *
	 * [非ガード節]
	 * ・消費税がnull値
	 * [ガード節]
	 * ・消費税がマイナス値
	 * ・消費税のスケール値が2以外
	 *
	 *</pre>
	 * @param amount 消費税
	 * @return 「消費税」項目ドメインタイプ
	 *
	 */
	public static ShoppingTaxExpenses from(BigDecimal amount) {
		
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(amount, "消費税金額");
		
		// 消費税項目ドメインタイプを生成
		return new ShoppingTaxExpenses(amount);
	}
	
	/**
	 *<pre>
	 * Integer値から「消費税」項目の値を表すドメインタイプを生成します。
	 * フォームの入力値(Integer値)から「消費税」項目の値を生成する場合に使用します。
	 *
	 * [非ガード節]
	 * ・非ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 * [ガード節]
	 * ・ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 *
	 *</pre>
	 * @param intAmount 消費税入力値
	 * @return 「消費税」項目ドメインタイプ
	 *
	 */
	public static ShoppingTaxExpenses from(Integer intAmount) {
		// 消費税(変換値)
		BigDecimal amount = null;
		
		// 消費税入力値ありの場合、Integer値をBigDecimalに変換
		if(intAmount != null) {
			// 整数の値に指定したスケール分0を追加
			String numStr = intAmount.toString() + ".00";
			// 整数の値を文字列変換
			amount = new BigDecimal(numStr.toString());
			// スケールを2に設定、小数点以下は切り捨て（HALF_DOWN）で丸める(0.5以上は切り上げ、0.5未満は切り捨て)
			amount.setScale(2, RoundingMode.HALF_DOWN);
		}
		
		// 消費税項目ドメインタイプを生成
		return ShoppingTaxExpenses.from(amount);
	}
	
	/**
	 *<pre>
	 * 消費税の値を指定した消費税の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する消費税の値
	 *
	 * @return 加算した消費税の値(this + addValue)
	 *
	 */
	public ShoppingTaxExpenses add(ShoppingTaxExpenses addValue) {
		return ShoppingTaxExpenses.from(super.add(addValue));
	}
}
