/**
 * 買い物登録の各支出項目ごとの更新前から更新後の変化の値(各種増減値)を格納した支出金額情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録の各支出項目ごとの更新前から更新後の変化の値(各種増減値)を格納した支出金額情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class BeforeAndAfterShoppingSisyutuKingakuData {
	/** 支出金額の増減フラグの値(増減なし) */
	private static int FLG_NON_UPDATE = 0;
	/** 支出金額の増減フラグの値(増) */
	private static int FLG_ADD = 1;
	/** 支出金額の増減フラグの値(減) */
	private static int FLG_SUBTRACT = 2;
	// 支出金額の増減フラグ
	private final int sisyutuKingakuFlg;
	// 支出金額増減値
	private final ExpenditureAmount sisyutuKingaku;
	// 支出金額の増減値で更新した支出テーブル情報
	private final ExpenditureItem updExpenditureItem;
	
	/**
	 *<pre>
	 * 更新前から更新後の変化の値(各種増減値)を格納した支出金額情報を生成します。
	 *</pre>
	 * @param before 更新前の支出金額情報
	 * @param after 更新後の支出金額情報
	 * @return 更新前から更新後の変化の値(各種増減値)を格納した支出金額情報
	 *
	 */
	public static BeforeAndAfterShoppingSisyutuKingakuData from(ExpenditureAmount before, ExpenditureAmount after, ExpenditureItem updExpenditureItem) {
		// 支出金額を加算するか減算するかのフラグ(前＞後なら減算,等しいなら更新なし、前＜後なら加算) 非null項目なのでnullチェック不要
		int sisyutuFlg = FLG_NON_UPDATE;
		int comp = before.getValue().compareTo(after.getValue());
		if(comp > 0) {
			sisyutuFlg = FLG_SUBTRACT;
		}
		if(comp < 0) {
			sisyutuFlg = FLG_ADD;
		}
		// 支出金額増減値
		ExpenditureAmount zougenti = ExpenditureAmount.from(before.getValue().subtract(after.getValue()).abs());
		
		// 支出金額増の場合
		if(sisyutuFlg == FLG_ADD) {
			return new BeforeAndAfterShoppingSisyutuKingakuData(sisyutuFlg, zougenti, updExpenditureItem.addSisyutuKingaku(zougenti));
		}
		// 支出金額減の場合
		if(sisyutuFlg == FLG_SUBTRACT) {
			return new BeforeAndAfterShoppingSisyutuKingakuData(sisyutuFlg, zougenti, updExpenditureItem.subtractSisyutuKingaku(zougenti));
		}
		// 変化なしの場合
		return new BeforeAndAfterShoppingSisyutuKingakuData(sisyutuFlg, zougenti, updExpenditureItem);
	}
	
	/**
	 *<pre>
	 * 更新前から更新後の値の変化ありの場合true、値の変換なしの場合はfalse
	 *</pre>
	 * @return  更新前から更新後の値の変化ありの場合true、値の変換なしの場合はfalse
	 *
	 */
	public boolean isUpdated() {
		if(sisyutuKingakuFlg == FLG_NON_UPDATE) {
			return false;
		} else {
			return true;
		}
	}
}
