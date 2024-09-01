/**
 * 情報管理(イベント)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.EventInfoForm;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(イベント)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventInfoManageResponse extends AbstractExpenditureItemInfoManageResponse {

	/**
	 *<pre>
	 * イベント一覧情報の明細データです
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
	public static class EventListItem {
		// イベントコード
		private final String eventCode;
		// 支出項目名
		private final String sisyutuItemName;
		// イベント名
		private final String eventName;
		// イベント内容詳細(任意入力)
		private final String eventDetailContext;
		// イベント開始日
		private final String eventStartDate;
		// イベント終了日
		private final String eventEndDate;
		
		/**
		 *<pre>
		 * 引数の値からイベント一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param eventCode イベントコード
		 * @param sisyutuItemName 支出項目名
		 * @param eventName イベント名
		 * @param eventDetailContext イベント内容詳細(任意入力)
		 * @param eventStartDate イベント開始日
		 * @param eventEndDate イベント終了日
		 * @return 店舗一覧情報の明細データ
		 *
		 */
		public static EventListItem from(String eventCode, String sisyutuItemName, String eventName, String eventDetailContext, String eventStartDate, String eventEndDate) {
			return new EventListItem(eventCode, sisyutuItemName, eventName, eventDetailContext, eventStartDate, eventEndDate);
		}
	}
	// イベント一覧
	private final List<EventListItem> eventList;
	// イベント情報入力フォーム
	private final EventInfoForm eventInfoForm;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param eventList イベント情報のリスト
	 * @param inputForm イベント情報入力フォーム
	 * @return 情報管理(イベント)画面表示情報
	 *
	 */
	public static EventInfoManageResponse getInstance(List<EventListItem> eventList, EventInfoForm inputForm) {
		if(CollectionUtils.isEmpty(eventList)) {
			return new EventInfoManageResponse(Collections.emptyList(), inputForm);
		} else {
			return new EventInfoManageResponse(eventList, inputForm);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/EventInfoManage");
		// イベント情報のリスト
		modelAndView.addObject("eventList", eventList);
		// イベント情報入力フォーム 未設定の場合はnullのまま値を設定する(画面側でnullかどうかを判定するため)
		modelAndView.addObject("eventInfoForm", eventInfoForm);
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// イベント情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/eventinfo/updateComplete/";
	}
}
