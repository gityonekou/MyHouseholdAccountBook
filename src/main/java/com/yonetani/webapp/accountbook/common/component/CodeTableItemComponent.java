/**
 * 指定したコードやコード分類に対応する値をコードテーブルから取得するためのコンポーネントです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/20 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.common.CodeTableItemList;
import com.yonetani.webapp.accountbook.domain.repository.common.CodeTableRepository;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 指定したコードやコード分類に対応する値をコードテーブルから取得するためのコンポーネントです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@Log4j2
public class CodeTableItemComponent {
	// コードマップ
	private final Map<String, Map<String, String>> codeMapTable;
	
	/**
	 *<pre>
	 * CodeTableItemComponentクラスコンストラクターです。
	 * インジェクション(DI)対象のインスタンス登録時、FWによって呼び出し、インスタンスが管理されます。
	 * つまり、このインスタンスはシングルトンでなければならず、開発者がこのコンストラクタを呼び出す必要
	 * はありません。
	 * 
	 *</pre>
	 * @param codeTableRepository
	 *
	 */
	public CodeTableItemComponent(CodeTableRepository codeTableRepository) {
		log.debug("CodeTableItemComponent constractor start:");
		
		// コード定義テーブル情報読み取り
		CodeTableItemList result = codeTableRepository.findAll();
		log.debug(result);
		
		// コードマップ作成
		this.codeMapTable = new HashMap<String, Map<String, String>>();
		result.getValues().forEach(codeTableItem -> {
			Map<String, String> codeValuePairMap = new LinkedHashMap<>();
			codeTableItem.getKeyValueList().forEach(keyValuePair -> codeValuePairMap.put(
					keyValuePair.getCode().toString(), keyValuePair.getCodeValue().toString()));
			codeMapTable.put(codeTableItem.getKubun().toString(), codeValuePairMap);
			
		});
		log.debug("CodeTableItemComponent constractor end:");
	}
	
	/**
	 *<pre>
	 * 指定した区分に対応するコードテーブルのキーと値のペア情報を取得します。
	 *</pre>
	 * @param kubun 取得対象のコード区分
	 * @return 対応するコードテーブル情報
	 *
	 */
	public List<CodeAndValuePair> getCodeValues(String kubun) {
		Map<String, String> resultMap = codeMapTable.get(kubun);
		if(resultMap == null) {
			return null;
		} else {
			List<CodeAndValuePair> keyValuePairList = new ArrayList<>();
			resultMap.entrySet().forEach(entry -> 
				keyValuePairList.add(CodeAndValuePair.from(entry.getKey(), entry.getValue())));
			return keyValuePairList;
		}
	}
	
	/**
	 *<pre>
	 * 指定した区分、キーに対応する値を取得します。
	 *</pre>
	 * @param kubun 取得対象のコード区分
	 * @return 対応するコードテーブル情報
	 *
	 */
	public String getCodeValue(String kubun, String key) {
		Map<String, String> resultMap = codeMapTable.get(kubun);
		return resultMap.get(key);
	}
}
