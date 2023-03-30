package com.insurance.kakao.insurance.service.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.insurance.kakao.insurance.model.vo.UpdateContract;

@SpringBootTest(classes = {UpdateContractStatusService.class})
@DisplayName("계약 상태 변경 서비스")
class UpdateContractStatusServiceTest {
	@Autowired
	private UpdateContractStatusService updateContractStatusService;

	@MockBean
	private InsuranceCommandMapper command;

	private final int contractNo = 2;
	private final UpdateContract updateContract = getUpdateContract(ContractStatusEnum.WITHDRAW);

	@Nested
	@DisplayName("계약상태 업데이트")
	class UpdateStatus {

		@Test
		@DisplayName("계약상태 업데이트 성공")
		void updateStatus() {
			given(command.updateContractStatus(contractNo, updateContract.getContractStatusValue())).willReturn(1);

			assertDoesNotThrow(() -> updateContractStatusService.update(updateContract));
		}

		@Test
		@DisplayName("계약상태 업데이트 실패")
		void updateStatusException() {
			given(command.updateContractStatus(contractNo, updateContract.getContractStatusValue())).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractStatusService.update(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR13);
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
