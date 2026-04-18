/**
 * null値を許容するID（識別子）を表す値オブジェクトの抽象基底クラスです。
 * null許容ID系ドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/12 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * null値を許容するID（識別子）を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・null許容ID（識別子）の基本的なバリデーション（null値は許容）
 * ・IDの統一的な表現
 * ・型安全性の確保
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証
 * ・型安全性：サブクラスで具体的なIDの型を表現
 * ・null安全性：null値を含む操作を安全に処理
 * 
 * [Identifierクラスとの違い]
 * ・Identifierクラス：null非許容、null値はエラー
 * ・NullableIdentifierクラス：null許容、null値は空文字として扱う表示を提供
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class NullableIdentifier {

	// IDの値
	private final String value;

	/**
	 *<pre>
	 * IDの値を検証します。
	 *
	 * [検証内容]
	 * ・null値チェック
	 * ・空文字チェック
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象のID値
	 * @param typeName IDの型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(String value, String typeName) {
		// null値は許容
		if(value == null) {
			return; // null値は許容するため、ガード節でエラーにせずに処理を終了
		}
		// ガード節(空文字)
		if(value.isEmpty()) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が空文字です。管理者に問い合わせてください。", typeName));
		}
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
