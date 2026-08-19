/**
 * ドメインタイプのfrom()系ファクトリメソッド呼び出しを、例外ではなくOptionalで安全に扱うためのユーティリティです。
 * 値が不正な形式かもしれない外部入力（Formのバリデーション等）を検証する場合に使用します。
 * from()自体の「不変条件違反=例外」という意味論は変更しません。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.util.Optional;
import java.util.function.Supplier;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ドメインタイプのfrom()系ファクトリメソッド呼び出しを、例外ではなくOptionalで安全に扱うためのユーティリティです。
 * 値が不正な形式かもしれない外部入力（Formのバリデーション等）を検証する場合に使用します。
 * from()自体の「不変条件違反=例外」という意味論は変更しません。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public final class SafeDomainFactory {
	private SafeDomainFactory() {}

	/**
	 *<pre>
	 * 指定したファクトリ処理を実行し、成功した場合はOptionalで結果を返します。
	 * MyHouseholdAccountBookRuntimeExceptionが発生した場合はOptional.empty()を返します。
	 *</pre>
	 * @param <T> 生成対象の型
	 * @param factory ドメインタイプを生成するファクトリ処理
	 * @return 生成に成功した場合は値ありのOptional、失敗(不変条件違反)した場合はOptional.empty()
	 *
	 */
	public static <T> Optional<T> tryCreate(Supplier<T> factory) {
		try {
			return Optional.ofNullable(factory.get());
		} catch (MyHouseholdAccountBookRuntimeException e) {
			return Optional.empty();
		}
	}
}
