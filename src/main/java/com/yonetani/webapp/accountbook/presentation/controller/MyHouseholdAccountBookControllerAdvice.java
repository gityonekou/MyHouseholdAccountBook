/**
 * マイ家計簿のコントローラーで共有するコントローラーアドバイスを定義したクラスです。
 * 主に、以下を定義しています。
 * ・例外発生時のハンドリング
 * ・WebDataBinderオブジェクトのカスタマイズ(今のところ使う予定ないのでコメントアウト中)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の共通利用するコントローラーアドバイスを定義したクラスです。
 * 主に、以下を定義しています。
 * ・例外発生時のハンドリング
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@ControllerAdvice
@Log4j2
public class MyHouseholdAccountBookControllerAdvice {
	
    /**
     *<pre>
     * @￰RequestParamや@￰PathVariableなどで受け取った値をオブジェクトにバインド（設定）する際に行う処理を定義できるアノテーション。
     * 型変換、フォーマッティング、バリデーションなどをカスタマイズすることができる。
     * (空文字の文字列項目をnullに変換する、文字列の空白を取り除くなど)
     *</pre>
     * @param dataBinder
     *
     */
//    @InitBinder
//    public void initBinder(WebDataBinder dataBinder) {
//    	// バインドするデータに対して両端の空白を削除する
//        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//    }
	
    /**
     *<pre>
     * MVCにおいて共通のデータをModelに設定したい場合、以下メソッドで設定する。
     * Handlerメソッド実行前に呼び出され、メソッドでなにか値を返却した場合は、返却したオブジェクトがModelに格納される。
     * 
     *</pre>
     * @param model
     *
     */
//    @ModelAttribute
//    public void setTimestamp(Model model) {
//        model.addAttribute("timestamp", System.currentTimeMillis());
//    }
	
	/**
	 *<pre>
	 * マイ家計簿の業務例外が発生した時のハンドリングです。
	 * Httpステータスコードは500を返却します。
	 *</pre>
	 * @param ex 発生した例外
	 * @return エラーページ
	 *
	 */
	@ExceptionHandler({ MyHouseholdAccountBookRuntimeException.class , MyHouseholdAccountBookException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleMyHouseholdAccountBookException(Exception ex) {
		log.error("[handle]:handleMyHouseholdAccountBookException Exception [message=" + ex.getLocalizedMessage() + "]");
		ModelAndView model = createErrorMessage(ex);
	    model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);	
		return model;
	}
	
	/**
	 *<pre>
	 * マイ家計簿のDBアクセス例外が発生した時のハンドリングです。
	 * Httpステータスコードは500を返却します。
	 *</pre>
	 * @param ex 発生した例外
	 * @return エラーページ
	 *
	 */
	@ExceptionHandler({ DataAccessException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleDataAccessException(DataAccessException ex) {
		log.error("[handle]:handleDataAccessException Exception [message=" + ex.getLocalizedMessage() + "]");
		ModelAndView model = createErrorMessage(ex);
	    model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);	
		return model;
	}
	
	/**
	 *<pre>
	 * マイ家計簿の汎用例外が発生した時のハンドリングです。
	 * Httpステータスコードは500を返却します。
	 *</pre>
	 * @param ex 発生した例外
	 * @return エラーページ
	 *
	 */
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Exception ex) {
		log.error("[handle]:handleException Exception [message=" + ex.getLocalizedMessage() + "]");
		ModelAndView model = createErrorMessage(ex);
	    model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);	
		return model;
	}
	
	/**
	 *<pre>
	 * ModelAndViewにエラーメッセージと発生日時の値を設定します。
	 *</pre>
	 * @param ex 発生した例外
	 * @return エラーページのModelAndView
	 *
	 */
	private ModelAndView createErrorMessage(Exception ex) {
		log.error(ex.getLocalizedMessage(), ex);
		
		// エラーメッセージとエラー発生日時をModelAndViewに設定し、エラーページ情報を生成する
		ModelAndView model = new ModelAndView();
		model.addObject("errorMessage", ex.getLocalizedMessage());
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss SSS");
		model.addObject("errorTimestamp", LocalDateTime.now().format(dateTimeFormat));
		model.setViewName("error");
		return model;
	}
}
