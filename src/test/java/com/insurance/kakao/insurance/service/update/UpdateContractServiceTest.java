package com.insurance.kakao.insurance.service.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {InsuranceUpdateService.class})
@DisplayName("계약 변경 서비스")
class UpdateContractServiceTest {
	@Autowired
	private InsuranceUpdateService insuranceUpdateService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	@MockBean
	private InsertGuaranteeOfContractService insertGuaranteeOfContractService;

	private final int contractNo = 2;
	private final UpdateContract updateContract = UpdateContract.builder()
			.contractNo(contractNo)
			.guaranteeNoList(List.of(1,2,3))
			.serviceName("com.insurance.kakao.insurance.service.update.InsertGuaranteeOfContractService#0")
			.build();
	private final double totalAmount = 123000;

	@Nested
	@DisplayName("만료상태인 경우 체크")
	class IsExpire{
		@Test
		@DisplayName("만료 상태인 경우")
		void isExpire(){
			final ContractResponse contract = new ContractResponse(LocalDate.parse("2023-01-23"), contractNo, 1, ContractStatusEnum.EXPIRE.getStatus());

			given(selectService.getContractInfo(contractNo)).willReturn(contract);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceUpdateService.updateContract(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.UPDATE_EXPIRE_CONTRACT);
		}
	}

	@Nested
	@DisplayName("총 보험료 수정")
	class UpdateTotalAmount{
		final ContractResponse contract = new ContractResponse(LocalDate.parse("2023-01-23"), contractNo, 1, ContractStatusEnum.NORMAL.getStatus());

		@Test
		@DisplayName("총 보험료 수정 성공")
		void updateTotalAmount(){
			given(selectService.getContractInfo(contractNo)).willReturn(contract);
			given(selectService.getTotalAmount(contractNo)).willReturn(totalAmount);
			given(command.updateTotalAmount(contractNo, totalAmount)).willReturn(1);

			assertDoesNotThrow(() -> insuranceUpdateService.updateContract(updateContract));
		}

		@Test
		@DisplayName("총 보험료 수정 실패")
		void updateTotalAmountException(){
			given(selectService.getContractInfo(contractNo)).willReturn(contract);
			given(selectService.getTotalAmount(contractNo)).willReturn(totalAmount);
			given(command.updateTotalAmount(contractNo, totalAmount)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceUpdateService.updateContract(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.UPDATE_TOTAL_AMOUNT);
		}
	}
}
