/**
 * マイ家計簿 固定費管理で使用するJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
// 対象外のボタンの場合(値がnullの場合)は読み飛ばします。
document.addEventListener('DOMContentLoaded', function() {
	
	// 指定した固定費を削除時のボタン押下時処理
	let btnItem = document.getElementById('fixed-cost-action-delete-btn');
	if(btnItem != null) {
		btnItem.addEventListener('click', function() {
			if(!this.disabled) {
				if(window.confirm('指定の固定費を削除しても宜しいですか？')) {
					this.disabled = true;
					document.forms['FixedCostDeleteSelectInfo'].submit();
				}
			}
		}, false);
	}
}, false);

