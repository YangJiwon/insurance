# With Springboot framework
Spring Boot, JAVA 11, MyBatis, Swagger3, H2를 사용하였습니다.

# 선행조건
1. https://github.com/kakao-insurance-quiz/20230327-yjw.git main 브랜치로 내려받기 하시면 됩니다.
2. JDK는 11버전으로 부탁드립니다.
3. Local 환경이다보니 h2 설정이 필요합니다.

# H2 DB 설정
1. http://localhost:8080/h2-console 에서 접근 가능합니다.
2. 설정은 아래 이미지를 참고 부탁드립니다. 계정은 sa/123qwe 입니다.
<img width="462" alt="image" src="https://user-images.githubusercontent.com/9064323/229141575-43aed92f-d8d6-43a5-b3e3-e1e90ab11aad.png">
3. 아래 스크립트를 사용해 테이블을 생성합니다.

```
-- 계약 테이블
CREATE TABLE IF NOT EXISTS CONTRACT (
  CONTRACT_NO INT NOT NULL AUTO_INCREMENT, -- 계약번호
  CONTRACT_NAME VARCHAR(100) NOT NULL, -- 계약명
  PRODUCT_NO INT NOT NULL, -- 상품번호
  CONTRACT_PERIOD INT NOT NULL,  --계약기간
  INSURANCE_START_DATE DATE NOT NULL, --보험시작일
  INSURANCE_END_DATE DATE NOT NULL, --보험종료일
  REGISTRATION_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  --등록일
  UPDATE_DATE DATETIME, --수정일
  TOTAL_AMOUNT NUMERIC(20, 2) NOT NULL, --총보험료
  CONTRACT_STATUS CHAR(1) NOT NULL,  --계약상태
  PRIMARY KEY (CONTRACT_NO)
);

-- 계약이 보유한 담보 테이블
CREATE TABLE IF NOT EXISTS CONTRACT_GUARANTEE (
  CONTRACT_NO INT NOT NULL, -- 계약번호
  GUARANTEE_NO INT NOT NULL, -- 담보 번호
  PRIMARY KEY (CONTRACT_NO, GUARANTEE_NO)
);

-- 상품 테이블
CREATE TABLE IF NOT EXISTS PRODUCT (
  PRODUCT_NO INT NOT NULL AUTO_INCREMENT, -- 상품번호
  PRODUCT_NAME VARCHAR(100) NOT NULL, -- 상품명
  PRODUCT_MIN_PERIOD int NOT NULL, -- 최소 계약 기간
  PRODUCT_MAX_PERIOD int NOT NULL, -- 최대 계약 기간
  PRIMARY KEY (PRODUCT_NO)
);

-- 담보 테이블
CREATE TABLE IF NOT EXISTS GUARANTEE (
  GUARANTEE_NO INT NOT NULL AUTO_INCREMENT, -- 담보번호
  GUARANTEE_NAME VARCHAR(100) NOT NULL, -- 담보명
  SUBSCRIPTION_AMOUNT NUMERIC(20, 2) NOT NULL, -- 가입금액
  STANDARD_AMOUNT NUMERIC(20, 2) NOT NULL, -- 기준금액
  PRIMARY KEY (GUARANTEE_NO)
);

-- 상품이 보유한 담보 테이블
CREATE TABLE IF NOT EXISTS PRODUCT_GUARANTEE (
  PRODUCT_NO INT NOT NULL, -- 상품번호
  GUARANTEE_NO INT NOT NULL, -- 담보번호
  PRIMARY KEY (PRODUCT_NO, GUARANTEE_NO)
);
```

# 테이블 정보
```
CONTRACT 테이블 - 보유한 상품의 번호 및 기본적인 계약 정보가 있습니다.
CONTRACT_GUARANTEE 테이블 - 해당 계약이 보유한 상품 담보 번호들이 있습니다.
PRODUCT 테이블 - 상품 정보가 있습니다. 최소/최대 계약기간을 벗어난다면 비즈니스 로직에서 exception이 발생합니다.
GUARANTEE 테이블 - 담보 정보를 가지고 있습니다.
PRODUCT_GUARANTEE 테이블 - 상품이 어떤 담보를 가지고 있는지 확인 가능합니다.
```

# Swagger URL & API 명세
```
API 목록 및 테스트는 http://localhost:8080/swagger-ui/index.html#/ 에서 가능합니다.
각 API 별 요청&응답 명세 또한 swagger 에서 확인 가능합니다.
```

* 새로운 상품 생성 API (POST /product)
> 새로운 상품 등록이 가능합니다. 
> 상품 정보 및 상품 하위의 담보 정보를 요청값으로 받습니다.
<img width="312" alt="image" src="https://user-images.githubusercontent.com/9064323/229086426-43a57dc6-ba7a-47a3-be19-a185cf7bd742.png">


* 새로운 담보 생성 API (POST /{productNo}/guarantee)
> 새로운 담보 등록이 가능합니다. 특정 상품 하위에 등록되기 떄문에 상품번호가 필요합니다.
<img width="421" alt="image" src="https://user-images.githubusercontent.com/9064323/229087788-501f1d90-2e24-4404-84a1-2564fce6d729.png">

* 계약 생성 API (POST /contract)
> 계약 등록 API 입니다. 상품번호 및 계약에 사용할 담보 리스트를 요청값으로 받습니다.
```
계약 시작일은 오늘보다 이전일 수 없습니다.
계약기간은 상품의 최소 계약기간보다 작거나 최대 계약기간보다 크면 안됩니다.
담보 리스트의 정보는 상품번호 하위의 담보 정보여야만 합니다.
```
<img width="588" alt="image" src="https://user-images.githubusercontent.com/9064323/229089810-f2976b61-6c76-4cc6-bec3-1c9c07167d9c.png">

* 계약 정보 조회 API (GET /{contractNo}/contract)
> 계약 정보를 조회할 수 있습니다. 계약 번호를 요청값으로 받습니다.
<img width="401" alt="image" src="https://user-images.githubusercontent.com/9064323/229109241-3e0729cc-981a-4757-b01c-745fbcb4741f.png">

* 담보 추가 API (POST /contract/guarantee)
> 계약에 담보 추가가 가능합니다. 계약 번호와 추가할 담보 리스트를 요청값으로 받으며, 추가된 담보 리스트에 따라 총 보험료가 재계산됩니다.
```
만료 상태의 계약은 담보 추가가 불가합니다.
현재 계약의 상품에 해당하는 담보만 추가 가능합니다.
이미 존재하는 담보가 있다면 추가가 불가합니다.

```
<img width="354" alt="image" src="https://user-images.githubusercontent.com/9064323/229109436-202fd70c-a4d3-4434-bf18-4140b3367168.png">

* 담보 삭제 API (DELETE /contract/guarantee)
> 계약에 담보 삭제가 가능합니다. 계약 번호와 삭제할 담보 리스트를 요청값으로 받으며, 삭제된 담보 리스트에 따라 총 보험료가 재계산됩니다.
```
만료 상태의 계약은 담보 삭제가 불가합니다.
현재 가지고 있는 담보 모두를 삭제할 수는 없습니다.
```
<img width="366" alt="image" src="https://user-images.githubusercontent.com/9064323/229109768-5c304cd1-befc-49bd-9d39-a0942fe717c0.png">

* 계약기간 변경 API (PUT /contract/period)
> 계약 종료일 변경이 가능합니다. 변경할 기간을 요청값으로 받아 계약 시작일 기준으로 종료일을 재계산합니다. 변경된 계약기간에 따라 총 보험료가 재계산됩니다.
```
만료 상태의 계약은 기간 변경이 불가합니다.
현재 계약기간과 동일한 입력값이라면 수정 로직을 수행하지 않습니다.
현재 계약 상품에 해당하는 최소/최대 계약기간 범위내에서만 수정이 가능합니다.
계산된 종료일이 오늘 날짜보다 이전이라면 수정이 불가합니다.
```
<img width="363" alt="image" src="https://user-images.githubusercontent.com/9064323/229109860-54cec48c-2824-46ef-86fa-1927a2c9a9d6.png">

* 계약상태 변경 API (PUT /contract/status)
> 계약 상태 변경이 가능합니다. 변경할 상태를 요청값으로 받아 계약상태를 변경합니다.
```
만료 상태의 계약은 상태 변경이 불가합니다.
현재 계약상태와 동일한 입력값이라면 수정 로직을 수행하지 않습니다.
```
<img width="397" alt="image" src="https://user-images.githubusercontent.com/9064323/229110164-a8e29f06-931c-4d47-92c0-ad265a61a2ae.png">

* 예상 보험료 계산 API (GET /estimate-amount)
> 보험 가입전 예상 보험료를 산출 가능합니다. 상품과 담보번호 리스트, 계약기간을 요청값으로 받습니다.
```
계약기간은 상품의 최소 계약기간보다 작거나 최대 계약기간보다 크면 안됩니다.
담보 리스트의 정보는 상품번호 하위의 담보 정보여야만 합니다.
```
<img width="717" alt="image" src="https://user-images.githubusercontent.com/9064323/229328061-4b060a3b-62cc-4572-b33e-810a450fc9e6.png">



# 참고 사항
1. 보험/상품/담보 생성 API에서 실행하는 로직들은 각각 별도 클래스로 만들었습니다.
계약 생성의 경우 유효성 검사를 진행하지만, 도메인 지식이 부족해 상품과 담보 생성 로직내에서는 어떤 유효성 검사가 필요할지 상상이 힘들었습니다.

2. 계약 수정(담보 추가/삭제, 기간, 상태) API 도 각 로직들을 별도 클래스로 만들고, 
수정에 필요한 공통 로직들은 InsuranceUpdateService에서 처리하도록 구성했습니다.

3. 계약 생성 및 수정 API 의 실질적인 로직을 수행하는 클래스들은 모두 default 접근제한자를 가집니다. 
이는 같은 패키지의 public class(InsuranceUpdateService, InsuranceCreateService)를 통해서만 접근이 가능합니다.


4. 익숙하지 않은 도메인이다보니, 네이밍에 어려움을 겪었습니다.
제가 생각한 기본적인 네이밍 규칙은 아래와 같습니다.
보험 - Insurance
계약 - Contract
상품 - Product
담보 - Guarantee
금액 - Amount
