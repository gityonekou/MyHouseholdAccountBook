/**
 * 固定費情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/27 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostKubun;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentDay;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonthOptionalContext;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 固定費情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class FixedCost {
	// ユーザID
	private final UserId userId;
	// 固定費コード
	private final FixedCostCode fixedCostCode;
	// 固定費名(支払名)
	private final FixedCostName fixedCostName;
	// 固定費内容詳細(支払内容詳細)
	private final FixedCostDetailContext fixedCostDetailContext;
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	// 固定費区分
	private final FixedCostKubun fixedCostKubun;
	// 固定費支払月(支払月)
	private final FixedCostTargetPaymentMonth fixedCostTargetPaymentMonth;
	// 固定費支払月任意詳細
	private final FixedCostTargetPaymentMonthOptionalContext fixedCostTargetPaymentMonthOptionalContext;
	// 固定費支払日(支払日)
	private final FixedCostPaymentDay fixedCostPaymentDay;
	// 支払金額
	private final FixedCostPaymentAmount fixedCostPaymentAmount;
	
	/**
	 *<pre>
	 * 引数の値から固定費情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param fixedCostCode 固定費コード
	 * @param fixedCostName 固定費名(支払名)
	 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
	 * @param expenditureItemCode 支出項目コード
	 * @param fixedCostKubun 固定費区分
	 * @param fixedCostTargetPaymentMonth 固定費支払月(支払月)
	 * @param fixedCostTargetPaymentMonthOptionalContext 固定費支払月任意詳細
	 * @param fixedCostPaymentDay 固定費支払日(支払日)
	 * @param fixedCostPaymentAmount 支払金額
	 * @return 固定費情報を表すドメインモデル
	 *
	 */
	public static FixedCost from(
			String userId,
			String fixedCostCode,
			String fixedCostName,
			String fixedCostDetailContext,
			String expenditureItemCode,
			String fixedCostKubun,
			String fixedCostTargetPaymentMonth,
			String fixedCostTargetPaymentMonthOptionalContext,
			String fixedCostPaymentDay,
			BigDecimal fixedCostPaymentAmount) {
		return new FixedCost(
				UserId.from(userId),
				FixedCostCode.from(fixedCostCode),
				FixedCostName.from(fixedCostName),
				FixedCostDetailContext.from(fixedCostDetailContext),
				ExpenditureItemCode.from(expenditureItemCode),
				FixedCostKubun.from(fixedCostKubun),
				FixedCostTargetPaymentMonth.from(fixedCostTargetPaymentMonth),
				FixedCostTargetPaymentMonthOptionalContext.from(fixedCostTargetPaymentMonthOptionalContext),
				FixedCostPaymentDay.from(fixedCostPaymentDay),
				FixedCostPaymentAmount.from(fixedCostPaymentAmount));
	}
	
	/**
	 *<pre>
	 * 引数の値から固定費情報を表すドメインモデルを生成して返します。
	 * 入力フォームからドメインを生成する際にこのメソッドを使用することを想定しています。
	 *</pre>
	 * @param userId ユーザID
	 * @param fixedCostCode 固定費コード
	 * @param fixedCostName 固定費名(支払名)
	 * @param fixedCostDetailContext 固定費内容詳細(支払内容詳細)
	 * @param expenditureItemCode 支出項目コード
	 * @param fixedCostKubun 固定費区分
	 * @param fixedCostTargetPaymentMonth 固定費支払月(支払月)
	 * @param fixedCostTargetPaymentMonthOptionalContext 固定費支払月任意詳細
	 * @param fixedCostPaymentDay 固定費支払日(支払日)
	 * @param fixedCostPaymentAmount 支払金額
	 * @return 固定費情報を表すドメインモデル
	 *
	 */
	public static FixedCost from(
			String userId,
			String fixedCostCode,
			String fixedCostName,
			String fixedCostDetailContext,
			String expenditureItemCode,
			String fixedCostKubun,
			String fixedCostTargetPaymentMonth,
			String fixedCostTargetPaymentMonthOptionalContext,
			String fixedCostPaymentDay,
			Integer fixedCostPaymentAmount) {
		return new FixedCost(
				UserId.from(userId),
				FixedCostCode.from(fixedCostCode),
				FixedCostName.from(fixedCostName),
				FixedCostDetailContext.from(fixedCostDetailContext),
				ExpenditureItemCode.from(expenditureItemCode),
				FixedCostKubun.from(fixedCostKubun),
				FixedCostTargetPaymentMonth.from(fixedCostTargetPaymentMonth),
				FixedCostTargetPaymentMonthOptionalContext.from(fixedCostTargetPaymentMonthOptionalContext),
				FixedCostPaymentDay.from(fixedCostPaymentDay),
				FixedCostPaymentAmount.from(fixedCostPaymentAmount));
	}
	
	/**
	 *<pre>
	 * 一括更新項目（支払日、支払金額）の値を更新した新しい固定費情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param fixedCostPaymentDay 固定費支払日(支払日)
	 * @param fixedCostPaymentAmount 支払金額
	 * @return 一括更新項目（支払日、支払金額）の値を更新した新しい固定費情報を表すドメインモデル
	 *
	 */
	public FixedCost updateBulkUpdateItem(String fixedCostPaymentDay, Integer fixedCostPaymentAmount) {
		return new FixedCost(
				UserId.from(userId.getValue()),
				FixedCostCode.from(fixedCostCode.getValue()),
				FixedCostName.from(fixedCostName.getValue()),
				FixedCostDetailContext.from(fixedCostDetailContext.getValue()),
				ExpenditureItemCode.from(expenditureItemCode.getValue()),
				FixedCostKubun.from(fixedCostKubun.getValue()),
				FixedCostTargetPaymentMonth.from(fixedCostTargetPaymentMonth.getValue()),
				FixedCostTargetPaymentMonthOptionalContext.from(fixedCostTargetPaymentMonthOptionalContext.getValue()),
				FixedCostPaymentDay.from(fixedCostPaymentDay),
				FixedCostPaymentAmount.from(fixedCostPaymentAmount));
	}
	
	/**
	 *<pre>
	 * 「固定費内容詳細(支払内容詳細)」項目と「固定費支払月任意詳細」項目の値から支出詳細の表示値を作成して返します。
	 * 
	 * 固定費内容詳細(支払内容詳細)項目、固定費支払月任意詳細のどちらかに値が設定されている場合、その値を返します。
	 * 固定費内容詳細(支払内容詳細)項目と固定費支払月任意詳細両方に値が設定されている場合、各値を「/」で連結した値を返します。
	 *</pre>
	 * @return 支出詳細の表示値
	 *
	 */
	public String getExpenditureDetailContext() {
		StringBuilder buff = new StringBuilder();
		// 固定費内容詳細(支払内容詳細)の値が設定されている場合、支出詳細の表示値に追加
		if(StringUtils.hasLength(fixedCostDetailContext.getValue())) {
			buff.append(fixedCostDetailContext.getValue());
		}
		// 固定費支払月任意詳細の値が設定されている場合、支出詳細の表示値に追加
		if(StringUtils.hasLength(fixedCostTargetPaymentMonthOptionalContext.getValue())) {
			// 固定費内容詳細(支払内容詳細)の値が設定済みの場合、区切り文字(/)を追加
			if(buff.length() > 0) {
				buff.append('/');
			}
			buff.append(fixedCostTargetPaymentMonthOptionalContext.getValue());
		}
		return buff.toString();
	}
}
