/**
 * マイ家計簿 ユーザ情報を照会するユースケースです。
 * ・ユーザIDに対応する現在の対象年月の値を取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/14 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.utils;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿 ユーザ情報を照会するユースケースです。
 * ・ユーザIDに対応する現在の対象年月の値を取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class AccountBookUserInquiryUseCase {

	// 指定ユーザIDの現在の対象年月取得リポジトリー
	private final AccountBookUserRepository userInfoSearchRepository;
	
	/**
	 *<pre>
	 * ユーザIDに対応する現在の対象年月の値を取得します。
	 *</pre>
	 * @param userId 検索条件のユーザID
	 * @return 現在の対象年・月の値(ドメインモデル)
	 *
	 */
	public NowTargetYearMonth getNowTargetYearMonth(String userId) {
		
		// ユーザIDをドメインオブジェクトに変換
		SearchQueryUserId userIdModel = SearchQueryUserId.from(userId);
		log.debug("検索条件=" + userIdModel);
		NowTargetYearMonth yearMonth = userInfoSearchRepository.getNowTargetYearMonth(userIdModel);
		log.debug("現在の対象年月:" + yearMonth);
		if(yearMonth.isEmpty()) {
			String message = "指定ユーザIDに対応する現在の対象年・月の値が取得できませんでした。管理者に問い合わせてください。[ユーザID：" 
					+ userIdModel.getUserId().toString() + "]";
			log.error(message);
			yearMonth.setMessage(message);
		}
		return yearMonth;
	}
}
