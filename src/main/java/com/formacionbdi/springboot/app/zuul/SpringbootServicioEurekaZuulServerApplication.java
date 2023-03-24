package com.formacionbdi.springboot.app.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/*	@EnableEurekaClient: habilitamos de forma explicita eurekaclient, no es obligatoria ya que tenemos la
dependencia en el pom de spring-cloud-starter-netflix-eureka-client en este caso si la pusimos ne el 
microservicio servicios-productos no la pusimos

@EnableZuulProxy: habilitamos zuul

/NOTA IMPORTANTE: ESTE PROYECTO SERA UTLIZADO COMO UN SERVIDOR DE RECURSOS y AOUT2 ESTA BASADO EN EL API
 * SERVLET AL IGUAL QUE ZUUL , POR LO TANTO NO PUEDE IMPLEMENTAR CON API GATEWAY DE SPRING YA QUE TRABAJA
 * CON PROGRAMACION REACTIVA
*/

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class SpringbootServicioEurekaZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioEurekaZuulServerApplication.class, args);
	}

}
