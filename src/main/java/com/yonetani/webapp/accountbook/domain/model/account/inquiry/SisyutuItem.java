/**
 * 支出項目テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ParentSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemLevel;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支出項目テーブル情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SisyutuItem {
	// ユーザID
	private final UserId userId;
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
	 * 引数の値から支出項目テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemCode 支出項目コード
	 * @param sisyutuItemName 支出項目名
	 * @param sisyutuItemDetailContext 支出項目詳細内容
	 * @param parentSisyutuItemCode 親支出項目コード
	 * @param sisyutuItemLevel 支出項目レベル(1～5)
	 * @param sisyutuItemSort 支出項目表示順
	 * @return 支出項目テーブル情報を表すドメインモデル
	 *
	 */
	public static SisyutuItem from(
			String userId,
			String sisyutuItemCode,
			String sisyutuItemName,
			String sisyutuItemDetailContext,
			String parentSisyutuItemCode,
			String sisyutuItemLevel,
			String sisyutuItemSort) {
		return new SisyutuItem(
				UserId.from(userId),
				SisyutuItemCode.from(sisyutuItemCode),
				SisyutuItemName.from(sisyutuItemName),
				SisyutuItemDetailContext.from(sisyutuItemDetailContext),
				ParentSisyutuItemCode.from(parentSisyutuItemCode),
				SisyutuItemLevel.from(sisyutuItemLevel),
				SisyutuItemSort.from(sisyutuItemSort));
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(240);
		buff.append("userId:")
		.append(userId)
		.append(",sisyutuItemCode:")
		.append(sisyutuItemCode)
		.append(",sisyutuItemName:")
		.append(sisyutuItemName)
		.append(",sisyutuItemDetailContext:")
		.append(sisyutuItemDetailContext)
		.append(",parentSisyutuItemCode:")
		.append(parentSisyutuItemCode)
		.append(",sisyutuItemLevel:")
		.append(sisyutuItemLevel)
		.append(",sisyutuItemSort:")
		.append(sisyutuItemSort);
		return buff.toString();
	}
}
