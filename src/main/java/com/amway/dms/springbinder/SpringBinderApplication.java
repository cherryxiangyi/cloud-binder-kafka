package com.amway.dms.springbinder;

import com.amway.dms.springbinder.dto.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SpringBootApplication
@EnableScheduling
public class SpringBinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBinderApplication.class, args);
	}

	@Bean
	public Consumer<Message> consumer() {
		return message -> System.out.println("received " + message);
	}

	@Bean
	public Supplier<Message> producer() {
		return () -> new Message(" jack from Streams");
	}
}
