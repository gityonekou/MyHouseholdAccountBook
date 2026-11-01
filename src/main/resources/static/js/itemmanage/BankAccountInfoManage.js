/**
 * 情報管理(銀行口座)画面のJSです。
 * 「無効化／有効化」ボタン押下時に確認ダイアログを表示し、OK時に有効/無効切替用フォームを送信します。
 */
document.addEventListener('DOMContentLoaded', function() {
	var btnItem = document.getElementById('bank-account-info-action-toggle-btn');
	if(btnItem != null) {
		btnItem.addEventListener('click', function() {
			if(!this.disabled) {
				if(window.confirm('選択中の銀行口座の有効/無効状態を切り替えても宜しいですか？')) {
					this.disabled = true;
					document.forms['BankAccountInfoToggleEnable'].submit();
				}
			}
		}, false);
	}
}, false);
