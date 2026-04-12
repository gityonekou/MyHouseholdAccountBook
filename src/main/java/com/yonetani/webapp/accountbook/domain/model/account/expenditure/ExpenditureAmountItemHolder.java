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
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.expenditure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yonetani.webapp.accountbook.application.usecase.common.ExpenditureItemInfoComponent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfo;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.MinorWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SevereWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ExpenditureAmountItemHolder {
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
	private static class ExpenditureAmountItemData {
		// 支出金額テーブル情報
		private ExpenditureAmountItem expenditureAmountItem;
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
	private static class BeforeAndAfterExpenditureAmountData {
		// 更新前の支出金額情報
		private final ExpenditureAmountItem beforeData;
		// 更新後の支出金額情報
		private final ExpenditureAmountItem afterData;
		// 支出金額の増減フラグ
		private final int expenditureAmountFlg;
		// 支出金額増減値
		private final ExpenditureAmount expenditureAmount;
		// 無駄遣い（軽度）支出金額の増減フラグ
		private final int minorWasteExpenditureFlg;
		// 無駄遣い（軽度）支出金額増減値
		private final MinorWasteExpenditureAmount minorWasteExpenditureAmount;
		// 無駄遣い（重度）支出金額の増減フラグ
		private final int severeWasteExpenditureFlg;
		// 無駄遣い（重度）支出金額増減値
		private final SevereWasteExpenditureAmount severeWasteExpenditureAmount;
		
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
		static BeforeAndAfterExpenditureAmountData from(
				ExpenditureAmountItem beforeData, ExpenditureCategory beforeKubun, 
				ExpenditureAmountItem afterData, ExpenditureCategory afterKubun) {
			
			// 支出金額を加算するか減算するかのフラグ(前＞後なら減算,それ以外は加算) 非null項目なのでnullチェック不要
			int sisyutuFlg = (beforeData.getExpenditureAmount().getValue().compareTo(afterData.getExpenditureAmount().getValue()) > 0)
					 ? FLG_SUBTRACT : FLG_ADD;
			
			// 支出金額増減値
			ExpenditureAmount zougenti = ExpenditureAmount.from(
					beforeData.getExpenditureAmount().getValue().subtract(afterData.getExpenditureAmount().getValue()).abs());
			
			// 無駄遣い（軽度）、無駄遣い（重度）加減算初期値
			int minorFlg = FLG_ADD;
			MinorWasteExpenditureAmount zougentiMinorWk = MinorWasteExpenditureAmount.from(null);
			int severeFlg = FLG_ADD;
			SevereWasteExpenditureAmount zougentiSevereWk = SevereWasteExpenditureAmount.from(null);
			
			// 更新前と更新後で支出区分の値が同じ場合
			if(beforeKubun.getValue().equals(afterKubun.getValue())) {

				// 支出区分が区分B（無駄遣い軽度）の場合、無駄遣い（軽度）の値を加減算
				if(ExpenditureCategory.isWastedB(beforeKubun)) {
					minorFlg = sisyutuFlg;
					zougentiMinorWk = MinorWasteExpenditureAmount.from(zougenti.getValue());

				// 支出区分が区分C（無駄遣い重度）の場合、無駄遣い（重度）の値を加減算
				} else if(ExpenditureCategory.isWastedC(beforeKubun)) {
					severeFlg = sisyutuFlg;
					zougentiSevereWk = SevereWasteExpenditureAmount.from(zougenti.getValue());
				}

			// 更新前と更新後で支出区分の値が違う場合
			} else {
				// 無駄遣い（軽度）支出金額の設定
				// 更新前の支出区分が区分B（無駄遣い軽度）の場合、無駄遣い（軽度）の値を減算
				if(ExpenditureCategory.isWastedB(beforeKubun)) {
					minorFlg = FLG_SUBTRACT;
					zougentiMinorWk = beforeData.getMinorWasteExpenditureAmount();

				// 更新後の支出区分が区分B（無駄遣い軽度）の場合、無駄遣い（軽度）の値を加算
				} else if (ExpenditureCategory.isWastedB(afterKubun)) {
					zougentiMinorWk = afterData.getMinorWasteExpenditureAmount();
				}

				// 無駄遣い（重度）支出金額の設定
				// 更新前の支出区分が区分C（無駄遣い重度）の場合、無駄遣い（重度）の値を減算
				if(ExpenditureCategory.isWastedC(beforeKubun)) {
					severeFlg = FLG_SUBTRACT;
					zougentiSevereWk = beforeData.getSevereWasteExpenditureAmount();

				// 更新後の支出区分が区分C（無駄遣い重度）の場合、無駄遣い（重度）の値を加算
				} else if (ExpenditureCategory.isWastedC(afterKubun)) {
					zougentiSevereWk = afterData.getSevereWasteExpenditureAmount();
				}
			}
			
			// 各種増減値のインスタンスを生成して返却
			return new BeforeAndAfterExpenditureAmountData(
					// 更新前の支出金額テーブル情報
					beforeData,
					// 更新後の支出金額テーブル情報
					afterData,
					// 支出金額を加算するか減算するかのフラグ
					sisyutuFlg,
					// 支出金額増減値
					zougenti,
					// 無駄遣い（軽度）支出金額を加算するか減算するかのフラグ
					minorFlg,
					// 無駄遣い（軽度）支出金額増減値
					zougentiMinorWk,
					// 無駄遣い（重度）支出金額を加算するか減算するかのフラグ
					severeFlg,
					// 無駄遣い（重度）支出金額増減値
					zougentiSevereWk);
		}
	}
	
	// 支出項目情報取得コンポーネント
	private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
	// 支出金額テーブルのデータを格納したマップ
	private Map<ExpenditureItemCode, ExpenditureAmountItemData> expenditureAmountItemMap = new HashMap<ExpenditureItemCode, ExpenditureAmountItemData>();
	
	/**
	 *<pre>
	 * 支出金額テーブル情報のリストをもとに、ホールドクラスを生成して返します。
	 *</pre>
	 * @param itemList ホールドに格納する支出金額テーブル情報のリスト
	 * @param expenditureItemInfoComponent 支出項目テーブル検索用コンポーネント
	 * @return 支出金額テーブル情報を格納したホルダー
	 *
	 */
	public static ExpenditureAmountItemHolder from(List<ExpenditureAmountItem> itemList, ExpenditureItemInfoComponent expenditureItemInfoComponent) {
		// ホルダーを新規作成
		ExpenditureAmountItemHolder holder = new ExpenditureAmountItemHolder(expenditureItemInfoComponent);
		// 支出金額テーブル情報のリスト件数分繰り返し
		itemList.forEach(inputData -> {
			// 支出金額テーブル情報のデータをもとに内部保持用データを作成
			ExpenditureAmountItemData data = new ExpenditureAmountItemData();
			data.setExpenditureAmountItem(inputData);
			data.setStatus(STATUS_NON_UPDATE);
			
			// 内部保持用データを支出項目コードをキーにマップに登録
			holder.expenditureAmountItemMap.put(inputData.getExpenditureItemCode(), data);
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
		addItemMap(addExpenditureData.getExpenditureItemCode(), addExpenditureData);
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
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(
				// ユーザID
				updExpData.getUserId(),
				// 支出項目コード
				updExpData.getExpenditureItemCode());
		
		// 更新前の支出金額テーブル情報を作成
		ExpenditureAmountItem beforeItem = createSisyutuKingakuItem(expenditureItemInfo, beforeData);
		// 更新後の支出金額テーブル情報を作成
		ExpenditureAmountItem afterItem = createSisyutuKingakuItem(expenditureItemInfo, updExpData);
		
		// 更新前-更新後
		BeforeAndAfterExpenditureAmountData sabunData = BeforeAndAfterExpenditureAmountData.from(
				beforeItem, beforeData.getExpenditureCategory(), afterItem, updExpData.getExpenditureCategory());
		// 更新する支出情報を元に、ホルダーに登録されている支出金額情報を指定された支出情報の値分加減算します。
		updateItemMap(updExpData.getExpenditureItemCode(), sabunData);
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
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(deleteData.getUserId(), deleteData.getExpenditureItemCode());
		
		// 支出テーブル情報から更新対象の支出金額テーブル情報を作成
		ExpenditureAmountItem deleteItem = createSisyutuKingakuItem(expenditureItemInfo, deleteData);
		
		// 削除する支出情報を元に、ホルダーに登録されている支出金額情報を指定された支出情報の値分減算します。
		subtractItemMap(deleteData.getExpenditureItemCode(), deleteItem);
	}
	
	/**
	 *<pre>
	 * ホルダーに設定されている支出金額テーブル情報のうち、新規追加分のデータのリストを返します。
	 *</pre>
	 * @return 新規追加分の支出金額テーブル情報のリスト
	 *
	 */
	public List<ExpenditureAmountItem> getAddList() {
		return getSisyutuKingakuItemList(STATUS_ADD);
	}
	
	/**
	 *<pre>
	 * ホルダーに設定されている支出金額テーブル情報のうち、更新対象分のデータのリストを返します。
	 *</pre>
	 * @return 更新対象分の支出金額テーブル情報のリスト
	 *
	 */
	public List<ExpenditureAmountItem> getUpdateList() {
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
	private List<ExpenditureAmountItem> getSisyutuKingakuItemList(int status) {
		List<ExpenditureAmountItem> resultList = new ArrayList<>();
		for(ExpenditureAmountItemData data : expenditureAmountItemMap.values()) {
			if(data.getStatus() == status) {
				resultList.add(data.getExpenditureAmountItem());
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
	private void addItemMap(ExpenditureItemCode sisyutuItemCode, ExpenditureItem addExpenditureData) {
		
		// 支出項目コードに対応する支出項目情報を取得
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(
				// ユーザID
				addExpenditureData.getUserId(),
				// 支出項目コード
				sisyutuItemCode);
		
		// 支出テーブル情報から支出金額テーブル情報を作成
		ExpenditureAmountItem addItem = createSisyutuKingakuItem(expenditureItemInfo, addExpenditureData);
		// 支出項目コードがホルダーに登録済みの場合、ホルダーのデータを更新
		ExpenditureAmountItemData addData = expenditureAmountItemMap.get(sisyutuItemCode);
		
		// 新規追加の場合
		if(addData == null) {
			// 内部保持用データを作成し各種値を設定
			addData = new ExpenditureAmountItemData();
			addData.setExpenditureAmountItem(addItem);
			addData.setStatus(STATUS_ADD);
			
		// ホルダーのデータを更新する場合
		} else {
			// DBに登録済みデータの場合、ステータスを更新に変更
			if(addData.getStatus() == STATUS_NON_UPDATE) {
				addData.setStatus(STATUS_UPDATE);
			}
			// 更新前の支出金額情報を取得
			ExpenditureAmountItem beforeItem = addData.getExpenditureAmountItem();
			// 新規の支出金額情報を前のデータに加算しホルダーに登録
			addData.setExpenditureAmountItem(ExpenditureAmountItem.from(
					// ユーザID
					beforeItem.getUserId().getValue(),
					// 対象年
					beforeItem.getTargetYear().getValue(),
					// 対象月
					beforeItem.getTargetMonth().getValue(),
					// 支出項目コード
					beforeItem.getExpenditureItemCode().getValue(),
					// 親支出項目コード
					beforeItem.getParentExpenditureItemCode().getValue(),
					// 支出予定金額=前の値+新規の値
					beforeItem.getExpectedExpenditureAmount().add(addItem.getExpectedExpenditureAmount()).getValue(),
					// 支出金額=前の値+新規の値
					beforeItem.getExpenditureAmount().add(addItem.getExpenditureAmount()).getValue(),
					// 無駄遣い（軽度）支出金額=前の値+新規の値
					beforeItem.getMinorWasteExpenditureAmount().add(addItem.getMinorWasteExpenditureAmount()).getValue(),
					// 無駄遣い（重度）支出金額=前の値+新規の値
					beforeItem.getSevereWasteExpenditureAmount().add(addItem.getSevereWasteExpenditureAmount()).getValue(),
					// 支出支払日=前の値と支出情報(ドメイン)の支払日のどちらか大きいほうの値
					beforeItem.getPaymentDate().max(addExpenditureData.getPaymentDate()).getValue()));
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		expenditureAmountItemMap.put(sisyutuItemCode, addData);
		
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(expenditureItemInfo.getParentExpenditureItemCode().getValue().equals(
				expenditureItemInfo.getExpenditureItemCode().getValue())) {
			return;
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			addItemMap(ExpenditureItemCode.from(expenditureItemInfo.getParentExpenditureItemCode()), addExpenditureData);
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
	private void updateItemMap(ExpenditureItemCode sisyutuItemCode, BeforeAndAfterExpenditureAmountData sabunData) {
		
		// ホルダーから加減算対象の支出金額情報を取得
		ExpenditureAmountItemData updateData = expenditureAmountItemMap.get(sisyutuItemCode);
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
		ExpenditureAmountItem beforeItem = updateData.getExpenditureAmountItem();
		// 前のデータの値を減算しホルダーに登録
		try {
			// 支出金額=前の値+加減算するの値
			ExpenditureAmount sisyutuKingaku = null;
			if(sabunData.getExpenditureAmountFlg() == FLG_ADD) {
				sisyutuKingaku = beforeItem.getExpenditureAmount().add(sabunData.getExpenditureAmount());
			} else {
				sisyutuKingaku = beforeItem.getExpenditureAmount().subtract(sabunData.getExpenditureAmount());
			}
			// 無駄遣い（軽度）支出金額=前の値+加減算するの値
			MinorWasteExpenditureAmount minorWaste = null;
			if(sabunData.getMinorWasteExpenditureFlg() == FLG_ADD) {
				minorWaste = beforeItem.getMinorWasteExpenditureAmount().add(sabunData.getMinorWasteExpenditureAmount());
			} else {
				minorWaste = beforeItem.getMinorWasteExpenditureAmount().subtract(sabunData.getMinorWasteExpenditureAmount());
			}
			// 無駄遣い（重度）支出金額=前の値+加減算するの値
			SevereWasteExpenditureAmount severeWaste = null;
			if(sabunData.getSevereWasteExpenditureFlg() == FLG_ADD) {
				severeWaste = beforeItem.getSevereWasteExpenditureAmount().add(sabunData.getSevereWasteExpenditureAmount());
			} else {
				severeWaste = beforeItem.getSevereWasteExpenditureAmount().subtract(sabunData.getSevereWasteExpenditureAmount());
			}
			// 支出金額情報を差額分更新
			updateData.setExpenditureAmountItem(ExpenditureAmountItem.from(
				// ユーザID
				beforeItem.getUserId().getValue(),
				// 対象年
				beforeItem.getTargetYear().getValue(),
				// 対象月
				beforeItem.getTargetMonth().getValue(),
				// 支出項目コード
				beforeItem.getExpenditureItemCode().getValue(),
				// 親支出項目コード
				beforeItem.getParentExpenditureItemCode().getValue(),
				// 支出予定金額の値は変更なし(前の値をそのまま設定)
				beforeItem.getExpectedExpenditureAmount().getValue(),
				// 支出金額
				sisyutuKingaku.getValue(),
				// 無駄遣い（軽度）支出金額=前の値+加減算するの値
				minorWaste.getValue(),
				// 無駄遣い（重度）支出金額=前の値+加減算するの値
				severeWaste.getValue(),
				// 支出支払日=前の値と更新後の支出情報(ドメイン)の支払日のどちらか大きいほうの値
				beforeItem.getPaymentDate().max(sabunData.getAfterData().getPaymentDate()).getValue()));
		} catch (Exception ex) {
			// 計算で値不正の場合、エラーログを出力
			log.error("例外発生のため支出金額情報をログ出力：[sabunData=" + sabunData + "]");
			throw ex;
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		expenditureAmountItemMap.put(sisyutuItemCode, updateData);
		
		// 支出項目コードに対応する支出項目情報を取得
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(beforeItem.getUserId(), sisyutuItemCode);
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(expenditureItemInfo.getParentExpenditureItemCode().getValue().equals(
				expenditureItemInfo.getExpenditureItemCode().getValue())) {
			return;
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			updateItemMap(ExpenditureItemCode.from(expenditureItemInfo.getParentExpenditureItemCode()), sabunData);
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
	private void subtractItemMap(ExpenditureItemCode sisyutuItemCode, ExpenditureAmountItem subtractItem) {
		
		// ホルダーから減算対象の支出金額情報を取得
		ExpenditureAmountItemData subtractData = expenditureAmountItemMap.get(sisyutuItemCode);
		if(subtractData == null) {
			log.error("例外発生のため支出金額情報をログ出力：[subtractSisyutuKingakuItem=" + subtractItem + "]");
			throw new MyHouseholdAccountBookRuntimeException("減算対象の支出金額情報が存在しません。管理者に問い合わせてください。sisyutuItemCode:" + sisyutuItemCode);
		}
		
		// テータスを更新に変更
		if(subtractData.getStatus() == STATUS_NON_UPDATE) {
			subtractData.setStatus(STATUS_UPDATE);
		}
		// 更新前の支出金額情報を取得
		ExpenditureAmountItem beforeItem = subtractData.getExpenditureAmountItem();
		// 前のデータの値を減算しホルダーに登録
		try {
			subtractData.setExpenditureAmountItem(ExpenditureAmountItem.from(
				// ユーザID
				beforeItem.getUserId().getValue(),
				// 対象年
				beforeItem.getTargetYear().getValue(),
				// 対象月
				beforeItem.getTargetMonth().getValue(),
				// 支出項目コード
				beforeItem.getExpenditureItemCode().getValue(),
				// 親支出項目コード
				beforeItem.getParentExpenditureItemCode().getValue(),
				// 支出予定金額の値は変更なし(前の値をそのまま設定)
				beforeItem.getExpectedExpenditureAmount().getValue(),
				// 支出金額=前の値-減算するの値
				beforeItem.getExpenditureAmount().subtract(subtractItem.getExpenditureAmount()).getValue(),
				// 無駄遣い（軽度）支出金額=前の値-減算するの値
				beforeItem.getMinorWasteExpenditureAmount().subtract(subtractItem.getMinorWasteExpenditureAmount()).getValue(),
				// 無駄遣い（重度）支出金額=前の値-減算するの値
				beforeItem.getSevereWasteExpenditureAmount().subtract(subtractItem.getSevereWasteExpenditureAmount()).getValue(),
				// 支出支払日の値は変更なし(前の値をsubtractItem設定)
				beforeItem.getPaymentDate().getValue()));
		} catch (Exception ex) {
			// 計算で値不正の場合、エラーログを出力
			log.error("例外発生のため支出金額情報をログ出力：[beforeSisyutuKingakuItem=" + beforeItem + "][subtractSisyutuKingakuItem=" + subtractItem + "]");
			throw ex;
		}
		
		// 内部保持用データを支出項目コードをキーにマップに登録 or 更新
		expenditureAmountItemMap.put(sisyutuItemCode, subtractData);
		
		// 支出項目コードに対応する支出項目情報を取得
		ExpenditureItemInfo expenditureItemInfo = expenditureItemInfoComponent.getExpenditureItemInfo(subtractItem.getUserId(), sisyutuItemCode);
		// 親の支出項目コード == 支出項目コードの場合、再帰終了
		if(expenditureItemInfo.getParentExpenditureItemCode().getValue().equals(
				expenditureItemInfo.getExpenditureItemCode().getValue())) {
			return;
		
		// 親の支出項目コード != 支出項目コードの場合、親の支出項目コードをもとに再帰呼び出し
		} else {
			subtractItemMap(ExpenditureItemCode.from(expenditureItemInfo.getParentExpenditureItemCode()), subtractItem);
		}
	}
	
	/**
	 *<pre>
	 * 支出情報から支出金額情報(ドメイン)を作成して返します。
	 *</pre>
	 * @param expenditureItemInfo 支出項目情報(ドメイン)
	 * @param expenditureData 支出情報(ドメイン)
	 * @return 支出金額情報(ドメイン)
	 *
	 */
	private ExpenditureAmountItem createSisyutuKingakuItem(ExpenditureItemInfo expenditureItemInfo, ExpenditureItem expenditureData) {
		return ExpenditureAmountItem.from(
				// ユーザID
				expenditureData.getUserId().getValue(),
				// 対象年
				expenditureData.getTargetYear().getValue(),
				// 対象月
				expenditureData.getTargetMonth().getValue(),
				// 支出項目コード
				expenditureItemInfo.getExpenditureItemCode().getValue(),
				// 親支出項目コード
				expenditureItemInfo.getParentExpenditureItemCode().getValue(),
				// 支出予定金額
				expenditureData.getExpectedExpenditureAmount().getValue(),
				// 支出金額
				expenditureData.getExpenditureAmount().getValue(),
				// 支出金額B：無駄遣い（軽度）支出金額
				ExpenditureCategory.isWastedB(expenditureData.getExpenditureCategory())
					? expenditureData.getExpenditureAmount().getValue() : null,
				// 支出金額C：無駄遣い（重度）支出金額
				ExpenditureCategory.isWastedC(expenditureData.getExpenditureCategory())
				? expenditureData.getExpenditureAmount().getValue() : null,
				// 支出支払日
				expenditureData.getPaymentDate().getValue());
	}
}
