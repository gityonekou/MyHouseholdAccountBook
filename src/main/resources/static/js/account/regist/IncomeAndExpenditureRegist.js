/**
 * マイ家計簿 収支登録画面で使用するJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
document.addEventListener('DOMContentLoaded', function() {
	
	let btnItems = [
			// 新規で追加ボタン
			{id: 'check', formname : 'CheckProcess'},
			// キャンセルボタン
			{id: 'cancel', formname : 'Cancel'}
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

