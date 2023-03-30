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

import org.apache.commons.collections4.CollectionUtils;


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

	final List<GuaranteeResponse> guaranteeAllList = List.of(
			new GuaranteeResponse(1, "테스트담보", 1, 10000, 100),
			new GuaranteeResponse(2, "테스트담보2", 1, 20000, 200),
			new GuaranteeResponse(3, "테스트담보3", 1, 30000, 400),
			new GuaranteeResponse(4, "테스트담보4", 1, 40000, 500),
			new GuaranteeResponse(5, "테스트담보5", 2, 50000, 500)
	);

	final List<GuaranteeResponse> guaranteeList = List.of(
			new GuaranteeResponse(1, "테스트담보", 1, 10000, 100),
			new GuaranteeResponse(2, "테스트담보2", 1, 20000, 200)
	);

	final ContractDetailResponse contractDetailResponse = new ContractDetailResponse(1, "테스트 계약", 2,
			LocalDate.parse("2023-03-12"), LocalDate.parse("2023-04-11"), LocalDateTime.now(),
			null, 104000, ContractStatusEnum.NORMAL.getStatus(),
			1, "테스트상품", 1, 3, guaranteeList);

	final List<ProductResponse> allProductList = List.of(
			new ProductResponse(1, "여행자보험1", 1, 3),
			new ProductResponse(2, "여행자보험2", 2, 6),
			new ProductResponse(3, "여행자보험3", 1, 5),
			new ProductResponse(4, "여행자보험4", 2, 9),
			new ProductResponse(5, "여행자보험5", 1, 12)
	);

	@Nested
	@DisplayName("상품 정보 조회")
	class GetProductInfo {
		@Test
		@DisplayName("상품 정보 조회 성공")
		void product() {
			given(query.selectAllProductInfo()).willReturn(allProductList);

			ProductResponse response = insuranceSelectService.getProductInfo(productNo);

			assertEquals(allProductList.get(0), response);
		}

		@Test
		@DisplayName("상품 정보 없음")
		void emptyProduct() {
			final int notExistProductNo = 100;

			given(query.selectAllProductInfo()).willReturn(allProductList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.getProductInfo(notExistProductNo));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR2);
		}
	}

	@Nested
	@DisplayName("담보 번호 리스트 조회")
	class GetGuaranteeNoList {
		@Test
		@DisplayName("담보 번호 리스트 조회 성공")
		void guaranteeNoList() {
			List<Integer> guaranteeNoList = List.of(1,2,3);

			given(query.selectGuaranteeNoList(contractNo)).willReturn(guaranteeNoList);

			assertDoesNotThrow(() -> insuranceSelectService.selectGuaranteeNoList(contractNo));
		}

		@Test
		@DisplayName("담보 번호 리스트 없음")
		void emptyGuaranteeNoList() {
			given(query.selectGuaranteeNoList(contractNo)).willReturn(null);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectGuaranteeNoList(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR8);
		}
	}

	@Nested
	@DisplayName("담보 리스트 조회")
	class GetGuaranteeList {

		@Test
		@DisplayName("담보 리스트 조회 성공")
		void guaranteeList() {
			given(query.selectAllGuaranteeList()).willReturn(guaranteeAllList);

			List<GuaranteeResponse> responseList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);

			assertTrue(CollectionUtils.isEqualCollection(responseList, guaranteeList));
		}

		@Test
		@DisplayName("담보 리스트 없음")
		void emptyGuaranteeList() {
			final List<Integer> notExistGuaranteeNoList = List.of(100,200);

			given(query.selectAllGuaranteeList()).willReturn(guaranteeAllList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectGuaranteeList(notExistGuaranteeNoList));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR4);
		}

		@Test
		@DisplayName("2개 이상의 담보 리스트가 조회")
		void otherProductGuaranteeList() {
			final List<Integer> otherProductGuaranteeNoList = List.of(1,5);

			given(query.selectAllGuaranteeList()).willReturn(guaranteeAllList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.selectGuaranteeList(otherProductGuaranteeNoList));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR5);
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

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR18);
		}
	}

	@Nested
	@DisplayName("계약 상세 정보 조회")
	class GetContractDetailInfo {
		@Test
		@DisplayName("계약 상세 정보 조회 성공")
		void contract() {
			given(query.getContractDetail(contractNo)).willReturn(contractDetailResponse);
			given(query.selectGuaranteeNoList(contractNo)).willReturn(guaranteeNoList);
			given(query.selectAllGuaranteeList()).willReturn(guaranteeAllList);

			assertDoesNotThrow(() -> insuranceSelectService.getContractDetail(contractNo));
		}

		@Test
		@DisplayName("계약 상세 정보 없음")
		void emptyContractDetail() {
			given(query.getContractDetail(contractNo)).willReturn(null);
			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					insuranceSelectService.getContractDetail(contractNo));

			assertEquals(exception.getErrorCode(), ErrorCode.ERROR18);
		}
	}
}
