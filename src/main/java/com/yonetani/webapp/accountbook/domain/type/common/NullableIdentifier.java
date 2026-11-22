/**
 * null値と空文字列を許容するID（識別子）を表す値オブジェクトの抽象基底クラスです。
 * 空文字列はnull値として値を保持します。
 * 必須設定ではないID系ドメインタイプはこのクラスを継承します。
 * (DB登録時に対象項目の値としてnull値が登録されることを想定しています)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/04/12 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(桁数チェック追加と空文字列許容に変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *<pre>
 * null値と空文字列を許容するID（識別子）を表す値オブジェクトの抽象基底クラスです。
 * 空文字列はnull値として値を保持します。
 * 必須設定ではないID系ドメインタイプはこのクラスを継承します。
 * (DB登録時に対象項目の値としてnull値が登録されることを想定しています)
 *
 * [責務]
 * ・null許容ID（識別子）の基本的なバリデーション（null値、空文字列は許容）
 * ・空文字列をnull値として保持
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
@Getter
@EqualsAndHashCode
public abstract class NullableIdentifier {

	// IDの値
	private final String value;

	/**
	 *<pre>
	 * NullableIdentifierクラスコンストラクターです。
	 * 引数で設定された値がnullか空文字列の場合、ID値にnullを設定します。
	 *</pre>
	 * @param value ID値
	 *
	 */
	protected NullableIdentifier(String value) {
		if(!StringUtils.hasLength(value)) {
			this.value = null;
			return;
		}
		this.value = value;
	}
	
	/**
	 *<pre>
	 * IDの値を検証します。
	 *
	 * [検証内容]
	 * ・null値チェック(nullは許容)
	 * ・空文字チェック(空文字列は許容)
	 * ・桁数チェック
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象のID値
	 * @param length IDの桁数
	 * @param typeName IDの型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(String value, int length, String typeName) {
		// null値は許容
		if(value == null) {
			return; // null値は許容するため、ガード節でエラーにせずに処理を終了
		}
		// ガード節(空文字)
		if(value.isEmpty()) {
			return; // 空文字列値は許容するため、ガード節でエラーにせずに処理を終了
		}
		// ガード節(桁数)
		if(value.length() != length) {
			throw new MyHouseholdAccountBookRuntimeException(
					String.format("「%s」項目の桁数が不正です。管理者に問い合わせてください。[length=%d]", typeName, value.length())); 
		}
	}
	
	/**
	 *<pre>
	 * IDの値がnullかどうかを判定します。
	 *</pre>
	 * @return null値の場合true、それ以外の場合false
	 *
	 */
	public boolean isNull() {
		return this.value == null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// null値の場合は空文字列を返却
		if(isNull()) {
			return "";
		}
		return value;
	}
}
