package com.zhonghui.spreadjs.controller;



import com.zhonghui.spreadjs.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * @author xwb
 *
 */
@Controller
@RequestMapping("")
public class PageController extends BaseController {


    @RequestMapping(value = {"", "/","/index" },method = RequestMethod.GET)
    @ResponseBody
    public String info() {
        return "index";
    }

    @RequestMapping(value = {"/doc/{docId}/{year}" },method = RequestMethod.GET)
    public String online(@PathVariable int year, @PathVariable String docId,Model model) {
        model.addAttribute("year",year);
        model.addAttribute("docId",docId);
        return "page/index";
    }


    @RequestMapping(value = { "/browser/support" },method = RequestMethod.GET)
    public String support() {
        return "page/browser";
    }
}
