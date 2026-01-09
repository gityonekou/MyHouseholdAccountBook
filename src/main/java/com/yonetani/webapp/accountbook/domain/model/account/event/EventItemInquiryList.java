/**
 * イベントテーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.event;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventDate;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * イベントテーブル情報(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EventItemInquiryList {
	
	/**
	 *<pre>
	 * イベント一覧明細情報(ドメイン)です
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
	public static class EventInquiryItem {
		// イベントコード
		private final EventCode eventCode;
		// 支出項目名
		private final SisyutuItemName sisyutuItemName;
		// イベント名
		private final EventName eventName;
		// イベント内容詳細
		private final EventDetailContext eventDetailContext;
		// イベント開始日
		private final EventDate eventStartDate;
		// イベント終了日
		private final EventDate eventEndDate;
		
		/**
		 *<pre>
		 * 引数の値からイベント一覧明細情報を表すドメインモデルを生成して返します。
		 *</pre>
		 * @param eventCode イベントコード
		 * @param sisyutuItemName 支出項目名
		 * @param eventName イベント名
		 * @param eventDetailContext イベント内容詳細
		 * @param eventStartDate イベント開始日
		 * @param eventEndDate イベント終了日
		 * @return イベント一覧明細情報を表すドメインモデル
		 *
		 */
		public static EventInquiryItem from(String eventCode, String sisyutuItemName, String eventName, String eventDetailContext,
				LocalDate eventStartDate, LocalDate eventEndDate) {
			return new EventInquiryItem(
					EventCode.from(eventCode),
					SisyutuItemName.from(sisyutuItemName),
					EventName.from(eventName),
					EventDetailContext.from(eventDetailContext),
					EventDate.from(eventStartDate),
					EventDate.from(eventEndDate)
					);
		}
	}
	// イベント情報のリスト
	private final List<EventInquiryItem> values;
	
	/**
	 *<pre>
	 * 引数の値からイベントテーブル情報(リスト情報)を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values イベント情報のリスト
	 * @return イベントテーブル情報(リスト情報)を表すドメインモデル
	 *
	 */
	public static EventItemInquiryList from(List<EventInquiryItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new EventItemInquiryList(Collections.emptyList());
		} else {
			return new EventItemInquiryList(values);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 280);
			buff.append("イベント一覧明細情報:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "イベント一覧明細情報:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
