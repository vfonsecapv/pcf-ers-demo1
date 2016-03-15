package io.pivotal.pcf.sme.ers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;

/**
 * PcfErsDemo1Application
 *
 * I just want to highlight the RibbonClient configuration (used by feign
 * clients). We would typically use Eureka (service registry), but for
 * simplicity we decided to support external configuration (in addition to the properties files).
 *
 * @TODO: - Git versioning (maven plugin) - concourse CI/CD -
 *
 * @author mborges
 *
 */
@SpringBootApplication
@Configuration
@EnableDiscoveryClient
@EnableHystrix
public class PcfErsDemo1Application {

	public static void main(String[] args) {
		SpringApplication.run(PcfErsDemo1Application.class, args);
	}
}
