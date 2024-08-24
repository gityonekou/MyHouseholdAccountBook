/**
 * 情報管理(イベント)画面の登録・更新イベント情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/17 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(イベント)画面の登録・更新イベント情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class EventInfoForm {
	// アクション
	private String action;
	// イベントコード
	private String eventCode;
	// 支出項目コード(イベントが属する支出項目コード)
	@NotBlank
	private String sisyutuItemCode;
	// 支出項目名(＞で区切った値)
	private String sisyutuItemName;
	
	// イベント名
	@NotBlank
	@Size(max = 100)
	private String eventName;
	// イベント内容詳細(任意入力項目)
	@Size(max = 300)
	private String eventDetailContext;
	// 開始日
	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate eventStartDate;
	// 終了日
	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate eventEndDate;
	
	/**
	 * 相関チェック(開始日 <= 終了日であること)
	 * 
	 * @return 開始日<=終了日の場合:true、開始日>終了日の場合:false
	 */
	@AssertTrue(message = "開始日 <= 終了日にしてください。")
	public boolean isCheckStartEndDate() {
		// 開始日・終了日を比較
		if(eventStartDate == null || eventEndDate == null) {
			return true;
		}
		if(eventStartDate.compareTo(eventEndDate) > 0) {
			return false;
		} else {
			return true;
		}
		
	}
}
