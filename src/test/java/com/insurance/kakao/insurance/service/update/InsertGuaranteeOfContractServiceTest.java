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
		final int productNo = 1;
		final ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-03-23"), contractNo, productNo, ContractStatusEnum.NORMAL.getStatus());

		@Test
		@DisplayName("등록하려는 담보 리스트에 없는 담보 존재")
		void notExistGuarantee() {
			final List<GuaranteeResponse> notExistGuaranteeList = List.of(
					new GuaranteeResponse(1, "테스트담보", 1, 10000, 100)
			);

			given(selectService.selectGuaranteeList(requestGuaranteeNoList)).willReturn(notExistGuaranteeList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR19);
		}

		@Test
		@DisplayName("등록하려는 담보 리스트에 현재 상품과 맞지 않는 담보 존재")
		void existOtherProduct() {
			final List<GuaranteeResponse> guaranteeListHasOtherProduct = List.of(
					new GuaranteeResponse(1, "테스트담보", 1, 10000, 100),
					new GuaranteeResponse(2, "테스트담보2", 2, 20000, 200)
			);

			given(selectService.selectGuaranteeList(requestGuaranteeNoList)).willReturn(guaranteeListHasOtherProduct);
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR14);
		}

		@Test
		@DisplayName("등록하려는 담보 리스트에 이미 가지고 있는 담보 존재")
		void alreadyExistGuarantee() {
			final List<GuaranteeResponse> guaranteeList = List.of(
					new GuaranteeResponse(1, "테스트담보", 1, 10000, 100),
					new GuaranteeResponse(2, "테스트담보2", 1, 20000, 200)
			);

			final List<GuaranteeResponse> curGuaranteeList = List.of(
					new GuaranteeResponse(1, "테스트담보", 1, 10000, 100)
			);

			given(selectService.selectGuaranteeList(requestGuaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getContractInfo(contractNo)).willReturn(contractResponse);
			given(selectService.selectGuaranteeList(contractNo)).willReturn(curGuaranteeList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insertGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR15);
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

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR10);
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
