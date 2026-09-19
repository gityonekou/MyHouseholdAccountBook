/**
 * PaymentMethodTableRepository(支払方法テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.paymentmethod;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
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
		return mapper.insert(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(PaymentMethod data) {
		return mapper.update(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateSort(PaymentMethod data) {
		return mapper.updateSort(createPaymentMethodReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findByUserId(SearchQueryUserId userId) {
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSort search) {
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByIdAndPaymentMethodSort(
				UserIdAndPaymentMethodSortSearchQueryDto.from(search));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB search) {
		List<PaymentMethodReadWriteDto> searchResult = mapper.findByIdAndPaymentMethodSortBetween(
				UserIdAndPaymentMethodSortBetweenABSearchQueryDto.from(search));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethod findById(SearchQueryUserIdAndPaymentMethodCode search) {
		PaymentMethodReadWriteDto result = mapper.findByIdAndPaymentMethodCode(UserIdAndPaymentMethodCodeSearchQueryDto.from(search));
		if(result == null) {
			return null;
		} else {
			return createPaymentMethod(result);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findSelectableByUserId(SearchQueryUserId userId) {
		List<PaymentMethodReadWriteDto> searchResult = mapper.findSelectableByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PaymentMethodInquiryList findEnabledByUserId(SearchQueryUserId userId) {
		List<PaymentMethodReadWriteDto> searchResult = mapper.findEnabledByUserId(UserIdSearchQueryDto.from(userId));
		return toInquiryList(searchResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByIdAndLessThanReserved(SearchQueryUserId userId) {
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
			return PaymentMethodInquiryList.from(null);
		} else {
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
				dto.getUserId(),
				dto.getPaymentMethodCode(),
				dto.getPaymentMethodName(),
				dto.getPaymentMethodMemo(),
				dto.getPaymentMethodKubun(),
				dto.getBankAccountCode(),
				dto.getClosingDay(),
				dto.getPaymentMethodSort(),
				dto.isEnableFlg(),
				dto.isEnableUpdateFlg());
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
				data.getUserId().getValue(),
				data.getPaymentMethodCode().getValue(),
				data.getPaymentMethodName().getValue(),
				data.getPaymentMethodMemo().getValue(),
				data.getPaymentMethodKubun().getValue(),
				data.getBankAccountCode() == null ? null : data.getBankAccountCode().getValue(),
				data.getClosingDay().getValue(),
				data.getPaymentMethodSort().getValue(),
				data.getEnableFlg().getValue(),
				data.getEnableUpdateFlg().getValue());
	}
}
