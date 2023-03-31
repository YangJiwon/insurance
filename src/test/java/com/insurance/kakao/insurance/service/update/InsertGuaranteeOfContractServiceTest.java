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
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {InsertGuaranteeOfContractService.class})
@DisplayName("기존 계약에 담보 등록 서비스 테스트")
class InsertGuaranteeOfContractServiceTest {
	@Autowired
	private InsertGuaranteeOfContractService insertGuaranteeOfContractService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	private final int contractNo = 2;

	@Nested
	@DisplayName("담보 등록시 벨리데이션")
	class Validation {
		final List<Integer> requestGuaranteeNoList = List.of(1,2);
		final UpdateContract updateContract = getUpdateContract(requestGuaranteeNoList);
		final ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-03-23"), contractNo, 1, ContractStatusEnum.NORMAL.getStatus());
		final int productNo = 1;
		final List<GuaranteeResponse> requestGuaranteeList = List.of(
				new GuaranteeResponse(1, "테스트담보", 10000, 100),
				new GuaranteeResponse(2, "테스트담보2",  20000, 200)
		);

		@Test
		@DisplayName("등록하려는 담보 리스트가 매핑정보에 없거나 현재 상품과 맞지 않는 담보 존재")
		void existOtherProduct() {
			given(selectService.selectGuaranteeList(requestGuaranteeNoList)).willReturn(requestGuaranteeList);
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(selectService.getNotExistGuaranteeCount(productNo, requestGuaranteeNoList)).willReturn(1L);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_GUARANTEE);
		}

		@Test
		@DisplayName("등록하려는 담보 리스트에 이미 가지고 있는 담보 존재")
		void alreadyExistGuarantee() {
			final List<GuaranteeResponse> curGuaranteeList = List.of(
					new GuaranteeResponse(1, "테스트담보", 10000, 100)
			);

			given(selectService.selectGuaranteeList(requestGuaranteeNoList)).willReturn(requestGuaranteeList);
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(selectService.getNotExistGuaranteeCount(productNo, requestGuaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(contractNo)).willReturn(curGuaranteeList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.ALREADY_EXIST_GUARANTEE);
		}
	}

	@Nested
	@DisplayName("담보 등록")
	class InsertGuarantee {
		final List<Integer> requestGuaranteeNoList = List.of(1,2);
		final UpdateContract updateContract = getUpdateContract(requestGuaranteeNoList);

		@Test
		@DisplayName("담보 등록 성공")
		void insertGuarantee() {
			given(command.insertGuaranteeOfContract(contractNo, requestGuaranteeNoList)).willReturn(requestGuaranteeNoList.size());

			assertDoesNotThrow(() -> insertGuaranteeOfContractService.update(updateContract));
		}

		@Test
		@DisplayName("담보 등록 실패")
		void insertGuaranteeException() {
			given(command.insertGuaranteeOfContract(contractNo, requestGuaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.update(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		}
	}

	private UpdateContract getUpdateContract(List<Integer> requestGuaranteeNoList){
		return UpdateContract.builder()
				.contractNo(contractNo)
				.serviceName(UpdateContractServiceEnums.INSERT_GUARANTEE.getName())
				.guaranteeNoList(requestGuaranteeNoList)
				.build();
	}
}
