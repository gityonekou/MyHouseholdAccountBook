/**
 * 支出項目テーブル(BASE)情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ParentSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemSort;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出項目テーブル(BASE)情報を表すドメインモデルです
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
public class SisyutuItemBase {
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 支出項目名
	private final SisyutuItemName sisyutuItemName;
	// 支出項目詳細内容
	private final SisyutuItemDetailContext sisyutuItemDetailContext;
	// 親支出項目コード
	private final ParentSisyutuItemCode parentSisyutuItemCode;
	// 支出項目レベル(1～5)
	private final SisyutuItemLevel sisyutuItemLevel;
	// 支出項目表示順
	private final SisyutuItemSort sisyutuItemSort;
	
	/**
	 *<pre>
	 * 引数の値から支出項目テーブル(BASE)情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @param sisyutuItemName 支出項目名
	 * @param sisyutuItemDetailContext 支出項目詳細内容
	 * @param parentSisyutuItemCode 親支出項目コード
	 * @param sisyutuItemLevel 支出項目レベル(1～5)
	 * @param sisyutuItemSort 支出項目表示順
	 * @return 支出項目テーブル(BASE)情報ドメインモデル
	 *
	 */
	public static SisyutuItemBase from(
			String sisyutuItemCode,
			String sisyutuItemName,
			String sisyutuItemDetailContext,
			String parentSisyutuItemCode,
			String sisyutuItemLevel,
			String sisyutuItemSort) {
		return new SisyutuItemBase(
				SisyutuItemCode.from(sisyutuItemCode),
				SisyutuItemName.from(sisyutuItemName),
				SisyutuItemDetailContext.from(sisyutuItemDetailContext),
				ParentSisyutuItemCode.from(parentSisyutuItemCode),
				SisyutuItemLevel.from(sisyutuItemLevel),
				SisyutuItemSort.from(sisyutuItemSort));
	}
	
	/**
	 *<pre>
	 * 各データの値をカンマ区切りの1行の文字列として返します。
	 *</pre>
	 * @return
	 *
	 */
	public String toLineString() {
		StringBuilder buff = new StringBuilder(150);
		buff.append(sisyutuItemCode)
		.append(',')
		.append(sisyutuItemName)
		.append(',')
		.append(sisyutuItemDetailContext)
		.append(',')
		.append(parentSisyutuItemCode)
		.append(',')
		.append(sisyutuItemLevel)
		.append(',')
		.append(sisyutuItemSort);
		return buff.toString();
	}
}
