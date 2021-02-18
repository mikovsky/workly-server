docker run \
  --name workly_local_db \
  -p 5432:5432 \
  -e POSTGRES_DB=workly_db \
  -e POSTGRES_USER=workly_user \
  -e POSTGRES_PASSWORD=workly_pass \
  -d \
  postgres:13-alpine
