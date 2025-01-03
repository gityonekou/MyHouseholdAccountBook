/**
 * 支出金額テーブル情報を格納したホルダークラスです。
 * 指定した年月で支出金額テーブル情報を検索し、データをホルダーに格納します。
 * 指定の支出金額テーブルのデータを更新、または新規追加する場合、ホルダーに格納された親の支出項目コードに対応する
 * 支出金額テーブル情報も更新(対象のデータがない場合は新規追加します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuB;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuC;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKubun;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出金額テーブル情報を格納したホルダークラスです。
 * 
 * 指定の支出金額テーブルのデータを更新、または新規追加する場合、ホルダーに格納された親の支出項目コードに対応する
 * 支出金額テーブル情報も更新(対象のデータがない場合は新規追加します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class SisyutuKingakuItemHolder {
	/** 更新なし */
	private static int STATUS_NON_UPDATE = 0;
	/** 新規追加 */
	private static int STATUS_ADD = 1;
	/** 更新 */
	private static int STATUS_UPDATE = 2;
	/** 支出金額の増減フラグの値(増) */
	private static int FLG_ADD = 1;
	/** 支出金額の増減フラグの値(減) */
	private static int FLG_SUBTRACT = 2;
	
	/**
	 *<pre>
	 * 支出金額テーブル情報の内部保持用クラスです。
	 * 支出金額テーブル情報と、情報の更新状態ステータス(non update、add、update)を持ちます。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@Data
	private static class SisyutuKingakuItemData {
		// 支出金額テーブル情報
		private SisyutuKingakuItem sisyutuKingakuItem;
		// ステータス
		private int status;
		
	}
	
	/**
	 *<pre>
	 * 更新前から更新後の変化の値(各種増減値)を格納した支出金額情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@Data
	private static class BeforeAndAfterSisyutuKingakuData {
		// 更新前の支出金額情報
		private final SisyutuKingakuItem beforeData;
		// 更新後の支出金額情報
		private final SisyutuKingakuItem afterData;
		// 支出金額の増減フラグ
		private final int sisyutuKingakuFlg;
		// 支出金額増減値
		private final SisyutuKingaku sisyutuKingaku;
		// 支出金額Bの増減フラグ
		private final int sisyutuKingakuBFlg;
		// 支出金額B増減値
		private final SisyutuKingakuB sisyutuKingakuB;
		// 支出金額Cの増減フラグ
		private final int sisyutuKingakuCFlg;
		// 支出金額C(支出金額C)増減値
		private final SisyutuKingakuC sisyutuKingakuC;
		
		/**
		 *<pre>
		 * 引数の値をもとにBeforeAndAfterSisyutuKingakuDataを作成して返します。
		 *</pre>
		 * @param beforeData 更新前の支出金額テーブル情報
		 * @param beforeKubun 更新前の支出区分の値
		 * @param afterData 更新後の支出金額テーブル情報
		 * @param afterKubun 更新後の支出区分の値
		 * @return BeforeAndAfterSisyutuKingakuData
		 *
		 */
		static BeforeAndAfterSisyutuKingakuData from(
				SisyutuKingakuItem beforeData, SisyutuKubun beforeKubun, 
				SisyutuKingakuItem afterData, SisyutuKubun afterKubun) {
			
			// 支出金額を加算するか減算するかのフラグ(前＞後なら減算,それ以外は加算) 非null項目なのでnullチェック不要
			int sisyutuFlg = (beforeData.getSisyutuKingaku().getValue().compareTo(afterData.getSisyutuKingaku().getValue()) > 0)
					 ? FLG_SUBTRACT : FLG_ADD;
			
			// 支出金額増減値
			SisyutuKingaku zougenti = SisyutuKingaku.from(
					beforeData.getSisyutuKingaku().getValue().subtract(afterData.getSisyutuKingaku().getValue()).abs());
			
			// 支出B、支出C加減算初期値
			int sisyutuBFlg = FLG_ADD;
			SisyutuKingakuB zougentiBWk = SisyutuKingakuB.from(null);
			int sisyutuCFlg = FLG_ADD;
			SisyutuKingakuC zougentiCWk = SisyutuKingakuC.from(null);
			
			// 更新前と更新後で支出区分の値が同じ場合
			if(beforeKubun.getValue().equals(afterKubun.getValue())) {
				
				// 支出区分が区分Bの場合、支出Bの値を加減算
				if(SisyutuKubun.isWastedB(beforeKubun)) {
					sisyutuBFlg = sisyutuFlg;
					zougentiBWk = SisyutuKingakuB.from(zougenti.getValue());
					
				// 支出区分が区分Cの場合、支出Cの値を加減算
				} else if(SisyutuKubun.isWastedC(beforeKubun)) {
					sisyutuCFlg = sisyutuFlg;
					zougentiCWk = SisyutuKingakuC.from(zougenti.getValue());
				}
				
			// 更新前と更新後で支出区分の値が違う場合
			} else {
				// 支出金額Bの設定
				// 更新前の支出区分が区分Bの場合、支出Bの値を減算
				if(SisyutuKubun.isWastedB(beforeKubun)) {
					sisyutuBFlg = FLG_SUBTRACT;
					zougentiBWk = beforeData.getSisyutuKingakuB();
					
				// 更新後の支出区分が区分Bの場合、支出Bの値を加算
				} else if (SisyutuKubun.isWastedB(afterKubun)) {
					zougentiBWk = afterData.getSisyutuKingakuB();
				}
				
				// 支出金額Cの設定
				// 更新前の支出区分が区分Cの場合、支出Cの値を減算
				if(SisyutuKubun.isWastedC(beforeKubun)) {
					sisyutuCFlg = FLG_SUBTRACT;
					zougentiCWk = beforeData.getSisyutuKingakuC();
					
				// 更新後の支出区分が区分Cの場合、支出Cの値を加算
				} else if (SisyutuKubun.isWastedC(afterKubun)) {
					zougentiCWk = afterData.getSisyutuKingakuC();
				}
			}
			
			// 各種増減値のインスタンスを生成して返却
			return new BeforeAndAfterSisyutuKingakuData(
					// 更新前の支出金額テーブル情報
					beforeData,
					// 更新後の支出金額テーブル情報
					afterData,
					// 支出金額を加算するか減算するかのフラグ
					sisyutuFlg,
					// 支出金額増減値
					zougenti,
					// 支出金額Bを加算するか減算するかのフラグ
					sisyutuBFlg,
					// 支出金額B増減値
					zougentiBWk,
					// 支出金額Cを加算するか減算するかのフラグ
					sisyutuCFlg,
					// 支出金額B増減値
					zougentiCWk);
		}
	}
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	// 支出金額テーブルのデータを格納したマップ
	private Map<SisyutuItemCode, SisyutuKingakuItemData> sisyutuKingakuItemMap = new HashMap<SisyutuItemCode, SisyutuKingakuItemData>();
	
	/**
	 *<pre>
	 * 支出金額テーブル情報のリストをもとに、ホールドクラスを生成して返します。
	 *</pre>
	 * @param itemList ホールドに格納する支出金額テーブル情報のリスト
	 * @param sisyutuItemComponent 支出項目テーブル検索用コンポーネント
	 * @return 支出金額テーブル情報を格納したホルダー
	 *
	 */
	public static SisyutuKingakuItemHolder from(List<SisyutuKingakuItem> itemList, SisyutuItemComponent sisyutuItemComponent) {
		// ホルダーを新規作成
		SisyutuKingakuItemHolder holder = new SisyutuKingakuItemHolder(sisyutuItemComponent);
		// 支出金額テーブル情報のリスト件数分繰り返し
		itemList.forEach(inputData -> {
			// 支出金額テーブル情報のデータをもとに内部保持用データを作成
			SisyutuKingakuItemData data = new SisyutuKingakuItemData();
			data.setSisyutuKingakuItem(inputData);
			data.setStatus(STATUS_NON_UPDATE);
			
			// 内部保持用データを支出項目コードをキーにマップに登録
			holder.sisyutuKingakuItemMap.put(inputData.getSisyutuItemCode(), data);
		});
		// 作成したホルダーを返却
		return holder;
	}

	/**
	 *<pre>
	 * 支出テーブル情報を元に支出金額テーブル情報を新規に作成し、ホルダーに追加します。
	 * また、関連する親の支出金額テーブル情報を更新します。
	 *</pre>
	 * @param addExpenditureData 支出金額テーブル情報の元となる支出テーブル情報
	 *
	 */
	public void add(ExpenditureItem addExpenditureData) {
		// 新規の出金額テーブル情報をホルダーに登録
		addItemMap(addExpenditureData.getSisyutuItemCode(), addExpenditureData);
	}
	
	/**
	 *<pre>
	 * 引数で渡された新・旧の支出テーブル情報を支出金額の更新データとして支出金額テーブル情報を作成し、ホルダーに追加します。
	 * また、関連する親の支出金額テーブル情報を更新します。
	 *</pre>
	 * @param beforeData 更新前の支出テーブル情報
	 * @param updData 更新する支出テーブル情報
	 *
	 */
	public void update(ExpenditureItem beforeData, ExpenditureItem updExpData) {
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(
				// ユーザID
				updExpData.getUserId(),
				// 支出項目コード
				updExpData.getSisyutuItemCode());
		
		// 更新前の支出金額テーブル情報を作成
		SisyutuKingakuItem beforeItem = createSisyutuKingakuItem(sisyutuItem, beforeData);
		// 更新後の支出金額テーブル情報を作成
		SisyutuKingakuItem afterItem = createSisyutuKingakuItem(sisyutuItem, updExpData);
		
		// 更新前-更新後
		BeforeAndAfterSisyutuKingakuData sabunData = BeforeAndAfterSisyutuKingakuData.from(
				beforeItem, beforeData.getSisyutuKubun(), afterItem, updExpData.getSisyutuKubun());
		// 更新する支出情報を元に、ホルダーに登録されている支出金額情報を指定された支出情報の値分加減算します。
		updateItemMap(updExpData.getSisyutuItemCode(), sabunData);
	}
	
	/**
	 *<pre>
	 * 引数で渡された支出テーブル情報に関連する支出金額情報を0円に更新する支出金額情報を作成し、ホルダーに追加します。
	 * また、関連する親の支出金額テーブル情報を更新します。
	 *</pre>
	 * @param deleteData 削除する支出テーブル情報
	 *
	 */
	public void delete(ExpenditureItem deleteData) {
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(deleteData.getUserId(), deleteData.getSisyutuItemCode());
		
		// 支出テーブル情報から更新対象の支出金額テーブル情報を作成
		SisyutuKingakuItem deleteItem = createSisyutuKingakuItem(sisyutuItem, deleteData);
		
		// 削除する支出情報を元に、ホルダーに登録されている支出金額情報を指定された支出情報の値分減算します。
		subtractItemMap(deleteData.getSisyutuItemCode(), deleteItem);
	}
	
	/**
	 *<pre>
	 * ホルダーに設定されている支出金額テーブル情報のうち、新規追加分のデータのリストを返します。
	 *</pre>
	 * @return 新規追加分の支出金額テーブル情報のリスト
	 *
	 */
	public List<SisyutuKingakuItem> getAddList() {
		return getSisyutuKingakuItemList(STATUS_ADD);
	}
	
	/**
	 *<pre>
	 * ホルダーに設定されている支出金額テーブル情報のうち、更新対象分のデータのリストを返します。
	 *</pre>
	 * @return 更新対象分の支出金額テーブル情報のリスト
	 *
	 */
	public List<SisyutuKingakuItem> getUpdateList() {
		return getSisyutuKingakuItemList(STATUS_UPDATE);
	}
	
	/**
	 *<pre>
	 * 指定されたステータスコードの支出金額テーブル情報のリストをホルダーから取得します。
	 *</pre>
	 * @param status 取得対象のステータス
	 * @return 支出金額テーブル情報のリスト
	 *
	 */
	private List<SisyutuKingakuItem> getSisyutuKingakuItemList(int status) {
		List<SisyutuKingakuItem> resultList = new ArrayList<>();
		for(SisyutuKingakuItemData data : sisyutuKingakuItemMap.values()) {
			if(data.getStatus() == status) {
				resultList.add(data.getSisyutuKingakuItem());
			}
		}
		return resultList;
	}
	
	/**
	 *<pre>
	 * 支出項目コードに対応する支出金額テーブル情報をホルダーに登録します
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @param addExpenditureData 支出情報(ドメイン)
	 *
	 */
	private void addItemMap(SisyutuItemCode sisyutuItemCode, ExpenditureItem addExpenditureData) {
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(
				// ユーザID
				addExpenditureData.getUserId(),
				// 支出項目コード
				sisyutuItemCode);
		
		// 支出テーブル情報から支出金額テーブル情報を作成
		SisyutuKingakuItem addItem = createSisyutuKingakuItem(sisyutuItem, addExpenditureData);
		// 支出項目コードがホルダーに登録済みの場合、ホルダーのデータを更新
		SisyutuKingakuItemData addData = sisyutuKingakuItemMap.get(sisyutuItemCode);
		
		// 新規追加の場合
		if(addData == null) {
			// 内部保持用データを作成し各種値を設定
			addData = new SisyutuKingakuItemData();
			addData.setSisyutuKingakuItem(addItem);
			addData.setStatus(STATUS_ADD);
			
		// ホルダーのデータを更新する場合
		} else {
			// DBに登録済みデータの場合、ステータスを更新に変更
			if(addData.getStatus() == STATUS_NON_UPDATE) {
				addData.setStatus(STATUS_UPDATE);
			}
			// 更新前の支出金額情報を取得
			SisyutuKingakuItem beforeItem = addData.getSisyutuKingakuItem();
			// 新規の支出金額情報を前のデータに加算しホルダーに登録
			addData.setSisyutuKingakuItem(SisyutuKingakuItem.from(
					// ユーザID
					beforeItem.getUserId().getValue(),
					// 対象年
					beforeItem.getTargetYear().getValue(),
					// 対象月
					beforeItem.getTargetMonth().getValue(),
					// 支出項目コード
					beforeItem.getSisyutuItemCode().getValue(),
					// 親支出項目コード
					beforeItem.getParentSisyutuItemCode().getValue(),
					// 支出予定金額=前の値+新規の値
					beforeItem.getSisyutuYoteiKingaku().add(addItem.getSisyutuYoteiKingaku()).getValue(),
					// 支出金額=前の値+新規の値
					beforeItem.getSisyutuKingaku().add(addItem.getSisyutuKingaku()).getValue(),
					// 支出金額B=前の値+新規の値
					beforeItem.getSisyutuKingakuB().add(addItem.getSisyutuKingakuB()).getValue(),
					// 支出金額C=前の値+新規の値
					beforeItem.getSisyutuKingakuC().add(addItem.getSisyutuKingakuC()).getValue(),
					// 支出支払日=前の値と支出情報(ドメイン)の支払日のどちらか大きいほうの値
					beforeItem.getSisyutushiharaiDate().max(addExpenditureData.getShiharaiDate()).getValue()));
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		sisyutuKingakuItemMap.put(sisyutuItemCode, addData);
		
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(sisyutuItem.getParentSisyutuItemCode().getValue().equals(
				sisyutuItem.getSisyutuItemCode().getValue())) {
			return;
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			addItemMap(SisyutuItemCode.from(sisyutuItem.getParentSisyutuItemCode()), addExpenditureData);
		}
	}
	
	/**
	 *<pre>
	 * 支出情報(ドメイン)の値をホルダーに登録済みの支出金額情報から加減算します。
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @param sabunData 支出金額情報(ドメイン)の更新前・更新後の差額を表したデータ
	 *
	 */
	private void updateItemMap(SisyutuItemCode sisyutuItemCode, BeforeAndAfterSisyutuKingakuData sabunData) {
		
		// ホルダーから加減算対象の支出金額情報を取得
		SisyutuKingakuItemData updateData = sisyutuKingakuItemMap.get(sisyutuItemCode);
		if(updateData == null) {
			log.error("例外発生のため支出金額情報をログ出力：[sabunData=" + sabunData + "]");
			throw new MyHouseholdAccountBookRuntimeException("加減算対象の支出金額情報が存在しません。管理者に問い合わせてください。[sisyutuItemCode="
							+ sisyutuItemCode + "]");
		}
		
		// テータスを更新に変更
		if(updateData.getStatus() == STATUS_NON_UPDATE) {
			updateData.setStatus(STATUS_UPDATE);
		}
		// 更新前の支出金額情報を取得
		SisyutuKingakuItem beforeItem = updateData.getSisyutuKingakuItem();
		// 前のデータの値を減算しホルダーに登録
		try {
			// 支出金額=前の値+加減算するの値
			SisyutuKingaku sisyutuKingaku = null;
			if(sabunData.getSisyutuKingakuFlg() == FLG_ADD) {
				sisyutuKingaku = beforeItem.getSisyutuKingaku().add(sabunData.getSisyutuKingaku());
			} else {
				sisyutuKingaku = beforeItem.getSisyutuKingaku().subtract(sabunData.getSisyutuKingaku());
			}
			// 支出金額B=前の値+加減算するの値
			SisyutuKingakuB sisyutuKingakuB = null;
			if(sabunData.getSisyutuKingakuBFlg() == FLG_ADD) {
				sisyutuKingakuB = beforeItem.getSisyutuKingakuB().add(sabunData.getSisyutuKingakuB());
			} else {
				sisyutuKingakuB = beforeItem.getSisyutuKingakuB().subtract(sabunData.getSisyutuKingakuB());
			}
			// 支出金額C=前の値+加減算するの値
			SisyutuKingakuC sisyutuKingakuC = null;
			if(sabunData.getSisyutuKingakuCFlg() == FLG_ADD) {
				sisyutuKingakuC = beforeItem.getSisyutuKingakuC().add(sabunData.getSisyutuKingakuC());
			} else {
				sisyutuKingakuC = beforeItem.getSisyutuKingakuC().subtract(sabunData.getSisyutuKingakuC());
			}
			// 支出金額情報を差額分更新
			updateData.setSisyutuKingakuItem(SisyutuKingakuItem.from(
				// ユーザID
				beforeItem.getUserId().getValue(),
				// 対象年
				beforeItem.getTargetYear().getValue(),
				// 対象月
				beforeItem.getTargetMonth().getValue(),
				// 支出項目コード
				beforeItem.getSisyutuItemCode().getValue(),
				// 親支出項目コード
				beforeItem.getParentSisyutuItemCode().getValue(),
				// 支出予定金額の値は変更なし(前の値をそのまま設定)
				beforeItem.getSisyutuYoteiKingaku().getValue(),
				// 支出金額
				sisyutuKingaku.getValue(),
				// 支出金額B=前の値+加減算するの値
				sisyutuKingakuB.getValue(),
				// 支出金額C=前の値+加減算するの値
				sisyutuKingakuC.getValue(),
				// 支出支払日=前の値と更新後の支出情報(ドメイン)の支払日のどちらか大きいほうの値
				beforeItem.getSisyutushiharaiDate().max(sabunData.getAfterData().getSisyutushiharaiDate()).getValue()));
		} catch (Exception ex) {
			// 計算で値不正の場合、エラーログを出力
			log.error("例外発生のため支出金額情報をログ出力：[sabunData=" + sabunData + "]");
			throw ex;
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		sisyutuKingakuItemMap.put(sisyutuItemCode, updateData);
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(beforeItem.getUserId(), sisyutuItemCode);
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(sisyutuItem.getParentSisyutuItemCode().getValue().equals(
				sisyutuItem.getSisyutuItemCode().getValue())) {
			return;
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			updateItemMap(SisyutuItemCode.from(sisyutuItem.getParentSisyutuItemCode()), sabunData);
		}
	}
	
	/**
	 *<pre>
	 * 支出情報(ドメイン)の値をホルダーに登録済みの支出金額情報から減算します。
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @param subtractItem 支出金額情報(ドメイン)
	 *
	 */
	private void subtractItemMap(SisyutuItemCode sisyutuItemCode, SisyutuKingakuItem subtractItem) {
		
		// ホルダーから減算対象の支出金額情報を取得
		SisyutuKingakuItemData subtractData = sisyutuKingakuItemMap.get(sisyutuItemCode);
		if(subtractData == null) {
			log.error("例外発生のため支出金額情報をログ出力：[subtractSisyutuKingakuItem=" + subtractItem + "]");
			throw new MyHouseholdAccountBookRuntimeException("減算対象の支出金額情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
		}
		
		// テータスを更新に変更
		if(subtractData.getStatus() == STATUS_NON_UPDATE) {
			subtractData.setStatus(STATUS_UPDATE);
		}
		// 更新前の支出金額情報を取得
		SisyutuKingakuItem beforeItem = subtractData.getSisyutuKingakuItem();
		// 前のデータの値を減算しホルダーに登録
		try {
			subtractData.setSisyutuKingakuItem(SisyutuKingakuItem.from(
				// ユーザID
				beforeItem.getUserId().getValue(),
				// 対象年
				beforeItem.getTargetYear().getValue(),
				// 対象月
				beforeItem.getTargetMonth().getValue(),
				// 支出項目コード
				beforeItem.getSisyutuItemCode().getValue(),
				// 親支出項目コード
				beforeItem.getParentSisyutuItemCode().getValue(),
				// 支出予定金額の値は変更なし(前の値をそのまま設定)
				beforeItem.getSisyutuYoteiKingaku().getValue(),
				// 支出金額=前の値-減算するの値
				beforeItem.getSisyutuKingaku().subtract(subtractItem.getSisyutuKingaku()).getValue(),
				// 支出金額B=前の値-減算するの値
				beforeItem.getSisyutuKingakuB().subtract(subtractItem.getSisyutuKingakuB()).getValue(),
				// 支出金額C=前の値-減算するの値
				beforeItem.getSisyutuKingakuC().subtract(subtractItem.getSisyutuKingakuC()).getValue(),
				// 支出支払日の値は変更なし(前の値をsubtractItem設定)
				beforeItem.getSisyutushiharaiDate().getValue()));
		} catch (Exception ex) {
			// 計算で値不正の場合、エラーログを出力
			log.error("例外発生のため支出金額情報をログ出力：[beforeSisyutuKingakuItem=" + beforeItem + "][subtractSisyutuKingakuItem=" + subtractItem + "]");
			throw ex;
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		sisyutuKingakuItemMap.put(sisyutuItemCode, subtractData);
		
		// 支出項目コードに対応する支出項目情報を取得
		SisyutuItem sisyutuItem = sisyutuItemComponent.getSisyutuItem(subtractItem.getUserId(), sisyutuItemCode);
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(sisyutuItem.getParentSisyutuItemCode().getValue().equals(
				sisyutuItem.getSisyutuItemCode().getValue())) {
			return;
		
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			subtractItemMap(SisyutuItemCode.from(sisyutuItem.getParentSisyutuItemCode()), subtractItem);
		}
	}
	
	/**
	 *<pre>
	 * 支出情報から支出金額情報(ドメイン)を作成して返します。
	 *</pre>
	 * @param sisyutuItem 支出項目情報(ドメイン)
	 * @param expenditureData 支出情報(ドメイン)
	 * @return 支出金額情報(ドメイン)
	 *
	 */
	private SisyutuKingakuItem createSisyutuKingakuItem(SisyutuItem sisyutuItem, ExpenditureItem expenditureData) {
		return SisyutuKingakuItem.from(
				// ユーザID
				expenditureData.getUserId().getValue(),
				// 対象年
				expenditureData.getTargetYear().getValue(),
				// 対象月
				expenditureData.getTargetMonth().getValue(),
				// 支出項目コード
				sisyutuItem.getSisyutuItemCode().getValue(),
				// 親支出項目コード
				sisyutuItem.getParentSisyutuItemCode().getValue(),
				// 支出予定金額
				expenditureData.getSisyutuYoteiKingaku().getValue(),
				// 支出金額
				expenditureData.getSisyutuKingaku().getValue(),
				// 支出金額B
				SisyutuKubun.isWastedB(expenditureData.getSisyutuKubun())
					? expenditureData.getSisyutuKingaku().getValue() : null,
				// 支出金額C
				SisyutuKubun.isWastedC(expenditureData.getSisyutuKubun())
				? expenditureData.getSisyutuKingaku().getValue() : null,
				// 支出支払日
				expenditureData.getShiharaiDate().getValue());
	}
}
