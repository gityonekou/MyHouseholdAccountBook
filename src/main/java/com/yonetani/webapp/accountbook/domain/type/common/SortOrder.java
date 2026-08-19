/**
 * 表示順を表す値オブジェクトの抽象基底クラスです。
 * 表示順系のドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 表示順を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・表示順の基本的なバリデーション（null、空文字）
 * ・表示順の統一的な表現
 * ・型安全性の確保
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証
 * ・桁数・数値チェックは表示順の桁数がドメインタイプごとに異なるため、各サブクラスのfrom()で行う
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class SortOrder {

	// 表示順の値
	private final String value;

	/**
	 *<pre>
	 * 表示順の値を検証します。
	 *
	 * [検証内容]
	 * ・null値チェック
	 * ・空文字チェック
	 *
	 * 桁数・数値チェックは表示順の桁数がドメインタイプごとに異なるため、
	 * サブクラスのfrom()でこのメソッドを呼び出した後に追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象の表示順の値
	 * @param typeName 表示順の型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(String value, String typeName) {
		// ガード節(null)
		if(value == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値がnullです。管理者に問い合わせてください。", typeName));
		}
		// ガード節(空文字)
		if(!StringUtils.hasLength(value)) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が未設定です。管理者に問い合わせてください。", typeName));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// 値の文字列表現を返却
		return value;
	}
}
