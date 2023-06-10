/**
 * マイ家計簿アプリのセキュリティ関連のDBアクセス設定クラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * マイ家計簿アプリのセキュリティ関連のDBアクセス設定クラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Configuration
@RequiredArgsConstructor
public class MyHouseholdAccountBookSecurityDatabaseConfig {
	
	// DBアクセス用データソース
	private final DataSource datasource;
	
	/**
	 * データベースを用いた認証を行うためのUserDetailsManagerをAuthenticationManagerに登録します。
	 * 
	 * @return
	 */
	@Bean
	public UserDetailsManager getJDBCUserDetailsManager() {
		return new JdbcUserDetailsManager(datasource);
	}
	
	/**
	 * パスワードのハッシュ化を行うアルゴリズムを登録します。
	 * 
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// Bcrypt,noop ,sha256 など、他のエンコード方式も包括して対応できるようにする
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
