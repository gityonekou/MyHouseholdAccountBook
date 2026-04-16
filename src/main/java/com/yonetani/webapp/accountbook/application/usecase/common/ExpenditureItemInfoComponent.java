/**
 * 支出項目情報を取得するコンポーネントクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/17 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfo;
import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfoInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemSortOrderBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditureinfo.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemSortOrder;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報を取得するコンポーネントクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ExpenditureItemInfoComponent {

	// 支出項目テーブル:SISYUTU_ITEM_TABLE参照リポジトリー
	private final SisyutuItemTableRepository sisyutuItemRepository;
	
	/**
	 *<pre>
	 * 指定した支出項目コードに対応する支出項目テーブル情報(ドメイン)を返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param expenditureItemCode 取得対象の支出項目コード
	 * @return 支出項目テーブル情報(ドメイン)
	 *
	 */
	public ExpenditureItemInfo getExpenditureItemInfo(UserId userId, ExpenditureItemCode expenditureItemCode) {
		log.debug("getExpenditureItemInfo:userid="+ userId + ",expenditureItemCode=" + expenditureItemCode);
		
		// 支出項目コードに対応する支出項目情報を取得
		ExpenditureItemInfo expenditureItemInfo = sisyutuItemRepository.findByPrimaryKey(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
		if(expenditureItemInfo == null) {
			// 選択した支出項目コードに対応する支出項目情報が存在しない場合エラーに遷移
			throw new MyHouseholdAccountBookRuntimeException("対象の支出項目情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + expenditureItemCode);
			
		}
		return expenditureItemInfo;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目コードが支出項目テーブルに存在するかを確認します。
	 *</pre>
	 * @param userId ユーザID
	 * @param expenditureItemCode 取得対象の支出項目コード
	 * @return 支出項目テーブルに存在する場合true、存在しない場合false
	 *
	 */
	public boolean hasExpenditureItemInfo(UserId userId, ExpenditureItemCode expenditureItemCode) {
		log.debug("hasExpenditureItemInfo:userid="+ userId + ",expenditureItemCode=" + expenditureItemCode);
		
		// 支出項目コードに対応する支出項目情報を取得
		ExpenditureItemInfo expenditureItemInfo = sisyutuItemRepository.findByPrimaryKey(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
		if(expenditureItemInfo == null) {
			return false;
			
		} else {
			return true;
		}
	}
	
	/**
	 *<pre>
	 * 支出項目の名称を＞区切りで連結した値で返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemCode 取得対象の支出項目コード
	 * @return 支出項目の名称を＞区切りで連結した値 名称取得に失敗した場合はnullを返却
	 *
	 */
	public String getExpenditureItemName(UserId userId, ExpenditureItemCode expenditureItemCode) {
		log.debug("getExpenditureItemName:userid="+ userId + ",expenditureItemCode=" + expenditureItemCode);
		
		// 指定した支出項目の存在チェック
		ExpenditureItemInfo expenditureItemInfo = sisyutuItemRepository.findByPrimaryKey(
				SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
		if(expenditureItemInfo == null) {
			throw new MyHouseholdAccountBookRuntimeException("支出項目情報が存在しません。管理者に問い合わせてください。expenditureItemCode:" + expenditureItemCode);
		}
		
		List<String> expenditureItemNameList = new ArrayList<String>();
		// 自分自身の支出項目名を設定
		expenditureItemNameList.add(expenditureItemInfo.getExpenditureItemName().getValue());
		
		// 親の支出項目コード
		String parentExpenditureItemCode = expenditureItemInfo.getParentExpenditureItemCode().getValue();
		// 親の支出項目レベル
		int expenditureItemLevel = expenditureItemInfo.getExpenditureItemLevel().getValue();
		
		// 項目レベル1の支出項目を取得するまで支出項目情報を取得を繰り返す。
		// また、DBデータ不正による無限ループになることを避けるため、データ格納件数が6件になった時点で
		// 繰り返しを終了する
		while(expenditureItemLevel > 1 && expenditureItemNameList.size() < 6) {
			// 親支出項目コードに対応する支出項目情報を取得
			ExpenditureItemInfo parentExpenditureItemInfo = sisyutuItemRepository.findByPrimaryKey(
					SearchQueryUserIdAndExpenditureItemCode.from(userId, ExpenditureItemCode.from(parentExpenditureItemCode)));
			if(parentExpenditureItemInfo == null) {
				throw new MyHouseholdAccountBookRuntimeException("支出項目情報が属する親の支出項目情報が存在しません。管理者に問い合わせてください。expenditureItemCode:" 
						+ expenditureItemCode + ", [sisyutuItemCodeの値からさかのぼって調査必要です]:[存在しない親コード=parentExpenditureItemCode:" + parentExpenditureItemCode + "]");
			} else {
				expenditureItemNameList.add(parentExpenditureItemInfo.getExpenditureItemName().getValue());
			}
			// 取得対象の親の支出項目コードを再設定
			parentExpenditureItemCode = parentExpenditureItemInfo.getParentExpenditureItemCode().getValue();
			// 親の支出項目レベルを設定
			expenditureItemLevel = parentExpenditureItemInfo.getExpenditureItemLevel().getValue();
			
		}
		
		// 項目名は逆順で取得されるため、順序を入れ替える
		List<String> wkList = new ArrayList<String>();
		for(int i = expenditureItemNameList.size() - 1; i >= 0; i--) {
			wkList.add(expenditureItemNameList.get(i));
		}
		
		// 予期しないエラー(いるかどうかは不明だけど、DBに不正データが格納される)
		if(expenditureItemNameList.size() > 5) {
			throw new MyHouseholdAccountBookRuntimeException("予期しないエラー(DBデータ不正による繰り返し不正。管理者に問い合わせてください。expenditureItemCode:" 
					+ expenditureItemCode + ", parentExpenditureItemCode:" + parentExpenditureItemCode);
		}
		
		// 親の支出項目名を＞区切りで設定
		return String.join("＞", wkList);
	}
	
	/**
	 *<pre>
	 * 支出項目一覧を取得し、画面情報に設定します。
	 *</pre>
	 * @param userId ユーザID
	 * @param response 支出項目一覧を設定する画面情報
	 *
	 */
	public void setSisyutuItemResponseList(UserId userId, AbstractExpenditureItemInfoManageResponse response) {
		log.debug("setSisyutuItemResponseList:userid="+ userId);
		
		// 支出項目を取得
		ExpenditureItemInfoInquiryList searchResult = sisyutuItemRepository.findByUserId(SearchQueryUserId.from(userId));
		if(searchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(ExpenditureItemInfo)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(searchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getExpenditureItemCode().getValue(),
					domain.getExpenditureItemName().getValue(),
					domain.getExpenditureItemDetailContext().getValue(),
					domain.getParentExpenditureItemCode().getValue(),
					domain.getExpenditureItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 *<pre>
	 * 検索条件(支出項目表示順A～支出項目表示順B)までの支出項目一覧を取得し、画面情報に設定します。
	 *</pre>
	 * @param userId ユーザID
	 * @param betweenA 検索条件:支出項目表示順A(BETWEEN A AND BのAの値(下限値))
	 * @param betweenB 検索条件:支出項目表示順B(BETWEEN A AND BのBの値(上限値))
	 * @param response 支出項目一覧を設定する画面情報
	 *
	 */
	public void setSisyutuItemResponseList(UserId userId, ExpenditureItemSortOrder betweenA, ExpenditureItemSortOrder betweenB, AbstractExpenditureItemInfoManageResponse response) {
		log.debug("setSisyutuItemResponseList:userid="+ userId + ",betweenA=" + betweenA + ",betweenB=" + betweenB);
		
		// 支出項目：飲食日用品から、日用消耗品と飲食の項目をすべて取得(表示順から取得する)
		ExpenditureItemInfoInquiryList searchResult = sisyutuItemRepository.findBySortOrderBetween(
				SearchQueryUserIdAndExpenditureItemSortOrderBetweenAB.from(
						// 検索条件:ユーザID
						userId,
						// 検索条件:支出項目表示順A
						betweenA,
						// 検索条件:支出項目表示順B
						betweenB));
		
		if(searchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(ExpenditureItemInfo)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(searchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getExpenditureItemCode().getValue(),
					domain.getExpenditureItemName().getValue(),
					domain.getExpenditureItemDetailContext().getValue(),
					domain.getParentExpenditureItemCode().getValue(),
					domain.getExpenditureItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
	}
}
