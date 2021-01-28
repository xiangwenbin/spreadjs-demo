package com.zhonghui.spreadjs.controller;



import com.grapecity.documents.excel.Workbook;
import com.zhonghui.spreadjs.controller.base.BaseController;
import com.zhonghui.spreadjs.vo.BasicInfoVo;
import com.zhonghui.spreadjs.vo.FormulaTemplateVo;
import com.zhonghui.spreadjs.vo.ProjectVo;
import org.apache.tools.ant.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grapecity.documents.excel.drawing.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author xwb
 *
 */
@Controller
@RequestMapping("/doc")
public class DocController extends BaseController {
    String[] colArray={
            "A","B","C","D","E","F","G","H","I","J","K","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
            "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
    };

    @Value("classpath:excel/spreadjs-demo.xlsx")
    Resource resource;

    @Value("${temp-dir}")
    String tempDir;

    @RequestMapping(value = { "/{docId}/{year2}" },method = RequestMethod.GET)
    @ResponseBody
    public String createDocByTemplate(@PathVariable int year2,@PathVariable String docId) throws IOException {
        int year=5;
        int startYear=2016;
        Workbook workbook = new Workbook();
        workbook.open(resource.getInputStream());
        //基础信息
        BasicInfoVo basicInfo= new BasicInfoVo();
        basicInfo.setCompany("中汇会计师事务所");
        basicInfo.setEditor("项文斌");
        basicInfo.setReviewer("王晶");
        basicInfo.setDate("2020-01-01");

        //数据校验
        List<FormulaTemplateVo> dataVal1List=new ArrayList(0);
        String dataVal1FormulaTemplate="#IF({0}12-利润表!{1}10<>0,\"不符,\"&{0}12-利润表!{1}10,\"与报表相符\")";
        for(int i=0,m=2,n=3;i<year;i++){
            FormulaTemplateVo f=new FormulaTemplateVo();
            f.setValue(MessageFormat.format(dataVal1FormulaTemplate, colArray[m+i], colArray[n+i]));
            dataVal1List.add(f);
        }

        //表格
        List<ProjectVo> projectList=new ArrayList<>(0);
        List<ProjectVo> projectList2=new ArrayList<>(0);
        List<ProjectVo> projectList3=new ArrayList<>(0);
        List<ProjectVo> projectList4=new ArrayList<>(0);

        //历史年
        List<String> yearList=new ArrayList<>(0);
        for(int i=0;i<year;i++){
            yearList.add(startYear+i+"年");
        }
        //预算年
        List<String> yearList2=new ArrayList<>(0);
        for(int i=0;i<year2;i++){
            yearList2.add((startYear+year-1+i)+"年");
        }

        for(int i=1;i<=10;i++){
            ProjectVo projectVo=new ProjectVo();
            projectVo.setIndex(i);
            projectVo.setName("");
            projectVo.setPrice("");
            projectList.add(projectVo);
            projectList2.add(projectVo);
            projectList3.add(projectVo);
            projectList4.add(projectVo);
        }



        workbook.addDataSource("basicInfo",basicInfo);
        workbook.addDataSource("dataVal1",dataVal1List);
        workbook.addDataSource("year",yearList);
        workbook.addDataSource("year2",yearList2);
        workbook.addDataSource("project",projectList);
        workbook.addDataSource("project2",projectList2);
        workbook.addDataSource("project3",projectList3);
        workbook.addDataSource("project4",projectList4);

        workbook.processTemplate();

        workbook.save(tempDir+"/"+docId+".xlsx");
        return  workbook.toJson();
    }

    @RequestMapping(value = { "spreadjs-demo.ssjson" },method = RequestMethod.GET)
    @ResponseBody
    public String demo() throws IOException {
        int year=5,year2=10;
        int startYear=2016;
        Workbook workbook = new Workbook();
        workbook.open(resource.getInputStream());
        //基础信息
        BasicInfoVo basicInfo= new BasicInfoVo();
        basicInfo.setCompany("中汇会计师事务所");
        basicInfo.setEditor("项文斌");
        basicInfo.setReviewer("王晶");
        basicInfo.setDate("2020-01-01");

        //数据校验
        List<FormulaTemplateVo> dataVal1List=new ArrayList(0);
        String dataVal1FormulaTemplate="#IF({0}12-利润表!{1}10<>0,\"不符,\"&{0}12-利润表!{1}10,\"与报表相符\")";
        for(int i=0,m=2,n=3;i<year;i++){
            FormulaTemplateVo f=new FormulaTemplateVo();
            f.setValue(MessageFormat.format(dataVal1FormulaTemplate, colArray[m+i], colArray[n+i]));
            dataVal1List.add(f);
        }

        //表格
        List<ProjectVo> projectList=new ArrayList<>(0);
        List<ProjectVo> projectList2=new ArrayList<>(0);
        List<ProjectVo> projectList3=new ArrayList<>(0);
        List<ProjectVo> projectList4=new ArrayList<>(0);

        //历史年
        List<String> yearList=new ArrayList<>(0);
        for(int i=0;i<year;i++){
            yearList.add(startYear+i+"年");
        }
        //预算年
        List<String> yearList2=new ArrayList<>(0);
        for(int i=0;i<year2;i++){
            yearList2.add((startYear+year-1+i)+"年");
        }

        for(int i=1;i<=10;i++){
            ProjectVo projectVo=new ProjectVo();
            projectVo.setIndex(i);
            projectVo.setName("");
            projectVo.setPrice("");
            projectList.add(projectVo);
            projectList2.add(projectVo);
            projectList3.add(projectVo);
            projectList4.add(projectVo);
        }



        workbook.addDataSource("basicInfo",basicInfo);
        workbook.addDataSource("dataVal1",dataVal1List);
        workbook.addDataSource("year",yearList);
        workbook.addDataSource("year2",yearList2);
        workbook.addDataSource("project",projectList);
        workbook.addDataSource("project2",projectList2);
        workbook.addDataSource("project3",projectList3);
        workbook.addDataSource("project4",projectList4);

        workbook.processTemplate();

        return  workbook.toJson();
    }
}
