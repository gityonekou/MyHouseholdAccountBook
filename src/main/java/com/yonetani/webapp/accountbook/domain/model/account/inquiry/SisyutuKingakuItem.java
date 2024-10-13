/**
 * 支出金額テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ParentSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuB;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuC;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuShiharaiDate;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuYoteiKingaku;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 支出金額テーブル情報を表すドメインモデルです
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
public class SisyutuKingakuItem {

	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 親支出項目コード
	private final ParentSisyutuItemCode parentSisyutuItemCode;
	// 支出予定金額
	private final SisyutuYoteiKingaku sisyutuYoteiKingaku;
	// 支出金額
	private final SisyutuKingaku sisyutuKingaku;
	// 支出金額b(支出金額B, 割合)
	private final SisyutuKingakuB sisyutuKingakuB;
	// 支出金額C(支出金額C, 割合)
	private final SisyutuKingakuC sisyutuKingakuC;
	// 支出支払日
	private final SisyutuShiharaiDate sisyutushiharaiDate;
}
