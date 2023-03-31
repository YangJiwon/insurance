package com.insurance.kakao.insurance.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.Cacheable;

import com.insurance.kakao.insurance.common.CacheKeyConstants;
import com.insurance.kakao.insurance.model.response.ContractDetailResponse;
import com.insurance.kakao.insurance.model.response.ContractResponse;
import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
import com.insurance.kakao.insurance.model.response.ProductResponse;

@Mapper
public interface InsuranceQueryMapper {
	@Cacheable(cacheNames = CacheKeyConstants.PRODUCT)
	List<ProductResponse> selectAllProductList();

	@Cacheable(cacheNames = CacheKeyConstants.GUARANTEE)
	List<GuaranteeResponse> selectAllGuaranteeList();

	ContractResponse getContractInfo(int contractNo);

	List<Integer> selectGuaranteeNoList(int contractNo);

	ContractDetailResponse getContractDetail(int contractNo);

	List<Integer> selectSendAdviceNoteList(LocalDate targetExpireDate);

	List<Integer> selectGuaranteeMappingList(int productNo);
}
