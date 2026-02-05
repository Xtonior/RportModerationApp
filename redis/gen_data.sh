#!/bin/sh

REDIS_HOST="redis"
REDIS_PORT="6379"
RECORD_COUNT=50

echo "Waiting for Redis at $REDIS_HOST:$REDIS_PORT..."

while ! redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping | grep -q PONG; do
  echo "Redis is not ready yet... sleeping"
  sleep 1
done

echo "Redis is ready. Starting injection..."

FIXED_ID="00000000-0000-0000-0000-000000000000"
FIXED_JSON="{\"clientId\":\"$FIXED_ID\",\"hasActiveRequests\":true}"

echo "Injecting fixed ID: $FIXED_ID"
redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" SET "client:$FIXED_ID" "$FIXED_JSON"

i=1
while [ $i -le "$RECORD_COUNT" ]
do
    CLIENT_ID="user-$(date +%s)-$i"
    
    if [ $((i % 2)) -eq 0 ]; then 
        HAS_REQ="true"
    else 
        HAS_REQ="false"
    fi
    
    JSON="{\"clientId\":\"$CLIENT_ID\",\"hasActiveRequests\":$HAS_REQ}"
    
    redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" SET "client:$CLIENT_ID" "$JSON"
    
    i=$((i + 1))
done

echo "Done. Injected $RECORD_COUNT random records and 1 fixed record."