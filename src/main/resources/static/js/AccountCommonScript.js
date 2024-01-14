/**
 * マイ家計簿共通のJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
// 対象外のボタンの場合(値がnullの場合)は読み飛ばします。
document.addEventListener('DOMContentLoaded', function() {
	
	let btnItems = [
			// 表示対象の月切り替えボタン(前月)
			{id: 'account-month-before-btn', formname : 'AccountMonthBefore'},
			// 表示対象の月切り替えボタン(次月)
			{id: 'account-month-next-btn', formname : 'AccountMonthNext'},
			// 表示対象の年間収支(マージ)切り替えボタン(前年)
			{id: 'account-year-mage-before-btn', formname : 'AccountYearMageBefore'},
			// 表示対象の年間収支(マージ)切り替えボタン(次年)
			{id: 'account-year-mage-next-btn', formname : 'AccountYearMageNext'},
			// 表示対象の年間収支(明細)切り替えボタン(前年)
			{id: 'account-year-meisai-before-btn', formname : 'AccountYearMeisaiBefore'},
			// 表示対象の年間収支(明細)切り替えボタン(次年)
			{id: 'account-year-meisai-next-btn', formname : 'AccountYearMeisaiNext'},
			// 年間収支(マージ)ボタン
			{id: 'account-year-mage-btn', formname : 'AccountYearMage'},
			// 年間収支(明細)ボタン
			{id: 'account-year-meisai-btn', formname : 'AccountYearMeisai'},
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

