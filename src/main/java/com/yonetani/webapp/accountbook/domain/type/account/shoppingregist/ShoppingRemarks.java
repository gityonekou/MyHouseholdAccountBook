/**
 * 買い物登録時の「備考」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録時の「備考」項目の値を表すドメインタイプです
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
public class ShoppingRemarks {
	// 買い物登録時の「備考」項目
	private final String value;
	
	/**
	 *<pre>
	 * 買い物登録時の「備考」項目の値を表すドメインタイプを生成します。
	 * 
	 * [非ガード節]
	 * ・null
	 * [ガード節]
	 * ・長さが150文字より大きい
	 *</pre>
	 * @param remarks 備考
	 * @return 買い物登録時の「備考」項目ドメインタイプ
	 *
	 */
	public static ShoppingRemarks from(String remarks) {
		// ガード節(長さが150文字より大きい(文字列長がある場合のみチェック)
		if(StringUtils.hasLength(remarks) && remarks.length() > 150) {
			throw new MyHouseholdAccountBookRuntimeException("「備考」項目の設定値が150文字より大きい値で指定されています。管理者に問い合わせてください。[length=" + remarks.length() + "]");
		}
		return new ShoppingRemarks(remarks);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
	
}
