/**
 * 指定年度の年間収支(明細)情報のリストの値を表すドメインモデルです。
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

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.InsyokuNitiyouhinKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.IruiJyuukyoSetubiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.JigyouKeihiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiHikazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiKazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuB;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuBC;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuC;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyumiGotakuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuusiKingaku;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 指定年度の年間収支(明細)情報のリストの値を表すドメインモデルです。
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
public class AccountYearMeisaiInquiryList {
	/**
	 *<pre>
	 * 年間収支(明細)情報(ドメインモデル)です
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
	public static class MeisaiInquiryListItem {
		// 対象月
		private final TargetMonth month;
		// 事業経費
		private final JigyouKeihiKingaku jigyouKeihiKingaku;
		// 固定(非課税)
		private final KoteiHikazeiKingaku koteiHikazeiKingaku;
		// 固定(課税)
		private final KoteiKazeiKingaku koteiKazeiKingaku;
		// 衣類住居設備
		private final IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingaku;
		// 飲食日用品
		private final InsyokuNitiyouhinKingaku insyokuNitiyouhinKingaku;
		// 趣味娯楽
		private final SyumiGotakuKingaku syumiGotakuKingaku;
		// 支出BC
		private final SisyutuKingakuBC sisyutuKingakuBC;
		// 支出
		private final SisyutuKingaku sisyutuKingaku;
		// 収支
		private final SyuusiKingaku syuusiKingaku;
		
		/**
		 *<pre>
		 * 引数の値から年間収支(明細)情報のドメインモデルを生成して返します。
		 *</pre>
		 * @param month 対象月
		 * @param jigyouKeihiKingaku 事業経費
		 * @param koteiHikazeiKingaku 固定(非課税)
		 * @param koteiKazeiKingaku 固定(課税)
		 * @param iruiJyuukyoSetubiKingaku 衣類住居設備
		 * @param insyokuNitiyouhinKingaku 飲食日用品
		 * @param syumiGotakuKingaku 趣味娯楽
		 * @param sisyutuKingakuB 支出B
		 * @param sisyutuKingakuC 支出C
		 * @param sisyutuKingaku 支出
		 * @param syuusiKingaku 収支
		 * @return 年間収支(明細)情報のドメインモデル
		 *
		 */
		public static MeisaiInquiryListItem from(
				String month,
				BigDecimal jigyouKeihiKingaku,
				BigDecimal koteiHikazeiKingaku,
				BigDecimal koteiKazeiKingaku,
				BigDecimal iruiJyuukyoSetubiKingaku,
				BigDecimal insyokuNitiyouhinKingaku,
				BigDecimal syumiGotakuKingaku,
				BigDecimal sisyutuKingakuB,
				BigDecimal sisyutuKingakuC,
				BigDecimal sisyutuKingaku,
				BigDecimal syuusiKingaku
				) {
			return new MeisaiInquiryListItem(
					TargetMonth.from(month),
					JigyouKeihiKingaku.from(jigyouKeihiKingaku),
					KoteiHikazeiKingaku.from(koteiHikazeiKingaku),
					KoteiKazeiKingaku.from(koteiKazeiKingaku),
					IruiJyuukyoSetubiKingaku.from(iruiJyuukyoSetubiKingaku),
					InsyokuNitiyouhinKingaku.from(insyokuNitiyouhinKingaku),
					SyumiGotakuKingaku.from(syumiGotakuKingaku),
					SisyutuKingakuBC.from(SisyutuKingakuB.from(sisyutuKingakuB), SisyutuKingakuC.from(sisyutuKingakuC)),
					SisyutuKingaku.from(sisyutuKingaku),
					SyuusiKingaku.from(syuusiKingaku));
		}
	}
	
	// 年間収支(明細)情報のリスト
	private final List<MeisaiInquiryListItem> values;
	// 事業経費合計
	private final JigyouKeihiKingaku jigyouKeihiKingakuGoukei;
	// 固定(非課税)合計
	private final KoteiHikazeiKingaku koteiHikazeiKingakuGoukei;
	// 固定(課税)合計
	private final KoteiKazeiKingaku koteiKazeiKingakuGoukei;
	// 衣類住居設備合計
	private final IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingakuGoukei;
	// 飲食日用品合計
	private final InsyokuNitiyouhinKingaku insyokuNitiyouhinKingakuGoukei;
	// 趣味娯楽合計
	private final SyumiGotakuKingaku syumiGotakuKingakuGoukei;
	// 支出BC合計
	private final SisyutuKingakuBC sisyutuKingakuBCGoukei;
	// 支出合計
	private final SisyutuKingaku sisyutuKingakuGoukei;
	// 収支合計
	private final SyuusiKingaku syuusiKingakuGoukei;
	
	/**
	 *<pre>
	 * 年間収支(明細)情報(ドメインモデル)のリストからAccountYearMeisaiInquiryListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 年間収支(明細)情報(ドメインモデル)のリスト
	 * @return AccountYearMeisaiInquiryListのドメインモデル
	 *
	 */
	public static AccountYearMeisaiInquiryList from(List<MeisaiInquiryListItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AccountYearMeisaiInquiryList(
					Collections.emptyList(),
					JigyouKeihiKingaku.from(BigDecimal.ZERO),
					KoteiHikazeiKingaku.from(BigDecimal.ZERO),
					KoteiKazeiKingaku.from(BigDecimal.ZERO),
					IruiJyuukyoSetubiKingaku.from(BigDecimal.ZERO),
					InsyokuNitiyouhinKingaku.from(BigDecimal.ZERO),
					SyumiGotakuKingaku.from(BigDecimal.ZERO),
					SisyutuKingakuBC.ZERO,
					SisyutuKingaku.ZERO,
					SyuusiKingaku.ZERO
					);
		} else {
			// 各種合計値を計算
			BigDecimal jigyouKeihiKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal koteiHikazeiKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal koteiKazeiKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal iruiJyuukyoSetubiKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal insyokuNitiyouhinKingakuGoukeiWk = BigDecimal.ZERO;
			BigDecimal syumiGotakuKingakuGoukeiWk = BigDecimal.ZERO;
			SisyutuKingakuBC sisyutuKingakuBCGoukei = SisyutuKingakuBC.ZERO;
			BigDecimal sisyutuKingakuGoukeiWk = BigDecimal.ZERO.setScale(2);
			BigDecimal syuusiKingakuGoukeiWk = BigDecimal.ZERO.setScale(2);
			
			for(MeisaiInquiryListItem item : values) {
				jigyouKeihiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(jigyouKeihiKingakuGoukeiWk, item.getJigyouKeihiKingaku().getValue());
				koteiHikazeiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(koteiHikazeiKingakuGoukeiWk, item.getKoteiHikazeiKingaku().getValue());
				koteiKazeiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(koteiKazeiKingakuGoukeiWk, item.getKoteiKazeiKingaku().getValue());
				iruiJyuukyoSetubiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(iruiJyuukyoSetubiKingakuGoukeiWk, item.getIruiJyuukyoSetubiKingaku().getValue());
				insyokuNitiyouhinKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(insyokuNitiyouhinKingakuGoukeiWk, item.getInsyokuNitiyouhinKingaku().getValue());
				syumiGotakuKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(syumiGotakuKingakuGoukeiWk, item.getSyumiGotakuKingaku().getValue());
				sisyutuKingakuBCGoukei = sisyutuKingakuBCGoukei.add(item.getSisyutuKingakuBC());
				sisyutuKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(sisyutuKingakuGoukeiWk, item.getSisyutuKingaku().getValue());
				syuusiKingakuGoukeiWk = DomainCommonUtils.addBigDecimalNullSafe(syuusiKingakuGoukeiWk, item.getSyuusiKingaku().getValue());
			}
			return new AccountYearMeisaiInquiryList(
					values,
					JigyouKeihiKingaku.from(jigyouKeihiKingakuGoukeiWk),
					KoteiHikazeiKingaku.from(koteiHikazeiKingakuGoukeiWk),
					KoteiKazeiKingaku.from(koteiKazeiKingakuGoukeiWk),
					IruiJyuukyoSetubiKingaku.from(iruiJyuukyoSetubiKingakuGoukeiWk),
					InsyokuNitiyouhinKingaku.from(insyokuNitiyouhinKingakuGoukeiWk),
					SyumiGotakuKingaku.from(syumiGotakuKingakuGoukeiWk),
					sisyutuKingakuBCGoukei,
					SisyutuKingaku.from(sisyutuKingakuGoukeiWk),
					SyuusiKingaku.from(syuusiKingakuGoukeiWk)
					);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 2) * 320);
			buff.append("年間収支(明細)情報:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			buff.append("[[合計][jigyouKeihiKingakuGoukei:")
			.append(jigyouKeihiKingakuGoukei)
			.append(",koteiHikazeiKingakuGoukei:")
			.append(koteiHikazeiKingakuGoukei)
			.append(",koteiKazeiKingakuGoukei:")
			.append(koteiKazeiKingakuGoukei)
			.append(",iruiJyuukyoSetubiKingakuGoukei:")
			.append(iruiJyuukyoSetubiKingakuGoukei)
			.append(",insyokuNitiyouhinKingakuGoukei:")
			.append(insyokuNitiyouhinKingakuGoukei)
			.append(",syumiGotakuKingakuGoukei:")
			.append(syumiGotakuKingakuGoukei)
			.append(",sisyutuKingakuBGoukei:")
			.append(sisyutuKingakuBCGoukei)
			.append(",sisyutuKingakuGoukei:")
			.append(sisyutuKingakuGoukei)
			.append(",syuusiKingakuGoukei:")
			.append(syuusiKingakuGoukei)
			.append("]]");
			return buff.toString();
		} else {
			return "年間収支(明細)情報:0件";
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
