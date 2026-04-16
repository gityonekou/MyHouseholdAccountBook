/**
 * 支出項目選択画面のユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/25 : 1.00.00  新規作成（リファクタリング対応 IncomeAndExpenditureRegistUseCaseからの分離）
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.application.usecase.common.ExpenditureItemInfoComponent;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.event.EventItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureSelectItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ExpenditureItemSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目選択画面のユースケースです。
 * 収支登録で新規の支出を登録する際の支出項目選択画面の表示・操作を担当します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ExpenditureItemSelectUseCase {

	// 支出項目情報取得コンポーネント
	private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
	// イベントテーブル:EVENT_ITEM_TABLEリポジトリー
	private final EventItemTableRepository eventRepository;

	/**
	 *<pre>
	 * 支出項目選択画面表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 支出項目選択画面の表示情報
	 *
	 */
	public ExpenditureItemSelectResponse readExpenditureAddSelect(LoginUserInfo user) {
		log.debug("readExpenditureAddSelect:userid=" + user.getUserId());

		// レスポンス
		ExpenditureItemSelectResponse response = ExpenditureItemSelectResponse.getInstance();
		// 支出項目一覧をすべて取得
		expenditureItemInfoComponent.setSisyutuItemResponseList(UserId.from(user.getUserId()), response);

		return response;
	}

	/**
	 *<pre>
	 * 指定の支出項目コードに対応する支出項目情報と支出項目一覧情報を取得し支出項目選択画面の表示情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCodeStr 支出項目コード
	 * @return 支出項目選択画面の表示情報
	 *
	 */
	public ExpenditureItemSelectResponse readExpenditureItemActSelect(LoginUserInfo user, String sisyutuItemCodeStr) {
		log.debug("readExpenditureItemActSelect:userid=" + user.getUserId() + ", sisyutuItemCode=" + sisyutuItemCodeStr);

		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支出項目コード
		ExpenditureItemCode expenditureItemCode = ExpenditureItemCode.from(sisyutuItemCodeStr);

		// レスポンス
		ExpenditureItemSelectResponse response = ExpenditureItemSelectResponse.getInstance();
		// 支出項目一覧をすべて取得
		expenditureItemInfoComponent.setSisyutuItemResponseList(userId, response);
		// 支出項目詳細内容を設定(支出項目選択画面からの支出項目選択なので、対象の支出項目がない場合:null)チェックは不要とする
		response.setSisyutuItemDetailContext(
				expenditureItemInfoComponent.getExpenditureItemInfo(userId, expenditureItemCode).getExpenditureItemDetailContext().getValue());
		// 支出項目コードに対応する支出項目名(＞で区切った値)を設定
		response.setSisyutuItemName(expenditureItemInfoComponent.getExpenditureItemName(userId, expenditureItemCode));

		// 選択した支出項目のフォームデータを作成
		ExpenditureSelectItemForm selectForm = new ExpenditureSelectItemForm();
		selectForm.setSisyutuItemCode(expenditureItemCode.getValue());

		// イベント支出項目でイベントが登録されている場合、対応するイベント一覧を取得し選択プルダウンリストとしてレスポンスに設定
		EventItemInquiryList inquiryList = eventRepository.findByUserIdAndExpenditureItemCode(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
		if(!inquiryList.isEmpty()) {
			// 検索結果ありの場合、イベント情報選択のプルダウンリストをレスポンスに設定
			response.addEventSelectList(inquiryList.getValues().stream().map(domain -> {
				// 表示テキストを作成(任意入力が入力されている場合、追加表示
				StringBuilder textWk = new StringBuilder(domain.getEventName().getValue());
				if(StringUtils.hasLength(domain.getEventDetailContext().getValue())) {
					textWk.append("【");
					textWk.append(domain.getEventDetailContext().getValue());
					textWk.append("】");
				}
				// OptionItemを返却
				return OptionItem.from(
					// プルダウン選択値:イベントコード
					domain.getEventCode().getValue(),
					// プルダウン表示テキスト:イベントイベント名(任意入力)
					textWk.toString());
			}).collect(Collectors.toList()));

			// イベント情報選択ボックスありフラグを設定
			selectForm.setEventCodeRequired(true);
			// 先頭のイベント情報を選択
			selectForm.setEventCode(inquiryList.getValues().get(0).getEventCode().getValue());
		}

		// 支出項目のフォームデータをレスポンスに設定
		response.setExpenditureSelectItemForm(selectForm);

		return response;
	}
}
