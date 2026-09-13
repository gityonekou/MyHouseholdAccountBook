/**
 * 「集計開始日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「集計開始日」項目の値を表すドメインタイプです
 * クレジットカードの利用金額を締める起算日(1～28)を表す。null許容(クレジットカード以外は未設定)。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ClosingDay {
	// 集計開始日
	private final String value;

	/**
	 *<pre>
	 * 「集計開始日」項目の値を表すドメインタイプを生成します。
	 *
	 * [非ガード節]
	 * ・null
	 * [ガード節]
	 * ・長さが2桁でない(値ありの場合のみ)
	 * ・数値に変換できない、または01～28の範囲外(値ありの場合のみ)
	 *</pre>
	 * @param closingDay 集計開始日
	 * @return 「集計開始日」項目ドメインタイプ
	 *
	 */
	public static ClosingDay from(String closingDay) {
		if(!StringUtils.hasLength(closingDay)) {
			return new ClosingDay(null);
		}
		if(closingDay.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「集計開始日」項目の設定値が不正です。管理者に問い合わせてください。[closingDay=" + closingDay + "]");
		}
		int intValue;
		try {
			intValue = Integer.parseInt(closingDay);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「集計開始日」項目の設定値が不正です。管理者に問い合わせてください。[closingDay=" + closingDay + "]");
		}
		if(intValue < 1 || intValue > 28) {
			throw new MyHouseholdAccountBookRuntimeException("「集計開始日」項目の設定値は01～28の範囲で指定してください。管理者に問い合わせてください。[closingDay=" + closingDay + "]");
		}
		return new ClosingDay(closingDay);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
