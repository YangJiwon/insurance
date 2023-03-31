package com.insurance.kakao.insurance.service.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.insurance.kakao.insurance.model.vo.UpdateContract;
import com.insurance.kakao.insurance.service.InsuranceSelectService;

@SpringBootTest(classes = {DeleteGuaranteeOfContractService.class})
@DisplayName("기존 계약에 담보 삭제 서비스 테스트")
class DeleteGuaranteeOfContractServiceTest {
	@Autowired
	private DeleteGuaranteeOfContractService deleteGuaranteeOfContractService;

	@MockBean
	private InsuranceCommandMapper command;

	@MockBean
	private InsuranceSelectService selectService;

	private final int contractNo = 1;

	@Nested
	@DisplayName("담보 삭제시 벨리데이션")
	class Validation {
		final List<Integer> guaranteeNoList = List.of(1,2,3);

		@Test
		@DisplayName("벨리데이션 통과")
		void validationPass() {
			final List<Integer> requestGuaranteeNoList = List.of(1,2);
			final UpdateContract updateContract = getUpdateContract(requestGuaranteeNoList);

			given(selectService.selectGuaranteeNoList(contractNo)).willReturn(guaranteeNoList);

			assertDoesNotThrow(() -> deleteGuaranteeOfContractService.validation(updateContract));
		}

		@Test
		@DisplayName("삭제하리는 담보 리스트가 현재 담보리스트 크기보다 큼")
		void requestListIsGreater() {
			final List<Integer> requestGuaranteeNoList = List.of(1,2,3,4);
			validationFail(requestGuaranteeNoList);
		}

		@Test
		@DisplayName("삭제하리는 담보 리스트가 현재 담보리스트 크기와 같음")
		void requestListIsSame() {
			final List<Integer> requestGuaranteeNoList = List.of(1,2,3);
			validationFail(requestGuaranteeNoList);
		}

		private void validationFail(List<Integer> requestGuaranteeNoList){
			final UpdateContract updateContract = getUpdateContract(requestGuaranteeNoList);

			given(selectService.selectGuaranteeNoList(contractNo)).willReturn(guaranteeNoList);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					deleteGuaranteeOfContractService.validation(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.NOT_VALID_DELETE_REQUEST);
		}
	}

	@Nested
	@DisplayName("담보 삭제")
	class DeleteGuarantee {
		final List<Integer> requestGuaranteeNoList = List.of(1,2);
		final UpdateContract updateContract = getUpdateContract(requestGuaranteeNoList);

		@Test
		@DisplayName("담보 삭제 성공")
		void deleteGuarantee() {
			given(command.deleteGuaranteeOfContract(contractNo, requestGuaranteeNoList)).willReturn(requestGuaranteeNoList.size());

			assertDoesNotThrow(() -> deleteGuaranteeOfContractService.update(updateContract));
		}

		@Test
		@DisplayName("담보 삭제 실패")
		void deleteGuaranteeException() {
			given(command.deleteGuaranteeOfContract(contractNo, requestGuaranteeNoList)).willReturn(1);

			BusinessErrorCodeException exception = assertThrows(BusinessErrorCodeException.class, () ->
					deleteGuaranteeOfContractService.update(updateContract));

			assertEquals(exception.getErrorCode(), ErrorCode.DELETE_GUARANTEE_OF_CONTRACT);
		}
	}

	private UpdateContract getUpdateContract(List<Integer> requestGuaranteeNoList){
		return UpdateContract.builder()
				.contractNo(contractNo)
				.serviceName(UpdateContractServiceEnums.DELETE_GUARANTEE.getName())
				.guaranteeNoList(requestGuaranteeNoList)
				.build();
	}
}
