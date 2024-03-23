/**
 * アップロードするファイルのバリデーションチェック用アノテーションです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/31 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 *<pre>
 * アップロードするファイルのバリデーションチェック用アノテーションです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileRequiredValidator.class)
@Documented	
public @interface FileRequired {
	String message() default "{com.yonetani.webapp.accountbook.common.validation.FileRequired.message}";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
	
	// field name
	String extension();
	
	@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface List {
		FileRequired[] value();
	}
}
