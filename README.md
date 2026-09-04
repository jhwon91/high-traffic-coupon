./gradlew jibDockerBuild

./gradlew jibDockerBuild && docker compose up -d --force-recreate coupon-service