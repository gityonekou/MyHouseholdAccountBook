/**
 * 支出テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ShiharaiDate;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
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
 * 支出テーブル情報を表すドメインモデルです
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
public class ExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 支出コード
	private final SisyutuCode sisyutuCode;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// イベントコード
	private final EventCode eventCode;
	// 支出名称
	private final SisyutuName sisyutuName;
	// 支出区分
	private final SisyutuKubun sisyutuKubun;
	// 支出詳細
	private final SisyutuDetailContext sisyutuDetailContext;
	// 支払日
	private final ShiharaiDate shiharaiDate;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final SisyutuKingaku sisyutuKingaku;
	// 削除フラグ
	private final DeleteFlg deleteFlg;
	
	/**
	 *<pre>
	 * 引数の値から支出テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param sisyutuCode 支出コード
	 * @param sisyutuItemCode 支出項目コード
	 * @param eventCode イベントコード
	 * @param sisyutuName 支出名称
	 * @param sisyutuKubun 支出区分
	 * @param sisyutuDetailContext 支出詳細
	 * @param shiharaiDate 支払日
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @param sisyutuKingaku 支出金額
	 * @param deleteFlg 削除フラグ
	 * @return 支出テーブル情報を表すドメインモデル
	 *
	 */
	public static ExpenditureItem from(
			String userId,
			String targetYear,
			String targetMonth,
			String sisyutuCode,
			String sisyutuItemCode,
			String eventCode,
			String sisyutuName,
			String sisyutuKubun,
			String sisyutuDetailContext,
			LocalDate shiharaiDate,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			boolean deleteFlg) {
		return new ExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				SisyutuCode.from(sisyutuCode),
				SisyutuItemCode.from(sisyutuItemCode),
				EventCode.from(eventCode),
				SisyutuName.from(sisyutuName),
				SisyutuKubun.from(sisyutuKubun),
				SisyutuDetailContext.from(sisyutuDetailContext),
				ShiharaiDate.from(shiharaiDate),
				SisyutuYoteiKingaku.from(sisyutuYoteiKingaku),
				SisyutuKingaku.from(sisyutuKingaku),
				DeleteFlg.from(deleteFlg));
		
	}
}
