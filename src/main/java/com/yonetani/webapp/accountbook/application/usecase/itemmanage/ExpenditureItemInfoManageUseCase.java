/**
 * 支出項目情報管理ユースケースです。
 * ・支出項目一覧情報取得
 * ・指定の支出項目情報取得
 * ・支出項目情報の更新
 * ・支出項目情報の追加
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageResponse;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報管理ユースケースです。
 * ・支出項目一覧情報取得
 * ・指定の支出項目情報取得
 * ・支出項目情報の更新
 * ・支出項目情報の追加
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
public class ExpenditureItemInfoManageUseCase {

	public ExpenditureItemInfoManageResponse readExpenditureItemInfo(UserSession user) {
		log.debug("readExpenditureItemInfo:userid=" + user.getUserId());
		// TODO 自動生成されたメソッド・スタブ
		return ExpenditureItemInfoManageResponse.getInstance();
	}

}
