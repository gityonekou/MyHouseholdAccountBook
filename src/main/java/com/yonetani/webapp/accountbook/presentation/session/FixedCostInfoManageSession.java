/**
 * 固定費情報管理画面のセッションスコープBeanです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.03  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 *<pre>
 * 固定費情報管理画面のセッションスコープBeanです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@Component
@SessionScope
@Data
public class FixedCostInfoManageSession implements Serializable {
	private static final long serialVersionUID = 1L;

	// 月別固定費一覧で最後に選択した月（"MM"形式 例:"03"）
	private String selectedMonth;
}
