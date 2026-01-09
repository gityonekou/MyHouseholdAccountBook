/**
 * 「無駄遣い支出金額合計」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/04 : 1.00.00  新規作成
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
import lombok.Getter;

/**
 *<pre>
 * 「無駄遣い支出金額合計」項目の値を表すドメインタイプです。
 *
 * TotalWasteExpenditure（月単位の無駄遣い合計支出金額）を集計した年間合計値を表します。
 * 無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値を内訳として保持します。
 * 年間収支明細照会などで使用されます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class WasteExpenditureTotalAmount extends NullableMoney {

	// 無駄遣い（軽度）支出金額の合計値
	private final MinorWasteExpenditure minorWasteExpenditure;
	// 無駄遣い（重度）支出金額の合計値
	private final SevereWasteExpenditure severeWasteExpenditure;
	/** 値が0の「無駄遣い支出金額合計」項目の値 */
	public static final WasteExpenditureTotalAmount ZERO = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.ZERO,
			SevereWasteExpenditure.ZERO);

	/**
	 * コンストラクタ
	 * @param value 無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値
	 * @param minorWasteExpenditure 無駄遣い（軽度）支出金額の合計値
	 * @param severeWasteExpenditure 無駄遣い（重度）支出金額の合計値
	 */
	private WasteExpenditureTotalAmount(BigDecimal value, MinorWasteExpenditure minorWasteExpenditure, SevereWasteExpenditure severeWasteExpenditure) {
		super(value);
		this.minorWasteExpenditure = minorWasteExpenditure;
		this.severeWasteExpenditure = severeWasteExpenditure;
	}

	/**
	 *<pre>
	 * 「無駄遣い（軽度）支出金額」項目と「無駄遣い（重度）支出金額」項目の合計値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・無駄遣い（軽度）支出金額がnull
	 * ・無駄遣い（重度）支出金額がnull
	 *
	 *</pre>
	 * @param minor 無駄遣い（軽度）支出金額の合計値
	 * @param severe 無駄遣い（重度）支出金額の合計値
	 * @return 「無駄遣い支出金額合計」項目ドメインタイプ
	 *
	 */
	public static WasteExpenditureTotalAmount from(MinorWasteExpenditure minor, SevereWasteExpenditure severe) {

		// ガード節(無駄遣い（軽度）支出金額がnull)
		if (minor == null) {
			throw new MyHouseholdAccountBookRuntimeException("「無駄遣い（軽度）支出金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}

		// ガード節(無駄遣い（重度）支出金額がnull)
		if (severe == null) {
			throw new MyHouseholdAccountBookRuntimeException("「無駄遣い（重度）支出金額」項目の設定値がnullです。管理者に問い合わせてください。");
		}

		// 無駄遣い（軽度）支出金額の設定値がnullの場合、無駄遣い（重度）支出金額の値を設定
		if(minor.getValue() == null) {
			return new WasteExpenditureTotalAmount(severe.getValue(), minor, severe);
		}

		// 無駄遣い（重度）支出金額の設定値がnullでない場合、無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値を設定
		if(severe.getValue() != null) {
			return new WasteExpenditureTotalAmount(minor.getValue().add(severe.getValue()), minor, severe);
		}

		// 無駄遣い（軽度）支出金額の値で無駄遣い支出金額合計ドメインタイプを生成
		return new WasteExpenditureTotalAmount(minor.getValue(), minor, severe);

	}

	/**
	 *<pre>
	 * 無駄遣い支出金額合計の値を指定した無駄遣い合計支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する無駄遣い合計支出金額の値
	 *
	 * @return 加算した無駄遣い支出金額合計の値(this + addValue)
	 *
	 */
	public WasteExpenditureTotalAmount add(TotalWasteExpenditure addValue) {
		if(this.getValue() == null) {
			// thisの値がnullの場合、addValueから新しいWasteExpenditureTotalAmountを生成
			if(addValue == null || addValue.getValue() == null) {
				return this;
			}
			return WasteExpenditureTotalAmount.from(addValue.getMinorWasteExpenditure(), addValue.getSevereWasteExpenditure());
		}
		if(addValue == null || addValue.getValue() == null) {
			return this;
		}
		MinorWasteExpenditure addMinor = minorWasteExpenditure.add(addValue.getMinorWasteExpenditure());
		SevereWasteExpenditure addSevere = severeWasteExpenditure.add(addValue.getSevereWasteExpenditure());
		return WasteExpenditureTotalAmount.from(addMinor, addSevere);
	}

	/**
	 *<pre>
	 * 無駄遣い支出金額合計の値が支出金額の何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(無駄遣い支出金額合計項目の値なし)の場合、空文字列を返却
	 *
	 * [ガード節]
	 * ・引数で指定した支出金額がnull値
	 *
	 *</pre>
	 * @param expenditureAmount 無駄遣い支出金額合計の割合算出用の支出金額の値
	 * @return 無駄遣い支出金額合計の割合(文字列)
	 *
	 */
	public String getPercentage(ExpenditureAmount expenditureAmount) {
		// ガード節(支出金額がnull値)
		if(expenditureAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[expenditureAmount=null]");
		}

		// 無駄遣い支出金額合計の値がnullか0の場合、空文字列を返却
		if(getValue() == null || ZERO.getValue().compareTo(getValue()) >= 0) {
			return "";
		}

		// 無駄遣い支出金額合計の割合=無駄遣い支出金額合計/支出金額 * 100(四捨五入)
		BigDecimal pt = getValue().divide(expenditureAmount.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);

		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}

	/**
	 *<pre>
	 * 無駄遣い支出金額合計のうち、無駄遣い（軽度）支出金額の金額の割合が何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(無駄遣い支出金額合計項目の値なし)の場合、0を返却
	 *
	 *</pre>
	 * @return 無駄遣い（軽度）支出金額の割合(文字列)
	 *
	 */
	public String getMinorWasteExpenditurePercentage() {
		// 無駄遣い支出金額合計の値がnullまたは無駄遣い（軽度）支出金額の値がnullの場合、0を返却
		if(getValue() == null || minorWasteExpenditure.getValue() == null) {
			return "0";
		}

		// 無駄遣い（軽度）支出金額の値が0の場合、0を返却
		if(MinorWasteExpenditure.ZERO.getValue().compareTo(minorWasteExpenditure.getValue()) >= 0) {
			return "0";
		}

		// 無駄遣い（軽度）支出金額の割合=無駄遣い（軽度）支出金額/無駄遣い支出金額合計 * 100(四捨五入)
		BigDecimal pt = minorWasteExpenditure.getValue().divide(getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);

		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
}
