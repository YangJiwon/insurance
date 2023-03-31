package com.insurance.kakao.insurance.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.insurance.kakao.insurance.model.response.ContractDetailResponse;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;

@Mapper
public interface InsuranceQueryMapper {
	ProductResponse getProductInfo(int productNo);

	List<GuaranteeResponse> selectGuaranteeList(List<Integer> guaranteeNoList);

	ContractResponse getContractInfo(int contractNo);

	List<Integer> selectContractGuaranteeMappingNoList(int contractNo);

	ContractDetailResponse getContractDetail(int contractNo);

	List<Integer> selectSendAdviceNoteList(LocalDate targetExpireDate);

	List<Integer> selectProductGuaranteeMappingList(int productNo);
}
