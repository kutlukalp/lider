package tr.org.liderahenk.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LiderRequestManager {
	
	
	
	@RequestMapping(value="/lider/rest/echo/{value}",method={RequestMethod.GET})
	@ResponseBody
	public String echo(@PathVariable String value){
		
		return value;
	}

}
