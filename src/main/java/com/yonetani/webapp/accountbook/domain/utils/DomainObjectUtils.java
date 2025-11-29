/**
 * ドメイン層で使用する汎用オブジェクト操作ユーティリティクラスです。
 * 文字列、コレクション、オブジェクトに関する汎用的な処理を提供します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import java.util.Collection;
import java.util.Objects;

/**
 *<pre>
 * ドメイン層で使用する汎用オブジェクト操作ユーティリティクラスです。
 *
 * [責務]
 * ・文字列の検証（null、空文字、空白のみ）
 * ・コレクションの検証（null、空）
 * ・オブジェクトの検証（null）
 * ・外部フレームワークへの依存を最小化
 *
 * [設計方針]
 * ・Spring Framework等の外部フレームワークに依存しない
 * ・ドメイン層で安全に使用できる
 * ・null安全な実装
 *
 * [使用例]
 * <code>
 * // 文字列の検証
 * if (DomainObjectUtils.isEmpty(value)) {
 *     throw new InvalidValueException("値が空です");
 * }
 *
 * // コレクションの検証
 * if (DomainObjectUtils.isEmpty(list)) {
 *     return Collections.emptyList();
 * }
 *
 * // nullチェック
 * DomainObjectUtils.requireNonNull(value, "値");
 * </code>
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class DomainObjectUtils {

	/**
	 * コンストラクタをprivateにしてインスタンス化を防止
	 */
	private DomainObjectUtils() {
		throw new AssertionError("ユーティリティクラスはインスタンス化できません");
	}

	// ========================================
	// 文字列関連のユーティリティメソッド
	// ========================================

	/**
	 *<pre>
	 * 文字列がnullまたは空文字列かどうかを判定します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @return nullまたは空文字列の場合true
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 *<pre>
	 * 文字列がnullでも空文字列でもないかどうかを判定します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @return nullでも空文字列でもない場合true
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 *<pre>
	 * 文字列がnull、空文字列、または空白のみで構成されているかどうかを判定します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @return null、空文字列、または空白のみの場合true
	 */
	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 *<pre>
	 * 文字列がnullでも空文字列でも空白のみでもないかどうかを判定します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @return nullでも空文字列でも空白のみでもない場合true
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 *<pre>
	 * 文字列の前後の空白を削除します。
	 * nullの場合はnullを返します。
	 *</pre>
	 * @param str 対象の文字列
	 * @return トリムされた文字列（nullの場合はnull）
	 */
	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	/**
	 *<pre>
	 * 文字列の前後の空白を削除します。
	 * nullまたは空白のみの場合はnullを返します。
	 *</pre>
	 * @param str 対象の文字列
	 * @return トリムされた文字列（空白のみの場合はnull）
	 */
	public static String trimToNull(String str) {
		String trimmed = trim(str);
		return isEmpty(trimmed) ? null : trimmed;
	}

	/**
	 *<pre>
	 * 文字列の前後の空白を削除します。
	 * nullの場合は空文字列を返します。
	 *</pre>
	 * @param str 対象の文字列
	 * @return トリムされた文字列（nullの場合は空文字列）
	 */
	public static String trimToEmpty(String str) {
		return str == null ? "" : str.trim();
	}

	/**
	 *<pre>
	 * 2つの文字列が等しいかどうかを判定します（null安全）。
	 *</pre>
	 * @param str1 文字列1
	 * @param str2 文字列2
	 * @return 等しい場合true（両方nullの場合もtrue）
	 */
	public static boolean equals(String str1, String str2) {
		return Objects.equals(str1, str2);
	}

	// ========================================
	// コレクション関連のユーティリティメソッド
	// ========================================

	/**
	 *<pre>
	 * コレクションがnullまたは空かどうかを判定します。
	 *</pre>
	 * @param collection 検証対象のコレクション
	 * @return nullまたは空の場合true
	 */
	public static boolean isEmptyCollection(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 *<pre>
	 * コレクションがnullでも空でもないかどうかを判定します。
	 *</pre>
	 * @param collection 検証対象のコレクション
	 * @return nullでも空でもない場合true
	 */
	public static boolean isNotEmptyCollection(Collection<?> collection) {
		return !isEmptyCollection(collection);
	}

	// ========================================
	// オブジェクト関連のユーティリティメソッド
	// ========================================

	/**
	 *<pre>
	 * オブジェクトがnullかどうかを判定します。
	 *</pre>
	 * @param obj 検証対象のオブジェクト
	 * @return nullの場合true
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}

	/**
	 *<pre>
	 * オブジェクトがnullでないかどうかを判定します。
	 *</pre>
	 * @param obj 検証対象のオブジェクト
	 * @return nullでない場合true
	 */
	public static boolean isNotNull(Object obj) {
		return obj != null;
	}

	/**
	 *<pre>
	 * オブジェクトがnullでないことを要求します。
	 * nullの場合は例外をスローします。
	 *</pre>
	 * @param <T> オブジェクトの型
	 * @param obj 検証対象のオブジェクト
	 * @param fieldName フィールド名（エラーメッセージ用）
	 * @return 検証対象のオブジェクト（nullでない場合）
	 * @throws IllegalArgumentException オブジェクトがnullの場合
	 */
	public static <T> T requireNonNull(T obj, String fieldName) {
		if (obj == null) {
			throw new IllegalArgumentException(
				String.format("「%s」がnullです。", fieldName));
		}
		return obj;
	}

	/**
	 *<pre>
	 * 2つのオブジェクトが等しいかどうかを判定します（null安全）。
	 *</pre>
	 * @param obj1 オブジェクト1
	 * @param obj2 オブジェクト2
	 * @return 等しい場合true（両方nullの場合もtrue）
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return Objects.equals(obj1, obj2);
	}

	// ========================================
	// デフォルト値関連のユーティリティメソッド
	// ========================================

	/**
	 *<pre>
	 * オブジェクトがnullの場合にデフォルト値を返します。
	 *</pre>
	 * @param <T> オブジェクトの型
	 * @param obj 検証対象のオブジェクト
	 * @param defaultValue デフォルト値
	 * @return オブジェクト（nullの場合はdefaultValue）
	 */
	public static <T> T defaultIfNull(T obj, T defaultValue) {
		return obj != null ? obj : defaultValue;
	}

	/**
	 *<pre>
	 * 文字列が空の場合にデフォルト値を返します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @param defaultValue デフォルト値
	 * @return 文字列（空の場合はdefaultValue）
	 */
	public static String defaultIfEmpty(String str, String defaultValue) {
		return isEmpty(str) ? defaultValue : str;
	}

	/**
	 *<pre>
	 * 文字列が空白の場合にデフォルト値を返します。
	 *</pre>
	 * @param str 検証対象の文字列
	 * @param defaultValue デフォルト値
	 * @return 文字列（空白の場合はdefaultValue）
	 */
	public static String defaultIfBlank(String str, String defaultValue) {
		return isBlank(str) ? defaultValue : str;
	}
}
