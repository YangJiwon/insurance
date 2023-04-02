package com.insurance.kakao.insurance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceQueryMapper;
import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
import com.insurance.kakao.insurance.model.response.ContractDetailResponse;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;

@SpringBootTest(classes = {InsuranceSelectService.class})
@DisplayName("계약 관련 조회 서비스 테스트")
class InsuranceSelectServiceTest {
	@Autowired
	private InsuranceSelectService insuranceSelectService;

	@MockBean
	private InsuranceQueryMapper query;

	final int productNo = 1;
	final int contractNo = 1;
	final List<Integer> guaranteeNoList = List.of(1,2);

	final List<GuaranteeResponse> guaranteeList = List.of(
			new GuaranteeResponse(1, "테스트담보",  10000, 100),
			new GuaranteeResponse(2, "테스트담보2",  20000, 200)
	);

	final ContractDetailResponse contractDetailResponse = new ContractDetailResponse(1, "테스트 계약", 2,
			LocalDate.parse("2023-03-12"), LocalDate.parse("2023-04-11"), LocalDateTime.now(),
			null, 104000, ContractStatusEnum.NORMAL.getStatus(),
			1, "테스트상품", 1, 3, guaranteeList);

	@Nested
	@DisplayName("상품 정보 조회")
	class GetProductInfo {
		@Test
		@DisplayName("상품 정보 조회 성공")
		void product() {
			ProductResponse productResponse = new ProductResponse(productNo, "여행자보험", 1, 3);

			given(query.getProductInfo(productNo)).willReturn(productResponse);

			assertDoesNotThrow(() -> insuranceSelectService.getProductInfo(productNo));
		}

		@Test
		@DisplayName("상품 정보 없음")
		void emptyProduct() {
			final int notExistProductNo = 100;

			given(query.getProductInfo(productNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.getProductInfo(notExistProductNo));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_PRODUCT);
		}
	}

	@Nested
	@DisplayName("계약에 매핑된 담보 번호 리스트 조회")
	class SelectContractGuaranteeNoList {
		@Test
		@DisplayName("계약에 매핑된 담보 번호 리스트 조회 성공")
		void contractGuaranteeNoList() {
			List<Integer> contractGuaranteeMappingNoList = List.of(1,2,3);

			given(query.selectContractGuaranteeMappingNoList(contractNo)).willReturn(contractGuaranteeMappingNoList);

			assertDoesNotThrow(() -> insuranceSelectService.selectContractGuaranteeMappingNoList(contractNo));
		}

		@Test
		@DisplayName("계약에 매핑된 번호 리스트 없음")
		void emptyContractGuaranteeNoList() {
			given(query.selectContractGuaranteeMappingNoList(contractNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectContractGuaranteeMappingNoList(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_CONTRACT_GUARANTEE_MAPPING_NO_LIST);
		}
	}

	@Nested
	@DisplayName("담보 리스트 조회")
	class GetGuaranteeList {

		@Test
		@DisplayName("담보 리스트 조회 성공")
		void guaranteeList() {
			given(query.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);

			assertDoesNotThrow(() -> insuranceSelectService.selectGuaranteeList(guaranteeNoList));
		}

		@Test
		@DisplayName("담보 리스트 없음")
		void emptyGuaranteeList() {
			given(query.selectGuaranteeList(guaranteeNoList)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectGuaranteeList(guaranteeNoList));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_GUARANTEE_LIST);
		}
	}

	@Test
	@DisplayName("총 보험료 계산")
	void getTotalAmount() {
		final int contractPeriod = 2;

		double totalAmount = insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
		assertEquals(totalAmount, 400d);
	}

	@Nested
	@DisplayName("계약 정보 조회")
	class GetContractInfo {
		@Test
		@DisplayName("계약 정보 조회 성공")
		void contract() {
			ContractResponse contractResponse = new ContractResponse(LocalDate.parse("2023-03-12"), 2, 1, ContractStatusEnum.NORMAL.getStatus());

			given(query.getContractInfo(contractNo)).willReturn(contractResponse);

			assertDoesNotThrow(() -> insuranceSelectService.getContractInfo(contractNo));
		}

		@Test
		@DisplayName("계약 정보 없음")
		void emptyContract() {
			given(query.getContractInfo(contractNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.getContractInfo(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_CONTRACT);
		}
	}

	@Nested
	@DisplayName("계약 상세 정보 조회")
	class GetContractDetailInfo {
		@Test
		@DisplayName("계약 상세 정보 조회 성공")
		void contract() {
			given(query.getContractDetail(contractNo)).willReturn(contractDetailResponse);
			given(query.selectContractGuaranteeMappingNoList(contractNo)).willReturn(guaranteeNoList);
			given(query.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);

			assertDoesNotThrow(() -> insuranceSelectService.getContractDetail(contractNo));
		}

		@Test
		@DisplayName("계약 상세 정보 없음")
		void emptyContractDetail() {
			given(query.getContractDetail(contractNo)).willReturn(null);
			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.getContractDetail(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_CONTRACT);
		}
	}

	@Nested
	@DisplayName("상품/담보 매핑 정보 조회")
	class SelectProductGuaranteeMappingList {
		final List<Integer> mappingList = List.of(1,2,3);

		@Test
		@DisplayName("상품/담보 매핑 정보 조회 성공")
		void productGuaranteeMapping() {
			given(query.selectProductGuaranteeMappingList(productNo)).willReturn(mappingList);

			assertDoesNotThrow(() -> insuranceSelectService.selectProductGuaranteeMappingList(productNo));
		}

		@Test
		@DisplayName("상품/담보 매핑 정보 조회 실패")
		void emptyProductGuaranteeMapping() {
			given(query.selectProductGuaranteeMappingList(productNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectProductGuaranteeMappingList(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.SELECT_PRODUCT_GUARANTEE_MAPPING_LIST);
		}

		@Test
		@DisplayName("상품/담보 매핑정보 담보 요청 갯수 조회")
		void notExistProductGuaranteeList() {
			final List<Integer> notExistGuaranteeNoList = List.of(4,5);
			final List<Integer> existGuaranteeNoList = List.of(1,2,3);

			given(query.selectProductGuaranteeMappingList(productNo)).willReturn(mappingList);

			long notExistCount = insuranceSelectService.notExistGuaranteeCount(productNo, notExistGuaranteeNoList);
			long existCount = insuranceSelectService.notExistGuaranteeCount(productNo, existGuaranteeNoList);

			assertEquals(notExistCount, notExistGuaranteeNoList.size());
			assertEquals(existCount, 0);
		}
	}

	@Nested
	@DisplayName("상품의 최소/최대 계약기간 벨리데이션")
	class ProductPeriodValidation{
		final ProductResponse productResponse = new ProductResponse(productNo, "여행자보험", 6, 8);

		@Test
		@DisplayName("변경하려는 기간이 상품의 최소 기간보다 작을때")
		void minPeriod() {
			given(query.getProductInfo(productNo)).willReturn(productResponse);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.validProductInfo(productNo, 1));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}

		@Test
		@DisplayName("변경하려는 기간이 상품의 최대 기간보다 클 떄")
		void maxPeriod() {
			given(query.getProductInfo(productNo)).willReturn(productResponse);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.validProductInfo(productNo, 10));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}
	}

	@Nested
	@DisplayName("예상 보험료 계산")
	class TotalAmount{
		final ProductResponse productResponse = new ProductResponse(productNo, "여행자보험", 1, 8);
		final List<Integer> mappingList = List.of(1,2);
		final int contractPeriod = 2;

		@Test
		@DisplayName("예상 보험료 계산")
		void getEstimateAmount(){
			given(query.getProductInfo(productNo)).willReturn(productResponse);
			given(query.selectProductGuaranteeMappingList(productNo)).willReturn(mappingList);
			given(query.selectGuaranteeList(guaranteeNoList)).willReturn(guaranteeList);

			double totalAmount = insuranceSelectService.getEstimateAmount(guaranteeNoList, contractPeriod, productNo);
			assertEquals(totalAmount, 400d);
		}
	}
}
