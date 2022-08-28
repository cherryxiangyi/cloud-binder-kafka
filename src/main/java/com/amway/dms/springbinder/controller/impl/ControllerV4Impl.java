package com.amway.dms.springbinder.controller.impl;

import com.amway.dms.springbinder.controller.ControllerV4;
import com.amway.dms.springbinder.dto.AutoRenewalRequest;
import com.amway.dms.springbinder.model.AccountEntity;
import com.amway.dms.springbinder.model.Event;
import com.amway.dms.springbinder.model.EventInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.OffsetDateTime;

@Log4j2
@Component
@Configuration
public class ControllerV4Impl implements ControllerV4 {

    private ObjectMapper objectMapper;

    @Autowired
    private StreamBridge streamBridge;

    @Value("${mdms.events.topic}")
    private String eventTopic;

    @Autowired
    public ControllerV4Impl(){
        this.objectMapper = new ObjectMapper();//initialize the mapper
        JavaTimeModule module = new JavaTimeModule();
        OffsetDateTimeSerializer serializer = OffsetDateTimeSerializer.INSTANCE;
        module.addSerializer(OffsetDateTime.class, serializer);
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    }

    @Override
    public ResponseEntity<Event> produceEvent(@RequestBody AutoRenewalRequest autoRenewalRequest){
        System.out.println("...xx...got data produceEvent:"+autoRenewalRequest);
        Event event = new Event();

        EventInfo eventInfo = new EventInfo();
        eventInfo.setSourceEventId(autoRenewalRequest.getAffNum() + "-" + autoRenewalRequest.getIboNum());
        eventInfo.setSourceTimestamp(OffsetDateTime.now().toString());
        eventInfo.setAffiliateCode(String.valueOf(autoRenewalRequest.getAffNum()));

        event.setEventInfo(eventInfo);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAbo(autoRenewalRequest.getIboNum());
        accountEntity.setAff(autoRenewalRequest.getAffNum());
        event.setEntity(accountEntity);
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

}
