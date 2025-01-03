/**
 * 「固定費名(支払名)」項目の値を表すドメインタイプです
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
 * 「固定費名(支払名)」項目の値を表すドメインタイプです
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
public class FixedCostName {
	// 固定費名(支払名)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費名(支払名)」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * 
	 *</pre>
	 * @param name 固定費名(支払名)
	 * @return 「固定費名(支払名)」項目ドメインタイプ
	 *
	 */
	public static FixedCostName from(String name) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(name)) {
			throw new MyHouseholdAccountBookRuntimeException("「固定費名(支払名)」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		return new FixedCostName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
