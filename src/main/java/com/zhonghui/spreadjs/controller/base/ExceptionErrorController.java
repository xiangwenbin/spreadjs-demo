package com.zhonghui.spreadjs.controller.base;

import com.zhonghui.spreadjs.exception.CustomException;
import com.zhonghui.core.model.vo.ResultVo;
import com.zhonghui.core.util.LoggerUtils;
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
	public ResultVo<String> defaultErrorHandler(HttpServletRequest req, Exception e) {

        LoggerUtils.error(e);
		return ResultVo.fail(500,e.getMessage(),String.class);

	}



}