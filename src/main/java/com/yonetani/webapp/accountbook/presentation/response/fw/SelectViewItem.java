/**
 * select-optionタグを表示するための画面表示部品です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.fw;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * select-optionタグを表示するための画面表示部品です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SelectViewItem {
	
	/**
	 *<pre>
	 * optionタブのvalue属性, text属性の値を表すクラスです
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class OptionItem {
		// value属性の値
		private final String value;
		// text属性の値
		private final String text;
		// disabled属性の値
		private final boolean disabled;
		
		/**
		 *<pre>
		 * optionタブの情報を生成して返します。生成されるoptionタグのdisabledの値はfalse(選択可能)となります。
		 *</pre>
		 * @param value value属性の値
		 * @param text text属性の値
		 * @return value
		 *
		 */
		public static OptionItem from(String value, String text) {
			return new OptionItem(value, text, false);
		}
		
		/**
		 *<pre>
		 * disabled属性の値を指定してoptionタブの情報を生成して返します。
		 * disabled属性の値にtureを指定した場合、このoption項目は選択不可(disabled)な状態で表示します。
		 * disabled属性の値にfalseを指定した場合、このoption項目を選択可能な状態で表示します。
		 *</pre>
		 * @param value value属性の値
		 * @param text text属性の値
		 * @param disabled disabled属性の値
		 * @return value
		 *
		 */
		public static OptionItem from(String value, String text, boolean disabled) {
			return new OptionItem(value, text, disabled);
		}
	}
	
	// Optionタグの表示情報のリスト
	private final List<OptionItem> optionList;
	
	/**
	 *<pre>
	 * 引数の値からSelectViewItem部品を生成して返します。
	 *</pre>
	 * @param optionList optionタグに表示する値のリスト
	 * @return SelectViewItem部品
	 *
	 */
	public static SelectViewItem from(List<OptionItem> optionList) {
		if(CollectionUtils.isEmpty(optionList)) {
			return new SelectViewItem(Collections.emptyList());
		} else {
			return new SelectViewItem(optionList);
		}
	}
}
