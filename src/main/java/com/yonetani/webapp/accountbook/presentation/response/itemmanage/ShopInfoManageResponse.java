/**
 * 情報管理(お店)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(お店)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopInfoManageResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細データです
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class ShopListItem {
		// 店舗コード
		private final String shopCode;
		// 店舗名
		private final String shopName;
		// 店舗区分名称
		private final String shopKubunName;
		// 店舗表示順
		private final String shopSort;
		
		/**
		 *<pre>
		 * 引数の値から店舗一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param shopCode 店舗コード
		 * @param shopName 店舗名
		 * @param shopKubunName 店舗区分名称
		 * @param shopSort 店舗表示順
		 * @return 店舗一覧情報の明細データ
		 *
		 */
		public static ShopListItem from(String shopCode, String shopName, String shopKubunName, String shopSort) {
			return new ShopListItem(shopCode, shopName, shopKubunName, shopSort);
		}
	}
	// 店舗グループ
	private final SelectViewItem shopKubunItem;
	// 店舗一覧情報の明細データ(変更可能分)
	private List<ShopListItem> shopList = new ArrayList<>();
	// 店舗一覧情報の明細データ(変更不可分)
	private List<ShopListItem> nonEditShopList = new ArrayList<>();
	// 店舗情報入力フォーム
	@Setter
	private ShopInfoForm shopInfoForm;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param addList 店舗グループ表示情報のリスト
	 * @return 情報管理(お店)画面表示情報
	 *
	 */
	public static ShopInfoManageResponse getInstance(List<OptionItem> addList) {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "グループメニューを開く"));
		if(!CollectionUtils.isEmpty(addList)) {
			optionList.addAll(addList);
		}
		// グループメニューを開くを選択状態で情報管理(お店)画面表示情報を生成
		return new ShopInfoManageResponse(SelectViewItem.from(optionList));
	}
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する店舗一覧情報の明細リスト
	 *
	 */
	public void addShopList(List<ShopListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			// 追加する店舗一覧情報の数分、追加処理を繰り返す
			addList.forEach(data -> {
				// 表示順で追加先を振り分け
				if(Integer.parseInt(data.getShopSort().toString()) > 900) {
					// 表示順(店舗コード)が900番より大きい場合、変更不可のリストに追加
					// 900番以降の場合、変更不可なので、表示順=店舗コードとなるのを利用
					nonEditShopList.add(data);
				} else {
					// 表示順が900番よりも小さい場合、変更可能データのリストに追加
					shopList.add(data);
				}
			});

		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShopInfoManage");
		// 店舗グループを設定
		modelAndView.addObject("shopKubun", shopKubunItem);
		// 店舗一覧情報を設定
		modelAndView.addObject("shopList", shopList);
		modelAndView.addObject("nonEditShopList", nonEditShopList);
		// 店舗情報入力フォーム
		if(shopInfoForm == null) {
			shopInfoForm = new ShopInfoForm();
			shopInfoForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		}
		modelAndView.addObject("shopInfoForm", shopInfoForm);
		
		return modelAndView;
	}

	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param shopForm バリデーションチェックを行った入力フォームの値
	 * @return 画面返却データのModelAndView
	 *
	 */
	public ModelAndView buildBindingError(ShopInfoForm shopForm) {
		setShopInfoForm(shopForm);
		return build();
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * ShopInfoManageResponseで同メソッドを呼び出した後、ShopInfoManageResponse独自定義のメソッド
	 * を呼び出す必要があるためAbstractResponseの同メソッドをオーバーライド
	 * </pre>
	 */
	@Override
	public ShopInfoManageResponse setLoginUserName(String loginUserName) {
		super.setLoginUserName(loginUserName);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// お店情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/shopinfo/updateComplete/";
	}
}
