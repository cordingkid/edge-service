# PolarBookShop API Gateway 에지서비스

### Spring Cloud Gateway
- catalog-service, order-service API 게이트 웨이 구현
- 시스템 복원력을 위한 resilience4j로 서킷 브레이커 설정
- 레디스를 사용한 사용률 제한 정의
- 레디스를 사용한 분산 세션 관리