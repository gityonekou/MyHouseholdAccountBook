/**
 * 収支情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収支情報の値を表すドメインモデルです
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
public class IncomeAndExpenditureInquiryItem {
	// 対象月
	private final TargetMonth month;
	// 収入金額
	private final SyuunyuuKingaku syuunyuuKingaku;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final SisyutuKingaku sisyutuKingaku;
	// 収支
	private final SyuusiKingaku syuusiKingaku;
	
	/**
	 *<pre>
	 * 引数の値から収支情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param month 対象月(TARGET_MONTH)
	 * @param syuunyuuKingaku 収入金額(INCOME_KINGAKU)
	 * @param sisyutuYoteiKingaku 支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)
	 * @param sisyutuKingaku 支出金額(EXPENDITURE_KINGAKU)
	 * @param syuusiKingaku 収支(INCOME_AND_EXPENDITURE_KINGAKU)
	 * @return 収支情報のドメインモデル
	 *
	 */
	public static IncomeAndExpenditureInquiryItem from(
			String month,
			BigDecimal syuunyuuKingaku,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal sisyutuKingaku,
			BigDecimal syuusiKingaku) {
		return new IncomeAndExpenditureInquiryItem(
				TargetMonth.from(month),
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
	public static IncomeAndExpenditureInquiryItem fromEmpty() {
		return new IncomeAndExpenditureInquiryItem(null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		if(month == null || !StringUtils.hasLength(month.toString())) {
			return true;
		} else {
			return false;
		}
	}
}