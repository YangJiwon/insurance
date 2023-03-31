package com.insurance.kakao.insurance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.CreateContract;

@SpringBootTest(classes = {InsuranceInsertService.class})
@DisplayName("계약 관련 등록 서비스")
public class InsuranceInsertServiceTest {
	@Autowired
	private InsuranceInsertService insuranceInsertService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	@MockBean
	private CacheService cacheService;

	final int productNo = 1;

	@Nested
	@DisplayName("계약 등록")
	class CreateContractService{
		final int contractPeriod = 3;
		final double totalAmount = 1000230;
		final List<Integer> guaranteeNoList = List.of(1,2,3);
		final List<GuaranteeResponse> guaranteeList = List.of(
				new GuaranteeResponse(1, "테스트담보", 10000, 100),
				new GuaranteeResponse(2, "테스트담보2", 20000, 200),
				new GuaranteeResponse(3, "테스트담보3", 30000, 300)
		);
		final ProductResponse product = new ProductResponse(productNo, "테스트 상품", 3, 12);
		final CreateContractRequest createContractRequest = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), contractPeriod, productNo, guaranteeNoList);

		@Test
		@DisplayName("종료일자가 오늘 날짜보다 이전")
		void createProductException(){
			final CreateContractRequest isNotValidEndDate = new CreateContractRequest("계약명", LocalDate.parse("2020-03-31"), 3, productNo, guaranteeNoList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(isNotValidEndDate));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_END_DATE);
		}

		@Test
		@DisplayName("상품에 포함되지 않은 담보 등록")
		void otherProduct(){
			given(selectService.getNotExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(2L);
			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(createContractRequest));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_GUARANTEE);
		}

		@Test
		@DisplayName("계약기간이 해당 상품의 최소/최대 기간과 맞지 않을 때")
		void isNotValidContractPeriod(){
			final CreateContractRequest hasMinPeriod = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), 1, productNo, guaranteeNoList);
			final CreateContractRequest hasMaxPeriod = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), 100, productNo, guaranteeNoList);

			given(selectService.getNotExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(hasMinPeriod));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_CONTRACT_PERIOD);

			exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(hasMaxPeriod));

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
			given(selectService.getNotExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(createContractRequest));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_CONTRACT);
		}

		@Test
		@DisplayName("담보 등록 실패")
		void insertGuaranteeOfContractException() {
			given(selectService.getNotExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(1);
			given(command.insertGuaranteeOfContract(0, guaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(createContractRequest));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		}

		@Test
		@DisplayName("계약 등록 성공")
		void createContract() {
			given(selectService.getNotExistGuaranteeCount(productNo, guaranteeNoList)).willReturn(0L);
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(1);
			given(command.insertGuaranteeOfContract(0, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> insuranceInsertService.createContract(createContractRequest));
		}
	}

	@Nested
	@DisplayName("상품 등록")
	class CreateProduct{
		final List<CreateGuaranteeRequest> createGuaranteeList = List.of(
				new CreateGuaranteeRequest("테스트 담보", 10000, 100, 1),
				new CreateGuaranteeRequest("테스트 담보2", 120000, 200, 2),
				new CreateGuaranteeRequest("테스트 담보3", 130000, 300, 3)
		);
		final List<Integer> guaranteeNoList = createGuaranteeList.stream()
				.map(CreateGuaranteeRequest::getGuaranteeNo)
				.collect(Collectors.toList());
		final CreateProductRequest createProduct = new CreateProductRequest("상품명", 1, 3, createGuaranteeList, productNo);

		@Test
		@DisplayName("상품 등록 실패")
		void createProductException(){
			given(command.insertProduct(createProduct)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createProduct(createProduct));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_PRODUCT);
		}

		@Test
		@DisplayName("상품 등록 성공")
		void createProduct(){
			given(command.insertProduct(createProduct)).willReturn(1);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
			given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> insuranceInsertService.createProduct(createProduct));
		}
	}

	@Nested
	@DisplayName("담보 등록")
	class CreateGuarantee{
		final List<CreateGuaranteeRequest> createGuaranteeList = List.of(
				new CreateGuaranteeRequest("테스트 담보", 10000, 100, 1),
				new CreateGuaranteeRequest("테스트 담보2", 120000, 200, 2),
				new CreateGuaranteeRequest("테스트 담보3", 130000, 300, 3)
		);
		final List<Integer> guaranteeNoList = createGuaranteeList.stream()
				.map(CreateGuaranteeRequest::getGuaranteeNo)
				.collect(Collectors.toList());
		final ProductResponse product = new ProductResponse(1, "테스트 상품", 3, 12);

		@Test
		@DisplayName("담보 등록 실패")
		void createGuaranteeException(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createGuarantee(productNo, createGuaranteeList));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_GUARANTEE);
		}

		@Test
		@DisplayName("담보 매핑 등록 실패")
		void createGuaranteeOfException(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
			given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createGuarantee(productNo, createGuaranteeList));

			assertEquals(exception.getErrorCode(), ErrorCode.CREATE_GUARANTEE_MAPPING);
		}

		@Test
		@DisplayName("담보 등록 성공")
		void createGuarantee(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
			given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> insuranceInsertService.createGuarantee(productNo, createGuaranteeList));
		}
	}
}
