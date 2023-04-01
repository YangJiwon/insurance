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
import com.insurance.kakao.insurance.model.response.ProductResponse;
import com.insurance.kakao.insurance.model.vo.CreateInsurance;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {CreateGuaranteeService.class})
@DisplayName("담보 생성 서비스 테스트")
class CreateGuaranteeServiceTest {
	@Autowired
	private CreateGuaranteeService createGuaranteeService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	final int productNo = 1;
	final List<CreateGuaranteeRequest> createGuaranteeList = List.of(
			new CreateGuaranteeRequest("테스트 담보", 10000, 100, 1),
			new CreateGuaranteeRequest("테스트 담보2", 120000, 200, 2)
	);
	final List<Integer> guaranteeNoList = createGuaranteeList.stream()
			.map(CreateGuaranteeRequest::getGuaranteeNo)
			.collect(Collectors.toList());
	final ProductResponse product = new ProductResponse(productNo, "테스트 상품", 3, 12);
	final CreateInsurance createInsurance = getCreateInsurance(productNo, createGuaranteeList);


	@Nested
	@DisplayName("담보 등록")
	class CreateGuarantee{
		@Test
		@DisplayName("담보 등록 실패")
		void createGuaranteeException(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createGuaranteeService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.INSERT_GUARANTEE);
		}

		@Test
		@DisplayName("담보 매핑 등록 실패")
		void createGuaranteeOfException(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
			given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(0);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					createGuaranteeService.create(createInsurance));

			assertEquals(exception.getErrorCode(), ErrorCode.CREATE_GUARANTEE_MAPPING);
		}

		@Test
		@DisplayName("담보 등록 성공")
		void createGuarantee(){
			given(selectService.getProductInfo(productNo)).willReturn(product);
			given(command.insertGuarantee(productNo, createGuaranteeList)).willReturn(createGuaranteeList.size());
			given(command.insertGuaranteeOfProduct(productNo, guaranteeNoList)).willReturn(guaranteeNoList.size());

			assertDoesNotThrow(() -> createGuaranteeService.create(createInsurance));
		}
	}

	private CreateInsurance getCreateInsurance(int productNo, List<CreateGuaranteeRequest> createContractRequestList){
		return CreateInsurance.builder()
				.createGuaranteeRequest(createContractRequestList)
				.productNo(productNo)
				.serviceName(CreateInsuranceServiceEnum.GUARANTEE.getName())
				.build();
	}
}
