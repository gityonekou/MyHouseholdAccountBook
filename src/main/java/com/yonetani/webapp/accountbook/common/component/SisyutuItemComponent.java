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

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;

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
	 * 支出項目の名称を＞区切りで連結した値で返します。
	 *</pre>
	 * @param user 支出項目一覧を取得するユーザ情報
	 * @param sisyutuItemCode 取得対象の支出項目コード
	 * @return 支出項目の名称を＞区切りで連結した値 名称取得に失敗した場合はnullを返却
	 *
	 */
	public String getSisyutuItemName(UserSession user, String sisyutuItemCode) {
		log.debug("getSisyutuItemName:sisyutuItemCode=" + sisyutuItemCode);
		
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
}
