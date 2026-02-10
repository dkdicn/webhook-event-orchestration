$baseUrl = "http://localhost:8080"

# Report data: message -> then callback with summary, category, priority, success
$reports = @(
    @{
        message = "3공구 지하 2층 철근 배근 작업 완료. 배근 간격 및 피복 두께 검측 실시하여 이상 없음 확인. 내일 콘크리트 타설 예정."
        summary = "3공구 지하2층 철근 배근 완료, 검측 이상 없음. 내일 콘크리트 타설 예정."
        category = "공정"
        priority = "MEDIUM"
        success = $true
    },
    @{
        message = "타워크레인 #2 와이어로프 마모율 15% 확인. 안전기준(10%) 초과로 즉시 교체 요청. 교체 완료 전까지 해당 크레인 사용 중지 조치."
        summary = "타워크레인#2 와이어로프 마모 15%로 안전기준 초과. 사용 중지 및 즉시 교체 요청."
        category = "안전"
        priority = "HIGH"
        success = $true
    },
    @{
        message = "2공구 지상 5층 거푸집 조립 중 레미콘 차량 3대 현장 도착 지연(2시간). 협력업체 믹서 배차 문제로 확인. 공정 지연 우려."
        summary = "레미콘 차량 2시간 지연 도착. 협력업체 배차 문제. 공정 지연 우려."
        category = "이슈"
        priority = "HIGH"
        success = $true
    },
    @{
        message = "현장 안전교육 실시. 금일 신규 투입 인력 12명 대상 안전보건교육 2시간 진행 완료. 개인보호구 지급 및 착용 상태 확인."
        summary = "신규 인력 12명 안전보건교육 2시간 완료. 보호구 지급 및 착용 확인."
        category = "안전"
        priority = "LOW"
        success = $true
    },
    @{
        message = "1공구 옹벽 콘크리트 타설 완료(350m3). 슬럼프 및 공기량 시험 3회 실시, 모두 기준치 이내. 양생 관리 시작."
        summary = "1공구 옹벽 콘크리트 350m3 타설 완료. 품질시험 적합. 양생 관리 돌입."
        category = "공정"
        priority = "MEDIUM"
        success = $true
    },
    @{
        message = "4공구 터파기 작업 중 지하 매설물(상수관) 발견. 작업 즉시 중단하고 관할 관청 및 설계팀에 통보. 우회 시공 방안 검토 필요."
        summary = "4공구 터파기 중 상수관 매설물 발견. 작업 중단, 관청 통보 및 우회 시공 검토 중."
        category = "이슈"
        priority = "HIGH"
        success = $true
    },
    @{
        message = "동절기 콘크리트 양생 관리. 야간 최저기온 -5도 예보에 따라 보온양생 매트 추가 설치. 온도 센서 모니터링 중."
        summary = "동절기 양생 관리. 야간 -5도 예보 대비 보온매트 추가 설치 및 온도 모니터링."
        category = "공정"
        priority = "MEDIUM"
        success = $true
    },
    @{
        message = "비계 안전점검 실시. 3공구 외부비계 수직도 확인 및 벽이음 상태 점검. 일부 구간 벽이음 보강 조치 완료."
        summary = "3공구 외부비계 점검 완료. 일부 벽이음 보강 조치."
        category = "안전"
        priority = "MEDIUM"
        success = $true
    },
    @{
        message = "자재 입고 현황. H빔 200톤, 철근 SD400 150톤 현장 반입 완료. 자재 적치장 정리 및 수량 검수 완료."
        summary = "H빔 200톤, 철근 150톤 반입 및 검수 완료."
        category = "기타"
        priority = "LOW"
        success = $true
    },
    @{
        message = "2공구 지상 3층 슬래브 콘크리트 타설 중 펌프카 고장 발생. 예비 펌프카 긴급 투입하여 작업 재개. 약 40분 공정 지연."
        summary = "펌프카 고장으로 40분 공정 지연. 예비 펌프카 투입하여 작업 재개."
        category = "이슈"
        priority = "MEDIUM"
        success = $false
        failReason = "Slack 전송 실패: webhook timeout (30s)"
    },
    @{
        message = "고소작업 안전난간 설치 점검. 5층 이상 작업 구간 전체 안전난간 및 추락방지망 설치 상태 확인. 2개소 미설치 구간 즉시 보완."
        summary = "고소작업 안전난간 점검. 2개소 미설치 구간 즉시 보완 완료."
        category = "안전"
        priority = "HIGH"
        success = $true
    },
    @{
        message = "금일 현장 인력 현황: 총 187명 투입. 철근공 45명, 형틀공 38명, 콘크리트공 25명, 장비 운전원 12명, 기타 67명."
        summary = "금일 총 187명 투입. 철근공 45, 형틀공 38, 콘크리트공 25, 장비운전 12, 기타 67명."
        category = "기타"
        priority = "LOW"
        success = $true
    }
)

$i = 0
foreach ($r in $reports) {
    $i++
    # Submit report
    $body = @{ message = $r.message } | ConvertTo-Json -Compress
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
    $result = Invoke-RestMethod -Uri "$baseUrl/api/reports" -Method POST -ContentType "application/json; charset=utf-8" -Body $bytes
    Write-Host "[$i] Report submitted: id=$($result.reportId)"

    # Send callback
    $cb = @{
        reportId = $result.reportId
        success = $r.success
        summary = $r.summary
        category = $r.category
        priority = $r.priority
        failReason = if ($r.failReason) { $r.failReason } else { $null }
    } | ConvertTo-Json -Compress
    $cbBytes = [System.Text.Encoding]::UTF8.GetBytes($cb)
    Invoke-RestMethod -Uri "$baseUrl/api/reports/callback" -Method POST -ContentType "application/json; charset=utf-8" -Body $cbBytes
    Write-Host "[$i] Callback processed: $($r.category) / $($r.priority) / success=$($r.success)"
}

Write-Host "`nDone! $i reports inserted."
