#!/usr/bin/env bash
# issue_burst 시나리오 검증. Worker 드레인을 폴링으로 기다린 뒤 결과 표 출력.
#
# 사용:  COUPON_ID=1 scripts/load/part-3/verify_burst.sh
# 환경:  TIMEOUT_SECONDS (기본 60)  STABLE_SECONDS (기본 3)

set -euo pipefail
COUPON_ID="${COUPON_ID:-1}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-60}"
STABLE_SECONDS="${STABLE_SECONDS:-3}"
cd "$(dirname "$0")/../../.."

ROW_COUNT_SQL="SELECT COUNT(*) FROM issuance WHERE coupon_id = $COUPON_ID"
row_count() {
  docker compose exec -T -e PGPASSWORD=coupon postgres \
    psql -U coupon -d coupon -tA -c "$ROW_COUNT_SQL"
}

printf '\033[1;36m===== Worker 드레인 대기 (최대 %ss, %ss 안정 시 확정) =====\033[0m\n' \
  "$TIMEOUT_SECONDS" "$STABLE_SECONDS"

prev="$(row_count)"
stable=0
elapsed=0
while (( elapsed < TIMEOUT_SECONDS )); do
  sleep 1
  current="$(row_count)"
  if [[ "$current" == "$prev" ]]; then
    stable=$((stable + 1))
    if (( stable >= STABLE_SECONDS )); then break; fi
  else
    stable=0
    printf '  +%ss issuance rows: %s\n' "$elapsed" "$current"
  fi
  prev="$current"
  elapsed=$((elapsed + 1))
done
printf '  드레인 완료. issuance rows = %s\n\n' "$prev"

# Postgres 는 같은 SELECT 안에서 별칭을 참조할 수 없으므로 파생 테이블로 한 번 감싼다.
SQL="
  SELECT
    v.issued_quantity, v.total_quantity, v.issuance_rows,
    CASE WHEN v.issuance_rows  > v.total_quantity  THEN 'FAIL' ELSE 'OK'   END AS over_issuance,
    CASE WHEN v.issued_quantity = v.issuance_rows  THEN 'OK'   ELSE 'FAIL' END AS count_match
  FROM (
    SELECT
      (SELECT issued_quantity FROM coupon   WHERE id        = $COUPON_ID) AS issued_quantity,
      (SELECT total_quantity  FROM coupon   WHERE id        = $COUPON_ID) AS total_quantity,
      (SELECT COUNT(*)        FROM issuance WHERE coupon_id = $COUPON_ID) AS issuance_rows
  ) v;
"
docker compose exec -T -e PGPASSWORD=coupon postgres psql -U coupon -d coupon -c "$SQL"

read -r field total rows over count < <(docker compose exec -T -e PGPASSWORD=coupon postgres \
  psql -U coupon -d coupon -tA -F' ' -c "$SQL")

if [[ "$over" == "FAIL" ]]; then
  printf '\033[1;31mFAIL: 과발급 (실제발급 %s > 재고 %s)\033[0m\n' "$rows" "$total"
elif [[ "$count" == "FAIL" ]]; then
  printf '\033[1;33mWARN: 카운터 불일치 (issued_quantity %s, 실제발급 %s)\033[0m\n' "$field" "$rows"
  printf '  → Worker 가 issued_quantity 까지 갱신하는지 확인. v2 에서는 같이 가야 함.\n'
else
  printf '\033[1;32mPASS: issuance_rows %s, issued_quantity %s, total %s\033[0m\n' \
    "$rows" "$field" "$total"
fi
