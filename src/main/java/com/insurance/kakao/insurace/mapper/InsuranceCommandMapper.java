package com.insurance.kakao.insurace.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.insurance.kakao.insurace.model.request.CreateGuaranteeRequest;
import com.insurance.kakao.insurace.model.request.CreateProductRequest;
import com.insurance.kakao.insurace.model.vo.CreateContract;

@Mapper
public interface InsuranceCommandMapper {
	int insertContract(CreateContract createContract);

	int insertGuaranteeOfContract(int contractNo, List<Integer> guaranteeNoList);

	int deleteGuaranteeOfContract(int contractNo, List<Integer> guaranteeNoList);

	int updateContractPeriod(int contractNo, LocalDate endDate, int contractPeriod);

	int updateContractStatus(int contractNo, String contractStatus);

	int updateTotalAmount(int contractNo, double totalAmount);

	int insertProduct(CreateProductRequest createProduct);

	int insertGuarantee(int productNo, List<CreateGuaranteeRequest> createGuaranteeList);
}
