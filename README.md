# cloud-binder-kafka
Basic SpringBoot project to connect to kafka by using binder

# Prerequisite 
- Ensure you do have a Confluent.io cluster to host the Kafka event (if not, please register FREE account https://www.confluent.io/get-started/ to host it)
- Start with a default (or create a new one) cluster then create a topic named: mdms.dev.auto-renewal
![alt text](https://github.com/cherryxiangyi/cloud-binder-kafka/blob/main/img/confluentBootstrapServer.png?raw=true)
- Remember to create and keep your API key and secret to be used later to configure your project ![alt text](https://github.com/cherryxiangyi/cloud-binder-kafka/blob/main/img/confluentAPIKey.png?raw=true)

## Environment/Tooling
- JDK 11
- gradle 6.7.1
- Intellij IDE
- Any API client tool . E.g : POSTMAN

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

5. It is time to start your project to test it out. Browse to main application class file > click the 'green play' button
![alt text](https://github.com/cherryxiangyi/cloud-binder-kafka/blob/main/img/runSprintBoot.png?raw=true)

You can also open terminal/command prompt to issue command to start your spring boot project. 
> :warning: **In this case make sure you exported your 3 envrionment variables manually before issueing this command**: Be very careful here!
```BASH
./gradlew bootRun
```

6. Open POSTMAN to call API to produce sample event to your topic in Confluent.io
```BASH
Method : POST
URL/Path signature : http://localhost:8080/api/v4/auto-renewal
Header: Content-Type=application/json
Body: raw
```
```JSON
{
  "iboNum": 9987654,
  "affNum": 10
}
```
> :white_check_mark: You should see something like below in your console
```SCRIPT
...xx...got data produceEvent:AutoRenewalRequest(iboNum=9987654, affNum=10)
.....
2022-08-28 17:20:11.575  INFO 20604 --- [container-0-C-1] o.s.i.h.s.MessagingMethodInvokerHelper   : Overriding default instance of MessageHandlerMethodFactory with provided one.
....consume got data ..xx..:GenericMessage [payload=byte[155], headers={skip-type-conversion=false, kafka_offset=2, scst_nativeHeadersPresent=true, kafka_consumer=org.apache.kafka.clients.consumer.KafkaConsumer@2529dcae, deliveryAttempt=1, kafka_timestampType=CREATE_TIME, kafka_receivedPartitionId=4, contentType=application/json, kafka_receivedTopic=mdms.dev.auto-renewal, kafka_receivedTimestamp=1661678411280, kafka_groupId=mdms-auto-renewal-dev-BE, accept=application/json}]
2022-08-28 17:20:11.592  INFO 20604 --- [container-0-C-1] c.a.d.s.c.AutoRenewalConsumerConfig      : Received a real-time message from meme on partition - 4 at offset - 2
....eventDto ..xx..:Event(eventInfo=EventInfo(sourceApplication=null, sourceTimestamp=2022-08-28T17:20:10.444242200+08:00, sourceHost=null, sourceEventId=10-9987654, entityType=null, entityId=null, eventType=null, affiliateCode=10, isoCountryCode=null), entity=AccountEntity(aff=10, abo=9987654))
......xx....done
```


