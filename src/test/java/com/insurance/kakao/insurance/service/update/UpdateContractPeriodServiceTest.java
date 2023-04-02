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

import com.insurance.kakao.insurance.common.CommonUtil;
import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {UpdateContractPeriodService.class})
@DisplayName("계약 기간 변경 서비스")
class UpdateContractPeriodServiceTest {
	@Autowired
	private UpdateContractPeriodService updateContractPeriodService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	private final int contractNo = 2;
	private final int requestContractPeriod = 3;
	private final int curContractPeriod = 6;
	private final int productNo = 1;
	private final ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-01-23"), curContractPeriod, productNo, ContractStatusEnum.NORMAL.getStatus());

	@Nested
	@DisplayName("계약 기간 변경시 벨리데이션")
	class Validation {
		final ProductResponse product = new ProductResponse(1, "테스트 상품", 3, 12);
		final UpdateContract updateContract = getUpdateContract(requestContractPeriod);

		@Test
		@DisplayName("현재 기간과 동일한 요청값일떄")
		void samePeriod() {
			final UpdateContract updateContract = getUpdateContract(curContractPeriod);

			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractPeriodService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.SAME_CONTRACT_PERIOD);
		}

		@Test
		@DisplayName("변경하려는 종료기간이 오늘날짜보다 이전일 떄")
		void endDateEarlierThanNow() {
			final ContractResponse notValidEndDate = new ContractResponse(LocalDate.parse("2022-01-23"), curContractPeriod, productNo, ContractStatusEnum.NORMAL.getStatus());

			given(selectService.getContractInfo(contractNo)).willReturn(notValidEndDate);
			given(selectService.getProductInfo(productNo)).willReturn(product);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractPeriodService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_END_DATE);
		}

		@Test
		@DisplayName("벨리데이션 통과")
		void validationPass() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(selectService.getProductInfo(productNo)).willReturn(product);

			assertDoesNotThrow(() -> updateContractPeriodService.validation(updateContract));
		}
	}

	@Nested
	@DisplayName("계약 기간 업데이트")
	class UpdatePeriod {
		final UpdateContract updateContract = getUpdateContract(requestContractPeriod);
		final LocalDate endDate = CommonUtil.plusMonth(contractResponse.getInsuranceStartDate(), requestContractPeriod);

		@Test
		@DisplayName("계약기간 업데이트 성공")
		void updatePeriod() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(command.updateContractPeriod(contractNo, endDate, requestContractPeriod)).willReturn(1);

			assertDoesNotThrow(() -> updateContractPeriodService.update(updateContract));
		}

		@Test
		@DisplayName("계약기간 업데이트 실패")
		void updatePeriodException() {
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(command.updateContractPeriod(contractNo, endDate, requestContractPeriod)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					updateContractPeriodService.update(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.UPDATE_CONTRACT_PERIOD);
		}
	}

	private UpdateContract getUpdateContract(int contractPeriod){
		return UpdateContract.builder()
				.contractNo(contractNo)
				.serviceName(UpdateContractServiceEnum.UPDATE_PERIOD.getName())
				.contractPeriod(contractPeriod)
				.build();
	}
}
