package com.insurance.kakao.insurance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
import com.insurance.kakao.insurance.mapper.InsuranceQueryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class SendAdviceNoteService {
	private final InsuranceQueryMapper query;
	private final InsuranceCommandMapper command;

	@Scheduled(cron = "0 0 0 * * *")
	void scheduleFixedDelayTask() {
		LocalDate targetExpireDate = LocalDate.now().plusDays(7);
		List<Integer> sendContractNoList = query.selectSendAdviceNoteList(targetExpireDate);
		if(CollectionUtils.isEmpty(sendContractNoList)){
			System.out.println("발송할 안내장이 없습니다.");
			return;
		}

		sendContractNoList.forEach(v -> {
			try{
				System.out.printf("계약번호(%d) : 만료일이 일주일 남았습니다. %n", v);
				command.updateSendAdviceNote(v);
			} catch(Exception e){
				log.error(String.format("%s 계약번호 안내장 발송에 실패했습니다. - %s", v, e.getMessage()));
			}
		});
	}
}
