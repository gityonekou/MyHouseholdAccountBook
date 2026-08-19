/**
 * 管理者メニュー ユーザ情報管理ユースケース(AdminMenuUserInfoUseCase.java)の単体テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.adminmenu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBaseList;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBaseList;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditureinfo.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.AdminMenuUserInfoRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.ShopBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.SisyutuItemBaseTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUserInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuUserInfoResponse;

/**
 *<pre>
 * 管理者メニュー ユーザ情報管理ユースケース(AdminMenuUserInfoUseCase.java)の単体テストクラスです。
 * 主に新規ユーザ追加時の支払方法マスタ初期投入(「支払方法がない」システム行・「現金」)を検証します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@ExtendWith(MockitoExtension.class)
class AdminMenuUserInfoUseCaseUnitTest {

	@InjectMocks
	private AdminMenuUserInfoUseCase service;

	@Mock
	private AdminMenuUserInfoRepository adminUserInfoRepository;
	@Mock
	private AccountBookUserRepository accountBookUserRepository;
	@Mock
	private SisyutuItemBaseTableRepository sisyutuItemBaseTableRepository;
	@Mock
	private ShopBaseTableRepository shopBaseTableRepository;
	@Mock
	private SisyutuItemTableRepository sisyutuItemTableRepository;
	@Mock
	private ShopTableRepository shopTableRepository;
	@Mock
	private PaymentMethodTableRepository paymentMethodTableRepository;

	private AdminMenuUserInfoForm createValidAddForm() {
		AdminMenuUserInfoForm form = new AdminMenuUserInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setUserId("newuser01");
		form.setUserName("新規ユーザ");
		form.setUserStatus(MyHouseholdAccountBookContent.USER_STATUS_ENABLED_VALUE);
		form.setUserRole(List.of(MyHouseholdAccountBookContent.USER_ROLE_USER_VALUE));
		form.setUserPassword("password1");
		form.setUserPasswordRetry("password1");
		form.setTargetYearMonth("202511");
		return form;
	}

	@Test
	@DisplayName("正常系：新規ユーザ追加時、支払方法マスタに「支払方法がない」(999)と「現金」(001)が投入される")
	void testExecAction_Add_正常系_支払方法マスタ初期投入() {
		// Given
		when(accountBookUserRepository.add(any(AccountBookUser.class))).thenReturn(1);
		when(sisyutuItemBaseTableRepository.findAll()).thenReturn(SisyutuItemBaseList.from(List.of(
				SisyutuItemBase.from("0001", "事業経費", "詳細", "0001", "1", "0100000000"))));
		when(sisyutuItemTableRepository.add(any())).thenReturn(1);
		when(shopBaseTableRepository.findAll()).thenReturn(ShopBaseList.from(List.of(
				ShopBase.from("001", "テスト店舗"))));
		when(shopTableRepository.add(any())).thenReturn(1);
		when(paymentMethodTableRepository.add(any(PaymentMethod.class))).thenReturn(1);

		// When
		AdminMenuUserInfoResponse response = service.execAction(createValidAddForm());

		// Then
		ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
		verify(paymentMethodTableRepository, times(2)).add(captor.capture());

		List<PaymentMethod> addedPaymentMethods = captor.getAllValues();
		// 「支払方法がない」システム行
		PaymentMethod noneData = addedPaymentMethods.stream()
				.filter(pm -> pm.getPaymentMethodCode().getValue().equals(MyHouseholdAccountBookContent.PAYMENT_METHOD_CODE_NONE_VALUE))
				.findFirst().orElseThrow();
		assertEquals("newuser01", noneData.getUserId().getValue());
		assertFalse(noneData.getEnableUpdateFlg().getValue(), "「支払方法がない」システム行は更新不可であること");
		assertTrue(noneData.getEnableFlg().getValue());
		assertNull(noneData.getBankAccountCode(), "「支払方法がない」システム行は銀行口座コードがNULLであること");

		// 「現金」
		PaymentMethod cashData = addedPaymentMethods.stream()
				.filter(pm -> pm.getPaymentMethodCode().getValue().equals("001"))
				.findFirst().orElseThrow();
		assertEquals("newuser01", cashData.getUserId().getValue());
		assertTrue(cashData.getEnableUpdateFlg().getValue(), "「現金」は通常の支払方法として更新可能であること");

		assertTrue(response.isTransactionSuccessFull());
	}

	@Test
	@DisplayName("異常系：支払方法テーブルへの追加件数が不正な場合、業務エラーとなる")
	void testExecAction_Add_異常系_支払方法追加件数不正() {
		// Given
		when(accountBookUserRepository.add(any(AccountBookUser.class))).thenReturn(1);
		when(sisyutuItemBaseTableRepository.findAll()).thenReturn(SisyutuItemBaseList.from(null));
		when(shopBaseTableRepository.findAll()).thenReturn(ShopBaseList.from(null));
		when(paymentMethodTableRepository.add(any(PaymentMethod.class))).thenReturn(0);

		// When & Then
		assertThrows(RuntimeException.class, () -> service.execAction(createValidAddForm()));
	}
}
