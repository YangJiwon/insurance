package com.insurance.kakao.insurace.model.vo;

import java.util.List;

import com.insurance.kakao.insurace.model.enums.ContractStatusEnum;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class UpdateContract {
	private int contractNo;
	private int contractPeriod;
	private ContractStatusEnum contractStatus;
	private List<Integer> guaranteeNoList;
	private String serviceName;
}
