/**
 * 「固定費コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費コード」項目の値を表すドメインタイプです
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
public class FixedCostCode {
	// 固定費コード
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 *</pre>
	 * @param fixedCostCode 固定費コード
	 * @return 「固定費コード」項目ドメインタイプ
	 *
	 */
	public static FixedCostCode from(String fixedCostCode) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(fixedCostCode)) {
			throw new MyHouseholdAccountBookRuntimeException("「固定費コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが4桁でない)
		if(fixedCostCode.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「固定費コード」項目の設定値が不正です。管理者に問い合わせてください。[fixedCostCode=" + fixedCostCode + "]");
		}
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(fixedCostCode);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「固定費コード」項目の設定値が不正です。管理者に問い合わせてください。[fixedCostCode=" + fixedCostCode + "]");
		}
		
		return new FixedCostCode(fixedCostCode);
	}
	
	/**
	 *<pre>
	 * 新規発番する固定費コードの値(数値)をもとに、「固定費コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する固定費コードの値(数値)
	 * @return 「固定費コード」項目ドメインタイプ
	 *
	 */
	public static FixedCostCode from(int count) {
		return new FixedCostCode(String.format("%04d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する固定費コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する固定費コードの値(数値)
	 * @return 固定費コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return FixedCostCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
