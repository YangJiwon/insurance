package com.insurance.kakao.insurance.service.insert;

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
import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.CreateContract;
import com.insurance.kakao.insurance.model.vo.CreateInsurance;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {CreateContractService.class})
@DisplayName("계약 생성 서비스 테스트")
class CreateContractServiceTest {
	@Autowired
	private CreateContractService createContractService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	final int productNo = 1;
	final int contractPeriod = 3;
	final double totalAmount = 1000230;
	final List<Integer> guaranteeNoList = List.of(1,2,3);
	final List<GuaranteeResponse> guaranteeList = List.of(
			new GuaranteeResponse(1, "테스트담보", 10000, 100),
			new GuaranteeResponse(2, "테스트담보2", 20000, 200),
			new GuaranteeResponse(3, "테스트담보3", 30000, 300)
	);
	final ProductResponse product = new ProductResponse(productNo, "테스트 상품", 3, 12);
	final CreateContractRequest createContractRequest = new CreateContractRequest("계약명", LocalDate.now(), contractPeriod, productNo, guaranteeNoList);
	final CreateInsurance createInsurance = getCreateInsurance(createContractRequest);

	@Nested
	@DisplayName("계약 등록")
	class CreateContractTest{
		@Test
		@DisplayName("시작일자가 오늘 날짜보다 이전")
		void createProductException(){
			final CreateInsurance isNotValidEndDate = getCreateInsurance(new CreateContractRequest("계약명", LocalDate.parse("2020-03-31"), 3, productNo, guaranteeNoList));

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(isNotValidEndDate));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_START_DATE);
		}

		@Test
		@DisplayName("상품에 포함되지 않은 담보 등록")
		void otherProduct(){
			given(selectService.notExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(2L);
			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_GUARANTEE);
		}

		@Test
		@DisplayName("계약기간이 해당 상품의 최소/최대 기간과 맞지 않을 때")
		void isNotValidContractPeriod(){
			final CreateInsurance hasMinPeriod = getCreateInsurance(new CreateContractRequest("계약명", LocalDate.now(), 1, productNo, guaranteeNoList));
			final CreateInsurance hasMaxPeriod = getCreateInsurance(new CreateContractRequest("계약명", LocalDate.now(), 100, productNo, guaranteeNoList));


			given(selectService.notExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(hasMinPeriod));
			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_CONTRACT_PERIOD);

			exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(hasMaxPeriod));
			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}

		final CreateContract createContract = CreateContract.builder()
				.contractName(createContractRequest.getContractName())
				.contractPeriod(contractPeriod)
				.productNo(productNo)
				.totalAmount(totalAmount)
				.confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				.insuranceStartDate(createContractRequest.getInsuranceStartDate())
				.insuranceEndDate(createContractRequest.getInsuranceEndDate())
				.guaranteeNoList(guaranteeNoList)
				.build();

		@Test
		@DisplayName("계약 등록 실패")
		void insertContractException() {
			given(selectService.notExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_CONTRACT);
		}

		@Test
		@DisplayName("담보 등록 실패")
		void insertGuaranteeOfContractException() {
			given(selectService.notExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(1);
			given(command.insertGuaranteeOfContract(0, guaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createContractService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		}

		@Test
		@DisplayName("계약 등록 성공")
		void createContract() {
			given(selectService.notExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(1);
			given(command.insertGuaranteeOfContract(0, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> createContractService.create(createInsurance));
		}
	}

	private CreateInsurance getCreateInsurance(CreateContractRequest createContractRequest){
		return CreateInsurance.builder()
				.createContractRequest(createContractRequest)
				.serviceName(CreateInsuranceServiceEnum.CONTRACT.getName())
				.build();
	}
}
