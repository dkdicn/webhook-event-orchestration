# Webhook 기반 이벤트 자동화·오케스트레이션 시스템

외부 시스템에서 발생하는 Webhook 이벤트를 수신하여  
SUCCESS / FAIL / DUPLICATE 상태로 분기 처리하고,  
운영 가시성과 안정성을 확보한 이벤트 자동화 시스템입니다.

단순히 이벤트를 처리하는 자동화가 아니라,  
중복 방지 · 상태 관리 · 에러 추적 · 운영자 관점을 고려한 구조로 설계했습니다.

## 기술 스택
- Backend: Spring Boot, JPA, Spring Security
- Database: PostgreSQL
- Workflow: n8n
- Frontend: Thymeleaf + JavaScript
- Infra: Docker
- Notification: Slack Webhook, SSE

## 관리자 화면

- 일별 성공 / 실패 / 대기 건수 요약
- 상태, 카테고리, 우선순위 기반 필터링
- 실패 이벤트 강조 표시
- 상세 로그 조회를 통한 원인 분석

자동화 결과를 사람이 다시 확인하고 대응할 수 있도록  
운영 가시성 확보에 중점을 두었습니다.

---

##  사용자 화면

- 사용자는 단순 입력만 수행
- 처리 결과는 시스템이 자동 분류
- 복잡한 처리 로직은 백엔드에서 관리

현업 사용자가 부담 없이 사용할 수 있도록 구성했습니다.

## 처리 흐름
1. 외부 시스템 → Webhook 이벤트 전송
2. n8n Webhook 노드에서 수신
3. Payload 파싱 및 필수 값 검증
4. SHA-256 기반 Idempotency Key 생성
5. DB 조회 → 중복 여부 판단
6. SUCCESS / FAIL / DUPLICATE 분기 처리
7. 결과 DB 저장
8. 성공 → Slack 알림 / 실패 → SSE 실시간 알림

## 핵심 설계
### Idempotency 처리
- Payload 원문을 SHA-256으로 해시하여 중복 요청 방지
- 이미 처리된 요청은 DUPLICATE 상태로 기록만 남김

### 실시간 알림 (SSE)
- 실패 이벤트 발생 시 서버 → 클라이언트 단방향 알림
- 단방향 전송만 필요한 구조에서 WebSocket 대비 경량화된 SSE 선택

### Global Error Handling
- n8n Global Error Trigger로 워크플로우 전체 에러 자동 수집
- 에러 발생 시에도 FAIL 상태로 DB 기록하여 데이터 유실 방지

##  참고

- n8n Workflow는 보안상 Private 환경에서 운영되며  
  설계 및 흐름은 포트폴리오 문서와 캡처 이미지로 대체했습니다.

