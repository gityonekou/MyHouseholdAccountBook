/**
 * 「支出項目コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/10/07 : 1.00.00                      新規作成
 * 2026/03/15 : 1.01.00  feature-1.00-dev00  リファクタリング対応 クラス名をSisyutuItemCodeからExpenditureItemCodeにリネーム
 * 2026/09/23 : 1.02.00  feature-1.03-dev1   追加リファクタリング対応(桁数チェックをIdentifierに委譲)
 * 2026/10/31 : 1.02.01  feature-1.03-dev1   tryFrom(String)追加(FixedCostInfoUpdateFormの相関チェックのドメイン層移動対応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import java.util.Optional;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;
import com.yonetani.webapp.accountbook.domain.type.common.SafeDomainFactory;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支出項目コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpenditureItemCode extends Identifier {
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 支出項目コード
	 *
	 */
	private ExpenditureItemCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 *</pre>
	 * @param code 支出項目コード
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemCode from(String code) {
		// 基本検証（null、空文字、長さが4桁でない）
		Identifier.validate(code, 4, "支出項目コード");
		
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[code=" + code + "]");
		}

		return new ExpenditureItemCode(code);
	}

	/**
	 *<pre>
	 * 「支出項目コード」項目の値を表すドメインタイプの生成を試みます。
	 * 不正な値(空文字列・4桁でない・数値変換不可)の場合は例外を発生させずOptional.empty()を返します。
	 *</pre>
	 * @param code 支出項目コード
	 * @return 生成に成功した場合は値ありのOptional、不正な値の場合はOptional.empty()
	 *
	 */
	public static Optional<ExpenditureItemCode> tryFrom(String code) {
		return SafeDomainFactory.tryCreate(() -> ExpenditureItemCode.from(code));
	}

	/**
	 *<pre>
	 * 新規発番する支出項目コードの値(数値)をもとに、「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する支出項目コードの値(数値)
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemCode from(int count) {
		return new ExpenditureItemCode(String.format("%04d", count));
	}

	/**
	 *<pre>
	 * 「親の支出項目コード」項目の値をもとに、「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param parentExpenditureItemCode 「親の支出項目コード」項目ドメインタイプ
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemCode from(ParentExpenditureItemCode parentExpenditureItemCode) {
		return new ExpenditureItemCode(parentExpenditureItemCode.getValue());
	}

	/**
	 *<pre>
	 * 新規発番する支出項目コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する支出項目コードの値(数値)
	 * @return 支出項目コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return ExpenditureItemCode.from(count).getValue();
	}
}
