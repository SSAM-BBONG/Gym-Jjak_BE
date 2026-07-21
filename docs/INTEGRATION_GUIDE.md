# Private RDS 접속 가이드 (AWS SSM 포트 포워딩)

이 문서는 팀원이 **MySQL Workbench에서 Private RDS에 안전하게 접속**하는 방법을 설명한다.

RDS는 퍼블릭 접근을 허용하지 않는다. 대신 AWS Systems Manager(SSM) 포트 포워딩을 이용해 아래 경로로 접속한다.

```text
MySQL Workbench
  -> 127.0.0.1:13306 (내 PC)
  -> AWS SSM Session Manager
  -> Backend EC2
  -> Private RDS:3306
```

> SSM 방식에서는 EC2의 SSH 22번 포트를 열거나 PEM 키를 Workbench에 등록할 필요가 없다.

## 1. 사전 조건

다음 조건이 모두 충족되어야 한다.

| 구분 | 필요 사항 |
| --- | --- |
| AWS 계정 | 본인 IAM 사용자 또는 AWS IAM Identity Center 계정의 AWS CLI 인증 정보 |
| IAM 권한 | `ssm:StartSession` 권한 등 Session Manager 세션 권한 |
| EC2 역할 | Backend EC2의 인스턴스 역할에 `AmazonSSMManagedInstanceCore` 정책 연결 |
| EC2 상태 | AWS Systems Manager의 관리형 노드에서 Backend EC2가 `Online` 상태 |
| RDS 보안 그룹 | MySQL `3306` 인바운드 소스를 Backend EC2 보안 그룹으로 허용 |
| 로컬 도구 | AWS CLI v2, Session Manager plugin, MySQL Workbench |

## 2. AWS CLI 및 Session Manager plugin 설치

### 2-1. 설치 확인

PowerShell에서 아래 명령을 실행한다.

```powershell
aws --version
session-manager-plugin
```

버전 정보가 출력되면 설치가 완료된 상태다.

명령을 찾을 수 없으면 AWS CLI v2와 Session Manager plugin을 각각 설치한 뒤 PowerShell을 새로 열어 다시 확인한다.

### 2-2. AWS 프로필 설정

팀원 개인의 AWS Access Key를 발급받은 경우 아래처럼 프로필을 생성한다.

```powershell
aws configure --profile gymjjak-team03
```

입력값 예시:

```text
AWS Access Key ID: 본인 Access Key ID
AWS Secret Access Key: 본인 Secret Access Key
Default region name: ap-northeast-2
Default output format: json
```

설정 확인:

```powershell
aws sts get-caller-identity --profile gymjjak-team03 --region ap-northeast-2
```

본인 AWS 계정 및 IAM 사용자 정보가 출력되면 정상이다.

## 3. SSM 관리형 노드 상태 확인

AWS 콘솔에서 아래 경로로 이동한다.

```text
AWS Systems Manager
-> 노드 관리
-> 관리형 노드
```

Backend EC2 인스턴스가 목록에 있고 `Ping status`가 `Online`인지 확인한다.

`Offline` 또는 목록에 없다면 인스턴스 역할의 `AmazonSSMManagedInstanceCore` 연결 여부와 EC2의 인터넷 또는 NAT Gateway 아웃바운드 연결을 확인한다.

## 4. RDS 포트 포워딩 시작

PowerShell에서 아래 명령을 실행한다.

```powershell
aws ssm start-session `
  --target "<BACKEND_EC2_INSTANCE_ID>" `
  --document-name "AWS-StartPortForwardingSessionToRemoteHost" `
  --parameters host=[<RDS_ENDPOINT>],portNumber=[3306],localPortNumber=[13306] `
  --profile gymjjak-team03 `
  --region ap-northeast-2
```

현재 운영 환경 예시:

```powershell
aws ssm start-session `
  --target "i-ec2-id값" `
  --document-name "AWS-StartPortForwardingSessionToRemoteHost" `
  --parameters host=[awsRDS엔드포인트.ap-northeast-2.rds.amazonaws.com],portNumber=[3306],localPortNumber=[13306] `
  --profile gymjjak-team03 `
  --region ap-northeast-2
```

성공 시 아래와 유사한 메시지가 출력된다.

```text
Starting session with SessionId: ...
Port 13306 opened for sessionId ...
Waiting for connections...
```

**이 PowerShell 창은 Workbench 사용 중 계속 열어 둔다.** 창을 닫거나 `Ctrl + C`를 입력하면 포트 포워딩도 종료된다.

## 5. MySQL Workbench 연결 설정

Workbench에서 새 연결을 만들거나 기존 연결을 편집한다.

```text
Connection Method: Standard TCP/IP
Hostname: 127.0.0.1
Port: 13306
Username: RDS 마스터 이름
Password: RDS 관리자 비밀번호
```

> `Standard TCP/IP over SSH`를 선택하면 안 된다. SSM이 이미 로컬 포트와 RDS를 연결하므로 Workbench가 SSH 터널을 추가로 만들 필요가 없다.

`Test Connection`을 눌러 접속을 확인한다.

## 6. 종료 방법

작업이 끝나면 SSM 명령을 실행한 PowerShell 창에서 `Ctrl + C`를 누른다.

```text
PowerShell 종료 또는 Ctrl + C
-> SSM 세션 종료
-> 127.0.0.1:13306 포트 닫힘
```

다음 작업 시에는 4단계 명령을 다시 실행한 뒤 Workbench에 접속한다.

## 7. 문제 해결

### `Invalid JSON` 또는 `Error parsing parameter --parameters`

PowerShell에서 JSON 큰따옴표가 제거되며 발생할 수 있다. JSON 대신 4단계의 shorthand 형식인 아래 구조를 사용한다.

```text
--parameters host=[RDS_ENDPOINT],portNumber=[3306],localPortNumber=[13306]
```

### `TargetNotConnected`

Backend EC2가 SSM 관리형 노드로 연결되지 않은 상태다.

1. Systems Manager 관리형 노드에서 `Online` 상태 확인
2. EC2 인스턴스 역할에 `AmazonSSMManagedInstanceCore` 연결 확인
3. EC2의 SSM 서비스 아웃바운드 연결 확인

### Workbench에서 `Cannot connect to 127.0.0.1:13306`

1. SSM PowerShell 창이 열려 있고 `Waiting for connections...` 상태인지 확인
2. Workbench 연결 방식이 `Standard TCP/IP`인지 확인
3. Hostname이 `127.0.0.1`, Port가 `13306`인지 확인

### SSM 세션은 열리지만 MySQL 연결이 실패함

RDS 보안 그룹을 확인한다.

```text
인바운드 규칙
유형: MySQL/Aurora
포트: 3306
소스: Backend EC2 보안 그룹
```

RDS를 Public access로 전환하거나 `0.0.0.0/0`에 3306을 공개할 필요는 없다.

### 쉽게 정리

- RDS = 잠긴 사무실 안의 금고
- EC2 = 사무실 내부에 있는 직원
- MySQL 3306 포트 = 금고 문
- 기존 SSH 방식 = 외부에서 사무실 출입문(22번)을 열고, 열쇠(PEM)로 직접 들어가는 방식
- SSM 방식 = AWS 경비실을 통해 신원 확인 후, 내부 직원에게 “내 PC와 금고 사이에 임시 통로를 열어줘”라고 요청하는 방식

즉 SSM이 만든 `127.0.0.1:13306`은 내 컴퓨터에서만 잠깐 열리는 개인용 임시 통로.

```text
내 Workbench
→ 내 PC의 임시 문: 127.0.0.1:13306
→ AWS SSM의 인증된 통로
→ Backend EC2
→ RDS 금고 문: 3306
```

전환 이유는 인바운드 규칙을 단순하게 만들고 보안을 높이기 위함

기존 SSH 방식은 팀원마다 학교·집·카페·핫스팟 등 장소가 바뀔 때마다 공인 IP가 달라져서, EC2 보안 그룹의 `22` 포트 허용 목록을 계속 수정해야 해야함. 또한 PEM 키를 팀원 PC에 배포·관리해야 해야함.

SSM 방식에서는 EC2의 `22` 포트를 외부에 열지 않아도 된다. 팀원 PC는 AWS 콘솔/API에 HTTPS `443`으로 세션을 요청하고, AWS가 IAM 권한을 확인한 뒤 세션을 연결. 누가 접속했는지도 AWS 세션 이력으로 확인할 수 있다.

현재 상태를 정리하면:

- RDS는 Private Subnet에 있어 외부에서 직접 접속 불가
- RDS 3306은 Backend EC2 보안 그룹만 허용
- 팀원은 IAM 권한으로 SSM 세션을 생성
- 각자 PC에서 `127.0.0.1:13306` 임시 포트를 열어 Workbench 접속
- EC2의 SSH 22번 포트는 SSM 검증이 끝나면 팀원 접속 목적상 닫아도 됨

즉, “문을 모두에게 열어두는 방식”에서 “AWS 경비실이 권한을 확인한 사람에게만 임시 출입 통로를 제공하는 방식”으로 변경한 것.
