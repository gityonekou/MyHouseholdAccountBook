/**
 * セッションに設定する支出登録情報です
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/07/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class ExpenditureRegistItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// データタイプ
	private final String dataType;
	// アクション
	private final String action;
	// 支出コード(仮登録用支出コード)
	private final String expenditureCode;
	// 支出項目コード
	private final String sisyutuItemCode;
	// イベントコード
	private final String eventCode;
	// 支出名
	private final String expenditureName;
	// 支出区分
	private final String expenditureKubun;
	// 支出詳細
	private final String expenditureDetailContext;
	// 支払日(DD)
	private final String siharaiDate;
	// 支払金額
	private final BigDecimal expenditureKingaku;
	
	/**
	 *<pre>
	 * 引数の値からセッションに設定する支出登録情報を生成して返します。
	 *</pre>
	 * @param dataType データタイプ(新規 or ロード)
	 * @param action アクション(追加／更新／削除)
	 * @param expenditureCode 支出コード(仮登録用支出コード)
	 * @param sisyutuItemCode 支出項目コード
	 * @param eventCode イベントコード
	 * @param expenditureName 支出名
	 * @param expenditureKubun 支出区分
	 * @param expenditureDetailContext 支出詳細
	 * @param siharaiDate 支払日(DD)
	 * @param expenditureKingaku 支払金額
	 * @return　支出登録情報
	 *
	 */
	public static ExpenditureRegistItem from(
			String dataType,
			String action,
			String expenditureCode,
			String sisyutuItemCode,
			String eventCode,
			String expenditureName,
			String expenditureKubun,
			String expenditureDetailContext,
			String siharaiDate,
			BigDecimal expenditureKingaku) {
		return new ExpenditureRegistItem(dataType, action, expenditureCode, sisyutuItemCode, eventCode, expenditureName, 
				expenditureKubun, expenditureDetailContext, siharaiDate, expenditureKingaku.setScale(2));
	}
}
