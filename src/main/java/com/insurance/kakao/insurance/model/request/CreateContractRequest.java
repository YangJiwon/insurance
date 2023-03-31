package com.insurance.kakao.insurance.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.insurance.kakao.insurance.common.CommonUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계약 등록 요청")
public class CreateContractRequest {
	@NotBlank
	@Schema(description = "계약 명", example = "테스트 계약")
	private String contractName;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "보험 시작 일자")
	private LocalDate insuranceStartDate;

	@Min(1)
	@Schema(description = "계약 기간", example = "2")
	private int contractPeriod;

	@Min(1)
	@Schema(description = "상품 번호", example = "1")
	private int productNo;

	@NotEmpty
	@Schema(description = "담보 번호 리스트")
	private List<Integer> guaranteeNoList;

	public LocalDate getInsuranceEndDate(){
		return CommonUtil.plusMonth(this.insuranceStartDate, this.contractPeriod);
	}
}
