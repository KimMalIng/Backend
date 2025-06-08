# "너 🅿️야?" - 대학생 맞춤 스케줄 컨트롤러 📆
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
      <img src="https://github.com/user-attachments/assets/55e87115-7705-4a06-96a4-ff32ea7b5821" width="200">
   </td>
    <td>
      <h3>
        “일정 관리가 어려운 당신을 위해, </br> <p></p>
        마감일까지 일정을 완료할 수 있도록 스케줄을 제작해줄게요”
      </h3>
      <p>
       참여 인원 : 5명 / 
       개발 주기 : 9개월 / 
       2024 인천대학교 컴퓨터공학부 캡스톤디자인 장려상 👑
      </p>
      <a href="https://youtu.be/0p-yikn_kUI"> 
        <p>
          소개 영상
        </p>
      </a>
    </td>
  </tr>
</table>

## 아 누가 계획 좀 세워줬으면 좋겠다!! 😩😨

### 네. 저희가 대신 일정을 스케줄링 해드리겠습니다!

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
      <img src="https://github.com/user-attachments/assets/9969c1d0-189e-4098-be29-03480c40723d" alt="문제점" width="500"/>
   </td>
    <td>
       <img src="https://github.com/user-attachments/assets/53a8d4c0-dd68-446b-a8ff-34af39641adc" alt="문제점" width="500"/>
    </td>
  </tr>
</table>

👤 : "너... P야? 그러면 우리 서비스를 사용해봐!"

### MBTI P 성향의 대학생들을 겨냥한 프로젝트 

* 일정의 마감일과 예상 소요 시간을 입력하면 자동으로 스케줄링 완료
* 입력된 사용자의 일정 완료도를 반영한 스케줄링 진행
* 고정적인 일정 (학교 시간표, 알바) 이외의 시간들을 효율적으로 사용
* 지속적인 알림 시스템으로 일정 리마인드

## Tech & Architecture 🔧

<table border="0" cellpadding="5" cellspacing="0">
  <tr>
    <th> </th>
    <th>기술 스택</th>
    <th>상세 내용</th>
  </tr>
  <tr>
    <td><strong>백엔드</strong></td>
    <td>Spring Boot 3.2.3 / Spring Security, Java 17</td>
    <td>
      라이브러리: Spring Data JPA, JWT, OAuth2.0, Thymeleaf<br/>
      인프라: Firebase, AWS RDS
    </td>
  </tr>
  <tr>
    <td><strong>프론트엔드</strong></td>
    <td>Next.js, React, React-Native </td>
    <td>
      주요 라이브러리: Axios, React-Hook-Form
    </td>
  </tr>
  <tr>
    <td><strong>협업</strong></td>
    <td>Notion, Postmanr</td>
    <td>협업 툴로 프로젝트 관리 및 API 문서화</td>
  </tr>
</table>

<img src="https://github.com/user-attachments/assets/a90b9c52-b4c5-4692-b6d0-2cebb9d9d05e" alt="Tech" width="500"/>
<img src="https://github.com/user-attachments/assets/45709fb0-60a0-4f94-a6b8-ec582c59ea25" alt="Architecture" width="500"/>

## 구현 기능 👨‍💻

[시연 영상](https://youtu.be/0p-yikn_kUI?si=qeC62S_RSVcd2MeL&t=220)

**1. 로그인 & 회원가입 , 기본 일정 등록**   
   * 일반 로그인 & Kakao & Naver OAuth2.0 모두 지원
   * 에브리타임 계정을 입력하여 현재 시간표의 수업 일정 등록
   * 기본적으로 고정되는 수면, 아침, 점심, 저녁 시간들에 대한 일정 등록
  
**2. 고정 일정 등록**
   * 알바 혹은 개인적인 일정 등록 (일반 카테고리)
   * 시간 시간과 종료 시간을 입력
   * 오늘 하루 해당 일정 이후에 아무런 스케줄을 잡고 싶지 않다면 '이후 일정 비우기' 버튼 사용 -> 종료 시간이 자동 24:00으로 배치

**3. 스케줄링 할 일정 등록**
   * 마감일이 존재하는 일정 (과제 등) 등록
   * 일정의 시작 날짜와 마감 날짜 설정
   * '자동 스케줄링' 버튼 입력 후 해당 일정을 완료하기까지 필요한 시간 지정
   * 완료 버튼을 누르면 자동 스케줄링이 진행되며 시간 날짜와 마감 날짜 사이의 빈 구간에 일정 배치

**4. 스케줄링된 일정에 완료도 입력**
   * 스케줄링이 완료된 일정은 Progress가 0%인 상태
   * 해당 일정에 완료도 반영 후 재스케줄링
   * 만약 100%로 설정하면 이후의 스케줄링된 일정은 삭제
  
**5. FCM을 사용한 지속적인 알림**
   * 일정 시작 시간과 완료 시간에 맞춰 알림 생성
  
## UI 화면 📱

### Web

<img src="https://github.com/user-attachments/assets/1522b517-6b52-4c76-88de-538453baa3c1" alt="web" width="500"/>
<img src="https://github.com/user-attachments/assets/bb467834-1dcd-4c91-a959-09898e6ae2bc" alt="web" width="500"/>
<img src="https://github.com/user-attachments/assets/75d2ada2-152f-4f42-86e2-01297dea9344" alt="web" width="500"/>
<img src="https://github.com/user-attachments/assets/5cfbc9fb-1fb3-4f96-bfa2-2bc1fd484135" alt="web" width="500"/>

### App

<img src="https://github.com/user-attachments/assets/4320b238-9491-4948-82ab-82a588d8a4cc" alt="web" width="500"/>

## 스케줄링 알고리즘 🖥️ 

[(알고리즘 소개 영상)](https://youtu.be/0p-yikn_kUI?si=rMTggXeN3B6llFvk&t=107)

### Flow Chart

<img src="https://github.com/user-attachments/assets/9e0d0b3c-b977-4243-8075-eb3c4d7b80b0" alt="web" width="800"/>

* 하루 24시간을 10분 단위(총 144칸) 로 나누어 시간 단위를 세분화
* 고정 일정(수업, 아르바이트 등) 을 우선 배치한 뒤, 남은 시간은 ‘잉여 시간 구간’으로 정의
* 사용자가 입력한 일정은 예상 소요 시간과 마감일을 포함하며, 이를 기반으로 하루 중 해당 일정이 차지해야 할 시간 비중을 계산
* Best Fit 알고리즘을 응용하여 잉여 시간 구간 중 가장 적절한 영역에 일정을 배정
* 시간 자원의 낭비를 최소화하고 효율성 극대화

## 문제 해결 ♒

### 단일 테이블 전략 vs 조인 전략

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
   <td>
      <img src="https://github.com/user-attachments/assets/0142870b-351e-4255-85b6-33cbb3203e79" alt="문제점" width="500"/>
   </td>
    <td>
       <img src="https://github.com/user-attachments/assets/a0965a4d-bd06-4b9b-bd12-559288976498" alt="문제점" width="500"/>
    </td>
  </tr>
</table>

* Job(일정)을 관리하는 과정에서 중복되는 필드가 많아 객체 지향 상속 구조 기반의 엔티티 계층 설계
* 해당 과정에서 단일 테이블 전략과 조인 전략 중 어떤 전략을 사용할 지 고민

**비교 실험 진행**

* 실제 운영 환경을 고려해 조인 전략 vs 단일 테이블 전략 간 성능 실험 진행
* 단일 테이블 전략이 오히려 성능이 낮았지만, 엔티티 유형을 구분하는 dtype 컬럼에 인덱스 적용을 통해 성능 튜닝
* 10만 건의 더미 데이터를 테스트 한 결과 조회 시간이 172ms에서 40.6ms로 약 4배 향상.

**최종적으로 단일 테이블 전략 선택!!**

이에 관련하여.. (이미지 클릭 시 이동합니다)

<a href="">
  <img src="https://github.com/user-attachments/assets/47ca802d-40e1-4fe5-af37-29d089d2fe9b" width="300">
</a>


## 팀 소개 👤

<img src="https://github.com/user-attachments/assets/edc314ef-cb95-4563-8a08-7aaf3363dfde" width="700">

