/**
 * ベース情報管理画面のベース情報ファイル登録フォームが格納されたformデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/31 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.adminmenu;

import org.springframework.web.multipart.MultipartFile;

import com.yonetani.webapp.accountbook.common.validation.FileRequired;

import lombok.Data;

/**
 *<pre>
 * ベース情報管理画面のベース情報ファイル登録フォームが格納されたformデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class AdminMenuUploadBaseInfoFileForm {
	// ベース情報ファイル
	@FileRequired(extension = "basedata")
	private MultipartFile baseInfoFile;
}
