package com.insurance.kakao.insurance.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.mapper.InsuranceQueryMapper;

@SpringBootTest(classes = {SendAdviceNoteService.class})
@DisplayName("안내장 발송 서비스 테스트")
class SendAdviceNoteServiceTest {
	@Autowired
	private SendAdviceNoteService sendAdviceNoteService;

	@MockBean
	private InsuranceQueryMapper query;

	@MockBean
	private InsuranceCommandMapper command;

	final List<Integer> sendContractNoList = List.of(1,2,3);
	final LocalDate targetExpireDate = LocalDate.now().plusDays(7);

	@Test
	@DisplayName("안내장 발송 대상 없음")
	void emptyList(){
		given(query.selectSendAdviceNoteList(targetExpireDate)).willReturn(null);

		sendAdviceNoteService.scheduleFixedDelayTask();

		sendContractNoList.forEach(v -> verify(command, times(0)).updateSendAdviceNote(v));
	}

	@Test
	@DisplayName("안내장 발송 정상")
	void sendAdviceNote(){
		given(query.selectSendAdviceNoteList(targetExpireDate)).willReturn(sendContractNoList);

		sendAdviceNoteService.scheduleFixedDelayTask();

		sendContractNoList.forEach(v -> verify(command, times(1)).updateSendAdviceNote(v));
	}
}
