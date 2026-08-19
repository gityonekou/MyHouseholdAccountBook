/**
 * 情報管理(支払方法)画面のJSです。
 * 支払方法種別の選択に応じて、銀行口座欄・集計開始日欄の表示/非表示を切り替えます。
 *
 * JavaのPaymentMethodKubun enumのメソッドを直接呼べないため、種別コードをこのファイルに直書きせず、
 * サーバー側で述語(requiresAccount()/requiresClosingDay())を評価した結果をHTMLのoption要素のdata属性に
 * 埋め込み、このJSはその属性値のみを参照する(突合レビュー指摘E)。
 */
function toggleAccountAndClosingDayAreas() {
	var select = document.getElementById('paymentMethodKubun');
	var selectedOption = select.options[select.selectedIndex];
	var requiresAccount = selectedOption.dataset.requiresAccount === 'true';
	var requiresClosingDay = selectedOption.dataset.requiresClosingDay === 'true';
	document.getElementById('bankAccountArea').style.display = requiresAccount ? 'block' : 'none';
	document.getElementById('closingDayArea').style.display = requiresClosingDay ? 'block' : 'none';
}

document.addEventListener('DOMContentLoaded', function() {
	var select = document.getElementById('paymentMethodKubun');
	if(select) {
		toggleAccountAndClosingDayAreas();
		select.addEventListener('change', toggleAccountAndClosingDayAreas);
	}
});
