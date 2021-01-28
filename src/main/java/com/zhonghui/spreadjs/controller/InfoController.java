package com.zhonghui.spreadjs.controller;



import com.zhonghui.spreadjs.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * @author xwb
 *
 */
@Controller
@RequestMapping("/info")
public class InfoController extends BaseController {


    @RequestMapping(value = { "" },method = RequestMethod.GET)
    @ResponseBody
    public String info() {
        return "{'status':'UP'}";
    }

	
}
