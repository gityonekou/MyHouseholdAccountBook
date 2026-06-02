/**
 * マイ家計簿 固定費管理で使用するJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
// 対象外のボタンの場合(値がnullの場合)は読み飛ばします。
document.addEventListener('DOMContentLoaded', function() {
	
	// 指定した固定費を削除時のボタン押下時処理
	let deleteBtnItem = document.getElementById('fixed-cost-action-delete-btn');
	if(deleteBtnItem != null) {
		deleteBtnItem.addEventListener('click', function() {
			if(!this.disabled) {
				if(window.confirm('指定の固定費を削除しても宜しいですか？')) {
					this.disabled = true;
					document.forms['FixedCostDeleteSelectInfo'].submit();
				}
			}
		}, false);
	}
	
	// 固定費の追加・更新画面のキャンセルボタン押下時の処理
	// (新規登録時、ブラウザデフォルトの必須チェックで画面遷移できないためjavascriptで送信を実行する)
	let cancelBtnItem = document.getElementById('fixed-cost-update-action-cancel-btn');
	if(cancelBtnItem != null) {
		cancelBtnItem.addEventListener('click', function() {
			if(!this.disabled) {
				this.disabled = true;
				// 押下ボタンの名前が消去されるので、actionCancelのhidden項目を新規作成
				const hiddenInput = document.createElement('input');
				hiddenInput.type = 'hidden';
				hiddenInput.name = 'actionCancel';
				hiddenInput.value = '';
				
				// フォームにhidden項目を追加してから送信
				document.forms['FixedCostInfoUpdate'].appendChild(hiddenInput);
				document.forms['FixedCostInfoUpdate'].submit();
			}
		}, false);
	}
	
}, false);

