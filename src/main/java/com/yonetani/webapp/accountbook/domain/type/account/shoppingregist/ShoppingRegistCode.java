/**
 * 「買い物登録コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
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
 * 「買い物登録コード」項目の値を表すドメインタイプです
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
public class ShoppingRegistCode {
	// 買い物登録コード
	private final String value;
	
	/**
	 *<pre>
	 * 「買い物登録コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param shoppingRegistCode 買い物登録コード
	 * @return 「買い物登録コード」項目ドメインタイプ
	 *
	 */
	public static ShoppingRegistCode from(String shoppingRegistCode) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(shoppingRegistCode)) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物登録コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが3桁でない)
		if(shoppingRegistCode.length() != 3) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物登録コード」項目の設定値が不正です。管理者に問い合わせてください。[shoppingRegistCode=" + shoppingRegistCode + "]");
		}
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(shoppingRegistCode);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物登録コード」項目の設定値が不正です。管理者に問い合わせてください。[shoppingRegistCode=" + shoppingRegistCode + "]");
		}
		
		return new ShoppingRegistCode(shoppingRegistCode);
	}
	
	/**
	 *<pre>
	 * 新規発番する買い物登録コードの値(数値)をもとに、「買い物登録コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・数値が0以下、または、1000以上
	 *</pre>
	 * @param code 新規発番する買い物登録コードの値(数値)
	 * @return 「買い物登録コード」項目ドメインタイプ
	 *
	 */
	public static ShoppingRegistCode from(int code) {
		// ガード節(指定値が0以下、または1000以上)
		if(code <= 0 || code >= 1000) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物登録コード」項目の設定値が不正です。管理者に問い合わせてください。[shoppingRegistCode=" + code + "]");
		}
		return new ShoppingRegistCode(String.format("%03d", code));
	}
	
	/**
	 *<pre>
	 * 新規発番する買い物登録コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する買い物登録コードの値(数値)
	 * @return 買い物登録コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return ShoppingRegistCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
