/**
 * マイ家計簿用SpringBootプロジェクトのmainです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *<pre>
 * マイ家計簿用SpringBootプロジェクトのmainです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@SpringBootApplication
public class MyHouseholdAccountBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyHouseholdAccountBookApplication.class, args);
	}

}
