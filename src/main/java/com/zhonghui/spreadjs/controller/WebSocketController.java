package com.zhonghui.spreadjs.controller;



import com.grapecity.documents.excel.Workbook;
import com.zhonghui.spreadjs.controller.base.BaseController;
import com.zhonghui.spreadjs.vo.BasicInfoVo;
import com.zhonghui.spreadjs.vo.FormulaTemplateVo;
import com.zhonghui.spreadjs.vo.ProjectVo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author xwb
 *
 */
@Controller
public class WebSocketController extends BaseController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     *
     * @param docId
     * @param sourceSubscription
     * @param message
     * @param headers
     * @param command
     */
    @MessageMapping("/save/doc/{docId}")
    public void saveDoc(@DestinationVariable String docId,@Header("sourceSubscription") String sourceSubscription , Message message, @Headers Map headers, @Payload String command) {
        System.out.println("command length:"+command.length());
        Map<String,Object> sendMap=new HashedMap();
        sendMap.put("sourceSubscription",sourceSubscription);
        messagingTemplate.convertAndSend(MessageFormat.format("/doc/{0}",docId),command,sendMap);
    }

}
