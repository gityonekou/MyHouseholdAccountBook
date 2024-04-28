/**
 * 管理者画面メニュー ベース情報管理のユースケースです。
 * ・ベース情報管理画面表示情報取得
 * ・指定ファイル情報をもとに、指定ユーザのベース情報を作成
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.adminmenu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBase;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.ShopBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.SisyutuItemBaseTableRepository;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUploadBaseInfoFileForm;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuBaseInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 管理者画面メニュー ベース情報管理のユースケースです。
 * ・ベース情報管理画面表示情報取得
 * ・指定ファイル情報をもとに、指定ユーザのベース情報を作成
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
public class AdminMenuBaseInfoUseCase {
	
	// 支出項目テーブルベースデータを登録/全件取得するリポジトリー
	private final SisyutuItemBaseTableRepository sisyutuItemBaseTableRepository;
	// 店舗テーブルベースデータを登録/全件取得するリポジトリー
	private final ShopBaseTableRepository shopBaseTableRepository;
	
	/**
	 *<pre>
	 * ベース情報管理画面の表示情報を取得します。
	 *</pre>
	 * @return ベース情報管理画面の表示情報(レスポンス)
	 *
	 */
	public AdminMenuBaseInfoResponse read() {
		log.debug("read:");
		AdminMenuBaseInfoResponse response = AdminMenuBaseInfoResponse.getInstance();
		// カウント結果を取得・設定
		response.setCountSisyutuItemBaseTable(sisyutuItemBaseTableRepository.countAll());
		response.setCountShopBaseTable(shopBaseTableRepository.countAll());
		// 初期表示のレスポンスを返却
		return response;
	}

	/**
	 *<pre>
	 * アップロードしたファイルをベース情報に登録します。
	 *</pre>
	 * @param baseInfoFileForm　アップロードしたファイル
	 * @return　ベース情報管理画面の表示情報(レスポンス)
	 *
	 */
	@Transactional(rollbackFor = MyHouseholdAccountBookException.class)
	public AdminMenuBaseInfoResponse upload(AdminMenuUploadBaseInfoFileForm baseInfoFileForm) {
		log.debug("upload:baseInfoFile=" + baseInfoFileForm.getBaseInfoFile().getOriginalFilename());
		AdminMenuBaseInfoResponse response = read();
		
		// 指定されたファイルを読み込み、対象のベースデータに登録します
		Map<String, List<String[]>> tableDataMap = new HashMap<String, List<String[]>>();
		
		try (
				BufferedInputStream bin = new BufferedInputStream(baseInfoFileForm.getBaseInfoFile().getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(bin, "UTF-8"))
			){
			
			// データを最初から読み込み、各データを出力対象のテーブルに出力します。
			String readLine = null;
			String targetTable = null;
			int linecount = 0;
			while((readLine = br.readLine()) != null) {
				linecount++;
				// 空行の場合は次のデータへ
				if(readLine.isEmpty()) {
					continue;
				}
				// コメント行、またはテーブル設定行の場合
				if(readLine.length() >= 2 && readLine.charAt(0) == '#') {
					// 先頭が##の場合、コメント行
					if(readLine.charAt(1) == '#') {
						continue;
					// 先頭が#[の場合、テーブル設定行の場合
					} else if(readLine.charAt(1) == '[') {
						// 出力対象のテーブル名(ベースのテーブル名)を取得
						int closeIndex = readLine.indexOf(']', 2);
						if(closeIndex == -1) {
							// 閉じかっこがない場合
							response.addErrorMessage("読み込みデータが不正です。[行数=" + linecount + "][" + readLine + "]");
							break;
						}
						targetTable = readLine.substring(2, closeIndex);
						if(!StringUtils.hasLength(targetTable)) {
							// 出力対象のテーブル名が指定されていない場合
							response.addErrorMessage("読み込みデータが不正です。[行数=" + linecount + "][" + readLine + "]");
							break;
						}
						// キー(テーブル名)が既に登録されている場合、エラー終了する
						if(tableDataMap.containsKey(targetTable)) {
							// 出力対象のテーブル名が指定されていない場合
							response.addErrorMessage("読み込みデータが不正です。テーブル名が重複いしています。[行数=" 
									+ linecount + "][" + readLine + "]");
							break;
						}
						// 指定テーブルのリポジトリーが存在するかをチェック
						if(!checkTargetTableKey(targetTable)) {
							// 出力対象のテーブル名が不正な値の場合
							response.addErrorMessage("読み込みデータが不正です。テーブル名に対応するリポジトリーがありません。[行数=" 
									+ linecount + "][" + readLine + "]");
							break;
						}
						// キー(テーブル名)と新規の文字列リストを登録
						tableDataMap.put(targetTable, new ArrayList<String[]>());
					} else {
						response.addErrorMessage("読み込みデータが不正です。'##'か'#[テーブル名]'である必要があります。[行数=" 
									+ linecount + "][" + readLine + "]");
						break;
					}
				// 上記以外の場合、登録データ	
				} else {
					// テーブル名が未設定の場合エラー
					if(targetTable == null) {
						response.addErrorMessage("読み込みデータが不正です。出力対象のテーブルが指定されていません。[行数=" 
									+ linecount + "][" + readLine + "]");
						break;
					}
					// テーブルキーの種類に応じてデータのチェックを行う
					String data = readLine.trim();
					String[] dataItems = data.split(",");
					
					// 支出項目テーブル:SISYUTU_ITEM_TABLEのベースデータ登録の場合
					if(targetTable.equals(MyHouseholdAccountBookContent.SISYUTU_ITEM_BASE_TABLE)) {
						// データ項目は6項目
						if(dataItems.length != 6) {
							response.addErrorMessage("読み込みデータが不正です。項目数不正。[行数=" + linecount + "][" + readLine + "]");
							break;
						}
						// 支出項目レベルは1～5の数値
						try {
							int level = Integer.parseInt(dataItems[4]);
							if(level < 1 || level > 5) {
								response.addErrorMessage("読み込みデータが不正です。支出項目レベル。[行数=" + linecount + "][" + readLine + "]");
								break;
							}
						} catch(NumberFormatException ex) {
							response.addErrorMessage("読み込みデータが不正です。支出項目レベル。[行数=" + linecount + "][" + readLine + "]");
							break;
						}
					// 店名テーブル:SHOP_TABLEのベースデータ登録の場合
					} else if(targetTable.equals(MyHouseholdAccountBookContent.SHOP_BASE_TABLE)) {
						// TODO:登録データのチェック処理を追加予定(データ項目確定後に追加)
					}
					List<String[]> dataList = tableDataMap.get(targetTable);
					dataList.add(dataItems);
				}
			}
			
		} catch(IOException ex) {
			throw new MyHouseholdAccountBookRuntimeException(ex);
		}
		
		// 読み込みデータを指定のテーブル出力ドメインを用いてDB出力する
		if(!response.isErrorResponse()) {
		
			// 登録されているテーブルキーの数分データ出力を繰り返す
			for(String key : tableDataMap.keySet()) {
				
				List<String[]> dataItemsList = tableDataMap.get(key);
				
				// テーブルキーの種類に応じて呼び出すリソースを振り分け
				switch (key) {
					// 支出項目テーブル:SISYUTU_ITEM_TABLEのベースデータの場合
					case MyHouseholdAccountBookContent.SISYUTU_ITEM_BASE_TABLE :
						dataItemsList.forEach(dataItems -> {
							// 登録する支出項目テーブル(BASE)情報を生成
							SisyutuItemBase addData =  SisyutuItemBase.from(
									dataItems[0],
									dataItems[1],
									dataItems[2],
									dataItems[3],
									dataItems[4],
									dataItems[5]
									);
							log.debug("SISYUTU_ITEM_BASE_TABLE:addData=" + addData);
							// データを追加
							int addCount = sisyutuItemBaseTableRepository.add(addData);
							// 追加件数が1件以上の場合、業務エラー
							if(addCount != 1) {
								throw new MyHouseholdAccountBookRuntimeException("支出項目テーブル(BASE)への追加件数が不正でした。[add data:" + addData + "]");
							}
						});
						break;
						
					// 店名テーブル:SHOP_TABLEのベースデータ登録の場合
					case MyHouseholdAccountBookContent.SHOP_BASE_TABLE :
						dataItemsList.forEach(dataItems -> {
							// 登録する店舗テーブル(BASE)情報を生成
							ShopBase addData = ShopBase.from(dataItems[0], dataItems[1]);
							log.debug("SHOP_BASE_TABLE:addData=" + addData);
							// データを追加
							int addCount = shopBaseTableRepository.add(addData);
							// 追加件数が1件以上の場合、業務エラー
							if(addCount != 1) {
								throw new MyHouseholdAccountBookRuntimeException("店舗テーブル(BASE)への追加件数が不正でした。[add data:" + addData + "]");
							}
						});
						break;
						
					// デフォルトはコード値の登録とする
					// 不正な値かどうかはtableDataMapに登録時にチェック済みなので、チェックしない
					default :	
				}
			}
			// 正常終了
			response.setTransactionSuccessFull();
		}
		
		return response;
	}

	/**
	 *<pre>
	 * 指定テーブルのリポジトリーが存在するかをチェック
	 *</pre>
	 * @param targetTable チェックするテーブル名
	 * @return 対応するリポジトリーが存在する場合true、存在しない場合false
	 *
	 */
	private boolean checkTargetTableKey(String targetTable) {
		switch(targetTable) {
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_BASE_TABLE:
			case MyHouseholdAccountBookContent.SHOP_BASE_TABLE:
				return true;
			default:
				return false;
		}
	}
}
