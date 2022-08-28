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
```json
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

## Take a closer look into this project
### Configuration to bind to Confluent.io
#### application.yml file 
**A) This part nothing special. Just declare the key-value pair properties to be used across the project and file.**

```YAML
mdms:
  events:
    topic: ${TOPIC_AUTORENEWALEVENTCONSUMER:mdms.dev.auto-renewal} #topic in Confluent.io cluster
    api: ${MDMS_EVENTS_API:default} #api that access to your Confluent.io cluster
    secret: ${MDMS_EVENTS_SECRET:default}
    bootstrapServer: ${MDMS_APP_BOOTSTRAP_SERVERS:default}
```
- key-value : `mdms.events.topic=${TOPIC_AUTORENEWALEVENTCONSUMER:mdms.dev.auto-renewal}`
- application trying to get environment variable `${TOPIC_AUTORENEWALEVENTCONSUMER}` , if can't find then will take the value set as `mdms.dev.auto-renewal`
- to access this key just use `${mdms.events.topic}`

**B) Let's take a look for kafka binder configuration**
```yaml
spring:
  application:
    name: ${DMS_APPLICATION_NAME:mdms-auto-renewal}
  profiles:
    active: dev
  cloud:
    function:
      definition: consumeAutoRenewalEvent
    stream:
      instanceCount: ${MDMS_APP_CONSUMER_INSTANCE_COUNT:1}
      instanceIndex: ${MDMS_APP_CONSUMER_INSTANCE_INDEX:0}
      bindings:
        consumeAutoRenewalEvent-in-0:
          destination: ${mdms.events.topic}
          binder: mdmsKafka
          consumer:
            concurrency: ${MDMS_APP_CONSUMER_INSTANCE_CONCURRENCY:1}
          group: ${spring.application.name}-${spring.profiles.active}-BE
        orderBuySupplier-out-0:
          destination: ${mdms.events.topic}
      kafka:
        binder:
          auto-create-topics: false
        bindings:
          consumeAutoRenewalEvent-in-0:
            consumer:
              configuration:
                max.poll.records: 1
      binders:
        mdmsKafka:
          type: kafka
          environment:
            spring:
              cloud:
                stream:
                  kafka:
                    binder:
                      brokers: ${mdms.events.bootstrapServer}
                      configuration:
                        message.max.bytes: 2097152
                        key.deserializer: org.apache.kafka.common.serialization.StringDeserializer
                        value.deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer
                        security.protocol: SASL_SSL
                        sasl.mechanism: PLAIN
                        sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username='${mdms.events.api}' password='${mdms.events.secret}';
                        ssl.endpoint.identification.algorithm: https
                      replicationFactor: 3
```
- In order to bind a channel to Confluent.io topic , we need to have a function/method to be acting as that job 
- Your class/java must have annotation `@Component` and your method/function must have annotation `@Bean`
```java 
@Component
public class AutoRenewalConsumerConfig { 

    @Bean
    public Consumer<Message<byte[]>> consumeAutoRenewalEvent() {  //you notice "consumeAutoRenewalEvent" acting as function linked to your application.yaml file
        return input -> {
            //do something of your message
        }
}
```
- Configure your function/method name in your application.yml file. In this case is `consumeAutoRenewalEvent`
```yaml
  cloud:
    function:
      definition: consumeAutoRenewalEvent
```
- We need to bind it into a channel with standard convention naming . `<function_name>-<in|out>-<index>` , "in" to consume event, "out" to produce event to topic. So, in this case is `consumeAutoRenewalEvent-in-0`
```yaml
      bindings:
        consumeAutoRenewalEvent-in-0:
          destination: ${mdms.events.topic}
          binder: mdmsKafka
          consumer:
            concurrency: ${MDMS_APP_CONSUMER_INSTANCE_CONCURRENCY:1}
          group: ${spring.application.name}-${spring.profiles.active}-BE
```
- **destination** is to the topic in Confluent.io to bind with
> :warning: **"bindings" node must be declared before "kafka" node for lower version of sprintboot,else you might hit topic exception error**
- **binder** you can named it anything except reserved keyword "kafka" , this name will be used to config later below to tell application the Confluent.io cluster info.
- **consumer** : any extra configuration during consume event
- **group** : any name but sepecific here, Confluent.io will automatic create the group for you to be able to let your application to consume back the last event and continue from there.

We now need to configure extra setting for the consume
```yaml
      kafka:
        binder:
          auto-create-topics: false
        bindings:
          consumeAutoRenewalEvent-in-0:
            consumer:
              configuration:
                max.poll.records: 1
```
- For **binder** we configure not to auto create topic `auto-create-topics`, it should only bind to the topic we provided
- Our channel name `consumeAutoRenewalEvent-in-0` to configure consume `max.poll.records`

Now, it is time to tell about Confluent.io cluster info for connection
```yaml
      binders:
        mdmsKafka:
          type: kafka
          environment:
            spring:
              cloud:
                stream:
                  kafka:
                    binder:
                      brokers: ${mdms.events.bootstrapServer}
                      configuration:
                        message.max.bytes: 2097152
                        key.deserializer: org.apache.kafka.common.serialization.StringDeserializer
                        value.deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer
                        security.protocol: SASL_SSL
                        sasl.mechanism: PLAIN
                        sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username='${mdms.events.api}' password='${mdms.events.secret}';
                        ssl.endpoint.identification.algorithm: https
                      replicationFactor: 3
```
- Remember we named it `mdmsKafka` as **binder** in the **bindings** section node. Hence, we now need to declare it here for extra info.
- **type** : the machanism we are using here is **kfaka** . Others like Rabbit
- **brokers** : your Confluent.io cluster bootstrap server url, usually with port 9092. Here we declare to read from top key-value pair setting.
- **value.deserializer** : Confluent.io can accept any form, it doesn't really care your format/type. But, Java does, so we need to provide  deserializer
- **sasl.jaas.config** : inside here you will provide your username and password
- The above configuration you can find more info from Confluent.io https://cloud.spring.io/spring-cloud-stream-binder-kafka/spring-cloud-stream-binder-kafka.html

**Let's setup producer** 
Configure `application.yml` file to bind to same topic with a channel name, this channel name(can be any) but in your class must use the same .
```yaml
      bindings:
        orderBuySupplier-out-0:
          destination: ${mdms.events.topic}
```
In this case, we named the channel **out** as `orderBuySupplier-out-0` and bind to same topic (destination)

In our java class can send the message to this channel
```java
//....
    @Autowired
    private StreamBridge streamBridge;
    
     @Override
    public ResponseEntity<Event> produceEvent(@RequestBody AutoRenewalRequest autoRenewalRequest){
        System.out.println("...xx...got data produceEvent:"+autoRenewalRequest);
        Event event = new Event();
       //...set up your event object
       //...
        try {
            byte[] messageBody = objectMapper.writeValueAsBytes(event);
            MessageHeaderAccessor headerAccessor = new MessageHeaderAccessor();
            headerAccessor.setHeader(KafkaHeaders.TOPIC,eventTopic);

            Message<byte[]> kafkaMessage =
                    MessageBuilder.createMessage(messageBody,headerAccessor.getMessageHeaders());
            streamBridge.send("orderBuySupplier-out-0", kafkaMessage); // we need to configure this channel
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(event);
    }
  
```

