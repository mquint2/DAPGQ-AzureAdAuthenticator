package co.com.bancodebogota;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "com.bancodebogota.aqsw.logs.kafka.producer", 
	 "com.bancodebogota.aqsw.logs.kafka.configuration",
	"co.com.bancodebogota"})

@SpringBootApplication
public class AuthenticationBDBApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationBDBApplication.class, args);
	}
}