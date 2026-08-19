/**
 * 「銀行名」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「銀行名」項目の値を表すドメインタイプです
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
public class BankName {
	// 銀行名
	private final String value;

	/**
	 *<pre>
	 * 「銀行名」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが50文字より大きい
	 *</pre>
	 * @param bankName 銀行名
	 * @return 「銀行名」項目ドメインタイプ
	 *
	 */
	public static BankName from(String bankName) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(bankName)) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行名」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが50文字より大きい)
		if(bankName.length() > 50) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行名」項目の設定値が50文字より大きい値で指定されています。管理者に問い合わせてください。[length=" + bankName.length() + "]");
		}
		return new BankName(bankName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
