/**
 * 「支出金額B」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 * 2025/12/21 : 1.01.00  リファクタリング対応(DDD適応)
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
 * 「支出金額B」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SisyutuKingakuB extends NullableMoney {
	
	/** 値が0の「支出金額B」項目の値 */
	public static final SisyutuKingakuB ZERO = SisyutuKingakuB.from(BigDecimal.ZERO.setScale(2));
	
	/**
	 * コンストラクタ
	 * @param value 支出金額B
	 */
	private SisyutuKingakuB(BigDecimal value) {
		super(value);
	}
	
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
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(kingakub, "支出金額B");
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
		return SisyutuKingakuB.from(super.add(addValue));
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
		return SisyutuKingakuB.from(super.subtract(subtractValue));
	}
	
	/**
	 *<pre>
	 * 支出金額Bの値をカンマ編集した文字列を返却
	 *</pre>
	 * @return 支出金額Bの値をカンマ編集した文字列
	 * @deprecated 基底クラスのtoFormatString()を使用してください
	 *
	 */
	@Deprecated
	public String toSisyutuKingakuBString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(getValue());
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
	 * @param expenditureAmount 支出金額Bの割合算出用の支出金額の値
	 * @return 支出金額Bの割合(文字列)
	 *
	 */
	public String getPercentage(ExpenditureAmount expenditureAmount) {
		// ガード節(支出金額がnull値)
		if(expenditureAmount == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出金額がnull値です。管理者に問い合わせてください。[expenditureAmount=null]");
		}
		// 支出金額Bの値がnullか0の場合、空文字列を返却
		if(getValue() == null || ZERO.getValue().compareTo(getValue()) >= 0) {
			return "";
		}

		// 支出金額Bの割合=支出金額B/支出金額 * 100(四捨五入)
		BigDecimal pt = getValue().divide(expenditureAmount.getValue(), 2, RoundingMode.HALF_UP).multiply(DomainCommonUtils.ONE_HUNDRED_BIGDECIMAL);
		// スケール0で四捨五入した文字列を返却
		return pt.setScale(0, RoundingMode.HALF_UP).toPlainString();
	}
}
