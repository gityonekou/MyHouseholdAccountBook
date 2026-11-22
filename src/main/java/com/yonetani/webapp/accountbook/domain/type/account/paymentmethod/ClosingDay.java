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
	 * ・null、空文字列
	 * [ガード節]
	 * ・長さが2桁でない
	 * ・数値に変換できない
	 * ・01～28の範囲外
	 *</pre>
	 * @param closingDay 集計開始日
	 * @return 「集計開始日」項目ドメインタイプ
	 *
	 */
	public static ClosingDay from(String closingDay) {
		// 非ガード節(null、空文字列。クレジットカード以外は未設定のため許容する)
		if(!StringUtils.hasLength(closingDay)) {
			return new ClosingDay(null);
		}
		// ガード節(長さが2桁でない)
		if(closingDay.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「集計開始日」項目の設定値が不正です。管理者に問い合わせてください。[closingDay=" + closingDay + "]");
		}
		int intValue;
		try {
			intValue = Integer.parseInt(closingDay);
		} catch(NumberFormatException ex) {
			// ガード節(数値に変換できない)
			throw new MyHouseholdAccountBookRuntimeException("「集計開始日」項目の設定値が不正です。管理者に問い合わせてください。[closingDay=" + closingDay + "]");
		}
		// ガード節(01～28の範囲外)
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

	/**
	 *<pre>
	 * 集計期間(開始日～終了日)の表示用文字列を取得します。
	 * 紙芝居(ManagePaymentMethod.html)の一覧表示に準拠し、開始日が1日の場合は「1日～月末」、
	 * それ以外は「N日～翌月(N-1)日」の形式とします。未設定の場合は「－」を返します。
	 *</pre>
	 * @return 集計期間の表示用文字列(例:「1日～月末」「15日～翌月14日」「－」)
	 *
	 */
	public String toPeriodDisplayString() {
		// 未設定の場合、「－」を返却
		if(value == null) {
			return "－";
		}
		int startDay = Integer.parseInt(value);
		// 開始日が1日の場合、「1日～月末」を返却
		if(startDay == 1) {
			return "1日～月末";
		}
		// 開始日が2日以上の場合、「N日～翌月(N-1)日」を返却
		return startDay + "日～翌月" + (startDay - 1) + "日";
	}
}
