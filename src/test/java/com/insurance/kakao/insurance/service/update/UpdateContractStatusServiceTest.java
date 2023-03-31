package com.insurance.kakao.insurance.service.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

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

@SpringBootTest(classes = {UpdateContractStatusService.class})
@DisplayName("계약 상태 변경 서비스")
class UpdateContractStatusServiceTest {
	@Autowired
	private UpdateContractStatusService updateContractStatusService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	private final int contractNo = 2;
	private final ContractStatusEnum requestStatus = ContractStatusEnum.WITHDRAW;
	private final UpdateContract updateContract = getUpdateContract(requestStatus);
	private final ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-01-23"), 1, 1, ContractStatusEnum.NORMAL.getStatus());

	@Nested
	@DisplayName("계약 상태 변경시 벨리데이션")
	class Validation {
		@Test
		@DisplayName("현재 계약 상태와 동일한 요청값일 떄")
		void sameStatus() {
			final ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-01-23"), 1, 1, requestStatus.getStatus());

			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractStatusService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.SAME_CONTRACT_STATUS);
		}

		@Test
		@DisplayName("벨리데이션 통과")
		void validationPass() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);

			assertDoesNotThrow(() -> updateContractStatusService.validation(updateContract));
		}
	}

	@Nested
	@DisplayName("계약상태 업데이트")
	class UpdateStatus {

		@Test
		@DisplayName("계약상태 업데이트 성공")
		void updateStatus() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(command.updateContractStatus(contractNo, updateContract.getContractStatusValue())).willReturn(1);

			assertDoesNotThrow(() -> updateContractStatusService.update(updateContract));
		}

		@Test
		@DisplayName("계약상태 업데이트 실패")
		void updateStatusException() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(command.updateContractStatus(contractNo, updateContract.getContractStatusValue())).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractStatusService.update(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.UPDATE_CONTRACT_STATUS);
		}
	}

	private UpdateContract getUpdateContract(ContractStatusEnum contractStatus){
		return UpdateContract.builder()
				.contractNo(contractNo)
				.serviceName(UpdateContractServiceEnums.UPDATE_STATUS.name())
				.contractStatus(contractStatus)
				.build();
	}
}
