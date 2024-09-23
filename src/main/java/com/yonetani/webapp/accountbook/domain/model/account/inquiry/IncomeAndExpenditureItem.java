/**
 * 収支テーブル情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
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
 * 収支テーブル情報の値を表すドメインモデルです
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
public class IncomeAndExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 収入金額
	private final SyuunyuuKingaku syuunyuuKingaku;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final SisyutuKingaku sisyutuKingaku;
	// 収支金額
	private final SyuusiKingaku syuusiKingaku;
	
	/**
	 *<pre>
	 * 引数の値から収支テーブル情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param syuunyuuKingaku 収入金額
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @param sisyutuKingaku 支出金額
	 * @param syuusiKingaku 収支金額
	 * @return 収支テーブル情報を表すドメインモデル
	 *
	 */
	public static IncomeAndExpenditureItem from(
			String userId,
			String targetYear,
			String targetMonth,
			BigDecimal syuunyuuKingaku,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			BigDecimal syuusiKingaku) {
		return new IncomeAndExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				SyuunyuuKingaku.from(syuunyuuKingaku),
				SisyutuYoteiKingaku.from(sisyutuYoteiKingaku),
				SisyutuKingaku.from(sisyutuKingaku),
				SyuusiKingaku.from(syuusiKingaku));
	}
	
	/**
	 *<pre>
	 * 値が空となるドメインモデルを生成して返します。
	 *</pre>
	 * @return
	 *
	 */
	public static IncomeAndExpenditureItem fromEmpty() {
		return new IncomeAndExpenditureItem(null, null, null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		if(userId == null) {
			return true;
		} else {
			return false;
		}
	}
}
