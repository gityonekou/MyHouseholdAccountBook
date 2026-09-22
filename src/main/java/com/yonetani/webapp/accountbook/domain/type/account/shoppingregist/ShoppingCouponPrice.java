/**
 * 買い物登録の「クーポン金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/26 : 1.00.00                      新規作成
 * 2026/09/21 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応（NullableMoneyを継承し、クーポン金額適応時に使用するCouponAmountクラス（「クーポン金額」項目を生成するメソッドを追加)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.domain.type.common.CouponAmount;
import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 買い物登録の「クーポン金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShoppingCouponPrice extends NullableMoney {
	
	/** 値がnullの「クーポン金額」項目の値（割引なし） */
	public static final ShoppingCouponPrice NULL = ShoppingCouponPrice.from((BigDecimal)null);
	
	/**
	 *<pre>
	 * ShoppingCouponPriceクラスコンストラクターです。
	 *</pre>
	 * @param value クーポン金額
	 *
	 */
	private ShoppingCouponPrice(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 買い物登録の「クーポン金額」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・クーポン金額がnull値
	 * [ガード節]
	 * ・クーポン金額がマイナス値
	 * ・クーポン金額がスケール値が2以外
	 * 
	 *</pre>
	 * @param amount クーポン金額
	 * @return 買い物登録の「クーポン金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingCouponPrice from(BigDecimal amount) {
		
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(amount, "クーポン金額");
		
		// クーポン金額項目ドメインタイプを生成
		return new ShoppingCouponPrice(amount);
		
	}
	
	/**
	 *<pre>
	 * Integer値から買い物登録の「クーポン金額」項目の値を表すドメインタイプを生成します。
	 * フォームの入力値(Integer値)から「クーポン金額」項目の値を生成する場合に使用します。
	 *
	 * [非ガード節]
	 * ・非ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 * [ガード節]
	 * ・ガード節についてはfrom(BigDecimal)メソッドに委譲しているため、from(BigDecimal)メソッドのガード節を参照
	 *
	 *</pre>
	 * @param intAmount クーポン金額入力値
	 * @return 買い物登録の「クーポン金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingCouponPrice from(Integer intAmount) {
		// クーポン金額(変換値)
		BigDecimal amount = null;
		
		// クーポン金額入力値ありの場合、Integer値をBigDecimalに変換
		if(intAmount != null) {
			// 整数の値に指定したスケール分0を追加
			String numStr = intAmount.toString() + ".00";
			// 整数の値を文字列変換
			amount = new BigDecimal(numStr.toString());
			// スケールを2に設定、小数点以下は切り捨て（HALF_DOWN）で丸める(0.5以上は切り上げ、0.5未満は切り捨て)
			amount.setScale(2, RoundingMode.HALF_DOWN);
		}
		
		// 買い物購入金額項目ドメインタイプを生成
		return ShoppingCouponPrice.from(amount);
	}
	
	/**
	 *<pre>
	 * クーポン金額の値を指定したクーポン金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算するクーポン金額の値
	 * @return 加算したクーポン金額の値(this + addValue)
	 *
	 */
	public ShoppingCouponPrice add(ShoppingCouponPrice addValue) {
		return new ShoppingCouponPrice(super.add(addValue));
	}
	
	/**
	 *<pre>
	 * 買い物登録の「クーポン金額」項目の値から、CouponAmount「クーポン金額」を生成して返します。
	 * 
	 * 買い物登録で入力したクーポン金額を実際の購入金額に適応する場合のShoppingCouponPrice⇒CouponAmountへの変換で使用します。
	 * 
	 *</pre>
	 * @return CouponAmount「クーポン金額」
	 *
	 */
	public CouponAmount toCouponAmount() {
		if(isNull()) {
			return CouponAmount.ZERO;
		}
		return CouponAmount.from(getValue());
	}
}
