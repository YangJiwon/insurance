package com.insurance.kakao.insurance.service.insert;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurance.model.request.CreateProductRequest;
import com.insurance.kakao.insurance.model.vo.CreateInsurance;

@SpringBootTest(classes = {CreateProductService.class})
@DisplayName("상품 생성 서비스 테스트")
class CreateProductServiceTest {
	@Autowired
	private CreateProductService createProductService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private CreateGuaranteeService createGuaranteeService;

	final int productNo = 1;

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
		final CreateInsurance createInsurance = getCreateInsurance(createProduct);

		@Test
		@DisplayName("상품 등록 실패")
		void createProductException(){
			given(command.insertProduct(createProduct)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createProductService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_PRODUCT);
		}

		@Test
		@DisplayName("상품 등록 성공")
		void createProduct(){
			given(command.insertProduct(createProduct)).willReturn(1);
		//	given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
	//		given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> createProductService.create(createInsurance));
		}
	}

	private CreateInsurance getCreateInsurance(CreateProductRequest createProductRequest){
		return CreateInsurance.builder()
				.createProductRequest(createProductRequest)
				.serviceName(CreateInsuranceServiceEnums.PRODUCT.getName())
				.build();
	}
}
