/**
 * 支出項目情報に関するコンポーネントです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/17 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報に関するコンポーネントです。
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
public class SisyutuItemComponent {

	// 支出項目テーブル:SISYUTU_ITEM_TABLE参照リポジトリー
	private final SisyutuItemTableRepository sisyutuItemRepository;
	
	/**
	 *<pre>
	 * 指定した支出項目コードに対応する支出項目テーブル情報(ドメイン)を返します。
	 *</pre>
	 * @param user 支出項目情報を取得するユーザ情報(ログインユーザ情報)
	 * @param sisyutuItemCode 取得対象の支出項目コード
	 * @return 支出項目テーブル情報(ドメイン)
	 *
	 */
	public SisyutuItem getSisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーに遷移
			throw new MyHouseholdAccountBookRuntimeException("対象の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
			
		}
		return sisyutuItem;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目コードが支出項目テーブルに存在するかを確認します。
	 *</pre>
	 * @param user 支出項目情報を取得するユーザ情報(ログインユーザ情報)
	 * @param sisyutuItemCode 取得対象の支出項目コード
	 * @return 支出項目テーブルに存在する場合true、存在しない場合false
	 *
	 */
	public boolean hasSisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			return false;
			
		} else {
			return true;
		}
	}
	
	/**
	 *<pre>
	 * 支出項目の名称を＞区切りで連結した値で返します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報(ログインユーザ情報)
	 * @param sisyutuItemCode 取得対象の支出項目コード
	 * @return 支出項目の名称を＞区切りで連結した値 名称取得に失敗した場合はnullを返却
	 *
	 */
	public String getSisyutuItemName(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("getSisyutuItemName:userid="+ user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 指定した支出項目の存在チェック
		SisyutuItem sisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(sisyutuItem == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
		}
		
		List<String> sisyutuItemNameList = new ArrayList<String>();
		// 自分自身の支出項目名を設定
		sisyutuItemNameList.add(sisyutuItem.getSisyutuItemName().toString());
		
		// 親の支出項目コード
		String parentSisyutuItemCode = sisyutuItem.getParentSisyutuItemCode().toString();
		// 親の支出項目レベル
		int sisyutuItemLevel = sisyutuItem.getSisyutuItemLevel().getValue();
		
		// 項目レベル1の支出項目を取得するまで支出項目情報を取得を繰り返す。
		// また、DBデータ不正による無限ループになることを避けるため、データ格納件数が6件になった時点で
		// 繰り返しを終了する
		while(sisyutuItemLevel > 1 && sisyutuItemNameList.size() < 6) {
			// 親支出項目コードに対応する支出項目情報を取得
			SisyutuItem parentSisyutuItem = sisyutuItemRepository.findByIdAndSisyutuItemCode(
					SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), parentSisyutuItemCode));
			if(parentSisyutuItem == null) {
				throw new MyHouseholdAccountBookRuntimeException("支出項目情報が属する親の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" 
						+ sisyutuItemCode + ", [sisyutuItemCodeの値からさかのぼって調査必要です]:[存在しない親コード=parentSisyutuItemCode:" + parentSisyutuItemCode + "]");
			} else {
				sisyutuItemNameList.add(parentSisyutuItem.getSisyutuItemName().toString());
			}
			// 取得対象の親の支出項目コードを再設定
			parentSisyutuItemCode = parentSisyutuItem.getParentSisyutuItemCode().toString();
			// 親の支出項目レベルを設定
			sisyutuItemLevel = parentSisyutuItem.getSisyutuItemLevel().getValue();
			
		}
		
		// 項目名は逆順で取得されるため、順序を入れ替える
		List<String> wkList = new ArrayList<String>();
		for(int i = sisyutuItemNameList.size() - 1; i >= 0; i--) {
			wkList.add(sisyutuItemNameList.get(i));
		}
		
		// 予期しないエラー(いるかどうかは不明だけど、DBに不正データが格納される)
		if(sisyutuItemNameList.size() > 5) {
			throw new MyHouseholdAccountBookRuntimeException("予期しないエラー(DBデータ不正による繰り返し不正。管理者に問い合わせてください。sisyutuItemCode:" 
					+ sisyutuItemCode + ", parentSisyutuItemCode:" + parentSisyutuItemCode);
		}
		
		// 親の支出項目名を＞区切りで設定
		return String.join("＞", wkList);
	}
	
	/**
	 *<pre>
	 * 支出項目一覧を取得し、画面情報に設定します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報(ログインユーザ情報)
	 * @param response 支出項目一覧を設定する画面情報
	 *
	 */
	public void setSisyutuItemList(LoginUserInfo user, AbstractExpenditureItemInfoManageResponse response) {
		log.debug("setSisyutuItemList:userid="+ user.getUserId());
		
		// 支出項目を取得
		SisyutuItemInquiryList sisyutuItemSearchResult = sisyutuItemRepository.findById(SearchQueryUserId.from(user.getUserId()));
		if(sisyutuItemSearchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(SisyutuItem)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(sisyutuItemSearchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getSisyutuItemCode().toString(),
					domain.getSisyutuItemName().toString(),
					domain.getSisyutuItemDetailContext().toString(),
					domain.getParentSisyutuItemCode().toString(),
					domain.getSisyutuItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 *<pre>
	 * 検索条件(支出項目表示順A～支出項目表示順B)までの支出項目一覧を取得し、画面情報に設定します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報(ログインユーザ情報)
	 * @param betweenA 検索条件:支出項目表示順A(BETWEEN A AND BのAの値(下限値))
	 * @param betweenB 検索条件:支出項目表示順B(BETWEEN A AND BのBの値(上限値))
	 * @param response 支出項目一覧を設定する画面情報
	 *
	 */
	public void setSisyutuItemList(LoginUserInfo user, String betweenA, String betweenB, AbstractExpenditureItemInfoManageResponse response) {
		log.debug("setSisyutuItemList:userid="+ user.getUserId() + ",betweenA=" + betweenA + ",betweenB=" + betweenB);
		
		// 支出項目：飲食日用品から、日用消耗品と飲食の項目をすべて取得(表示順から取得する)
		SisyutuItemInquiryList sisyutuItemSearchResult = sisyutuItemRepository.findByIdAndSisyutuItemSortBetween(
				SearchQueryUserIdAndSisyutuItemSortBetweenAB.from(
						// 検索条件:ユーザID
						user.getUserId(),
						// 検索条件:支出項目表示順A
						betweenA,
						// 検索条件:支出項目表示順B
						betweenB));
		
		if(sisyutuItemSearchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(SisyutuItem)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(sisyutuItemSearchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getSisyutuItemCode().toString(),
					domain.getSisyutuItemName().toString(),
					domain.getSisyutuItemDetailContext().toString(),
					domain.getParentSisyutuItemCode().toString(),
					domain.getSisyutuItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
	}
}
