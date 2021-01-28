package com.zhonghui.spreadjs.controller.base;

import com.zhonghui.core.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseController {

	@Autowired
	private Environment env;


	protected Model setModelAndView(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("envProfile", env.getActiveProfiles()[0]);
		return model;
		
	}

	
}
