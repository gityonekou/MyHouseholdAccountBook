/**
 * セッションに設定する収入登録情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/06/23 : 1.00.00                      新規作成
 * 2026/03/20 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/11/08 : 1.02.00  feature-1.03-dev1   from()の引数を値オブジェクト型に変更(型安全性対応。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeCategory;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeDetailContext;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * セッションに設定する収入登録情報です。
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
public class IncomeRegistItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// データタイプ(DATA_TYPE_NEW/DATA_TYPE_LOAD。セッション内の処理フロー制御用マーカーのため、対応するドメインVOは存在しない)
	private final String dataType;
	// アクション(ACTION_TYPE_ADD/UPDATE/DELETE/NON_UPDATE。dataTypeと同じ理由でString)
	private final String action;
	// 収入コード(仮登録用収入コード。ドメイン型IncomeCodeとは桁数が異なり変換不可のためString)
	private final String incomeCode;
	// 収入区分
	private final String incomeCategory;
	// 収入詳細
	private final String incomeDetailContext;
	// 収入金額
	private final BigDecimal incomeKingaku;

	/**
	 *<pre>
	 * 引数の値からセッションに設定する収入登録情報を生成して返します。
	 * 引数は値オブジェクト型で受け取り(型安全性対応。06_コーディング規約.md §8)、内部では引き続き
	 * プリミティブ型で保持します(本クラスはHttpSessionに格納されるSerializableなクラスのため、
	 * Serializable実装を持たないドメイン値オブジェクトをフィールドとして保持することはできない)。
	 *</pre>
	 * @param dataType データタイプ(新規 or ロード)
	 * @param action アクション(追加／更新／削除)
	 * @param incomeCode 収入コード(仮登録用収入コード)
	 * @param incomeCategory 収入区分
	 * @param incomeDetailContext 収入詳細
	 * @param incomeKingaku 収入金額
	 * @return 収入登録情報
	 *
	 */
	public static IncomeRegistItem from(
			String dataType,
			String action,
			String incomeCode,
			IncomeCategory incomeCategory,
			IncomeDetailContext incomeDetailContext,
			IncomeAmount incomeKingaku) {
		return new IncomeRegistItem(
				dataType,
				action,
				incomeCode,
				// 収入区分
				incomeCategory.getValue(),
				// 収入詳細(null許容)
				incomeDetailContext.getValue(),
				// 収入金額(IncomeAmount.from()で既にスケール2が保証されているため再設定は不要)
				incomeKingaku.getValue());
	}
}
