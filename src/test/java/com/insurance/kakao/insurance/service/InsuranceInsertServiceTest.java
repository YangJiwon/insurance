package com.insurance.kakao.insurance.service;

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

	final int productNo = 1;

	@Nested
	@DisplayName("계약 등록")
	class CreateContractService{
		final int contractPeriod = 3;
		final double totalAmount = 1000230;
		final List<Integer> guaranteeNoList = List.of(1,2,3);
		final List<GuaranteeResponse> guaranteeList = List.of(
				new GuaranteeResponse(1, "테스트담보", 1, 10000, 100),
				new GuaranteeResponse(2, "테스트담보2", 1, 20000, 200),
				new GuaranteeResponse(3, "테스트담보3", 1, 30000, 300)
		);
		final ProductResponse product = new ProductResponse("테스트 상품", 3, 12);
		final CreateContractRequest createContractRequest = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), contractPeriod, guaranteeNoList);

		@Test
		@DisplayName("종료일자가 오늘 날짜보다 이전")
		void createProductException(){
			final CreateContractRequest isNotValidEndDate = new CreateContractRequest("계약명", LocalDate.parse("2020-03-31"), 3, guaranteeNoList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(isNotValidEndDate));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR11);
		}

		@Test
		@DisplayName("계약기간이 해당 상품의 최소/최대 기간과 맞지 않을 때")
		void isNotValidContractPeriod(){
			final CreateContractRequest hasMinPeriod = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), 1, guaranteeNoList);
			final CreateContractRequest hasMaxPeriod = new CreateContractRequest("계약명", LocalDate.parse("2023-03-31"), 100, guaranteeNoList);

			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(hasMinPeriod));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR3);

			exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(hasMaxPeriod));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR3);
		}

		final CreateContract createContract = CreateContract.builder()
				.contractName(createContractRequest.getContractName())
				.contractPeriod(contractPeriod)
				.productNo(productNo)
				.productName(product.getProductName())
				.totalAmount(totalAmount)
				.confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				.insuranceStartDate(createContractRequest.getInsuranceStartDate())
				.insuranceEndDate(createContractRequest.getInsuranceEndDate())
				.guaranteeNoList(guaranteeNoList)
				.build();

		@Test
		@DisplayName("계약 등록 실패")
		void insertContractException() {
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(createContractRequest));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR1);
		}

		@Test
		@DisplayName("담보 등록 실패")
		void insertGuaranteeOfContractException() {
			given(selectService.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);
			given(selectService.getTotalAmount(guaranteeList, contractPeriod)).willReturn(totalAmount);
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertContract(createContract)).willReturn(1);
			given(command.insertGuaranteeOfContract(0, guaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createContract(createContractRequest));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR7);
		}

		@Test
		@DisplayName("계약 등록 성공")
		void createContract() {
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
				new CreateGuaranteeRequest("테스트 담보", 10000, 100, productNo),
				new CreateGuaranteeRequest("테스트 담보2", 120000, 200, productNo),
				new CreateGuaranteeRequest("테스트 담보3", 130000, 300, productNo)
		);
		final CreateProductRequest createProduct = new CreateProductRequest("상품명", 1, 3, createGuaranteeList, productNo);

		@Test
		@DisplayName("상품 등록 실패")
		void createProductException(){
			given(command.insertProduct(createProduct)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createProduct(createProduct));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR21);
		}

		@Test
		@DisplayName("담보 등록 실패")
		void createGuaranteeException(){
			given(command.insertProduct(createProduct)).willReturn(1);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createProduct(createProduct));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR22);
		}

		@Test
		@DisplayName("상품 등록 성공")
		void createProduct(){
			given(command.insertProduct(createProduct)).willReturn(1);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());

			assertDoesNotThrow(() -> insuranceInsertService.createProduct(createProduct));
		}
	}

	@Nested
	@DisplayName("담보 등록")
	class CreateGuarantee{
		final List<CreateGuaranteeRequest> createGuaranteeList = List.of(
				new CreateGuaranteeRequest("테스트 담보", 10000, 100, productNo),
				new CreateGuaranteeRequest("테스트 담보2", 120000, 200, productNo),
				new CreateGuaranteeRequest("테스트 담보3", 130000, 300, productNo)
		);
		final ProductResponse product = new ProductResponse("테스트 상품", 3, 12);

		@Test
		@DisplayName("상품 조회 실패")
		void notExistProduct(){
			given(selectService.getProductInfo(productNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createGuarantee(productNo, createGuaranteeList));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR2);
		}

		@Test
		@DisplayName("담보 등록 실패")
		void createGuaranteeException(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceInsertService.createGuarantee(productNo, createGuaranteeList));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR22);
		}

		@Test
		@DisplayName("담보 등록 성공")
		void createGuarantee(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());

			assertDoesNotThrow(() -> insuranceInsertService.createGuarantee(productNo, createGuaranteeList));
		}
	}
}
