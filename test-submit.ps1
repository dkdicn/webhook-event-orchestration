$body = @{ message = "5공구 지상 8층 철골 조립 완료. 볼트 체결 토크 검사 실시, 전량 합격. 내일 데크플레이트 설치 예정." } | ConvertTo-Json -Compress
$bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
$r = Invoke-RestMethod -Uri "http://localhost:8080/api/reports" -Method POST -ContentType "application/json; charset=utf-8" -Body $bytes
$r | ConvertTo-Json
