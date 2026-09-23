/**
 * 簡易タイプの買い物登録を行うユースケース（照会系）です。買い物登録(簡易タイプ)画面の情報取得を行います。
 * ・買い物登録(簡易タイプ)画面情報取得(新規登録時)
 * ・買い物登録(簡易タイプ)画面情報取得(更新時)
 * ・店舗区分変更時の画面情報取得
 * ・バリデーションチェックエラー時の画面情報取得
 * ・買い物登録方法選択画面(メニュー選択画面)へのリダイレクト情報取得
 * ・各月の収支参照画面へのリダイレクト情報取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/03 : 1.00.00                      新規作成
 * 2025/12/28 : 1.01.00  feature-1.00-dev00  リファクタリング対応（DDD適応)
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応、追加リファクタリング対応(買い物登録ドメインの見直し)
 * 2026/09/23 : 1.02.01  feature-1.03-dev1   追加リファクタリング対応(SimpleShoppingRegistUseCaseを照会系、登録系に分割)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent;
import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 簡易タイプの買い物登録を行うユースケース（照会系）です。買い物登録(簡易タイプ)画面の情報取得を行います。
 * ・買い物登録(簡易タイプ)画面情報取得(新規登録時)
 * ・買い物登録(簡易タイプ)画面情報取得(更新時)
 * ・店舗区分変更時の画面情報取得
 * ・バリデーションチェックエラー時の画面情報取得
 * ・買い物登録方法選択画面(メニュー選択画面)へのリダイレクト情報取得
 * ・各月の収支参照画面へのリダイレクト情報取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class SimpleShoppingRegistInquiryUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 店舗情報取得リポジトリー
	private final ShopTableRepository shopRepository;
	// 買い物登録情報リポジトリー
	private final ShoppingRegistTableRepository shoppingRegistRepository;
	// 簡易タイプ買い物リスト取得コンポーネント
	private final SimpleShoppingRegistListComponent simpleShoppingRegistListComponent;
	// 支払方法情報取得コンポーネント
	private final PaymentMethodInfoComponent paymentMethodInfoComponent;

	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面情報取得
	 *
	 * 指定した対象年月に応じた買い物登録(簡易タイプ)画面の表示情報を取得します。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth  買い物登録を行う対象年月
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);

		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);

		// デフォルトの簡易タイプ買い物登録情報フォームデータを生成
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		// アクション(新規登録)
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 店舗区分は店舗区分で食品・日用品店舗(901)をデフォルト選択する
		inputForm.setShopKubunCode(MyHouseholdAccountBookContent.SHOP_KUBUN_GROCERIES_SELECTED_VALUE);
		// 対象年月：ドメインタイプで入力値チェックを行った値を設定
		inputForm.setTargetYearMonth(domainTargetYearMonth.getValue());
		// デフォルトのカレンダー日付を設定する(targetYearMonth + 01)
		inputForm.setShoppingDate(LocalDate.parse(domainTargetYearMonth.getValue() + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER));

		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(userId, inputForm);
	}

	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面情報取得
	 *
	 * 指定した買い物登録情報に応じた買い物登録(簡易タイプ)画面の表示情報を取得します。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @param shoppingRegistCode 更新対象の買い物登録コード
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse read(LoginUserInfo user, String targetYearMonth, String shoppingRegistCode) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",shoppingRegistCode=" + shoppingRegistCode);

		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);
		// 買い物登録コードのドメインタイプを生成
		ShoppingRegistCode domainShoppingRegistCode = ShoppingRegistCode.from(shoppingRegistCode);

		// 登録されている買い物情報を取得
		ShoppingRegist result = shoppingRegistRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(userId, domainTargetYearMonth, domainShoppingRegistCode));
		// 選択した買い物登録コードに対応するデータなしの場合、予期しないエラーとする
		if(result == null) {
			throw new MyHouseholdAccountBookRuntimeException("更新対象の買い物登録情報が存在しません。管理者に問い合わせてください。[targetYearMonth:"
					+ targetYearMonth + "][shoppingRegistCode:" + shoppingRegistCode + "]");
		}
		// 取得した買い物情報に対応する簡易タイプ買い物登録情報フォームデータを生成
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		// アクション(更新)
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 対象年月
		inputForm.setTargetYearMonth(result.getTargetYearMonth().getValue());
		// 買い物登録コード
		inputForm.setShoppingRegistCode(result.getShoppingRegistCode().getValue());
		// 店舗区分
		inputForm.setShopKubunCode(result.getShopKubunCode().getValue());
		// 店舗コード
		inputForm.setShopCode(result.getShopCode().getValue());
		// 支払方法コード
		inputForm.setPaymentMethodCode(result.getPaymentMethodCode().getValue());
		// 買い物日
		inputForm.setShoppingDate(result.getShoppingDate().getValue());
		// 備考
		inputForm.setShoppingRemarks(result.getShoppingRemarks().getValue());
		// 食料品(必須)
		inputForm.setShoppingFoodExpenses(result.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().toIntegerValue());
		// 消費税：食料品(必須)
		inputForm.setShoppingFoodTaxExpenses(result.getShoppingFoodExpenditureItem().getShoppingFoodTaxExpenses().toIntegerValue());
		// 食料品B(無駄遣い)
		inputForm.setShoppingFoodBExpenses(result.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().toIntegerValue());
		// 消費税：食料品B(無駄遣い)
		inputForm.setShoppingFoodBTaxExpenses(result.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteTaxExpenses().toIntegerValue());
		// 食料品C(お酒類)
		inputForm.setShoppingFoodCExpenses(result.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().toIntegerValue());
		// 消費税：食料品C(お酒類)
		inputForm.setShoppingFoodCTaxExpenses(result.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteTaxExpenses().toIntegerValue());
		// 外食
		inputForm.setShoppingDineOutExpenses(result.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().toIntegerValue());
		// 消費税：外食
		inputForm.setShoppingDineOutTaxExpenses(result.getShoppingDineOutExpenditureItem().getShoppingDineOutTaxExpenses().toIntegerValue());
		// 日用品
		inputForm.setShoppingConsumerGoodsExpenses(result.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().toIntegerValue());
		// 消費税：日用品
		inputForm.setShoppingConsumerGoodsTaxExpenses(result.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsTaxExpenses().toIntegerValue());
		// 衣料品(私服)
		inputForm.setShoppingClothesExpenses(result.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().toIntegerValue());
		// 消費税：衣料品(私服)
		inputForm.setShoppingClothesTaxExpenses(result.getShoppingClothesExpenditureItem().getShoppingClothesTaxExpenses().toIntegerValue());
		// 仕事
		inputForm.setShoppingWorkExpenses(result.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().toIntegerValue());
		// 消費税：仕事
		inputForm.setShoppingWorkTaxExpenses(result.getShoppingWorkExpenditureItem().getShoppingWorkTaxExpenses().toIntegerValue());
		// 住居設備
		inputForm.setShoppingHouseEquipmentExpenses(result.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().toIntegerValue());
		// 消費税：住居設備
		inputForm.setShoppingHouseEquipmentTaxExpenses(result.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentTaxExpenses().toIntegerValue());
		// クーポン
		inputForm.setShoppingCouponPrice(result.getShoppingCouponPrice().toIntegerValue());
		// 購入金額合計
		inputForm.setTotalPurchasePrice(DomainCommonUtils.convertInteger(result.getTotalPurchasePrice().getValue()));
		// 購入金額合計(disabled)
		inputForm.setTotalPurchasePriceView(DomainCommonUtils.convertInteger(result.getTotalPurchasePrice().getValue()));
		// 消費税合計
		inputForm.setTaxTotalPurchasePrice(DomainCommonUtils.convertInteger(result.getTaxTotalPurchasePrice().getValue()));
		// 消費税合計(disabled)
		inputForm.setTaxTotalPurchasePriceView(DomainCommonUtils.convertInteger(result.getTaxTotalPurchasePrice().getValue()));
		// 買い物合計金額
		inputForm.setShoppingTotalAmount(DomainCommonUtils.convertInteger(result.getShoppingTotalAmount().getValue()));
		// 買い物合計金額(disabled)
		inputForm.setShoppingTotalAmountView(DomainCommonUtils.convertInteger(result.getShoppingTotalAmount().getValue()));

		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(userId, inputForm);

	}

	/**
	 *<pre>
	 * 店舗区分変更時の画面返却データのModelAndViewを生成して返します。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param registInfoForm 買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse readChangeShopKubun(LoginUserInfo user, SimpleShoppingRegistInfoForm registInfoForm) {
		log.debug("readChangeShopKubun:userid=" + user.getUserId() + ",inputForm=" + registInfoForm);
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(UserId.from(user.getUserId()), registInfoForm);
	}

	/**
	 *<pre>
	 * バリデーションチェックエラー時の入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param registInfoForm 買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	public SimpleShoppingRegistResponse readBindingError(LoginUserInfo user, SimpleShoppingRegistInfoForm registInfoForm) {
		log.debug("readBindingError:userid=" + user.getUserId() + ",inputForm=" + registInfoForm);
		// 買い物登録(簡易タイプ)画面の表示情報を生成して返却
		return createResponse(UserId.from(user.getUserId()), registInfoForm);
	}

	/**
	 *<pre>
	 * 買い物登録方法選択画面(メニュー選択画面)にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 表示対象の対象年月
	 * @return 買い物登録方法選択画面(メニュー選択画面)リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnShoppingTopRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readReturnShoppingTopRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getReturnShoppingTopRedirectInstance(targetYearMonth);
		return response;
	}

	/**
	 *<pre>
	 * 各月の収支参照画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 表示対象の対象年月
	 * @return 各月の収支参照画面リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnInquiryMonthRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readReturnInquiryMonthRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getReturnInquiryMonthRedirectInstance(targetYearMonth);
		return response;
	}

	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面の表示情報を生成して返します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param registInfoForm  買い物情報(簡易タイプ)入力フォーム
	 * @return 買い物登録(簡易タイプ)画面の表示情報
	 *
	 */
	private SimpleShoppingRegistResponse createResponse(UserId userId, SimpleShoppingRegistInfoForm registInfoForm) {

		// コードテーブルから店舗区分情報を取得
		List<CodeAndValuePair> shopKubunCodeList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN);
		if(shopKubunCodeList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「店舗区分情報：" + MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN + "」が登録されていません。管理者に問い合わせてください");
		}
		// 店舗区分情報から店舗区分のオプションリスト情報を作成
		List<OptionItem> shopKubunOptionItemList = shopKubunCodeList.stream().map(pair ->
			OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toUnmodifiableList());

		// 選択した店舗区分に属する店舗情報を取得
		ShopInquiryList shopSearchResult = shopRepository.findById(SearchQueryUserIdAndShopKubunCode.from(userId, ShopKubunCode.from(registInfoForm.getShopKubunCode())));
		// 店舗情報ありの場合、店舗名選択肢のリストを作成(デフォルト支払方法をdata属性経由でJSに渡すため、5.6節)
		List<SimpleShoppingRegistResponse.ShopOptionItem> shopNameOptionItemList = null;
		if(!shopSearchResult.isEmpty()) {
			// 店舗情報をレスポンスに設定
			shopNameOptionItemList = shopSearchResult.getValues().stream().map(domain ->
				SimpleShoppingRegistResponse.ShopOptionItem.from(
						domain.getShopCode().getValue(),
						domain.getShopName().getValue(),
						domain.getDefaultPaymentMethodCode() == null ? null : domain.getDefaultPaymentMethodCode().getValue()))
				.collect(Collectors.toUnmodifiableList());
		}
		// 支出テーブルに該当項目の支出データが登録されていることを確認
		// レスポンスを生成
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getInstance(shopKubunOptionItemList, shopNameOptionItemList,
				paymentMethodInfoComponent.getPaymentMethodOptions(userId), registInfoForm);
		if(shopSearchResult.isEmpty()) {
			// 店舗情報が0件の場合、メッセージを設定
			response.addMessage("選択した店舗区分に属する店舗情報が0件です。店舗区分を再選択してください。");
		}

		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, TargetYearMonth.from(registInfoForm.getTargetYearMonth())),
				// 値を設定するレスポンス
				response);

		return response;
	}
}
