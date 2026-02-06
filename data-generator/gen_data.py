import uuid
import csv
import json
import random

def generate_test_data(count=100):
    categories = ["VIP", "REGULAR", "GUEST", "ADMIN"]
    filename = 'users.csv'
    
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        for _ in range(count):
            client_id = str(uuid.uuid4())
            category = random.choice(categories)
            has_active_requests = "true" if random.random() < 0.2 else "false"
            
            writer.writerow([client_id, category, has_active_requests])
    
    print(f"Файл {filename} создан. Сгенерировано {count} пользователей.")

if __name__ == "__main__":
    generate_test_data(50)