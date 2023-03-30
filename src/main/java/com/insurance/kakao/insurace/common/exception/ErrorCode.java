package com.insurance.kakao.insurace.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	ERROR1(HttpStatus.INTERNAL_SERVER_ERROR, "계약 생성에 실패하였습니다"),
	ERROR2(HttpStatus.BAD_REQUEST, "상품 조회에 실패하였습니다"),
	ERROR3(HttpStatus.BAD_REQUEST, "잘못된 계약기간입니다"),
	ERROR4(HttpStatus.BAD_REQUEST, "담보 리스트 조회에 실패하였습니다"),
	ERROR5(HttpStatus.BAD_REQUEST, "잘못된 상품번호입니다"),
	ERROR6(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 클 수 없습니다"),
	ERROR7(HttpStatus.BAD_REQUEST, "계약 담보 생성에 실패하였습니다"),
	ERROR8(HttpStatus.BAD_REQUEST, "담보 번호 리스트 조회에 실패하였습니다"),
	ERROR9(HttpStatus.INTERNAL_SERVER_ERROR, "담보 삭제에 실패하였습니다"),
	ERROR10(HttpStatus.INTERNAL_SERVER_ERROR, "담보 추가에 실패하였습니다"),
	ERROR11(HttpStatus.BAD_REQUEST, "종료일은 오늘보다 이전일 수 없습니다"),
	ERROR12(HttpStatus.INTERNAL_SERVER_ERROR, "기간 업데이트에 실패하였습니다"),
	ERROR13(HttpStatus.INTERNAL_SERVER_ERROR, "상태 업데이트에 실패하였습니다"),
	ERROR14(HttpStatus.BAD_REQUEST, "현재 가지고 있는 상품과 맞지 않는 담보입니다"),
	ERROR15(HttpStatus.BAD_REQUEST, "이미 보유한 담보입니다"),
	ERROR16(HttpStatus.BAD_REQUEST, "잘못된 삭제 요청입니다"),
	ERROR17(HttpStatus.BAD_REQUEST, "만료 상태의 계약은 상태 업데이트가 불가합니다"),
	ERROR18(HttpStatus.BAD_REQUEST, "계약 조회에 실패하였습니다"),
	ERROR19(HttpStatus.BAD_REQUEST, "존재하지 않는 담보 요청이 존재합니다"),
	ERROR20(HttpStatus.INTERNAL_SERVER_ERROR, "계약 업데이트에 실패하였습니다"),
	ERROR21(HttpStatus.INTERNAL_SERVER_ERROR, "상품 생성에 실패하였습니다"),
	ERROR22(HttpStatus.INTERNAL_SERVER_ERROR, "담보 생성에 실패하였습니다");

	private final HttpStatus httpStatus;
	private final String errMsg;
}
