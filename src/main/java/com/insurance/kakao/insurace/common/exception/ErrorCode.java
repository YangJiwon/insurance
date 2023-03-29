package com.insurance.kakao.insurace.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	ERROR1(HttpStatus.INTERNAL_SERVER_ERROR, "계약 생성에 실패하였습니다"),
	ERROR2(HttpStatus.INTERNAL_SERVER_ERROR, "상품 조회에 실패하였습니다"),
	ERROR3(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 날짜입니다"),
	ERROR4(HttpStatus.INTERNAL_SERVER_ERROR, "담보 리스트 조회에 실패하였습니다"),
	ERROR5(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 상품번호입니다"),
	ERROR6(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 클 수 없습니다"),
	ERROR7(HttpStatus.BAD_REQUEST, "계약 담보 생성에 실패하였습니다"),
	ERROR8(HttpStatus.INTERNAL_SERVER_ERROR, "담보 번호 리스트 조회에 실패하였습니다"),
	ERROR9(HttpStatus.INTERNAL_SERVER_ERROR, "담보 삭제에 실패하였습니다"),
	ERROR10(HttpStatus.INTERNAL_SERVER_ERROR, "담보 추가에 실패하였습니다"),
	ERROR11(HttpStatus.INTERNAL_SERVER_ERROR, "종료일은 오늘보다 이전일 수 없습니다"),
	ERROR12(HttpStatus.INTERNAL_SERVER_ERROR, "기간 업데이트에 실패하였습니다"),
	ERROR13(HttpStatus.INTERNAL_SERVER_ERROR, "상태 업데이트에 실패하였습니다"),
	ERROR14(HttpStatus.INTERNAL_SERVER_ERROR, "현재 가지고 있는 상품과 맞지 않는 담보입니다"),
	ERROR15(HttpStatus.INTERNAL_SERVER_ERROR, "이미 보유한 담보입니다"),
	ERROR16(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 삭제 요청입니다"),
	ERROR17(HttpStatus.INTERNAL_SERVER_ERROR, "만료 상태의 계약은 상태 업데이트가 불가합니다.");

	private final HttpStatus httpStatus;
	private final String errMsg;
}
