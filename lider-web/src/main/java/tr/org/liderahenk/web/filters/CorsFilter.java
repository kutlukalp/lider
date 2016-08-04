package tr.org.liderahenk.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
		((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
//		((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers", "Authorization");
		((HttpServletResponse) response).addHeader("Access-Control-Max-Age", "1800");
//		((HttpServletResponse) response).addHeader("Access-Control-Allow-Credentials", "true");
		filterChain.doFilter(request, response);
	}

}
