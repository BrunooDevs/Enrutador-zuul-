package com.formacionbdi.springboot.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/*cuando es un componente y ademas se hereda de de zull automaticamente spring lo registra como un filtro
 * de zuul*/

@Component
public class PostiempoTranscurridoFilter extends ZuulFilter{
	
	
	
	private static Logger log = LoggerFactory.getLogger(PostiempoTranscurridoFilter.class);
	

	/*este metodo se utiliza para validar si vamos a ejecutar o no el filtro, si retorna true ejecuta
	 * el metodo public Object run()*/
	
	@Override
	public boolean shouldFilter() {
		
		return true;
	}

	/*Object run(): en este metodo se resuelve la logica de nuestro filtro, */
	
	@Override
	public Object run() throws ZuulException {
		
		/*pasamos datos al request, por lo tanto debemos obtener el objeto http request*/
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		log.info("entrando a post");
		
		/*obtenemos el parametro envia en la clase PretiempoTrancurridoFilter desde el request
		 * se guarda de tipo object y debemos de realizar un cast*/
		Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
		Long tiempoFinal = System.currentTimeMillis();
		
		Long tiempoTranscurrido = tiempoFinal - tiempoInicio;
		
		log.info(String.format("Tiempo trancurrido en segundos %s seg",tiempoTranscurrido.doubleValue()/1000.00));
		log.info(String.format("Tiempo trancurrido en milisegundos %s ms",tiempoTranscurrido));
		
		
		
		return null;
	}

	
	
	/*definimos el tipo de filtro a utlizar existenhay 3, "pre" significa antes de que se resulva la ruta y 
	 * antes de la comunicacion con el microservicio, "post" y el "route" donde se recuelve la ruta*/
	
	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "post";
	}


	
	@Override
	public int filterOrder() {
		
		return 1;
	}

}
