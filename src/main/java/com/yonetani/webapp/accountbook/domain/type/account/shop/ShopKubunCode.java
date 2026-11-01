/**
 * 「店舗区分コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/01/28 : 1.00.00                      新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(Identifier継承に変更)
 * 2026/11/01 : 1.01.01  feature-1.03-dev1   tryFrom()・requiresConsumerGoodsAmount()追加(SimpleShoppingRegistInfoFormの相関チェックのドメイン層移動対応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import java.util.Optional;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;
import com.yonetani.webapp.accountbook.domain.type.common.SafeDomainFactory;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「店舗区分コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShopKubunCode extends Identifier {
	
	/**
	 *<pre>
	 * ShopKubunCodeクラスコンストラクターです。
	 *</pre>
	 * @param value 店舗区分コード
	 *
	 */
	private ShopKubunCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「店舗区分コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param code 店舗区分コード
	 * @return 「店舗区分コード」項目ドメインタイプ
	 *
	 */
	public static ShopKubunCode from(String code) {
		
		// 基本検証（null、空文字、長さが3桁でない）
		validate(code, 3, "店舗区分コード");
		
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「店舗区分コード」項目の設定値が不正です。管理者に問い合わせてください。[shopKubunCode=" + code + "]");
		}
		return new ShopKubunCode(code);
	}

	/**
	 *<pre>
	 * 「店舗区分コード」項目の値を表すドメインタイプの生成を試みます。
	 * 不正な値(空文字列・3桁でない・数値変換不可)の場合は例外を発生させずOptional.empty()を返します。
	 *</pre>
	 * @param code 店舗区分コード
	 * @return 生成に成功した場合は値ありのOptional、不正な値の場合はOptional.empty()
	 *
	 */
	public static Optional<ShopKubunCode> tryFrom(String code) {
		return SafeDomainFactory.tryCreate(() -> ShopKubunCode.from(code));
	}

	/**
	 *<pre>
	 * この店舗区分が、日用品への金額入力を必須とする区分(薬局/薬局複合店/病院、または理髪店)かどうかを判定します。
	 *
	 * これらの店舗区分は、買い物登録(簡易タイプ)画面で日用品以外のカテゴリへの入力経路を持たないため、
	 * 日用品に金額が入力されていることを必須とする。
	 *</pre>
	 * @return 日用品への金額入力を必須とする区分の場合：true、それ以外：false
	 *
	 */
	public boolean requiresConsumerGoodsAmount() {
		return getValue().equals(MyHouseholdAccountBookContent.SHOP_KUBUN_MEDICALSHOP_SELECTED_VALUE)
				|| getValue().equals(MyHouseholdAccountBookContent.SHOP_KUBUN_BARBERSHOP_SELECTED_VALUE);
	}
}
