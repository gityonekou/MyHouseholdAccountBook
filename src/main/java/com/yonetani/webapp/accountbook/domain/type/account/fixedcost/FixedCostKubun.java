/**
 * 「固定費区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費区分」項目の値を表すドメインタイプです
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
public class FixedCostKubun {
	// 固定費区分
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費区分」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・設定値が1(支払い金額確定) or 2(予定支払い金額(支出金額は0円で初期登録))以外
	 *</pre>
	 * @param kubun 固定費区分
	 * @return 「固定費区分」項目ドメインタイプ
	 *
	 */
	public static FixedCostKubun from(String kubun) {
		// ガード節(設定値が1 or 2以外)
		if(!Objects.equals(kubun, MyHouseholdAccountBookContent.FIXED_COST_FIX_SELECTED_VALUE)
			&& !Objects.equals(kubun, MyHouseholdAccountBookContent.FIXED_COST_ESTIMATE_SELECTED_VALUE)) {
			throw new MyHouseholdAccountBookRuntimeException("「固定費区分」項目の設定値が不正です。管理者に問い合わせてください。[value=" + kubun + "]");
		}
		return new FixedCostKubun(kubun);
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
	 * 固定費区分の設定内容が「2(予定支払い金額(支出金額は0円で初期登録)」の場合、true、それ以外の場合はfalseを返します。
	 * 
	 * 月毎の支出情報初期登録時に対象の支出項目の支出金額を0円、支出予定金額を設定されている支出金額にする場合の判定に用います。
	 * 
	 *</pre>
	 * @return
	 *
	 */
	public boolean isClearStart() {
		// 固定費区分の設定内容が「2(予定支払い金額(支出金額は0円で初期登録)」の場合、true
		if(Objects.equals(value, MyHouseholdAccountBookContent.FIXED_COST_ESTIMATE_SELECTED_VALUE)) {
			return true;
		// 上記以外の場合、false
		} else {
			return false;
		}
	}
}
