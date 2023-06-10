/**
 * マイ家計簿アプリのセキュリティ設定クラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *<pre>
 * マイ家計簿アプリのセキュリティ設定クラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyHouseholdAccountBookSecurityConfig {
	
	/**
	 *<pre>
	 * 家計簿アプリにセキュリティ情報を設定します。
	 *</pre>
	 * @param http Httpセキュリティ
	 * @return セキュリティ設定情報
	 * @throws Exception 例外発生時
	 *
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		// csrf除外対象
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
		// 同一ドメインでiframeを許可する設定
		http.headers(headers -> headers.frameOptions().sameOrigin());
		
		// アクセス権限に関する設定
		http.authorizeHttpRequests(authz -> authz
			// cssなど、静的なリソースはアクセス制限をかけない(permitAll)
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
			// /indexはアクセス制限をかけない
			.requestMatchers("/").permitAll()
			// h2コンソールへのアクセス権限
			.requestMatchers("/h2-console/**").hasRole("ADMIN")
			// 管理者ページへのアクセス権限
			.requestMatchers("/myhacbook/admin/**").hasRole("ADMIN")
			// 家計簿ページのアクセス権限
			.requestMatchers("/myhacbook/**").hasRole("USER")
			// その他すべてのURLに対して認証を要求
			.anyRequest().authenticated()
		);
		
		// ログインに関する設定
		http.formLogin(login -> login
				// ログイン認証処理(POST)のパス
				.loginProcessingUrl("/login/")
				// ログイン画面へのパスはアクセス制限をかけない(permitAll)
				.loginPage("/login/").permitAll()
				// ログインに失敗した場合の遷移先
				.failureUrl("/login/")
				// ログイン成功時
				.defaultSuccessUrl("/myhacbook/topmenu/", true)
				// ユーザ名とパスワードのネーム指定
				.usernameParameter("username")
				.passwordParameter("password")
		);
		
		// ログアウトに関する設定
		http.logout(logout -> logout
				// ログアウト処理(POST)のパス
				.logoutUrl("/logout/")
				// ログアウト完了時の遷移先(indexページ)
				.logoutSuccessUrl("/")
				// ログアウト完了時、指定のクッキーを削除
				// JSESSIONIDはログイン時にCSRFトークンが保存されているクッキーでもあります。
				// 詳しくは、ファイルヘッダ記載のCSRF解説のページを参照
				.deleteCookies("JSESSIONID")
		);
		
		// ビルド結果を返す
		return http.build();
	}

}
