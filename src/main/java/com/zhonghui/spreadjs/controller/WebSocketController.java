package com.zhonghui.spreadjs.controller;



import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.gson.Gson;
import com.grapecity.documents.excel.Workbook;
import com.zhonghui.spreadjs.bean.command.BaseCommand;
import com.zhonghui.spreadjs.bean.command.SetValueCommand;
import com.zhonghui.spreadjs.constants.CommandConstants;
import com.zhonghui.spreadjs.controller.base.BaseController;
import com.zhonghui.spreadjs.util.CommonUtil;
import com.zhonghui.spreadjs.vo.CommandFrameBodyVo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.MessageFormat;
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

    @Value("${temp-dir}")
    String tempDir;

    static Interner<String> pool = Interners.newWeakInterner();

    /**
     *
     * @param docId
     * @param sourceSubscription
     * @param message
     * @param headers
     * @param body
     */
    @MessageMapping("/save/doc/{docId}")
    public void saveDoc(@DestinationVariable String docId,@Header("sourceSubscription") String sourceSubscription , Message message, @Headers Map headers, @Payload CommandFrameBodyVo body) {
        if(StringUtils.isNotEmpty(docId)&&StringUtils.isNotEmpty(body.getSheetName())){
            String lockValue=docId+"-"+body.getSheetName();
            synchronized (pool.intern(lockValue)){
                //服务器协同
                if(CommandConstants.EDIT_CELL.equals(body.getCmd())&&StringUtils.isNotEmpty(body.getServerCommand())){
                    Gson gson = new Gson();
                    SetValueCommand setValueCommand=gson.fromJson(body.getServerCommand(), SetValueCommand.class);
                    Workbook workbook = new Workbook();

                    String filePath=CommonUtil.joinFilePath(tempDir,body.getDocId()+".xlsx");
                    workbook.open(filePath);
                    this.executeCommand(setValueCommand,workbook);
                    workbook.save(filePath);
                }
                System.out.println(body.getCmd());
                Map<String,Object> sendMap=new HashedMap();
                sendMap.put("sourceSubscription",sourceSubscription);
                messagingTemplate.convertAndSend(MessageFormat.format("/doc/{0}",docId),body.getSpreadCommand(),sendMap);
            }
        }
    }

    private void executeCommand(BaseCommand baseCommand, Workbook workbook) {

        baseCommand.execute(workbook);

    }
}
