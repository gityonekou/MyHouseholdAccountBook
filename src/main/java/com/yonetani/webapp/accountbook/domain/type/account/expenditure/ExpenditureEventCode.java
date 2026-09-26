/**
 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプです。
 * 
 * [注意]
 * 支出テーブル情報の「イベントコード」項目は他のID系のドメインタイプと違い空文字列を許容するため
 * Identifier、NullableIdentifierの継承不可となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/04/12 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(NullableIdentifier継承を廃止)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditure;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプです。
 * 支出テーブル情報の「イベントコード」項目は、NULL、及び、空文字列を許容する項目になります。
 * イベントコード項目の値が空文字列となるパターン
 * ・登録済みの固定費一覧情報からセッションに設定する支出登録情報登録時（IncomeAndExpenditureInitUseCase）クラスのreadInitInfoメソッド参照
 * ・画面のリクエストパラメータ受け取り時(空文字列が設定されている場合)
 * 
 * [注意]
 *　支出テーブル情報の「イベントコード」項目は他のID系のドメインタイプと違い空文字列を許容するため
 *　Identifier、NullableIdentifierの継承不可となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ExpenditureEventCode {
	
	/** 値がnullの支出テーブル情報の「イベントコード」項目の値 */
	public static final ExpenditureEventCode NULL = ExpenditureEventCode.from(null);
	
	// IDの値
	private final String value;
	
	/**
	 *<pre>
	 * 支出テーブル情報の「イベントコード」項目の値を表すドメインタイプを生成します。
	 * 支出テーブル情報の「イベントコード」項目は、NULL、及び、空文字列を許容する項目になります。
	 * 
	 * [非ガード節]
	 * ・NULL値
	 * ・空文字列
	 * [ガード節]
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 * 
	 *</pre>
	 * @param code イベントコード
	 * @return 「イベントコード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureEventCode from(String code) {
		
		// null値、または空文字列の場合は、null値の「イベントコード」項目ドメインタイプを返す
		if(!StringUtils.hasLength(code)) {
			return new ExpenditureEventCode(null);
		}
		
		// ガード節(桁数)
		if(code.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException(
					String.format("「支出テーブル情報の「イベントコード」」項目の桁数が不正です。管理者に問い合わせてください。[length=%d]", code.length())); 
		}
		
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("支出テーブル情報の「イベントコード」項目の設定値が不正です。管理者に問い合わせてください。[eventCode=" + code + "]");
		}
		
		return new ExpenditureEventCode(code);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// null値の場合は空文字列を返却
		if(value == null) {
			return "";
		}
		return value;
	}
}
