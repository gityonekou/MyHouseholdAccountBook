/**
 * PaymentMethodTableRepository(支払方法テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   PaymentMethod.from()型安全性対応(06-§8)に伴うDTO→VO変換の呼び出し元への移動
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.paymentmethod;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ClosingDay;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodMemo;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.EnableUpdateFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.paymentmethod.PaymentMethodReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.paymentmethod.PaymentMethodTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * PaymentMethodTableRepository(支払方法テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Repository
@RequiredArgsConstructor
public class PaymentMethodTableDataSource implements PaymentMethodTableRepository {

	// マッパー
	private final PaymentMethodTableMapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(PaymentMethod data) {
		// 支払方法情報を支払方法テーブルに出力
		return mapper.insert(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(PaymentMethod data) {
		// 支払方法テーブル:PAYMENT_METHOD_TABLEを更新
		return mapper.update(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateSort(PaymentMethod data) {
		// 支払方法テーブル:PAYMENT_METHOD_TABLEの表示順の値を更新
		return mapper.updateSort(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findByUserId(SearchQueryUserId userId) {
		// 検索結果を取得(全件、システム予約値を含む)
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSort search) {
		// 検索結果を取得
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByIdAndPaymentMethodSort(
				UserIdAndPaymentMethodSortSearchQueryDto.from(search));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB search) {
		// 検索結果を取得
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByIdAndPaymentMethodSortBetween(
				UserIdAndPaymentMethodSortBetweenABSearchQueryDto.from(search));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethod findById(SearchQueryUserIdAndPaymentMethodCode search) {
		// 検索結果を取得
		PaymentMethodReadWriteDto result = mapper.findByIdAndPaymentMethodCode(UserIdAndPaymentMethodCodeSearchQueryDto.from(search));
		if(result == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createPaymentMethod(result);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findSelectableByUserId(SearchQueryUserId userId) {
		// 検索結果を取得(有効な支払方法のみ)
		List<PaymentMethodReadWriteDto> searchResult = mapper.findSelectableByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findEnabledByUserId(SearchQueryUserId userId) {
		// 検索結果を取得(有効な支払方法のみ)
		List<PaymentMethodReadWriteDto> searchResult = mapper.findEnabledByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByIdAndLessThanReserved(SearchQueryUserId userId) {
		// ユーザIDで検索し、システム予約帯(990～999)を除いた登録件数を返す
		return mapper.countByIdAndLessThanReserved(UserIdSearchQueryDto.from(userId));
	}

	/**
	 *<pre>
	 * 検索結果のDTOリストをドメインモデルのリスト情報に変換して返します。
	 *</pre>
	 * @param searchResult 検索結果のDTOリスト
	 * @return 支払方法テーブル情報(リスト情報)を表すドメインモデル
	 *
	 */
	private PaymentMethodInquiryList toInquiryList(List<PaymentMethodReadWriteDto> searchResult) {
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return PaymentMethodInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return PaymentMethodInquiryList.from(searchResult.stream().map(dto -> createPaymentMethod(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}

	/**
	 *<pre>
	 * 引数で指定した支払方法テーブル:PAYMENT_METHOD_TABLE読込・出力情報から支払方法テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 支払方法テーブル:PAYMENT_METHOD_TABLE読込・出力情報
	 * @return 支払方法テーブル情報ドメインモデル
	 *
	 */
	private PaymentMethod createPaymentMethod(PaymentMethodReadWriteDto dto) {
		return PaymentMethod.from(
				// ユーザID
				UserId.from(dto.getUserId()),
				// 支払方法コード
				PaymentMethodCode.from(dto.getPaymentMethodCode()),
				// 支払方法名
				PaymentMethodName.from(dto.getPaymentMethodName()),
				// 支払方法メモ
				PaymentMethodMemo.from(dto.getPaymentMethodMemo()),
				// 支払方法種別
				PaymentMethodKubun.from(dto.getPaymentMethodKubun()),
				// 銀行口座コード(未設定の場合はnull)
				StringUtils.hasLength(dto.getBankAccountCode()) ? BankAccountCode.from(dto.getBankAccountCode()) : null,
				// 集計開始日
				ClosingDay.from(dto.getClosingDay()),
				// 表示順
				PaymentMethodSort.from(dto.getPaymentMethodSort()),
				// 有効/無効フラグ
				EnableFlg.from(dto.isEnableFlg()),
				// 更新可否フラグ
				EnableUpdateFlg.from(dto.isEnableUpdateFlg()));
	}

	/**
	 *<pre>
	 * 引数で指定した支払方法テーブル情報ドメインモデルからDTOを生成して返します。
	 *</pre>
	 * @param data 支払方法テーブル情報ドメインモデル
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE読込・出力情報
	 *
	 */
	private PaymentMethodReadWriteDto createPaymentMethodReadWriteDto(PaymentMethod data) {
		return PaymentMethodReadWriteDto.from(
				// ユーザID
				data.getUserId().getValue(),
				// 支払方法コード
				data.getPaymentMethodCode().getValue(),
				// 支払方法名
				data.getPaymentMethodName().getValue(),
				// 支払方法メモ
				data.getPaymentMethodMemo().getValue(),
				// 支払方法種別
				data.getPaymentMethodKubun().getValue(),
				// 銀行口座コード(未設定の場合はnull)
				data.getBankAccountCode() == null ? null : data.getBankAccountCode().getValue(),
				// 集計開始日
				data.getClosingDay().getValue(),
				// 表示順
				data.getPaymentMethodSort().getValue(),
				// 有効/無効フラグ
				data.getEnableFlg().getValue(),
				// 更新可否フラグ
				data.getEnableUpdateFlg().getValue());
	}
}
