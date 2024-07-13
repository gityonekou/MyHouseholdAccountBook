/**
 * セッションに設定する収支登録情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * セッションに設定する収支登録情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class IncomeRegistItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// データタイプ
	private final String dataType;
	// アクション
	private final String action;
	// 収入コード(仮登録用収入コード)
	private final String incomeCode;
	// 収入区分
	private final String incomeKubun;
	// 収入詳細
	private final String incomeDetailContext;
	// 収入金額
	private final BigDecimal incomeKingaku;
	
	/**
	 *<pre>
	 * 引数の値からセッションに設定する収支登録情報を生成して返します。
	 *</pre>
	 * @param dataType データタイプ(新規 or ロード)
	 * @param action アクション(追加／更新／削除)
	 * @param incomeCode 収入コード(仮登録用収入コード)
	 * @param incomeKubun 収入区分
	 * @param incomeDetailContext 収入詳細
	 * @param incomeKingaku 収入金額
	 * @return 収支登録情報
	 *
	 */
	public static IncomeRegistItem from(
			String dataType,
			String action,
			String incomeCode,
			String incomeKubun,
			String incomeDetailContext,
			BigDecimal incomeKingaku) {
		return new IncomeRegistItem(dataType, action, incomeCode, incomeKubun, incomeDetailContext, incomeKingaku);
	}
}
