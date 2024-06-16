/**
 * マイ家計簿共通のJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
// 対象外のボタンの場合(値がnullの場合)は読み飛ばします。
document.addEventListener('DOMContentLoaded', function() {
	
	let btnItems = [
			// 買い物登録ボタン
			{id: 'shopping-add-btn', formname : 'ShoppinAdd'},
			// 各月の収支表示ボタン
			{id: 'ccount-month-view-btn', formname : 'AccountMonthView'},
			// 各月の収支詳細ボタン
			{id: 'account-month-detai-btn', formname : 'AccountMonthDetail'},
			// 各年の収支詳細ボタン
			{id: 'account-year-detai-btn', formname : 'AccountYearDetail'}
		];
	
	for(let item of btnItems) {
		let btnItem = document.getElementById(item.id);
		if(btnItem != null) {
			btnItem.addEventListener('click', function() {
				if(!this.disabled) {
					this.disabled = true;
					document.forms[item.formname].submit();
				}
			}, false);
		}
	}
}, false);

