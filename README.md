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

![Uploading image.png…]()
