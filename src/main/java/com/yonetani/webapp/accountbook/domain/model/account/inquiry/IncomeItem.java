/**
 * 収入テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.DeleteFlg;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収入テーブル情報を表すドメインモデルです
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
public class IncomeItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 収入コード
	private final SyuunyuuCode syuunyuuCode;
	// 収入区分
	private final SyuunyuuKubun syuunyuuKubun;
	// 収入詳細
	private final SyuunyuuDetailContext syuunyuuDetailContext;
	// 収入金額
	private final SyuunyuuKingaku syuunyuuKingaku;
	// 削除フラグ
	private final DeleteFlg deleteFlg;
	
	/**
	 *<pre>
	 * 引数の値から収入テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param syuunyuuCode 収入コード
	 * @param syuunyuuKubun 収入区分
	 * @param syuunyuuDetailContext 収入詳細
	 * @param syuunyuuKingaku 収入金額
	 * @param deleteFlg 削除フラグ
	 * @return 収入テーブル情報を表すドメインモデル
	 *
	 */
	public static IncomeItem from(
			String userId,
			String targetYear,
			String targetMonth,
			String syuunyuuCode,
			String syuunyuuKubun,
			String syuunyuuDetailContext,
			BigDecimal syuunyuuKingaku,
			boolean deleteFlg) {
		return new IncomeItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				SyuunyuuCode.from(syuunyuuCode),
				SyuunyuuKubun.from(syuunyuuKubun),
				SyuunyuuDetailContext.from(syuunyuuDetailContext),
				SyuunyuuKingaku.from(syuunyuuKingaku),
				DeleteFlg.from(deleteFlg));
		
	}
}
