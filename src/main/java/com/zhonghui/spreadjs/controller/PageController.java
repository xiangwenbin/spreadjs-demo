package com.zhonghui.spreadjs.controller;



import com.zhonghui.spreadjs.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 
 * @author xwb
 *
 */
@Controller
@RequestMapping("")
public class PageController extends BaseController {


    @RequestMapping(value = {"/index/{docId}/{year}" },method = RequestMethod.GET)
    public String online(@PathVariable int year, @PathVariable String docId,Model model) {
        model.addAttribute("year",year);
        model.addAttribute("docId",docId);
        return "page/index";
    }


}
