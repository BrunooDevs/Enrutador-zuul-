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
public class PretiempoTranscurridoFilter extends ZuulFilter{
	
	
	
	private static Logger log = LoggerFactory.getLogger(PretiempoTranscurridoFilter.class);
	

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
		
		log.info(String.format("%s request enrutado %s", request.getMethod(),request.getRequestURL().toString()));
		
		/*asignamos un valor al request*/
		Long tiempoInicio = System.currentTimeMillis();
		request.setAttribute("tiempoInicio", tiempoInicio);
		
		return null;
	}

	
	
	/*definimos el tipo de filtro a utlizar, hay 3, pre significa antes de que se resulva la ruta y 
	 * antes de la comunicacion con el microservicio, post y el route donde se recuelve la ruta*/
	
	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}


	
	@Override
	public int filterOrder() {
		
		return 1;
	}

}
