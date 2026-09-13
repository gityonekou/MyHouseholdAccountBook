/**
 * 「有効/無効フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は有効（選択肢・集計に使う）、falseの時は無効（選択肢・集計から除外）を表す値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「有効/無効フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は有効（選択肢・集計に使う）、falseの時は無効（選択肢・集計から除外）を表す値となります。
 *
 * `EnableUpdateFlg`（更新可否フラグ）とは別概念であり、混同しないこと。
 * ・`ENABLE_FLG`：このマスタ行を選択肢・集計に使うかどうか（ユーザーが画面上でON/OFFを切り替える）
 * ・`ENABLE_UPDATE_FLG`：このマスタ行をユーザーが編集できるかどうか（システムが固定。システム予約行のみfalse）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class EnableFlg {
	private final boolean value;

	/**
	 *<pre>
	 * 「有効/無効フラグ」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param enableFlg 有効/無効フラグ trueの時は有効、falseの時は無効
	 * @return 「有効/無効フラグ」項目ドメインタイプ
	 *
	 */
	public static EnableFlg from(boolean enableFlg) {
		return new EnableFlg(enableFlg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	/**
	 *<pre>
	 * 「有効/無効フラグ」項目の値を取得します。
	 *</pre>
	 * @return 有効/無効フラグ
	 *
	 */
	public boolean getValue() {
		return value;
	}
}
