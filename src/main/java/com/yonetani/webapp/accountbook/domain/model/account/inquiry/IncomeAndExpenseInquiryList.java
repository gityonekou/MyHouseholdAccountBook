/**
 * 収支情報の明細リストの値を表すドメインモデルです。
 * 各明細のリスト情報と合計値をラッピングしています。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支情報の明細リストの値を表すドメインモデルです。
 * 各明細のリスト情報と合計値をラッピングしています。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenseInquiryList {
	
	// 年間収支(マージ)情報のリスト
	private final List<IncomeAndExpenseInquiryItem> values;
	
	// 収入金額合計
	private final SyuunyuuKingaku syuunyuuKingakuGoukei;
	// 支出金額合計
	private final SisyutuKingaku sisyutuKingakuGoukei;
	// 支出予定金額合計
	private final SisyutuYoteiKingaku sisyutuYoteiKingakuGoukei;
	// 収支合計
	private final SyuusiKingaku syuusiKingakuGoukei;
	
	/**
	 *<pre>
	 * 収支情報の値を表すドメインモデルのリストから収支情報の明細リストの値を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param values 収支情報の値を表すドメインモデルのリスト
	 * @return 明細リストの値を表すドメインモデル
	 *
	 */
	public static IncomeAndExpenseInquiryList from(List<IncomeAndExpenseInquiryItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new IncomeAndExpenseInquiryList(
					Collections.emptyList(),
					SyuunyuuKingaku.from(BigDecimal.ZERO),
					SisyutuKingaku.from(BigDecimal.ZERO),
					SisyutuYoteiKingaku.from(BigDecimal.ZERO),
					SyuusiKingaku.from(BigDecimal.ZERO));
		} else {
			// 各種合計値を計算
			BigDecimal syuunyuuKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal sisyutuKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal sisyutuYoteiKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal syuusiKingakuGoukeiWk = BigDecimal.ZERO;
			for(IncomeAndExpenseInquiryItem item : values) {
				syuunyuuKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(syuunyuuKingakuGoukeiWk, item.getSyuunyuuKingaku().getValue());
				sisyutuKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(sisyutuKingakuGoukeiWk, item.getSisyutuKingaku().getValue());
				sisyutuYoteiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(sisyutuYoteiKingakuGoukeiWk, item.getSisyutuYoteiKingaku().getValue());
				syuusiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(syuusiKingakuGoukeiWk, item.getSyuusiKingaku().getValue());
			}
			return new IncomeAndExpenseInquiryList(
					values,
					SyuunyuuKingaku.from(syuunyuuKingakuGoukeiWk),
					SisyutuKingaku.from(sisyutuKingakuGoukeiWk),
					SisyutuYoteiKingaku.from(sisyutuYoteiKingakuGoukeiWk),
					SyuusiKingaku.from(syuusiKingakuGoukeiWk));
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 2) * 110);
			buff.append("収支情報の明細リスト:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			buff.append("[[合計][syuunyuuKingakuGoukei:")
			.append(syuunyuuKingakuGoukei)
			.append(",sisyutuKingakuGoukei:")
			.append(sisyutuKingakuGoukei)
			.append(",sisyutuYoteiKingakuGoukei:")
			.append(sisyutuYoteiKingakuGoukei)
			.append(",syuusiKingakuGoukei:")
			.append(syuusiKingakuGoukei)
			.append("]]");
			return buff.toString();
		} else {
			return "収支情報の明細リスト:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
