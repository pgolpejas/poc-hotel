# TODO #

* Github actions
  * Fix problem in git step
* Informational BD
  * Aggregated information of reservations and hotels and rooms
  * Store in MongoDB or PostgreSQL with aggregate in jsonb field
  * Consume from Kafka HotelSnapshot, RoomSnapshot and ReservationSnapshot
  * Add redis cache
    * Cache reservations
    * Cache hotels
    * Cache rooms
* Retool/budibase ??
  * Show reservations
* Performance test with k6
  * Github action
* Mutation tests
  * Pitest
  * Github actions
* Grafana metrics:
  * Response time of each endpoint (histogram)
  * Number of requests of each endpoint (counter)
  * Number of errors (loki)
  * Number of reservations created (counter)
  * ...
* Documentation
  * BMPN format ??
  * PlantUML for sequence diagrams ??
* Kubernetes
  * Helm chart
  * Deploy in minikube
  * ArgoCD??
  * AWS/Azure/GCP ??
* Frontend with angular or react
  * Show reservations
  * Create reservations
  * Delete reservations
  * Update reservations

