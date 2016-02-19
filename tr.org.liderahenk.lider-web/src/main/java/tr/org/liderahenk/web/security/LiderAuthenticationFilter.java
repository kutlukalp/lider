package tr.org.liderahenk.web.security;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tr.org.liderahenk.lider.core.api.enums.CrudType;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;

/**
 * Main filter class which is used to authenticate requests.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LiderAuthenticationFilter extends AuthenticatingFilter {

	private static Logger log = LoggerFactory.getLogger(LiderAuthenticationFilter.class);

	@Autowired
	private IOperationLogService logService;

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		log.debug("parsing token from request header");
		HttpServletRequest req = (HttpServletRequest) request;

		String user = req.getHeader("username");
		String pwd = req.getHeader("password");

		log.debug("creating usernamepassword token for user: {}, pwd: {}", user, "*****");
		return new UsernamePasswordToken(user, pwd);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		log.debug("will try to authenticate now...");
		return executeLogin(request, response);
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		HttpServletRequest req = (HttpServletRequest) request;
		String user = req.getHeader("username");
		logService.createLog(new Date(), user, null, null, "Unauthorized access request", getHost(request), "HTTP 401",
				"Unauthorized access request: " + req.getRequestURI(), CrudType.Login, user);

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false;
	}

}
