/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 * ・支出項目コード
 * ・支出区分
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/01 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 * ・支出項目コード
 * ・支出区分
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
public class SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun {
	// ユーザID
	private final UserId userId;
	// 年月(YYYYMM)
	private final TargetYearMonth yearMonth;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 支出区分
	private final SisyutuKubun sisyutuKubun;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・年月度(YYYYMM)
	 * ・支出項目コード
	 * ・支出区分
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 年月(YYYYMM)
	 * @param sisyutuItemCode 支出項目コード
	 * @param sisyutuKubun 支出区分
	 * @return 検索条件(ユーザID, 年月度(YYYYMM), 支出項目コード, 支出区分)
	 *
	 */
	public static SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun from(UserId userId, TargetYearMonth yearMonth, SisyutuItemCode sisyutuItemCode, SisyutuKubun sisyutuKubun) {
		return new SearchQueryUserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubun(userId, yearMonth, sisyutuItemCode, sisyutuKubun);
	}
}
