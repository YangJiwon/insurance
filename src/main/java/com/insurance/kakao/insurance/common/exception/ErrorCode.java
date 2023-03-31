package com.insurance.kakao.insurance.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	INSERT_CONTRACT(HttpStatus.INTERNAL_SERVER_ERROR, "계약 생성에 실패하였습니다"),
	SELECT_PRODUCT(HttpStatus.BAD_REQUEST, "상품 조회에 실패하였습니다"),
	NOT_VALID_CONTRACT_PERIOD(HttpStatus.BAD_REQUEST, "잘못된 계약기간입니다"),
	SELECT_GUARANTEE_LIST(HttpStatus.BAD_REQUEST, "담보 리스트 조회에 실패하였습니다"),
	INSERT_GUARANTEE_OF_CONTRACT(HttpStatus.BAD_REQUEST, "계약 담보 생성에 실패하였습니다"),
	SELECT_CONTRACT_GUARANTEE_MAPPING_NO_LIST(HttpStatus.BAD_REQUEST, "담보 번호 리스트 조회에 실패하였습니다"),
	DELETE_GUARANTEE_OF_CONTRACT(HttpStatus.INTERNAL_SERVER_ERROR, "계약 담보 삭제에 실패하였습니다"),
	NOT_VALID_START_DATE(HttpStatus.BAD_REQUEST, "시작일은 오늘보다 이전일 수 없습니다"),
	NOT_VALID_END_DATE(HttpStatus.BAD_REQUEST, "종료일은 오늘보다 이전일 수 없습니다"),
	UPDATE_CONTRACT_PERIOD(HttpStatus.INTERNAL_SERVER_ERROR, "계약 기간 업데이트에 실패하였습니다"),
	UPDATE_CONTRACT_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "계약 상태 업데이트에 실패하였습니다"),
	ALREADY_EXIST_GUARANTEE(HttpStatus.BAD_REQUEST, "이미 보유한 담보입니다"),
	NOT_VALID_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 삭제 요청입니다"),
	UPDATE_EXPIRE_CONTRACT(HttpStatus.BAD_REQUEST, "만료 상태의 계약은 변경이 불가합니다"),
	SELECT_CONTRACT(HttpStatus.BAD_REQUEST, "계약 조회에 실패하였습니다"),
	UPDATE_TOTAL_AMOUNT(HttpStatus.INTERNAL_SERVER_ERROR, "총 보험료 업데이트에 실패하였습니다"),
	UPDATE_ONLY_DATE(HttpStatus.INTERNAL_SERVER_ERROR, "수정 날짜 업데이트에 실패하였습니다"),
	INSERT_PRODUCT(HttpStatus.INTERNAL_SERVER_ERROR, "상품 생성에 실패하였습니다"),
	INSERT_GUARANTEE(HttpStatus.INTERNAL_SERVER_ERROR, "담보 생성에 실패하였습니다"),
	SAME_CONTRACT_PERIOD(HttpStatus.BAD_REQUEST, "현재 계약기간과 동일합니다"),
	SAME_CONTRACT_STATUS(HttpStatus.BAD_REQUEST, "현재 계약상태와 동일합니다"),
	NOT_VALID_GUARANTEE(HttpStatus.BAD_REQUEST, "맞지 않는 담보입니다"),
	SELECT_PRODUCT_GUARANTEE_MAPPING_LIST(HttpStatus.BAD_REQUEST, "상품에 포함된 담보 리스트 조회에 실패하였습니다"),
	CREATE_GUARANTEE_MAPPING(HttpStatus.INTERNAL_SERVER_ERROR, "담보 매핑 생성에 실패하였습니다");

	private final HttpStatus httpStatus;
	private final String errMsg;
}
