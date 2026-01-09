/**
 * 「無駄遣い合計支出金額」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(SisyutuKingakuBC → TotalWasteExpenditure)
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
import lombok.Getter;

/**
 *<pre>
 * 「無駄遣い合計支出金額」項目の値を表すドメインタイプです
 *
 * 無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値を表します。
 * 支出項目毎に集計され、無駄遣い全体の金額と割合が可視化されます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class TotalWasteExpenditure extends NullableMoney {
	// 無駄遣い（軽度）支出金額の値
	private final MinorWasteExpenditure minorWasteExpenditure;
	// 無駄遣い（重度）支出金額の値
	private final SevereWasteExpenditure severeWasteExpenditure;
	/** 値が0の「無駄遣い合計支出金額」項目の値 */
	public static final TotalWasteExpenditure ZERO = TotalWasteExpenditure.from(
			MinorWasteExpenditure.ZERO,
			SevereWasteExpenditure.ZERO);

	/**
	 * コンストラクタ
	 * @param value 無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値
	 * @param minorWasteExpenditure 無駄遣い（軽度）支出金額
	 * @param severeWasteExpenditure 無駄遣い（重度）支出金額
	 */
	private TotalWasteExpenditure(BigDecimal value, MinorWasteExpenditure minorWasteExpenditure, SevereWasteExpenditure severeWasteExpenditure) {
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
	 * @param minor 無駄遣い（軽度）支出金額
	 * @param severe 無駄遣い（重度）支出金額
	 * @return 「無駄遣い合計支出金額」項目ドメインタイプ
	 *
	 */
	public static TotalWasteExpenditure from(MinorWasteExpenditure minor, SevereWasteExpenditure severe) {

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
			return new TotalWasteExpenditure(severe.getValue(), minor, severe);
		}
		
		// 無駄遣い（重度）支出金額の設定値がnullでない場合、無駄遣い（軽度）支出金額と無駄遣い（重度）支出金額の合計値を設定
		if(severe.getValue() != null) {
			return new TotalWasteExpenditure(minor.getValue().add(severe.getValue()), minor, severe);
		}
		
		// 無駄遣い（軽度）支出金額の値で無駄遣い合計支出金額ドメインタイプを生成
		return new TotalWasteExpenditure(minor.getValue(), minor, severe);

	}

	/**
	 *<pre>
	 * 無駄遣い合計支出金額の値を指定した無駄遣い合計支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する無駄遣い合計支出金額の値
	 *
	 * @return 加算した無駄遣い合計支出金額の値(this + addValue)
	 *
	 */
	public TotalWasteExpenditure add(TotalWasteExpenditure addValue) {
		if(this.getValue() == null) {
			return addValue;
		}
		if(addValue.getValue() == null) {
			return this;
		}
		MinorWasteExpenditure addMinor = minorWasteExpenditure.add(addValue.getMinorWasteExpenditure());
		SevereWasteExpenditure addSevere = severeWasteExpenditure.add(addValue.getSevereWasteExpenditure());
		return TotalWasteExpenditure.from(addMinor, addSevere);
	}
	
	/**
	 *<pre>
	 * 無駄遣い合計支出金額の値が支出金額の何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(無駄遣い合計支出金額項目の値なし)の場合、空文字列を返却
	 *
	 * [ガード節]
	 * ・引数で指定した支出金額がnull値
	 *
	 *</pre>
	 * @param expenditureAmount 無駄遣い合計支出金額の割合算出用の支出金額の値
	 * @return 無駄遣い合計支出金額の割合(文字列)
	 *
	 */
	public String getPercentage(ExpenditureAmount expenditureAmount) {
		// ガード節(支出金額がnull値)
		if(expenditureAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[expenditureAmount=null]");
		}
		
		// 無駄遣い合計支出金額の値がnullか0の場合、空文字列を返却
		if(getValue() == null || ZERO.getValue().compareTo(getValue()) >= 0) {
			return "";
		}

		// 無駄遣い合計支出金額の割合=無駄遣い合計支出金額/支出金額 * 100(四捨五入)
		BigDecimal pt = getValue().divide(expenditureAmount.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}

	/**
	 *<pre>
	 * 無駄遣い合計支出金額のうち、無駄遣い（軽度）支出金額の金額の割合が何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(無駄遣い合計支出金額項目の値なし)の場合、0を返却
	 *
	 *</pre>
	 * @return 無駄遣い（軽度）支出金額の割合(文字列)
	 *
	 */
	public String getMinorWasteExpenditurePercentage() {
		// 無駄遣い合計支出金額の値がnullまたは無駄遣い（軽度）支出金額の値がnullの場合、0を返却
		if(getValue() == null || minorWasteExpenditure.getValue() == null) {
			return "0";
		}
		
		// 無駄遣い（軽度）支出金額の値が0の場合、0を返却
		if(MinorWasteExpenditure.ZERO.getValue().compareTo(minorWasteExpenditure.getValue()) >= 0) {
			return "0";
		}

		// 無駄遣い（軽度）支出金額の割合=無駄遣い（軽度）支出金額/無駄遣い合計支出金額 * 100(四捨五入)
		BigDecimal pt = minorWasteExpenditure.getValue().divide(getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
}
