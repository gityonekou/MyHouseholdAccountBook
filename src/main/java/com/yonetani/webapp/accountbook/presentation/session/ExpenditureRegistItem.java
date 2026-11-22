/**
 * セッションに設定する支出登録情報です
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/07/09 : 1.00.00                      新規作成
 * 2026/03/20 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 * 2026/11/08 : 1.02.01  feature-1.03-dev1   from()の引数を値オブジェクト型に変更(型安全性対応。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * セッションに設定する支出登録情報です
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
public class ExpenditureRegistItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// データタイプ(DATA_TYPE_NEW/DATA_TYPE_LOAD。セッション内の処理フロー制御用マーカーのため、対応するドメインVOは存在しない)
	private final String dataType;
	// アクション(ACTION_TYPE_ADD/UPDATE/DELETE/NON_UPDATE。dataTypeと同じ理由でString)
	private final String action;
	// 支出コード(仮登録用支出コード:yyyyMMddHHmmssSSS。ドメイン型ExpenditureCode(3桁固定)とは桁数が異なり変換不可のためString)
	private final String expenditureCode;
	// 支出項目コード
	private final String expenditureItemCode;
	// イベントコード
	private final String eventCode;
	// 支出名
	private final String expenditureName;
	// 支出区分
	private final String expenditureCategory;
	// 支出詳細
	private final String expenditureDetailContext;
	// 支払日(DD。年月を含まない日のみの2桁文字列のため、対応するドメインVOは存在しない)
	private final String siharaiDate;
	// 支払方法コード
	private final String paymentMethodCode;
	// 支払金額
	private final BigDecimal expenditureKingaku;
	// 0円開始設定フラグ
	private final boolean clearStartFlg;

	/**
	 *<pre>
	 * 引数の値からセッションに設定する支出登録情報を生成して返します。
	 * 引数は値オブジェクト型で受け取り(型安全性対応。06_コーディング規約.md §8)、内部では引き続き
	 * プリミティブ型で保持します(本クラスはHttpSessionに格納されるSerializableなクラスのため、
	 * Serializable実装を持たないドメイン値オブジェクトをフィールドとして保持することはできない)。
	 *</pre>
	 * @param dataType データタイプ(新規 or ロード)
	 * @param action アクション(追加／更新／削除)
	 * @param expenditureCode 支出コード(仮登録用支出コード)
	 * @param expenditureItemCode 支出項目コード
	 * @param eventCode イベントコード
	 * @param expenditureName 支出名
	 * @param expenditureCategory 支出区分
	 * @param expenditureDetailContext 支出詳細
	 * @param siharaiDate 支払日(DD)
	 * @param paymentMethodCode 支払方法コード
	 * @param expenditureKingaku 支払金額
	 * @param clearStartFlg 支払金額の0円開始設定フラグ
	 * @return　支出登録情報
	 *
	 */
	public static ExpenditureRegistItem from(
			String dataType,
			String action,
			String expenditureCode,
			ExpenditureItemCode expenditureItemCode,
			ExpenditureEventCode eventCode,
			ExpenditureName expenditureName,
			ExpenditureCategory expenditureCategory,
			ExpenditureDetailContext expenditureDetailContext,
			String siharaiDate,
			PaymentMethodCode paymentMethodCode,
			ExpenditureAmount expenditureKingaku,
			boolean clearStartFlg) {
		return new ExpenditureRegistItem(
				dataType,
				action,
				expenditureCode,
				// 支出項目コード
				expenditureItemCode.getValue(),
				// イベントコード(null許容。NullableIdentifier継承のため未設定時はnullを返す)
				eventCode.getValue(),
				// 支出名
				expenditureName.getValue(),
				// 支出区分
				expenditureCategory.getValue(),
				// 支出詳細(null許容)
				expenditureDetailContext.getValue(),
				siharaiDate,
				// 支払方法コード
				paymentMethodCode.getValue(),
				// 支払金額(ExpenditureAmount.from()で既にスケール2が保証されているため再設定は不要)
				expenditureKingaku.getValue(),
				clearStartFlg);
	}
}
