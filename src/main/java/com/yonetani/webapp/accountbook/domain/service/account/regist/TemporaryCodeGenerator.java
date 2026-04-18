/**
 * 収支登録画面で使用する仮登録用コードを生成するドメインサービスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.service.account.regist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *<pre>
 * 収支登録画面で使用する仮登録用コードを生成するドメインサービスです。
 * 仮登録用コードはタイムスタンプ形式で採番します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
public class TemporaryCodeGenerator {

	/** インスタンス化禁止 */
	private TemporaryCodeGenerator() {}

	/**
	 *<pre>
	 * 仮登録用コード（単体採番）を生成します。
	 * 形式: yyyyMMddHHmmssSSS（17桁）
	 *</pre>
	 * @return 仮登録用コード（17桁）
	 *
	 */
	public static String generate() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
	}

	/**
	 *<pre>
	 * 仮登録用コード（連番採番）のベース部分を生成します。
	 * 形式: yyyyMMddHHmmss（14桁）
	 * 呼び出し元が {@code String.format("%s%03d", base, count)} で17桁の完成形に仕上げます。
	 *</pre>
	 * @return 仮登録用コードのベース部分（14桁）
	 *
	 */
	public static String generateBase() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
	}

}
