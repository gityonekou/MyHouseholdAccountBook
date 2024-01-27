/**
 * コード定義テーブルの参照を行うリポジトリーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.common;

import com.yonetani.webapp.accountbook.domain.model.common.CodeTableItemList;

/**
 *<pre>
 * コード定義テーブルの参照を行うリポジトリーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface CodeTableRepository {

	/**
	 *<pre>
	 * コード定義テーブルの情報を全件取得します。
	 *</pre>
	 * @return コード定義テーブルファイルの取得結果(リスト情報)の値を表すドメインモデル
	 *
	 */
	CodeTableItemList findAll();
}
