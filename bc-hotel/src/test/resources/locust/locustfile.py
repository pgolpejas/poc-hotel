from random import randint

from locust import HttpUser, task

######################################################################################
# Manual installation: https://docs.locust.io/en/stable/installation.html
# Run with: locust -H http://localhost:8080
# if locust command not found: add locust installation path to PATH environment
######################################################################################

######################################################################################
# Running in Docker
# docker run -p 8089:8089 -v $PWD:/mnt/locust locustio/locust -f /mnt/locust/locustfile.py
# if you need another instance to test on another port
# docker run -p 8090:8089 -v $PWD:/mnt/locust locustio/locust -f /mnt/locust/locustfile.py
######################################################################################

# Open: http://localhost:8089/ or http://localhost:8090/ (if you have two instances)



headers = {
    "Accept": "application/json",
    "Content-Type": "application/json",
}

search_json = {
              "hotelId": "1234Abd",
              "checkIn": "01/07/2024",
              "checkOut": "30/07/2024",
              "ages": [
                1,
                4,
                34,
                35
              ]
            }
search_id = '1234Abd-20230701-20230730-1-4-34-35'

class QuickStartUser(HttpUser):

    @task(1)
    def launch_search(self):
        response = self.client.post("/hotel-availability/1.0/search", json = search_json)
        print(response.text)

    @task(2)
    def launch_count(self):
        with self.client.get("/hotel-availability/1.0/count/{}".format(search_id), catch_response=True) as response:

            if response.status_code == 200:
                print(response.text)
                response.success()
            else:
                response.failure(f'{search_id} not found ')





