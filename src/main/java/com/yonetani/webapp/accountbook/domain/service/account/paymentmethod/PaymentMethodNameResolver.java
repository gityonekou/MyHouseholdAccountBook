/**
 * 支払方法コード→支払方法名／銀行口座名の解決を、メモリ上のMap参照のみで行うドメインサービスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.service.account.paymentmethod;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ResolvedBankAccountName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ResolvedPaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopDefaultPaymentMethodCode;

/**
 *<pre>
 * 支払方法コード→支払方法名／銀行口座名の解決を、DBアクセスなしでメモリ上のMap参照のみで行うドメインサービスです。
 * 支払方法・銀行口座のマスタ情報は呼び出し側(PaymentMethodInfoComponent)で取得済みのものを受け取ります。
 *
 * [なぜドメインサービスか]
 * ・「システム予約値は『－』と表示する」「現金・電子マネー(前払い式)は銀行口座名を持たない」といった表示変換の業務ルールである
 * ・複数の支払方法・銀行口座の情報にまたがって解決する必要があるため、単一の集約では完結しない
 * ・DBアクセスを伴わないため、Spring管理(@Component/@Service)にせず通常のクラスとして生成する
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public class PaymentMethodNameResolver {
	// 支払方法コード→支払方法情報のMap
	private final Map<PaymentMethodCode, PaymentMethod> paymentMethodMap;
	// 銀行口座コード→銀行口座情報のMap
	private final Map<BankAccountCode, BankAccount> bankAccountMap;

	/**
	 *<pre>
	 * 取得済みの支払方法マスタ・銀行口座マスタからリゾルバを生成します。
	 * 支払方法コード・銀行口座コードをキーにしたMapを内部で構築します。
	 *</pre>
	 * @param paymentMethods 支払方法マスタの一覧
	 * @param bankAccounts 銀行口座マスタの一覧
	 *
	 */
	public PaymentMethodNameResolver(PaymentMethodInquiryList paymentMethods, BankAccountInquiryList bankAccounts) {
		this.paymentMethodMap = paymentMethods.getValues().stream()
				.collect(Collectors.toMap(PaymentMethod::getPaymentMethodCode, Function.identity()));
		this.bankAccountMap = bankAccounts.getValues().stream()
				.collect(Collectors.toMap(BankAccount::getBankAccountCode, Function.identity()));
	}

	/**
	 *<pre>
	 * 支払方法コードに対応する支払方法名を解決します。
	 * システム予約値の場合は「－」を返します。
	 *</pre>
	 * @param code 支払方法コード
	 * @return 支払方法名（解決済み）の値オブジェクト。解決できない場合の値オブジェクト格納値は「－」
	 *
	 */
	public ResolvedPaymentMethodName getPaymentMethodName(PaymentMethodCode code) {
		if(code.isSystemReserved()) {
			return ResolvedPaymentMethodName.UNRESOLVED;
		}
		return findPaymentMethodName(code);
	}

	/**
	 *<pre>
	 * 店舗デフォルト支払方法コードに対応する支払方法名を解決します。
	 *</pre>
	 * @param code 店舗デフォルト支払方法コード
	 * @return 支払方法名（解決済み）の値オブジェクト。解決できない場合の値オブジェクト格納値は「－」
	 *
	 */
	public ResolvedPaymentMethodName getPaymentMethodName(ShopDefaultPaymentMethodCode code) {
		// 店舗デフォルト支払方法コードの値がnullの場合は解決できないため「－」を返す
		if(code.isNull()) {
			return ResolvedPaymentMethodName.UNRESOLVED;
		}
		// 店舗デフォルト支払方法コードを支払方法コードに変換
		return findPaymentMethodName(PaymentMethodCode.from(code.getValue()));
	}

	/**
	 *<pre>
	 * 支払方法コードをMapから引き、支払方法名を解決します。未登録の場合は「－」を返します。
	 * システム予約値の判定は呼び出し側で行ってください。
	 *</pre>
	 * @param code 支払方法コード
	 * @return 支払方法名（解決済み）の値オブジェクト。解決できない場合は「－」
	 *
	 */
	private ResolvedPaymentMethodName findPaymentMethodName(PaymentMethodCode code) {
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
		if(code.isSystemReserved()) {
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
