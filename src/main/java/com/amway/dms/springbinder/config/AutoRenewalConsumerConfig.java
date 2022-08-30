package com.amway.dms.springbinder.config;

import com.amway.dms.springbinder.model.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

@Log4j2
@Component
public class AutoRenewalConsumerConfig {
    ObjectMapper objectMapper;//need this to deserializer the kafka event during consume

    @Autowired
    public AutoRenewalConsumerConfig(){
        this.objectMapper = new ObjectMapper();//initialize the mapper
        JavaTimeModule module = new JavaTimeModule();
        OffsetDateTimeSerializer serializer = OffsetDateTimeSerializer.INSTANCE;
        module.addSerializer(OffsetDateTime.class, serializer);
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    }

    //@Bean
    public Consumer<Message<byte[]>> consumeAutoRenewalEvent() {  //you notice "orderConsume" acting as function linked to your application.yaml file
        return input -> {
            System.out.println("....consume got data ..xx..:"+ input);
            log.info("Received a real-time message from meme on partition - {} at offset - {}",
                    input.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID), input.getHeaders().get(KafkaHeaders.OFFSET));

            try {
                Event eventDto = objectMapper.readValue(input.getPayload(), Event.class);
                System.out.println("....eventDto ..xx..:"+ eventDto);
            } catch (JsonProcessingException jpe) {
                log.error("Unable to create event with key - {} at partition - {} offset- {}",
                        input.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY),
                        input.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID),
                        input.getHeaders().get(KafkaHeaders.OFFSET));
            } catch (IOException ie) {
                log.error("Unable to parse event with key - {} at partition - {} offset- {}",
                        input.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY),
                        input.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID),
                        input.getHeaders().get(KafkaHeaders.OFFSET));
            }
            System.out.println("......xx....done");
        };
    }
}
