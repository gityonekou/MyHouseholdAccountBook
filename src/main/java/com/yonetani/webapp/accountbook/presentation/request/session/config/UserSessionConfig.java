/**
 * ユーザ情報をセッションから取得するためのクラス「UserSession」をBean登録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.session.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;

/**
 *<pre>
 * ユーザ情報をセッションから取得するためのクラス「UserSession」をBean登録します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Configuration
public class UserSessionConfig {

    /**
     *<pre>
     * 「UserSession」をBean登録します。
     *</pre>
     * @return UserSession
     *
     */
    @Bean
    UserSession getUserSession() {
        return new UserSession();
    }
}
