package rocks.basset.brewery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class TsbbSfgBreweryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TsbbSfgBreweryApplication.class, args);
	}

}
