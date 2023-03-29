package com.insurance.kakao.insurace.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.insurance.kakao.insurace.model.response.ContractDetailResponse;
import com.insurance.kakao.insurace.model.response.ContractResponse;
import com.insurance.kakao.insurace.model.response.GuaranteeResponse;
import com.insurance.kakao.insurace.model.response.ProductResponse;

@Mapper
public interface InsuranceQueryMapper {
	ProductResponse getProductInfo(int productNo);

	List<GuaranteeResponse> selectGuaranteeList(List<Integer> guaranteeNoList);

	ContractResponse getContractInfo(int contractNo);

	List<Integer> selectGuaranteeNoList(int contractNo);

	ContractDetailResponse getContractDetail(int contractNo);
}
