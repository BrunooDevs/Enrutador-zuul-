package com.formacionbdi.springboot.app.zuul.oauth2;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpMethod;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.netflix.config.sources.URLConfigurationSource;

/*habilitamos la configuracion del servidor de recursos*/

/*@RefreshScope: nos permite actualizar los componentes como contorladores, clases que le estamos injectando
 * con la anotacion @Value tambien el Environment cuando hacemos un cambio desde el servidor de configuracion o desde
 * el repositorio, actualiza se refresca el contexto vuelve a injectar 
 * y se vuelve a inicializar el componente con los cambios reflejados en tiempo real y sin tener que reiniciar
 * la aplicacion y todo esto mediante una ruta url un edpoint de actuator
 * 
 * NOTA: SE DEBE AGREGAR LA DEPENCIA Ops/Spring boot actuator */

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	/*NOTA: PODEMOS OCUPAR EL OBJETO Environment env para poner el nombre de la propiedad de la configuracion que tenemos
	 * en el repositorio git como lo isimos en el microservicio servicio-oauth o tambien podemos ocupar la anotacion
	 * @Value("${config.security.oauth.jwt.key}")  */
	@Value("${config.security.oauth.jwt.key}") 
	private String jwtKey;
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		
		resources.tokenStore(TokenStore() );
	}

	/*configure(HttpSecurity http): este metodo se utiliza para proteger nuestro endpoint*/
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		/*antMatchers: le damos permisos a todos a acceder a nuestro endpoint
		 antMatchers("/api/security/oauth/token": esta es la ruta para generar el token en la cual nos autenticamos
		 permitAll(): indicamos que todo el mundo se pueda autenticar cualquier usuario pueda iniciar sesion
		 */
		/*NOTA: NUESTRAS RUTAS ESPECIFICAS DEBEN DE IR AL PRINCIPIO POR EJEMPLO LA RUTA /api/security/oauth/** 
		 * y las genericas se configuran al final*/
		http.authorizeRequests().antMatchers("/api/security/oauth/**").permitAll()
		
		/*(HttpMethod.GET indicamos que la ruta /api/productos/listar  , "/api/items/listar" y 
		 * "/api/usuarios/usuarios  solo se pueda usar el verbo GET
		 * permitAll(): indicamos que las rutas sean publicas todo el mundo puede consumirlo*/
		.antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios").permitAll()
	
		/*podemos utilizar hasRole() para indicar a un rol determinado, la diferencia con hasAnyRole() 
		 * podemos colocar varios roles separados por comas es decir que los usuarios pueden acceder a estas
		 * rutas cuando tengan un determinado rol asignados, NOTA EN EL METODO .hasAnyRole("ADMIN", "USER")
		 * SOLO SE PONE ADMIN, USER NO SE PONE EL PREFIJO GUION BAJO ADMIN_ YA QUE SE LE AGREGA AUTOMATICAMENTE
		 * */
		
	
		.antMatchers(HttpMethod.GET, "/api/productos/ver/{id}",
				"/api/items/ver/{id}/cantidad/{cantidad}",
				"/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
		
		/*solo el admin podra crear nuevos usuarios estas rutas son para crear*/
		/*simlñificamos las rutas para los administradores de la siguiente forma en una sola linea
		  .antMatchers("/api/productos/**","/api/items/**","/api/usuarios/**").hasRole("ADMIN")
		  con esto estamos diciendo que vamos a implementar el crud para los administradores cualquier
		  metodo de peticion y con cualquier ruta.
		 */
		
		
		 .antMatchers("/api/productos/**","/api/items/**","/api/usuarios/**").hasRole("ADMIN")
		
		/*.anyRequest().authenticated(): cualquier ruta que no se aya configurado en las reglas la 
		 * configuramos para que requiera autenticacino
		 * */
		
		.anyRequest().authenticated()
		
		/*NOTA: SI NO AGREGAMOS EL VERBO AUTOMATICAMENTE SE PUEDE UTILIZAR POST,PUT,DELETE,GET quedara
		 * de forma generica
		 
		 *NOTA 2: SIEMPRE NUESTRAS RUTAS ESPECIFICAS DEBEN DE IR AL PRINCIPIO, Y DESPUES AL FINAL TODAS LA REGLAS Y 
		 *RUTAS QUE NO EMOS CONFIGURADO QUE NO EMOS ESPECIFICADO SE VAN A CONFIGURAR DE FORMA AUTOMATICA EN LAS REGLAS
		 *QUE SON MAS GENERICAS COMO EL CRUD PARA EL ADMIN*/
		
		/*CORS: es un mecanismo que utliza las cabeceras http para permitir que una aplicacion cliente que reside que 
		 * esta en otro dominio o servidor distinto al backend tenga los permisos para acceder a lso recursos del 
		 * backend recursos protegidos con spring security
		 * 
		 * esta configuracion es opcional solomente si nuestras aplicaciones cliente residen en otro dominio para el
		 * intercambio de recursos de origen cruzado tipicamente un cliente con angular con jquery o con reac que
		 * estan en un dominio distinto se quieren conectar a nuestro backend con nuestras APIrest*/
		
		.and().cors().configurationSource(corsConfigurationSource() )
		
		
		;
	}
	
	
	
	
	
	
	/*NOTA: ESTOS DOS METODOS el  AccessTokenConverter() y TokenStore()
	 * SON LOS MISMO QUE TENEMOS EN EL MICROSERVICIO usuarios-oaut2 en la clase 
	  AuthorizationServerConfig ya que el token debe de ser identico identica configuracion que en el 
	  servidor donde se crea y se firma el token que es en el servidor de autenticacion (mciroservicio
	  usuarios-oaut2) de echo en estos dos metodos se pone la llave secreta que es algun_codigo_secreto" la
	  cual se firma el token en el microservicio usuarios-oauth*/
	
	/*JWTAccessTokenConverter es del tipo concreto y no AccessTokenConverter*/
	


	@Bean
	public JwtAccessTokenConverter AccessTokenConverter() {
		
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		/*tokenConverter.setSigningKey
		 * agregamos un codigo secreto para firmar el token tiene que ser unico despues este mismo lo vamos
		 * a utilizar en el servidor de recursos para validar el token que sea el correcto con la misma firma 
		 * y asi dar accesoa los clientes */
		
		/*convertimos el jwtKey primero a base64 (la llave se validara en base64)
		 * setSigningKey recibe un string y no bytes, por eso invocamos el metodo encodeToString, y asu vez el metodo
		 *encodeToString recibe bytes, entonces por eso convertimos  env.getProperty("config.security.oauth.jwt.key").getBytes() 
		 *a bytes*/
		tokenConverter.setSigningKey(Base64.getEncoder().encodeToString(jwtKey.getBytes()));
		
		return tokenConverter;
	}
	
	
	@Bean
	public JwtTokenStore TokenStore() {
	
		/*JwtTokenStore(): recibe por argumento el tokenConverter del metodo AccessTokenConverter()
		  el TokenStore para poder crear el token y almacenarlo necesitamos el componente que se encarga
		  de convertir el token con todos los datos con toda la informacion como puede ser
		  username, roles y con cualquier informacion que queramos almacenar*/
		return new JwtTokenStore( AccessTokenConverter() );
	}
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration corsConfig = new CorsConfiguration();
		
		/*configuramos nuestras aplicaciones cliente los origenes por ejemplo si ubicacion como el dominio con el 
		 * puerto
		 * addAllowedOrigin: agregamos los origenes su ubicacion fisicas podemos agregar localhost:8080 o agregar
		 * un patron con asterisco lo cual indica que es de forma generica y agregara de forma automatica a cualquier
		 * origen si quisieramos agregar todos en una lista seria de la siguiente manera
		 * corsConfig.setAllowedOrigins(Arrays.asList("localhost:4200","localhost:8080"));
		 * */
		 corsConfig.setAllowedOrigins(Arrays.asList("*"));
		 
		 /*configuramos los permisos a los verbos http
		  * NOTA: ES IMPORTANTE AGREGAR OPTIONS YA QUE OAUTH2 LO UTILIZA*/
		 corsConfig.setAllowedMethods(Arrays.asList("POST","GET","DELETE","PUT","OPTIONS"));
		 
		 corsConfig.setAllowCredentials(true);
		 
		 corsConfig.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
		
		 
		 /*UrlBasedCorsConfigurationSource source: pasamos esta configuracion del corsConfig a nuestras endpoints */
		 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		 
		 /*pasamos la ruta hacia nuestros endpoints /** indica que se aplicara a todas nuestras rutas*/
		 source.registerCorsConfiguration("/**", corsConfig);
		 
		return source;
	}
	
	/*FilterRegistrationBean<CorsFilter> corsFilter()registramos un filtro de cors para que no solamente quede 
	 * configurado en spring security , sino que quede  configurado a nivel global en un filtro de spring a toda 
	  nuestra aplicacion en general*/
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		
		/*pasamos al metodo new CorsFilter() pasamos la configuracion de corsConfigurationSource() */
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter( corsConfigurationSource() ));
		
		/*le damos una prioridad alta con setOrder*/
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		/*retorname el bean encargado de registrar el CorsFilter*/
		return bean;
	}
	
	

}