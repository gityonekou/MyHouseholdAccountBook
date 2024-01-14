/**
 * アップロードするファイルのバリデーションチェックを行います。
 * 以下のチェックを行います
 * ・ファイルの指定必須
 * ・ファイルが空(0バイト)でない
 * ・ファイルの種類が？？である（未実装）
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/31 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * アップロードするファイルのバリデーションチェックを行います。
 * 以下のチェックを行います
 * ・ファイルの指定必須
 * ・ファイルが空(0バイト)でない
 * ・ファイルの種類が？？である（未実装）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Log4j2
public class FileRequiredValidator implements ConstraintValidator<FileRequired, MultipartFile>{

	private String extension;
	private String defaultMessage;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(FileRequired constraintAnnotation) {
		extension = constraintAnnotation.extension();
		defaultMessage = constraintAnnotation.message();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		// 以下ページを参考に作成
		// https://macchinetta.github.io/server-guideline-thymeleaf/current/ja/ArchitectureInDetail/WebApplicationDetail/Validation.html
		if(value != null) {
			log.debug("isValid: extension=" + extension + ",getOriginalFilename=" + value.getOriginalFilename());
		} else {
			log.debug("isValid: extension=" + extension + ",value=null");
		}
		// ファイルの指定あり、かつ、ファイルサイズが0出ない場合
		if(value != null && !value.getOriginalFilename().isEmpty()) {
			// ファイルの拡張子が指定のもの以外の場合
			String[] splitNames = value.getOriginalFilename().split("\\.");
			if(splitNames[splitNames.length - 1].equals(extension)) {
				return true;
			} else {
				// エラーメッセージを設定
				setErrorMessage(context, "指定ファイルの拡張子が不正です。正しい拡張子[" + extension + "]のファイルを選択してください。");
				return false;
			}
			
		// 上記以外の場合、入力チェックエラーとする
		} else {
			// エラーメッセージを設定
			setErrorMessage(context, "ファイルが指定されていないか、ファイルサイズが空です。");
			return false;
		}
		
	}
	
	/**
	 *<pre>
	 * 指定のエラーメッセージをバリデーションのエラーチェック結果に追加します
	 *</pre>
	 * @param context
	 * @param addMessage
	 *
	 */
	private void setErrorMessage(ConstraintValidatorContext context, String addMessage) {
		/* エラーメッセージを設定 */
		// デフォルトのConstraintViolationオブジェクトの生成を無効にする
		context.disableDefaultConstraintViolation();
		// 独自ConstraintViolationオブジェクトを生成する
		// buildConstraintViolationWithTemplateで出力するメッセージを定義
		context.buildConstraintViolationWithTemplate(defaultMessage + "[" + addMessage + "]")
		// エラーメッセージを出力したいフィールド名を指定(別フィールドに指定したエラーメッセージを出力する場合)
		//.addPropertyNode(confirmField)
		// メッセージを追加
		.addConstraintViolation();
	}

}
