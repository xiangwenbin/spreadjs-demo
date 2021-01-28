package com.zhonghui.spreadjs.controller.base;

import com.zhonghui.spreadjs.exception.CustomException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;


/**
 * @author xwb
 */
@ApiIgnore
@ControllerAdvice
public class ExceptionErrorController extends BaseController {


	/**
	 *
	 * 全局异常处理请求
	 * @param req HttpServletRequest
	 * @param e Exception
	 * @return
	 */
	@ApiIgnore
	@ExceptionHandler(value = CustomException.class)
	@ResponseBody
	public String defaultErrorHandler(HttpServletRequest req, Exception e) {

		return e.getMessage();

	}



}