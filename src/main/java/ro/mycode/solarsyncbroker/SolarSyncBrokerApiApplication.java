package ro.mycode.solarsyncbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SolarSyncBrokerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolarSyncBrokerApiApplication.class, args);
	}

}
