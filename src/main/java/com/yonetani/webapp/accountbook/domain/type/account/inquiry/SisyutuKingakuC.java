/**
 * 「支出金額C」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出金額C」項目の値を表すドメインタイプです
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
public class SisyutuKingakuC {
	// 支出金額C
	private final BigDecimal value;
	// 値が0の「支出金額C」項目の値
	public static final SisyutuKingakuC ZERO = SisyutuKingakuC.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「支出金額C」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・支出金額Cがnull値
	 * [ガード節]
	 * ・支出金額Cがマイナス値
	 * ・支出金額Cがスケール値が2以外
	 * 
	 *</pre>
	 * @param kingakuc 支出金額C
	 * @return 「支出金額C」項目ドメインタイプ
	 *
	 */
	public static SisyutuKingakuC from(BigDecimal kingakuc) {
		
		// 非ガード(支出金額Cがnull値の場合、値nullの「支出金額C」項目ドメインタイプを生成
		if (kingakuc == null) {
			return new SisyutuKingakuC(null);
		}
		// ガード節(支出金額Cがマイナス値)
		if (BigDecimal.ZERO.compareTo(kingakuc) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakuc.intValue() + "]");
		}
		// ガード節(支出金額Cのスケール値が2以外)
		if (kingakuc.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakuc.scale() + "]");
		}
		
		// 支出金額C項目ドメインタイプを生成
		return new SisyutuKingakuC(kingakuc);
		
	}
	
	/**
	 *<pre>
	 * 支出金額Cの値を指定した支出金額Cの値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額Cの値
	 * @return 加算した支出金額Cの値(this + addValue)
	 *
	 */
	public SisyutuKingakuC add(SisyutuKingakuC addValue) {
		if(this.value == null) {
			return addValue;
		}
		if(addValue.getValue() == null) {
			return this;
		}
		return SisyutuKingakuC.from(this.value.add(addValue.getValue()));
	}
	
	/**
	 *<pre>
	 * 支出金額Cの値を指定した支出金額Cの値で減算(this - subtractValue)した値を返します。
	 *</pre>
	 * @param subtractValue 減算する支出金額Cの値
	 * @return 減算した支出金額Cの値(this - subtractValue)
	 *
	 */
	public SisyutuKingakuC subtract(SisyutuKingakuC subtractValue) {
		// 減算する支出金額の値がnullなら減算せずにthisの値を返す
		if(subtractValue.getValue() == null) {
			return this;
		}
		// thisがnullなら、減算後の値がマイナスとなる計算を実施したことになるのでデータ不正
		if(this.value == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」がnull値の場合におけるマイナス減算は不正です。管理者に問い合わせてください。[value=" + subtractValue.toSisyutuKingakuCString() + "]");
		}
		return new SisyutuKingakuC(this.value.subtract(subtractValue.getValue()));
	}
	
	
	/**
	 *<pre>
	 * 支出金額Cの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Cの値をカンマ編集した文字列
	 *
	 */
	public String toSisyutuKingakuCString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
	
	/**
	 *<pre>
	 * 支出金額Cの値が支出金額の何パーセントかを表した値。小数点以下0桁で四捨五入
	 * 値がnull(支払金額B項目の値なし)の場合、空文字列を返却
	 *</pre>
	 * @param expenditureAmount 支出金額Cの割合算出用の支出金額の値
	 * @return 支出金額Cの割合
	 *
	 */
	public String getPercentage(ExpenditureAmount expenditureAmount) {

		// ガード節(支出金額がnull値)
		if(expenditureAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[expenditureAmount=null]");
		}
		// 支出金額Cの値がnullか0の場合、空文字列を返却
		if(value == null || ZERO.getValue().compareTo(value) >= 0) {
			return "";
		}

		// 支出金額Cの割合=支出金額C/支出金額 * 100(四捨五入)
		BigDecimal pt = value.divide(expenditureAmount.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "sisyutuKingaku=" + toSisyutuKingakuCString();
	}
}
