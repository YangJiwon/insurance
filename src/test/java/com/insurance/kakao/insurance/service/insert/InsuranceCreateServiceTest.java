package com.insurance.kakao.insurance.service.insert;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.insurance.kakao.insurance.model.request.CreateContractRequest;
import com.insurance.kakao.insurance.model.vo.CreateInsurance;

@SpringBootTest(classes = {InsuranceCreateService.class})
@DisplayName("보험 관련 등록 서비스 테스트")
class InsuranceCreateServiceTest {
	@Autowired
	private InsuranceCreateService insuranceCreateService;

	@MockBean
	private CreateContractService createContractService;

	final CreateContractRequest createContractRequest = new CreateContractRequest("계약명", LocalDate.now(), 1, 1, List.of(1,2,3));
	final CreateInsurance createInsurance = CreateInsurance.builder()
			.createContractRequest(createContractRequest)
			.serviceName("com.insurance.kakao.insurance.service.insert.CreateContractService#0").build();

	@Test
	@DisplayName("등록 로직 정상")
	void create(){
		assertDoesNotThrow(() -> insuranceCreateService.create(createInsurance));
	}
}
