# With Springboot framework
Spring Boot, JAVA 11, MyBatis, Swagger3, H2, Caffeine cache를 사용하였습니다.

# Local Mac 기준 선행조건 순서 Intellij 기준 Setup
1. https://github.com/kakao-insurance-quiz/20230327-yjw.git main 브랜치로 내려받기 하시면 됩니다.
2. IntelliJ에 Lombok Plugin을 설치합니다.
```
Preferences > Plugins > Browse repositories.. > Lombok 검색 > Install > restart
```
3. JDK는 11버전으로 부탁드립니다.
4. Local 환경이다보니 h2 설정이 필요합니다.

# H2 DB connections 환경 설정
1. http://localhost:8080/h2-console 에서 접근 가능합니다.
2. 아래 이미지처럼 설정합니다. 계정은 rennes91/123qwe 입니다.
<img width="466" alt="image" src="https://user-images.githubusercontent.com/9064323/229078474-518d8063-b4c8-4f1b-9459-f172ef471fb7.png">
3. 아래 스크립트를 사용해 테이블을 생성합니다.


# Swagger URL
API 목록 및 테스트는 http://localhost:8080/swagger-ui/index.html#/  에서 가능합니다.

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
계약날짜는 오늘보다 이전일 수 없습니다.
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
