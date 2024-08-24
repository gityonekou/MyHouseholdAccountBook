/**
 * マイ家計簿 イベント管理で使用するJavaScriptです。
 *   
 */
// ドキュメントロード時に各ボタンのイベントリスナーを登録します。
// 対象外のボタンの場合(値がnullの場合)は読み飛ばします。
document.addEventListener('DOMContentLoaded', function() {
	
	// 指定したイベント情報を削除時のボタン押下時処理
	let btnItem = document.getElementById('event-info-action-delete-btn');
	if(btnItem != null) {
		btnItem.addEventListener('click', function() {
			if(!this.disabled) {
				if(window.confirm('指定のイベントを終了ステータスに変更しても宜しいですか？')) {
					this.disabled = true;
					document.forms['EventInfoDelete'].submit();
				}
			}
		}, false);
	}
}, false);

