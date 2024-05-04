/**
 * リダイレクト時、リダイレクト前のレスポンスからリダイレクト先のレスポンスに引き継ぐメッセージです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.fw;

import java.util.List;

import lombok.Data;

/**
 *<pre>
 * リダイレクト時、リダイレクト前のレスポンスからリダイレクト先のレスポンスに引き継ぐメッセージです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class CompleteRedirectMessages {
	// リダイレクト先に引き継ぐメッセージのリスト
	private List<String> redirectMessages;
}
