 package com.insurance.kakao.insurance.service;

 import java.time.LocalDate;
 import java.util.List;
 import java.util.stream.Collectors;

 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 import com.insurance.kakao.insurance.common.exception.BusinessErrorCodeException;
 import com.insurance.kakao.insurance.common.exception.ErrorCode;
 import com.insurance.kakao.insurance.mapper.InsuranceCommandMapper;
 import com.insurance.kakao.insurance.model.enums.ContractStatusEnum;
 import com.insurance.kakao.insurance.model.request.CreateContractRequest;
 import com.insurance.kakao.insurance.model.request.CreateGuaranteeRequest;
 import com.insurance.kakao.insurance.model.request.CreateProductRequest;
 import com.insurance.kakao.insurance.model.response.GuaranteeResponse;
 import com.insurance.kakao.insurance.model.response.ProductResponse;
 import com.insurance.kakao.insurance.model.vo.CreateContract;

 import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceInsertService {
	private final InsuranceCommandMapper command;
	private final InsuranceSelectService insuranceSelectService;

	@Transactional
	public Integer createContract(CreateContractRequest contract) {
		LocalDate startDate = contract.getInsuranceStartDate();
		if (startDate.isBefore(LocalDate.now())) {
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_START_DATE);
		}

		int contractPeriod = contract.getContractPeriod();
		int productNo = contract.getProductNo();

		List<Integer> guaranteeNoList = contract.getGuaranteeNoList();
		long notExistGuaranteeCount = insuranceSelectService.getNotExistGuaranteeCount(productNo, guaranteeNoList);
		if(notExistGuaranteeCount > 0){
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_GUARANTEE);
		}

		List<GuaranteeResponse> guaranteeList = insuranceSelectService.selectGuaranteeList(guaranteeNoList);
		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		if(product.isNotValidPeriod(contractPeriod)) {
			throw new BusinessErrorCodeException(ErrorCode.NOT_VALID_CONTRACT_PERIOD);
		}

		double totalAmount = insuranceSelectService.getTotalAmount(guaranteeList, contractPeriod);
		CreateContract createContract = CreateContract.builder()
				.contractName(contract.getContractName())
				.contractPeriod(contractPeriod)
				.productNo(productNo)
				.totalAmount(totalAmount)
				.confirmStatus(ContractStatusEnum.NORMAL.getStatus())
				.insuranceStartDate(startDate)
				.insuranceEndDate(contract.getInsuranceEndDate())
				.guaranteeNoList(guaranteeNoList)
				.build();

		if (command.insertContract(createContract) != 1) {
			throw new BusinessErrorCodeException(ErrorCode.INSERT_CONTRACT);
		}

		int contractNo = createContract.getContractNo();
		if (command.insertGuaranteeOfContract(contractNo, guaranteeNoList) != guaranteeNoList.size()) {
			throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE_OF_CONTRACT);
		}

		return contractNo;
	}

	@Transactional
	public void createProduct(CreateProductRequest createProduct) {
		if(command.insertProduct(createProduct) != 1){
			throw new BusinessErrorCodeException(ErrorCode.INSERT_PRODUCT);
		}

		int productNo = createProduct.getProductNo();
		List<CreateGuaranteeRequest> guaranteeList = createProduct.getGuaranteeRequestList();

		insertGuarantee(productNo, guaranteeList);
	}

	@Transactional
	public void createGuarantee(int productNo, List<CreateGuaranteeRequest> guaranteeList) {
		ProductResponse product = insuranceSelectService.getProductInfo(productNo);
		insertGuarantee(product.getProductNo(), guaranteeList);
	}

	private void insertGuarantee(int productNo, List<CreateGuaranteeRequest> guaranteeList){
		if(command.insertGuarantee(productNo, guaranteeList) != guaranteeList.size()){
			throw new BusinessErrorCodeException(ErrorCode.INSERT_GUARANTEE);
		}

		List<Integer> guaranteeNoList = guaranteeList.stream()
				.map(CreateGuaranteeRequest::getGuaranteeNo)
				.collect(Collectors.toList());

		if(command.insertGuaranteeOfProduct(productNo, guaranteeNoList) != guaranteeNoList.size()){
			throw new BusinessErrorCodeException(ErrorCode.CREATE_GUARANTEE_MAPPING);
		}
	}
}
