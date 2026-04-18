/**
 * 「支出金額B」項目と「支出金額C」項目の合計値(「支出金額BとCの合計値」項目)を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/27 : 1.00.00  新規作成
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
 * 「支出金額B」項目と「支出金額C」項目の合計値を表すドメインタイプです
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
public class SisyutuKingakuBC {
	// 支出金額Bと支出金額Cの合計値
	private final BigDecimal value;
	// 支出金額Bの値
	private final SisyutuKingakuB sisyutuKingakuB;
	// 支出金額Cの値
	private final SisyutuKingakuC sisyutuKingakuC;
	// 値が0の「支出金額BC」項目の値
	public static final SisyutuKingakuBC ZERO = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(BigDecimal.ZERO.setScale(2)),
			SisyutuKingakuC.from(BigDecimal.ZERO.setScale(2)));
	
	/**
	 *<pre>
	 * 「支出金額B」項目と「支出金額C」項目の合計値を表すドメインタイプを生成します
	 * 
	 * [ガード節]
	 * ・支出金額Bがnull
	 * ・支出金額Cがnull
	 * 
	 *</pre>
	 * @param kingakuB 支出金額B
	 * @param kingakuC 支出金額C
	 * @return 「支出金額BとCの合計値」項目ドメインタイプ
	 *
	 */
	public static SisyutuKingakuBC from(SisyutuKingakuB kingakuB, SisyutuKingakuC kingakuC) {
		
		// ガード節(支出金額Bがnull)
		if (kingakuB == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額B」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(支出金額Cがnull)
		if (kingakuC == null) {
			throw new MyHouseholdAccountBookRuntimeException("「支出金額C」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		// 支出金額Bの設定値がnullの場合、支出金額Cの値を設定
		if(kingakuB.getValue() == null) {
			return new SisyutuKingakuBC(kingakuC.getValue(), kingakuB, kingakuC);
		}
		// 支出金額Cの設定値がnullでない場合、支出金額Bと支出金額Cの合計値を設定
		if(kingakuC.getValue() != null) {
			return new SisyutuKingakuBC(kingakuB.getValue().add(kingakuC.getValue()), kingakuB, kingakuC);
		}
		// 支出金額Bの値で支出金額BCドメインタイプを生成
		return new SisyutuKingakuBC(kingakuB.getValue(), kingakuB, kingakuC);
		
	}
	
	/**
	 *<pre>
	 * 支出金額BCの値を指定した支出金額BCの値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額BCの値
	 * 
	 * @return 加算した支出金額BCの値(this + addValue)
	 *
	 */
	public SisyutuKingakuBC add(SisyutuKingakuBC addValue) {
		if(this.value == null) {
			return addValue;
		}
		if(addValue.getValue() == null) {
			return this;
		}
		SisyutuKingakuB addB = sisyutuKingakuB.add(addValue.getSisyutuKingakuB());
		SisyutuKingakuC addC = sisyutuKingakuC.add(addValue.getSisyutuKingakuC());
		return SisyutuKingakuBC.from(addB, addC);
	}
	
	/**
	 *<pre>
	 * 支出金額BCの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Bの値をカンマ編集した文字列
	 *
	 */
	public String toSisyutuKingakuBCString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
	
	/**
	 *<pre>
	 * 支出金額BCの値が支出金額の何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(支払金額BC項目の値なし)の場合、空文字列を返却
	 * 
	 * [ガード節]
	 * ・引数で指定した支出金額がnull値
	 * 
	 *</pre>
	 * @param sisyutuKingaku 支出金額BCの割合算出用の支出金額の値
	 * @return 支出金額Bの割合(文字列)
	 *
	 */
	public String getPercentage(SisyutuKingaku sisyutuKingaku) {
		// ガード節(支出金額がnull値)
		if(sisyutuKingaku == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[sisyutuKingaku=null]");
		}
		// 支出金額BCの値がnullか0の場合、空文字列を返却
		if(value == null || ZERO.getValue().compareTo(value) >= 0) {
			return "";
		}
		
		// 支出金額BCの割合=支出金額BC/支出金額 * 100(四捨五入)
		BigDecimal pt = value.divide(sisyutuKingaku.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
	
	/**
	 *<pre>
	 * 支出金額BCのうち、支出金額Bの金額の割合が何パーセントかを取得。小数点以下0桁で四捨五入
	 * 値がnull(支払金額BC項目の値なし)の場合、空文字列を返却
	 * 
	 * [ガード節]
	 * ・引数で指定した支出金額がnull値
	 * 
	 *</pre>
	 * @param sisyutuKingaku 支出金額BCの割合算出用の支出金額の値
	 * @return 支出金額Bの割合(文字列)
	 *
	 */
	public String getSisyutuKingakuBPercentage() {
		// 支出金額BCの値がnullか支出金額Bの値がnull場合、0を返却
		if(value == null || sisyutuKingakuB.getValue() == null) {
			return "0";
		}
		// 支出金額Bの値が0の場合、0を返却(
		if(SisyutuKingakuB.ZERO.getValue().compareTo(sisyutuKingakuB.getValue()) >= 0) {
			return "0";
		}
		
		// 支出金額Bの割合=支出金額B/支出金額BC * 100(四捨五入)
		BigDecimal pt = sisyutuKingakuB.getValue().divide(value, 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "sisyutuKingakuBC=" + toSisyutuKingakuBCString();
	}
}
