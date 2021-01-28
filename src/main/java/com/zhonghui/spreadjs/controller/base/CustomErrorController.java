package com.zhonghui.spreadjs.controller.base;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import springfox.documentation.annotations.ApiIgnore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * @author xwb
 */
@Controller
@RequestMapping("/error")
@ApiIgnore
public class CustomErrorController extends BaseController   implements ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;




	private Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
		return errorAttributes.getErrorAttributes(request, includeStackTrace);
	}

	@RequestMapping("")
	public String handleError(Model model, WebRequest request, HttpServletResponse response){
		//获取statusCode:401,404,500
		int code = response.getStatus();
		String XMLHttpRequest = request.getHeader("x-requested-with");
		if ("XMLHttpRequest".equals(XMLHttpRequest)) {
			return "/error/ajax/"+code;
		}
		return "/error/" + code;
	}


	/**
	 * 错误页面 400 404 500
	 * @param model
	 * @param code
	 * @return
	 */
	@ApiIgnore
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	public String error(WebRequest request, HttpServletRequest req, HttpServletResponse response,
			Model model, @PathVariable("code") int code) {
		ErrorBean error=new ErrorBean(code, getErrorAttributes(request, true));
		model.addAttribute("error",error);
		this.setModelAndView(req, response, model);
		return "page/error_" + code;
	}

	@Override
	public String getErrorPath() {
		return null;
	}


	private class ErrorBean {

		public Integer status;
		public String error;
		public String message;
		public String timeStamp;
		public String trace;

		public ErrorBean(int status, Map<String, Object> errorAttributes) {
			this.status = status;
			this.error = (String) errorAttributes.get("error");
			this.message = (String) errorAttributes.get("message");
			this.timeStamp = errorAttributes.get("timestamp").toString();
			this.trace = (String) errorAttributes.get("trace");
		}


	}

}