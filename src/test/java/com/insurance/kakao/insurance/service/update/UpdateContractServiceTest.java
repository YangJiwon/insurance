package com.insurance.kakao.insurance.service.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
import com.insurance.kakao.insurance.common.exception.ErrorCode;
import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {InsuranceUpdateService.class})
@DisplayName("계약 변경 서비스")
class UpdateContractServiceTest {
	@Autowired
	private InsuranceUpdateService insuranceUpdateService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	@MockBean
	private InsertGuaranteeOfContractService insertGuaranteeOfContractService;

	private final int contractNo = 2;
	final UpdateContract updateContract = UpdateContract.builder()
			.contractNo(contractNo)
			.guaranteeNoList(List.of(1,2,3))
			.serviceName("com.insurance.kakao.insurance.service.update.InsertGuaranteeOfContractService#0")
			.build();
	final double totalAmount = 123000;

	@Test
	@DisplayName("총 보험료 수정 성공")
	void updateTotalAmount(){
		given(selectService.getTotalAmount(contractNo)).willReturn(totalAmount);
		given(command.updateTotalAmount(contractNo, totalAmount)).willReturn(1);

		assertDoesNotThrow(() -> insuranceUpdateService.updateContract(updateContract));
	}

	@Test
	@DisplayName("총 보험료 수정 실패")
	void updateTotalAmountException(){
		given(selectService.getTotalAmount(contractNo)).willReturn(totalAmount);
		given(command.updateTotalAmount(contractNo, totalAmount)).willReturn(0);

		BusinessErrorCodeException smallPeriod = assertThrows(BusinessErrorCodeException.class, () ->
				insuranceUpdateService.updateContract(updateContract));

		assertEquals(smallPeriod.getErrorCode(), ErrorCode.ERROR20);
	}
}
