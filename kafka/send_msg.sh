#!/bin/bash

TOPIC="topic-1"
BROKER="localhost:9092"
CONTAINER="kafka-container"

EVENT_ID=$(cat /proc/sys/kernel/random/uuid)
CLIENT_ID="00000000-0000-0000-0000-000000000000"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

JSON_PAYLOAD="{\"eventId\":\"$EVENT_ID\",\"clientId\":\"$CLIENT_ID\",\"category\":\"TEST\",\"text\":\"Sample report text\",\"timestamp\":\"$TIMESTAMP\"}"

echo "Sending to topic $TOPIC..."
echo "$JSON_PAYLOAD" | docker exec -i $CONTAINER /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server $BROKER \
  --topic $TOPIC

echo "Sent: $JSON_PAYLOAD"