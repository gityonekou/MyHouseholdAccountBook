/**
 * 支払方法テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.paymentmethod;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ClosingDay;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodMemo;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.EnableUpdateFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支払方法テーブル情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class PaymentMethod {
	// ユーザID
	private final UserId userId;
	// 支払方法コード
	private final PaymentMethodCode paymentMethodCode;
	// 支払方法名
	private final PaymentMethodName paymentMethodName;
	// 支払方法メモ(null許容)
	private final PaymentMethodMemo paymentMethodMemo;
	// 支払方法種別
	private final PaymentMethodKubun paymentMethodKubun;
	// 銀行口座コード(null許容。種別により要否が異なる)
	private final BankAccountCode bankAccountCode;
	// 集計開始日(null許容。クレジットカードのみ)
	private final ClosingDay closingDay;
	// 支払方法表示順
	private final PaymentMethodSort paymentMethodSort;
	// 有効/無効フラグ
	private final EnableFlg enableFlg;
	// 更新可否フラグ
	private final EnableUpdateFlg enableUpdateFlg;

	/**
	 *<pre>
	 * 引数の値から支払方法テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param paymentMethodCode 支払方法コード
	 * @param paymentMethodName 支払方法名
	 * @param paymentMethodMemo 支払方法メモ(null許容)
	 * @param paymentMethodKubun 支払方法種別
	 * @param bankAccountCode 銀行口座コード(null許容)
	 * @param closingDay 集計開始日(null許容)
	 * @param paymentMethodSort 支払方法表示順
	 * @param enableFlg 有効/無効フラグ
	 * @param enableUpdateFlg 更新可否フラグ
	 * @return 支払方法テーブル情報を表すドメインモデル
	 *
	 */
	public static PaymentMethod from(
			String userId,
			String paymentMethodCode,
			String paymentMethodName,
			String paymentMethodMemo,
			String paymentMethodKubun,
			String bankAccountCode,
			String closingDay,
			String paymentMethodSort,
			boolean enableFlg,
			boolean enableUpdateFlg) {
		return new PaymentMethod(
				UserId.from(userId),
				PaymentMethodCode.from(paymentMethodCode),
				PaymentMethodName.from(paymentMethodName),
				PaymentMethodMemo.from(paymentMethodMemo),
				PaymentMethodKubun.from(paymentMethodKubun),
				StringUtils.hasLength(bankAccountCode) ? BankAccountCode.from(bankAccountCode) : null,
				ClosingDay.from(closingDay),
				PaymentMethodSort.from(paymentMethodSort),
				EnableFlg.from(enableFlg),
				EnableUpdateFlg.from(enableUpdateFlg));
	}
}
