/**
 * 「銀行口座メモ」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
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
 * 「銀行口座メモ」項目の値を表すドメインタイプです
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
public class BankAccountMemo {
	// 銀行口座メモ
	private final String value;

	/**
	 *<pre>
	 * 「銀行口座メモ」項目の値を表すドメインタイプを生成します。
	 *
	 * [非ガード節]
	 * ・null
	 * [ガード節]
	 * ・長さが100文字より大きい
	 *</pre>
	 * @param bankAccountMemo 銀行口座メモ
	 * @return 「銀行口座メモ」項目ドメインタイプ
	 *
	 */
	public static BankAccountMemo from(String bankAccountMemo) {
		// ガード節(長さが100文字より大きい(文字列長がある場合のみチェック))
		if(StringUtils.hasLength(bankAccountMemo) && bankAccountMemo.length() > 100) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行口座メモ」項目の設定値が100文字より大きい値で指定されています。管理者に問い合わせてください。[length=" + bankAccountMemo.length() + "]");
		}
		return new BankAccountMemo(bankAccountMemo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
