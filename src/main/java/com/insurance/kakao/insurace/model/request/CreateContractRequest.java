package com.insurance.kakao.insurace.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.insurance.kakao.insurace.common.CommonUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "계약 요청")
public class CreateContractRequest {
	@NotBlank
	@Schema(description = "계약 명", example = "테스트 계약")
	private String contractName;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd") //TODO:: example
	@Schema(description = "보험 시작 일자")
	private LocalDate insuranceStartDate;

	@Min(1)
	@Schema(description = "계약 기간", example = "2")
	private int contractPeriod;

	@NotEmpty
	@Schema(description = "담보 번호 리스트") //TODO:: example
	private List<Integer> guaranteeNoList;

	public LocalDate getInsuranceEndDate(){
		return CommonUtil.plusMonth(this.insuranceStartDate, this.contractPeriod);
	}
}
