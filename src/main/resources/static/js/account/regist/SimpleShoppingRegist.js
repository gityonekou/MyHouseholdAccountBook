/**
 * マイ家計簿 買い物登録（簡易タイプ）画面で使用するJavaScriptです。
 *   
 */
// 買い物日入力欄にDateピッカーを設定
$(function() {
	$.datepicker.setDefaults($.datepicker.regional["ja"]);
	$("#shoppingDate").datepicker();
});

// 店舗名の値変更時、選択した店舗のデフォルト支払方法を支払方法欄に自動設定する(5.6節)。
// 新規登録時(action=add)のみ自動設定し、更新時(action=update)は登録済みの実際の支払方法を優先し上書きしない。
// ユーザーが自動設定後に手動で支払方法を変更することは妨げない(あくまで初期値のヒント)。
// デフォルト支払方法が未設定の店舗に切り替えた場合は、支払方法欄を未選択状態に戻す。
function applyDefaultPaymentMethodFromSelectedShop() {
	if ($('#SimpleShoppingRegistInfo [name=action]').val() !== 'add') {
		return;
	}
	let defaultPaymentMethodCode = $('#shopCode').find('option:selected').attr('data-default-payment-method');
	$('#SimpleShoppingRegistInfo [name=paymentMethodCode]').val(defaultPaymentMethodCode ? defaultPaymentMethodCode : '');
}
$('#shopCode').change(function() {
	applyDefaultPaymentMethodFromSelectedShop();
});

// 初期表示時、店舗名選択欄は先頭の店舗がブラウザにより暗黙的に選択されるため、
// 支払方法が未選択の場合に限り、先頭店舗のデフォルト支払方法を反映する(新規登録時のみ)。
// (支払方法が既に選択されている場合はバリデーションエラー再表示等でユーザーの選択値を保持するため上書きしない)
$(function() {
	if ($('#SimpleShoppingRegistInfo [name=action]').val() === 'add'
			&& !$('#SimpleShoppingRegistInfo [name=paymentMethodCode]').val()) {
		applyDefaultPaymentMethodFromSelectedShop();
	}
});

// 店舗区分の値変更時
$('#shopKubunCode').change(function() {
	// 買い物登録フォームの送信データをコピー
	// (Spring Security のcsrfの項目は重複となるが、問題なく動いているように見える。（問題ないなら、このまま使う)
	let clonedElements = $('#SimpleShoppingRegistInfo').children().clone(false);
    // コピーした各入力項目のフォームデータを店舗区分変更時のフォームデータに追加
    $('#shopKubunChangeForm').append(clonedElements);
    // 店舗区分の選択値を設定
    $('#shopKubunChangeForm [name=shopKubunCode]').val($('#SimpleShoppingRegistInfo [name=shopKubunCode]').val());
    // 送信
	$('#shopKubunChangeForm').submit();		
});

// 登録ボタン押下時
$('#execRegist').click(function() {
	if(confirm('買い物情報を登録します。宜しいですか？')) {
		$('#execRegist').prop('disabled', 'true');
		$('#SimpleShoppingRegistInfo').submit();
	}
});

// テキスト入力エリアからフォーカスが外れた場合、各種金額の合計値を設定
$(function(){
	$('input').blur(function() {
	
		// 購入金額入力欄からフォーカスが外れた場合、対応する消費税の値を更新
		let targetId = $(this).attr('id');
		// 値を変更した入力欄が食料品(必須)の場合
		if(targetId == "shoppingFoodExpenses") {
			// 食料品(必須)の消費税の値を設定(8%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingFoodTaxExpenses]').val(reducedSalesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingFoodExpenses]')));
		}
		// 値を変更した入力欄が食料品B(無駄遣い)の場合
		if(targetId == "shoppingFoodBExpenses") {
			// 食料品B(無駄遣い)の消費税の値を設定(8%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingFoodBTaxExpenses]').val(reducedSalesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingFoodBExpenses]')));
		}
		// 値を変更した入力欄が食料品C(お酒類)の場合
		if(targetId == "shoppingFoodCExpenses") {
			// 食料品C(お酒類)の消費税の値を設定(8%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingFoodCTaxExpenses]').val(reducedSalesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingFoodCExpenses]')));
		}
		// 値を変更した入力欄が外食の場合
		if(targetId == "shoppingDineOutExpenses") {
			// 外食の消費税の値を設定(10%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingDineOutTaxExpenses]').val(salesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingDineOutExpenses]')));
		}
		// 値を変更した入力欄が日用品の場合
		if(targetId == "shoppingConsumerGoodsExpenses") {
			// 日用品の消費税の値を設定(10%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingConsumerGoodsTaxExpenses]').val(salesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingConsumerGoodsExpenses]')));
		}
		// 値を変更した入力欄が衣料品(私服)の場合
		if(targetId == "shoppingClothesExpenses") {
			// 衣料品(私服)の消費税の値を設定(10%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingClothesTaxExpenses]').val(salesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingClothesExpenses]')));
		}
		// 値を変更した入力欄が仕事の場合
		if(targetId == "shoppingWorkExpenses") {
			// 仕事の消費税の値を設定(10%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingWorkTaxExpenses]').val(salesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingWorkExpenses]')));
		}
		// 値を変更した入力欄が住居設備の場合
		if(targetId == "shoppingHouseEquipmentExpenses") {
			// 住居設備の消費税の値を設定(10%で計算)
			$('#SimpleShoppingRegistInfo [name=shoppingHouseEquipmentTaxExpenses]').val(salesTaxValue($('#SimpleShoppingRegistInfo [name=shoppingHouseEquipmentExpenses]')));
		}
			
		// 購入金額合計を加算していく
		let totalAmountValue = "";
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodBExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodCExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingDineOutExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingConsumerGoodsExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingClothesExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingWorkExpenses]'));
		totalAmountValue = addValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingHouseEquipmentExpenses]'));

		// 消費税合計を加算していく
		let taxTotalAmountValue = "";
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodBTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingFoodCTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingDineOutTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingConsumerGoodsTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingClothesTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingWorkTaxExpenses]'));
		taxTotalAmountValue = addValue(taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingHouseEquipmentTaxExpenses]'));
		
		// 合計値設定
		$('#SimpleShoppingRegistInfo [name=totalPurchasePrice]').val(totalAmountValue);
		$('#SimpleShoppingRegistInfo [name=totalPurchasePriceView]').val(totalAmountValue);
		$('#SimpleShoppingRegistInfo [name=taxTotalPurchasePrice]').val(taxTotalAmountValue);
		$('#SimpleShoppingRegistInfo [name=taxTotalPurchasePriceView]').val(taxTotalAmountValue);
		if(totalAmountValue === "") {
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmount]').val("");
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmountView]').val("");
		} else if (taxTotalAmountValue === "") {
			totalAmountValue = subtractValue(totalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingCouponPrice]'));
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmount]').val(totalAmountValue);
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmountView]').val(totalAmountValue);
		} else {
			totalAmountValue = subtractValue(totalAmountValue + taxTotalAmountValue, $('#SimpleShoppingRegistInfo [name=shoppingCouponPrice]'));
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmount]').val(totalAmountValue);
			$('#SimpleShoppingRegistInfo [name=shoppingTotalAmountView]').val(totalAmountValue);
		}
	});
	
	// 指定のエレメントから空白文字などをとリーム、数値の大文字を数値の小文字に変換した値を返します。
	getElementValue = function(element){
		// 入力値に半角スペース、全角スペースが含まれる場合、削除(空文字列に変換)
		let inputStr = element.val().replace(/\s|　/g,'');
		// 入力値に全角数値が含まれる場合、対応する半角数値に変換
		let workStr = inputStr.replace(/[０-９]/g, function(s){return String.fromCharCode(s.charCodeAt(0)-0xFEE0)});
		// 入力値の値を変換後の値に設定
		element.val(workStr);
		return workStr;
	}
	
	// 入力エリアの値を数値変換し、amountValueで渡された値に加算した結果を返します。
	addValue = function(amountValue, element){
		// 入力エリアの値を取得
		let elementValue = getElementValue(element);
		
		// 引数で渡されたamountValueの値に入力値の数値を加算の場合、渡されたamountValueの値をそのまま返却
		// (空文字列、全角数値変換後の)入力値が空文字列
		if(elementValue === "") {
			return amountValue;
		}
		// 入力値を数値に変換
		let numValue = Number(elementValue);
		// 数値変換に失敗した場合は渡されたamountValueの値をそのまま返却
		if(!Number.isFinite(Number(numValue))) {
			return amountValue;
		// amountValueの値が空文字列(まだ数値の加算なし)の場合、変換した数値の値を返却
		} else if(amountValue === "") {
			return numValue;
		// 加算した値を返却
		} else {
			return amountValue + numValue;
		}
	}
	
	// 入力エリアの値を数値変換し、amountValueで渡された値を減算した結果を返します。
	subtractValue = function(amountValue, element){
		// 入力エリアの値を取得
		let elementValue = getElementValue(element);
		
		// 引数で渡されたamountValueの値に入力値の数値を加算の場合、渡されたamountValueの値をそのまま返却
		// (空文字列、全角数値変換後の)入力値が空文字列
		if(elementValue === "") {
			return amountValue;
		}
		// 入力値を数値に変換
		let numValue = Number(elementValue);
		// 数値変換に失敗した場合は渡されたamountValueの値をそのまま返却
		if(!Number.isFinite(Number(numValue))) {
			return amountValue;
		// amountValueの値が空文字列(まだ数値の加算なし)の場合、変換した数値の値を返却
		} else if(amountValue === "") {
			return numValue;
		// 減算した値を返却
		} else {
			return amountValue - numValue;
		}
	}
	
	// 指定のエレメントの値に対応する軽減消費税(8%)の値を返します。
	reducedSalesTaxValue = function(element){
		let elementValue = getElementValue(element);
		if(elementValue === "") {
			return "";
		}
		let numValue = Number(elementValue);
		if(!Number.isFinite(Number(numValue))) {
			return "";
		}
		return Math.floor(numValue * 0.08);
	}
	
	// 指定のエレメントの値に対応する消費税(10%)の値を返します。
	salesTaxValue = function(element){
		let elementValue = getElementValue(element);
		if(elementValue === "") {
			return "";
		}
		let numValue = Number(elementValue);
		if(!Number.isFinite(Number(numValue))) {
			return "";
		}
		return Math.floor(numValue * 0.1);
	}
});

