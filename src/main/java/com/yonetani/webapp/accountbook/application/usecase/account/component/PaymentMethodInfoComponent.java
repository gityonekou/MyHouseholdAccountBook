/**
 * 支払方法コード→表示名／銀行口座名の解決、および選択肢生成を行うコンポーネントです。
 * 固定費登録・収支登録・買い物登録・月別収支照会など複数のUseCaseから共通利用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.bankaccount.BankAccountTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支払方法コード→表示名／銀行口座名の解決、および選択肢生成を行うコンポーネントです。
 * 固定費登録・収支登録・買い物登録・月別収支照会など複数のUseCaseから共通利用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Component
@RequiredArgsConstructor
public class PaymentMethodInfoComponent {
	// 支払方法情報取得リポジトリー
	private final PaymentMethodTableRepository paymentMethodRepository;
	// 銀行口座情報取得リポジトリー
	private final BankAccountTableRepository bankAccountRepository;

	/**
	 *<pre>
	 * 画面表示の起点(UseCaseのread系メソッド)で1回だけ呼び出し、以降は戻り値のリゾルバをループ内で使い回してください。
	 * 内部で支払方法マスタ・銀行口座マスタをそれぞれ1回ずつ(合計2回)取得し、メモリ上のMapで名称解決するリゾルバを構築します。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @return 支払方法コード→表示名／銀行口座名を解決するリゾルバ
	 *
	 */
	public PaymentMethodNameResolver createResolver(UserId userId) {
		PaymentMethodInquiryList paymentMethods = paymentMethodRepository.findByUserId(SearchQueryUserId.from(userId));
		BankAccountInquiryList bankAccounts = bankAccountRepository.findById(SearchQueryUserId.from(userId));

		Map<PaymentMethodCode, PaymentMethod> paymentMethodMap = paymentMethods.getValues().stream()
				.collect(Collectors.toMap(PaymentMethod::getPaymentMethodCode, Function.identity()));
		Map<BankAccountCode, BankAccount> bankAccountMap = bankAccounts.getValues().stream()
				.collect(Collectors.toMap(BankAccount::getBankAccountCode, Function.identity()));

		return new PaymentMethodNameResolver(paymentMethodMap, bankAccountMap);
	}

	/**
	 *<pre>
	 * 呼び出し頻度が低い単発の解決(1件だけ表示するような画面)向けの薄いラッパーです。
	 * 一覧・複数行を扱う画面では必ずcreateResolver()を使ってください(N+1回避)。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @param code 支払方法コード
	 * @return 支払方法名（解決済み）の値オブジェクト(システム予約値・未登録の場合は「－」)
	 *
	 */
	public ResolvedPaymentMethodName getPaymentMethodName(UserId userId, PaymentMethodCode code) {
		return createResolver(userId).getPaymentMethodName(code);
	}

	/**
	 *<pre>
	 * 呼び出し頻度が低い単発の解決(1件だけ表示するような画面)向けの薄いラッパーです。
	 * 一覧・複数行を扱う画面では必ずcreateResolver()を使ってください(N+1回避)。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @param code 支払方法コード
	 * @return 銀行口座名（解決済み）の値オブジェクト(システム予約値・銀行口座なし・未登録の場合は「－」)
	 *
	 */
	public ResolvedBankAccountName getBankAccountName(UserId userId, PaymentMethodCode code) {
		return createResolver(userId).getBankAccountName(code);
	}

	/**
	 *<pre>
	 * ログインユーザの選択可能な支払方法一覧を選択ボックス用に取得します。
	 * 内部でfindSelectableByUserId()を使用するため、システム予約値・無効な支払方法は含まれません。
	 * 収支登録・買い物登録・店舗マスタのデフォルト支払方法選択で使用します。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @return 支払方法の選択肢のリスト
	 *
	 */
	public List<OptionItem> getPaymentMethodOptions(UserId userId) {
		PaymentMethodInquiryList list = paymentMethodRepository.findSelectableByUserId(SearchQueryUserId.from(userId));
		return list.getValues().stream()
				.map(pm -> OptionItem.from(pm.getPaymentMethodCode().getValue(), pm.getPaymentMethodName().getValue()))
				.collect(Collectors.toList());
	}

	/**
	 *<pre>
	 * ログインユーザの固定費登録画面用の支払方法一覧を選択ボックス用に取得します。
	 * 内部でfindEnabledByUserId()を使用するため、有効な支払方法であればシステム予約値(「支払方法がない」)も含まれます。
	 * 固定費登録画面専用（買い物集計8項目の登録で「支払方法がない」を選択できる必要があるため）。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @return 支払方法の選択肢のリスト
	 *
	 */
	public List<OptionItem> getFixedCostPaymentMethodOptions(UserId userId) {
		PaymentMethodInquiryList list = paymentMethodRepository.findEnabledByUserId(SearchQueryUserId.from(userId));
		return list.getValues().stream()
				.map(pm -> OptionItem.from(pm.getPaymentMethodCode().getValue(), pm.getPaymentMethodName().getValue()))
				.collect(Collectors.toList());
	}

	/**
	 *<pre>
	 * ログインユーザの選択可能な銀行口座一覧を選択ボックス用に取得します。
	 * 内部でBankAccountTableRepository.findSelectableByUserId()を使用するため、無効な口座は含まれません。
	 * 支払方法マスタ管理画面の銀行口座選択で使用します。
	 *</pre>
	 * @param userId 対象のユーザID
	 * @return 銀行口座の選択肢のリスト
	 *
	 */
	public List<OptionItem> getBankAccountOptions(UserId userId) {
		BankAccountInquiryList list = bankAccountRepository.findSelectableByUserId(SearchQueryUserId.from(userId));
		return list.getValues().stream()
				.map(ba -> OptionItem.from(ba.getBankAccountCode().getValue(), ba.getBankName().getValue()))
				.collect(Collectors.toList());
	}

	/**
	 *<pre>
	 * 支払方法コード→支払方法名／銀行口座名 の解決を、DBアクセスなしでメモリ上のMap参照のみで行うリゾルバです。
	 * PaymentMethodInfoComponent.createResolver()で1度構築すれば、以降の名称解決はすべてこのリゾルバで完結します。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.03)
	 *
	 */
	@RequiredArgsConstructor
	public static class PaymentMethodNameResolver {
		// 支払方法コード→支払方法情報のMap
		private final Map<PaymentMethodCode, PaymentMethod> paymentMethodMap;
		// 銀行口座コード→銀行口座情報のMap
		private final Map<BankAccountCode, BankAccount> bankAccountMap;

		/**
		 *<pre>
		 * 支払方法コードに対応する支払方法名を解決します。
		 * システム予約値(999等)、またはマスタに存在しないコードの場合は「－」を返します。
		 *</pre>
		 * @param code 支払方法コード
		 * @return 支払方法名（解決済み）の値オブジェクト。解決できない場合の値オブジェクト格納値は「－」
		 *
		 */
		public ResolvedPaymentMethodName getPaymentMethodName(PaymentMethodCode code) {
			if(code == null || code.isSystemReserved()) {
				return ResolvedPaymentMethodName.UNRESOLVED;
			}
			PaymentMethod paymentMethod = paymentMethodMap.get(code);
			return paymentMethod == null ? 
					ResolvedPaymentMethodName.UNRESOLVED : 
						ResolvedPaymentMethodName.from(paymentMethod.getPaymentMethodName().getValue());
		}

		/**
		 *<pre>
		 * 支払方法コードに対応する銀行口座名を解決します。
		 * システム予約値、対応する支払方法の銀行口座コードがnull(現金・電子マネー(前払い式))、
		 * またはマスタに存在しないコードの場合は「－」を返します。
		 *</pre>
		 * @param code 支払方法コード
		 * @return 銀行口座名（解決済み）の値オブジェクト。解決できない場合は「－」
		 *
		 */
		public ResolvedBankAccountName getBankAccountName(PaymentMethodCode code) {
			if(code == null || code.isSystemReserved()) {
				return ResolvedBankAccountName.UNRESOLVED;
			}
			PaymentMethod paymentMethod = paymentMethodMap.get(code);
			if(paymentMethod == null || paymentMethod.getBankAccountCode() == null) {
				return ResolvedBankAccountName.UNRESOLVED;
			}
			BankAccount bankAccount = bankAccountMap.get(paymentMethod.getBankAccountCode());
			return bankAccount == null ? 
					ResolvedBankAccountName.UNRESOLVED :
						ResolvedBankAccountName.from(bankAccount.getBankName().getValue());
		}
	}
	
	/**
	 *<pre>
	 * 支払方法コードに対応する支払方法名を解決した結果を保持する値オブジェクトです
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.03)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class ResolvedPaymentMethodName {
		// 解決できない場合の値オブジェクト
		public static final ResolvedPaymentMethodName UNRESOLVED = new ResolvedPaymentMethodName("－");
		// 支払方法名（解決済み）
		private final String value;
		
		/**
		 *<pre>
		 * 支払方法名（解決済み）の値オブジェクトを生成します。
		 *</pre>
		 * @param value 支払方法名（解決済み）
		 * @return 支払方法名（解決済み）値オブジェクト「ResolvedPaymentMethodName」
		 *
		 */
		private static ResolvedPaymentMethodName from(String value) {
			return new ResolvedPaymentMethodName(value);
		}
	}
	
	/**
	 *<pre>
	 * 支払方法コードに対応する銀行口座名を解決した結果を保持する値オブジェクトです
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.03)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class ResolvedBankAccountName {
		// 解決できない場合の値オブジェクト
		public static final ResolvedBankAccountName UNRESOLVED = new ResolvedBankAccountName("－");
		// 銀行口座名（解決済み）
		private final String value;
		
		/**
		 *<pre>
		 * 銀行口座名（解決済み）の値オブジェクトを生成します。
		 *</pre>
		 * @param value 銀行口座名（解決済み）
		 * @return 銀行口座名（解決済み）値オブジェクト「ResolvedBankAccountName」
		 *
		 */
		private static ResolvedBankAccountName from(String value) {
			return new ResolvedBankAccountName(value);
		}
	}
}

