/**
 * 「支出金額B」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出金額B」項目の値を表すドメインタイプです
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
public class SisyutuKingakuB {	
	// 支出金額B
	private final BigDecimal value;
	// 値が0の「支出金額B」項目の値
	public static final SisyutuKingakuB ZERO = SisyutuKingakuB.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 *<pre>
	 * 「支出金額B」項目の値を表すドメインタイプを生成します
	 * 
	 * [非ガード節]
	 * ・支出金額Bがnull値
	 * [ガード節]
	 * ・支出金額Bがマイナス値
	 * ・支出金額Bがスケール値が2以外
	 * 
	 *</pre>
	 * @param kingakub 支出金額B
	 * @return 「支出金額B」項目ドメインタイプ
	 *
	 */
	public static SisyutuKingakuB from(BigDecimal kingakub) {
		
		// 非ガード(支出金額Bがnull値の場合、値nullの「支出金額B」項目ドメインタイプを生成
		if (kingakub == null) {
			return new SisyutuKingakuB(null);
		}
		// ガード節(支出金額Bがマイナス値)
		if (BigDecimal.ZERO.compareTo(kingakub) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakub.intValue() + "]");
		}
		// ガード節(支出金額Bのスケール値が2以外)
		if (kingakub.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kingakub.scale() + "]");
		}
		
		// 支出金額B項目ドメインタイプを生成
		return new SisyutuKingakuB(kingakub);
		
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値を指定した支出金額Bの値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額Bの値
	 * 
	 * @return 加算した支出金額Bの値(this + addValue)
	 *
	 */
	public SisyutuKingakuB add(SisyutuKingakuB addValue) {
		if(this.value == null) {
			return addValue;
		}
		if(addValue.getValue() == null) {
			return this;
		}
		return SisyutuKingakuB.from(this.value.add(addValue.getValue()));
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値を指定した支出金額Bの値で減算(this - subtractValue)した値を返します。
	 *</pre>
	 * @param subtractValue 減算する支出金額Bの値
	 * @return 減算した支出金額Bの値(this - subtractValue)
	 *
	 */
	public SisyutuKingakuB subtract(SisyutuKingakuB subtractValue) {
		// 減算する支出金額の値がnullなら減算せずにthisの値を返す
		if(subtractValue.getValue() == null) {
			return this;
		}
		// thisがnullなら、減算後の値がマイナスとなる計算を実施したことになるのでデータ不正
		if(this.value == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」がnull値の場合におけるマイナス減算は不正です。管理者に問い合わせてください。[value=" + subtractValue.toSisyutuKingakuBString() + "]");
		}
		return new SisyutuKingakuB(this.value.subtract(subtractValue.getValue()));
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Bの値をカンマ編集した文字列
	 *
	 */
	public String toSisyutuKingakuBString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値が支出金額の何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(支払金額B項目の値なし)の場合、空文字列を返却
	 * 
	 * [ガード節]
	 * ・引数で指定した支出金額がnull値
	 * 
	 *</pre>
	 * @param sisyutuKingaku 支出金額Bの割合算出用の支出金額の値
	 * @return 支出金額Bの割合(文字列)
	 *
	 */
	public String getPercentage(SisyutuKingaku sisyutuKingaku) {
		// ガード節(支出金額がnull値)
		if(sisyutuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[sisyutuKingaku=null]");
		}
		// 支出金額Bの値がnullの場合、空文字列を返却
		if(value == null) {
			return "";
		}
		
		// 支出金額Bの割合=支出金額B/支出金額 * 100(四捨五入)
		BigDecimal pt = value.divide(sisyutuKingaku.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "sisyutuKingaku=" + toSisyutuKingakuBString();
	}
}
