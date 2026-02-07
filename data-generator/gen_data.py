import uuid
import json
import random
import redis
import csv

def generate_and_sync():
    try:
        r = redis.Redis(host='localhost', port=6379, decode_responses=True)
        r.ping()
    except:
        print("Ошибка подключения к Redis!")
        return

    all_categories = ["BILLING", "TECH_SUPPORT", "ACCOUNT_ISSUE", "DELIVERY"]
    filename_csv = 'data.csv'
    count = 50

    with open(filename_csv, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['CLIENT_ID', 'CATEGORY', 'EXPECTED_RESULT'])

        for i in range(count):
            client_id = str(uuid.uuid4())
            
            active_cat = random.choice(all_categories)
            
            client_data = {
                "clientId": client_id,
                "activeCategories": [active_cat]
            }
            r.set(f"client:{client_id}", json.dumps(client_data))

            if i % 2 == 0:
                writer.writerow([client_id, active_cat, 'SHOULD_BE_BLOCKED'])
            else:
                other_cats = [c for c in all_categories if c != active_cat]
                writer.writerow([client_id, random.choice(other_cats), 'SHOULD_PASS'])

    print(f"Данные созданы и загружены")

if __name__ == "__main__":
    generate_and_sync()