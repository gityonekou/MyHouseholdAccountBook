/**
 *イベント情報管理ユースケースです。
 * ・情報管理(イベント)画面の表示情報取得(初期表示)
 * ・新規イベントの対象支出項目情報取得(対象選択時)
 * ・イベント情報の追加・更新・イベント終了設定
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItem;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndEventCode;
import com.yonetani.webapp.accountbook.domain.repository.account.event.EventItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.EventInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.EventInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.EventInfoManageResponse.EventListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 *イベント情報管理ユースケースです。
 * ・情報管理(イベント)画面の表示情報取得(初期表示)
 * ・新規イベントの対象支出項目情報取得(対象選択時)
 * ・イベント情報の追加・更新・イベント終了設定
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EventInfoManageUseCase {
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	// イベント情報取得リポジトリー
	private final EventItemTableRepository eventRepository;
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(イベント)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	public EventInfoManageResponse readEventInfo(LoginUserInfo user) {
		log.debug("readEventInfo:userid=" + user.getUserId());
		return createEventInfoManageResponse(user, null);
	}

	/**
	 *<pre>
	 * 新規イベント情報と情報管理(イベント)画面の表示情報を取得します。
	 * 新規追加するイベントが属する支出項目コードをもとに、新規のイベント情報を入力フォーム設定値を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param sisyutuItemCode 新規追加するイベントが属する支出項目コード(イベント関連の支出項目コード)
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	public EventInfoManageResponse readAddEventInfo(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("readAddEventInfo:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 選択した支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(user, sisyutuItemCode);
			
		// イベント情報入力フォームを生成
		EventInfoForm inputForm = new EventInfoForm();
		// アクション：新規登録
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 支出項目コード
		inputForm.setSisyutuItemCode(sisyutuItemCode);
		// 支出項目コードに対応する支出項目名(＞で区切った値)を設定
		inputForm.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, sisyutuItemCode));
		// イベント名:支出項目名を仮設定
		inputForm.setEventName(sisyutuItem.getSisyutuItemName().toString());
		// イベント内容詳細(任意入力項目):支出項目詳細内容を仮設定
		inputForm.setEventDetailContext(sisyutuItem.getSisyutuItemDetailContext().toString());
		
		// 画面表示情報を生成して返却
		return createEventInfoManageResponse(user, inputForm);
	}
	
	/**
	 *<pre>
	 * 選択したイベントコードに応じたイベント情報と情報管理(イベント)画面の表示情報を取得します。
	 * 選択したイベントコードに応じたイベント情報をイベント情報を入力フォームに設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param eventCode 表示対象のイベントコード
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	public EventInfoManageResponse readUpdateEventInfo(LoginUserInfo user, String eventCode) {
		log.debug("readUpdateEventInfo:userid=" + user.getUserId() + ",eventCode=" + eventCode);
		
		// イベントコードに対応するイベント情報を取得
		EventItem eventItem = eventRepository.findByIdAndEventCode(SearchQueryUserIdAndEventCode.from(user.getUserId(), eventCode));
		// イベントコードに対応するイベント情報がない場合、エラー
		if(eventItem == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象のイベント情報が存在しません。管理者に問い合わせてください。eventCode:" + eventCode);
		}
		// イベント情報入力フォームを生成
		EventInfoForm inputForm = new EventInfoForm();
		// アクション：更新
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// イベントコード
		inputForm.setEventCode(eventItem.getEventCode().toString());
		// 支出項目コード
		inputForm.setSisyutuItemCode(eventItem.getSisyutuItemCode().toString());
		// 支出項目名(＞で区切った値)
		inputForm.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, eventItem.getSisyutuItemCode().toString()));
		// イベント名
		inputForm.setEventName(eventItem.getEventName().toString());
		// イベント内容詳細(任意入力項目)
		inputForm.setEventDetailContext(eventItem.getEventDetailContext().toString());
		// 開始日
		inputForm.setEventStartDate(eventItem.getEventStartDate().getValue());
		// 終了日
		inputForm.setEventEndDate(eventItem.getEventEndDate().getValue());
		
		// 画面表示情報を生成して返却
		return createEventInfoManageResponse(user, inputForm);
	}

	/**
	 *<pre>
	 * イベント情報登録・更新の入力チェックエラー時の情報管理(イベント)画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm イベント情報入力フォーム
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	public EventInfoManageResponse readBindingErrorSetInfo(LoginUserInfo user, EventInfoForm inputForm) {
		log.debug("readBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		return createEventInfoManageResponse(user, inputForm);
	}

	/**
	 *<pre>
	 * イベント情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm イベント情報入力フォーム
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	@Transactional
	public EventInfoManageResponse execAction(LoginUserInfo user, EventInfoForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		EventInfoManageResponse response = createEventInfoManageResponse(user, inputForm, false);
		
		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			// 新規採番するイベントコードの値を取得
			int count = eventRepository.countById(SearchQueryUserId.from(user.getUserId()));
			count++;
			if(count > 9999) {
				response.addErrorMessage("イベント情報は9999件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 固定費コードを入力フォームに設定
			inputForm.setEventCode(EventCode.getNewCode(count));
			
			// 追加する固定費情報
			EventItem addData = createEventItem(user.getUserId(), inputForm);
			
			// 固定費テーブルに登録
			int addCount = eventRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("イベントテーブル:EVENT_ITEM_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("新規イベント情報を追加しました。[code:" + addData.getEventCode() + "]" + addData.getEventName());
			
		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			// 更新する固定費情報
			EventItem updateData = createEventItem(user.getUserId(), inputForm);
			
			// 固定費テーブルに登録(支出項目コードは更新対象外項目なので注意)
			int updateCount = eventRepository.update(updateData);
			// 更新件数が1件以上の場合、業務エラー
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("イベントテーブル:EVENT_ITEM_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updateData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("イベント情報を更新しました。[code:" + updateData.getEventCode() + "]" + updateData.getEventName());
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定したイベント情報のイベント終了処理(論理削除処理)
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param eventCode イベント終了対象のイベントコード
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	@Transactional
	public EventInfoManageResponse execDelete(LoginUserInfo user, String eventCode) {
		log.debug("execDelete:userid=" + user.getUserId() + ",eventCode=" + eventCode);
		
		// イベントコードに対応するイベント情報を取得
		EventItem deleteData = eventRepository.findByIdAndEventCode(SearchQueryUserIdAndEventCode.from(user.getUserId(), eventCode));
		if(deleteData == null) {
			throw new MyHouseholdAccountBookRuntimeException("削除対象のイベント情報がイベントテーブル:EVENT_ITEM_TABLEに存在しません。管理者に問い合わせてください。[eventCode=" + eventCode + "]");
		}
		
		// 削除処理を実行
		int deleteCount = eventRepository.delete(deleteData);
		// 追加件数が1件以上の場合、業務エラー
		if(deleteCount != 1) {
			throw new MyHouseholdAccountBookRuntimeException("イベントテーブル:EVENT_ITEM_TABLEへの削除件数が不正でした。[件数=" + deleteCount + "][delete data:" + deleteData + "]");
		}
		// レスポンスを生成(エラー時はエラー画面に遷移するのでイベント情報は使用しない:nullを指定)
		EventInfoManageResponse response = createEventInfoManageResponse(user, null, false);
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		// 完了メッセージ
		response.addMessage("指定のイベント情報をイベント終了に更新しました。[code:" + deleteData.getEventCode() + "]" + deleteData.getEventName());
		
		return response;
	}
	
	/**
	 *<pre>
	 * イベント情報入力フォームを指定して情報管理(イベント)画面の表示情報を生成します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm イベント情報入力フォーム
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	private EventInfoManageResponse createEventInfoManageResponse(LoginUserInfo user, EventInfoForm inputForm) {
		return createEventInfoManageResponse(user, inputForm, true);
	}
	
	/**
	 *<pre>
	 * イベント情報入力フォームを指定して情報管理(イベント)画面の表示情報を生成します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm イベント情報入力フォーム
	 * @param msgFlg ベント一覧の表示件数が0件の場合にメッセージを表示するかどうか
	 * @return 情報管理(イベント)画面の表示情報
	 *
	 */
	private EventInfoManageResponse createEventInfoManageResponse(LoginUserInfo user, EventInfoForm inputForm, boolean msgFlg) {
		// レスポンス
		EventInfoManageResponse response = null;

		// 現在有効な(開催終了ステータスとなっていない)イベントの一覧を取得
		EventItemInquiryList inquiryList = eventRepository.findById(SearchQueryUserId.from(user.getUserId()));
		if(inquiryList.isEmpty()) {
			response = EventInfoManageResponse.getInstance(null, inputForm);
			if(msgFlg) {
				response.addMessage("有効なイベント情報が未登録です。");
			}
		} else {
			List<EventListItem> eventList = inquiryList.getValues().stream().map(domain ->
					EventListItem.from(
							// イベントコード
							domain.getEventCode().toString(),
							// 支出項目名
							domain.getSisyutuItemName().toString(),
							// イベント名
							domain.getEventName().toString(),
							// イベント内容詳細(任意入力)
							domain.getEventDetailContext().toString(),
							// イベント開始日
							DomainCommonUtils.formatyyyyNenMMGatuddNiti(domain.getEventStartDate().getValue()),
							// イベント終了日
							DomainCommonUtils.formatyyyyNenMMGatuddNiti(domain.getEventEndDate().getValue()))
			).collect(Collectors.toUnmodifiableList());
			response = EventInfoManageResponse.getInstance(eventList, inputForm);
		}
		
		// イベント費(0059)に属する支出項目一覧をすべて取得
		sisyutuItemComponent.setSisyutuItemList(
				// ログインユーザ情報
				user,
				// 検索条件:支出項目表示順A：支出項目(イベント)の表示順:0603000000
				MyHouseholdAccountBookContent.SISYUTU_ITEM_EVENT_SORT_VALUE,
				// 検索条件:支出項目表示順B：ベントに属する支出項目の最大値:0603999999
				MyHouseholdAccountBookContent.SISYUTU_ITEM_EVENT_SORT_MAX_VALUE,
				// 画面表示情報
				response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 引数のフォームデータからイベントテーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param inputForm フォームデータ
	 * @return イベントテーブル情報(ドメイン)
	 *
	 */
	private EventItem createEventItem(String userId,  EventInfoForm inputForm) {
		return EventItem.from(
				// ユーザID
				userId,
				// イベントコード
				inputForm.getEventCode(),
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// イベント名
				inputForm.getEventName(),
				// イベント内容詳細
				inputForm.getEventDetailContext(),
				// イベント開始日
				inputForm.getEventStartDate(),
				// イベント終了日
				inputForm.getEventEndDate(),
				// イベント終了フラグ
				false);
	}
}
