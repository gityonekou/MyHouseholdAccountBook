/**
 * 「無駄遣い（重度）支出金額」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(SisyutuKingakuC → SevereWasteExpenditure)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  リファクタリング対応(DDD適応)により新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「無駄遣い（重度）支出金額」項目の値を表すドメインタイプです
 *
 * 支出金額登録時に「無駄遣い（重度）」として分類された支出金額を表します。
 * 支出項目毎に集計され、無駄遣いの金額と割合が可視化されます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SevereWasteExpenditure extends NullableMoney {

	/** 値が0の「無駄遣い（重度）支出金額」項目の値 */
	public static final SevereWasteExpenditure ZERO = SevereWasteExpenditure.from(NullableMoney.NULLABLE_MONEY_ZERO);

	/**
	 * コンストラクタ
	 * @param value 無駄遣い（重度）支出金額
	 */
	private SevereWasteExpenditure(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「無駄遣い（重度）支出金額」項目の値を表すドメインタイプを生成します
	 *
	 * [非ガード節]
	 * ・無駄遣い（重度）支出金額がnull値
	 * [ガード節]
	 * ・無駄遣い（重度）支出金額がマイナス値
	 * ・無駄遣い（重度）支出金額のスケール値が2以外
	 *
	 *</pre>
	 * @param amount 無駄遣い（重度）支出金額
	 * @return 「無駄遣い（重度）支出金額」項目ドメインタイプ
	 *
	 */
	public static SevereWasteExpenditure from(BigDecimal amount) {
		
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(amount, "無駄遣い（重度）支出金額");
		
		// 無駄遣い（重度）支出金額項目ドメインタイプを生成
		return new SevereWasteExpenditure(amount);
	}

	/**
	 *<pre>
	 * 無駄遣い（重度）支出金額の値を指定した無駄遣い（重度）支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する無駄遣い（重度）支出金額の値
	 * @return 加算した無駄遣い（重度）支出金額の値(this + addValue)
	 *
	 */
	public SevereWasteExpenditure add(SevereWasteExpenditure addValue) {
		return SevereWasteExpenditure.from(super.add(addValue));
	}

	/**
	 *<pre>
	 * 無駄遣い（重度）支出金額の値を指定した無駄遣い（重度）支出金額の値で減算(this - subtractValue)した値を返します。
	 *</pre>
	 * @param subtractValue 減算する無駄遣い（重度）支出金額の値
	 * @return 減算した無駄遣い（重度）支出金額の値(this - subtractValue)
	 *
	 */
	public SevereWasteExpenditure subtract(SevereWasteExpenditure subtractValue) {
		return SevereWasteExpenditure.from(super.subtract(subtractValue));
	}

	/**
	 *<pre>
	 * 無駄遣い（重度）支出金額の値が支出金額の何パーセントかを表した値。小数点以下0桁で四捨五入
	 * 値がnull(無駄遣い（重度）支出金額項目の値なし)の場合、空文字列を返却
	 *</pre>
	 * @param expenditureAmount 無駄遣い（重度）支出金額の割合算出用の支出金額の値
	 * @return 無駄遣い（重度）支出金額の割合
	 *
	 */
	public String getPercentage(ExpenditureAmount expenditureAmount) {

		// ガード節(支出金額がnull値)
		if(expenditureAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[expenditureAmount=null]");
		}
		
		// 無駄遣い（重度）支出金額の値がnullか0の場合、空文字列を返却
		if(getValue() == null || ZERO.getValue().compareTo(getValue()) >= 0) {
			return "";
		}

		// 無駄遣い（重度）支出金額の割合=無駄遣い（重度）支出金額/支出金額 * 100(四捨五入)
		BigDecimal pt = getValue().divide(expenditureAmount.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
}
