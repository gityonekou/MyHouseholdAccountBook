/**
 * コード定義テーブルの参照を行うリポジトリーを実装したデータソースです
 * コード定義テーブルはプロパティ「accountbook.property.codetable-file-path」に定義した
 * ファイルから取得します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.common.CodeTableItem;
import com.yonetani.webapp.accountbook.domain.model.common.CodeTableItemList;
import com.yonetani.webapp.accountbook.domain.repository.common.CodeTableRepository;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * コード定義テーブルの参照を行うリポジトリーを実装したデータソースです
 * コード定義テーブルはプロパティ「accountbook.property.codetable-file-path」に定義した
 * ファイルから取得します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@Log4j2
public class CodeTableDataSource implements CodeTableRepository {

	// コード定義ファイルへのパス
	@Value("${accountbook.property.codetable-file-path}")
	private String codeTableFilePath;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodeTableItemList findAll() {
		// コード定義ファイルを読み込み、コード定義テーブル情報の取得結果(リスト情報)を作成します
		log.debug("コード定義ファイルパス:" + codeTableFilePath);
		List<CodeTableItem> codeTableItemList = new ArrayList<>();
		try (
				BufferedInputStream bin = new BufferedInputStream(new FileInputStream(codeTableFilePath));
				BufferedReader br = new BufferedReader(new InputStreamReader(bin, "UTF-8"))){
			
			// コード定義ファイルの読み込み
			String readLine = null;
			int linecount = 0;
			String kubun = null;
			List<CodeAndValuePair> keyValueList = new ArrayList<>();
			while((readLine = br.readLine()) != null) {
				linecount++;
				// 空行かコメント行(先頭が#)の場合は次のデータへ
				readLine = readLine.trim();
				if(readLine.isEmpty() || readLine.charAt(0) == '#') {
					continue;
				}
				// データを,で分割
				String[] dataItems = readLine.split(",");
				if(dataItems.length != 3) {
					log.error("コード定義ファイルの読み込みデータが不正です。[行数=" + linecount + "][" + readLine + "]");
					throw new MyHouseholdAccountBookRuntimeException("コード定義ファイル設定値不正");
				}
				
				// 区分が変更された場合、前のコード定義情報を登録
				if(kubun != null && !Objects.equals(kubun, dataItems[0])) {
					codeTableItemList.add(CodeTableItem.from(kubun, keyValueList));
					keyValueList = new ArrayList<>(); 
				}
				// 現在の行のコード定義情報のペアを登録
				kubun = dataItems[0];
				keyValueList.add(CodeAndValuePair.from(dataItems[1], dataItems[2]));
			}
			// 最後のコード定義情報を追加
			if(kubun != null) {
				codeTableItemList.add(CodeTableItem.from(kubun, keyValueList));
			}
			
		} catch (IOException ex) {
			log.error(ex);
			throw new MyHouseholdAccountBookRuntimeException(ex);
		}
		return CodeTableItemList.from(codeTableItemList);
	}

}
