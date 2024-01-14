/**
 * 管理者画面メニュー ベース情報詳細画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.adminmenu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 管理者画面メニュー ベース情報詳細画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMenuBaseInfoDetailResponse extends AbstractResponse {
	
	// 表示対象のテーブル名
	private final String targetTableName;
	// 表示対象のテーブルの項目名表示行
	@Setter
	private String tableItemsLine;
	// 表示対象のデータ
	private List<String> tableDataList = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return ベース情報詳細画面表示情報
	 *
	 */
	public static AdminMenuBaseInfoDetailResponse getInstance(String targetTableName) {
		return new AdminMenuBaseInfoDetailResponse(targetTableName);
	}
	
	/**
	 *<pre>
	 * 表示対象のデータを追加します
	 *</pre>
	 * @param addList 追加するデータのリスト
	 *
	 */
	public void addTableDataList(List<String> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			tableDataList.addAll(addList);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("adminmenu/baseinfodetail");
		modelAndView.addObject("targetTableName", targetTableName);
		modelAndView.addObject("tableItemsLine", tableItemsLine);
		modelAndView.addObject("tableDataList", tableDataList);
		return modelAndView;
	}

}
