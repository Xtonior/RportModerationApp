#!/bin/sh
sleep 2

FILE="/data/users_redis_data.csv"

if [ -f "$FILE" ]; then
  while IFS=, read -r client_id category has_active
  do
    JSON_VALUE="{\"clientId\":\"$client_id\",\"category\":\"$category\",\"hasActiveRequests\":$has_active}"

    redis-cli -h redis SET "client:$client_id" "$JSON_VALUE"
    echo "Loaded client:$client_id"
  done < "$FILE"
else
  echo "Error: $FILE not found"
fi

echo "Redis pre-fill complete!"