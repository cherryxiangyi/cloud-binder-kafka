# cloud-binder-kafka
Basic SpringBoot project to connect to kafka by using binder

# Prerequisite 
- Ensure you do have a Confluent.io cluster to host the Kafka event (if not, please register FREE account https://www.confluent.io/get-started/ to host it)
- Start with a default (or create a new one) cluster then create a topic named: mdms.dev.auto-renewal

## Environment/Tooling
- JDK 11
- gradle 6.7.1
- Intellij IDE

## Try this project
1. clone this project to your local 
```BASH
git clone https://github.com/cherryxiangyi/cloud-binder-kafka
```
2. Open this project into your Intellij IDE
3. Browse to the main application java > click the "green play" button > edit configuration
![alt text](https://github.com/cherryxiangyi/cloud-binder-kafka/blob/main/img/startSpringBoot.png?raw=true)
4. Ensure to add the following environment variables to connect to your Confluent.io cluster
- MDMS_APP_BOOTSTRAP_SERVERS = <your_confluent_cluster_url>:9092
- MDMS_EVENTS_API = <api_key_to_connect_to_your_confluent_cluster>
- MDMS_EVENTS_SECRET = <secret_key_to_connect_to_your_confluent_cluster>
![alt text](https://github.com/cherryxiangyi/cloud-binder-kafka/blob/main/img/envVariables.png?raw=true)
