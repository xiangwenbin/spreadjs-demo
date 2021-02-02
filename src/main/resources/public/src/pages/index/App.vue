<!-- 首页 -->

  <!--
    <gc-spread-sheets
        :hostClass='"spreadHost"' @workbookInitialized="initSpread">

    </gc-spread-sheets>
    -->  
<template>
  <div class="m-index">
    <gc-spread-sheets-designer
    :styleInfo="styleInfo"
    :config="designerConfig" 
     @designerInitialized="designerInitialized">
    </gc-spread-sheets-designer>
  </div>
</template>

<script>
import { Vue, Component } from "vue-property-decorator";
// import '@grapecity/spread-sheets-vue';
//设计器资源
import "@grapecity/spread-sheets-designer-resources-cn";
//表格资源
import '@grapecity/spread-sheets-resources-zh';
import "@grapecity/spread-sheets-designer-vue";
import '@grapecity/spread-sheets/styles/gc.spread.sheets.excel2016colorful.css';
import '@grapecity/spread-sheets-designer/styles/gc.spread.sheets.designer.min.css';


import GC from '@grapecity/spread-sheets';
import ExcelIO from "@grapecity/spread-excelio";
import DesignerGC from "@grapecity/spread-sheets-designer";
import axios from "axios";

import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import {Base64} from 'js-base64';
import * as bytes from 'bytes';

import {ServerCommands} from "../../util/const.js"


GC.Spread.Common.CultureManager.culture("zh-cn");

export default
@Component({
  components: {  }
})
class App extends Vue {
  

  styleInfo = { height: "100%", width: "100%" };

  retry=null;
  client=null;
  websocketConfig={  
    host:"http://localhost:8080/ws/stomp",
    heartbeat:{
      outgoing:10000,incoming:10000
    },
    maxWebSocketFrameSize:1024*10
  }

  spread=null;
  designer=null;
  designerConfig=null;
  subscribeId="sub-"+new Date().getTime();
  // subscribeId=null;

  docId=window["docId"];

  disabledMenuList=[
    GC.Spread.Sheets.Designer.CommandNames.CellType,
    GC.Spread.Sheets.Designer.CommandNames.CellsInsert,
    GC.Spread.Sheets.Designer.CommandNames.InsertShape
    ];

  initRetry(){
    this.retry=5;
  }

  initDesingerConfig(){
    let c=JSON.parse(
      JSON.stringify(DesignerGC.Spread.Sheets.Designer.DefaultConfig)
    );
    c.commandMap={};
    //添加协作下菜单禁用标记
    // for(let i=0;i<this.disabledMenuList.length;i++){
    //   let command=GC.Spread.Sheets.Designer.getCommand(this.disabledMenuList[i]);
    //   command.enableContext=command.enableContext+"&&!coopetation";
    //   c.commandMap[command.commandName]=command;
    // }


    this.designerConfig=c;
    
    console.log("this.designerConfig",this.designerConfig);
    console.log("GC",GC);
  }

  created(){
    this.initRetry();
    this.initDesingerConfig();
    
  }

  mounted() {
     
  }
  

  initSpread(spread) {
    this.spread = spread;
  }

  setDesignerMenuDisable(){
    if(this.disabledMenuList.length>0){
      this.designer.setData("coopetation",true);
      for(let i=0;i<this.disabledMenuList.length;i++){
        let command=GC.Spread.Sheets.Designer.getCommand(this.disabledMenuList[i]);
        command.enableContext=command.enableContext+"&&!coopetation";
        this.designerConfig.commandMap[command.commandName]=command;
        console.log("comamnd",command);
      }
      this.designer.setConfig(this.designerConfig);
      this.designer.refresh()
    }
    

    // this.designer.setData("coopetation",true);
    // this.designer.refresh()
    
    // this.designer.setData("coopetation",true);
    // designer.setConfig();
    

  }

  designerInitialized(designer) {
   
    this.designer = designer; 
    this.setDesignerMenuDisable();
    // this.designer.getCommand(GC.Spread.Sheets.Designer.CommandNames.CommandNames())
    axios({
      method: "GET",
      url: `http://localhost:8080/api/doc/${this.docId}/${window["year"]}`,
      headers: { "X-Requested-With": "XMLHttpRequest" }
      }).then(result => {
        let workbook=this.designer.getWorkbook();
        // workbook.suspendPaint();
        workbook.fromJSON(result.data);
        this.resetSheetRowAndCol(workbook);
        // workbook.suspendPaint();
      }).then(()=>{
        
      }).then(()=>{
        this.connnectWebSocket();
      }).then(()=>{
        var cm = this.designer.getWorkbook().commandManager();
        cm.addListener('myListener',(args)=>{
           this.onCommandExecute(args);
        })
      }).catch(error => {
        console.log(error);
    });
  }
  /**
  *
  **/
  getStompClient(websocketConfig){
    let host=websocketConfig.host;
    let heartbeat=websocketConfig.heartbeat;
    let maxWebSocketFrameSize=websocketConfig.maxWebSocketFrameSize;

    return new Promise((resolve,reject)=>{
      let client= Stomp.over(new SockJS(host,null,{
        transports:["websocket","xhr-streaming"]
      }));
      // client will send heartbeats every 10000ms
      client.heartbeat.outgoing = heartbeat.outgoing; 
      client.heartbeat.incoming = heartbeat.incoming;
      client.maxWebSocketFrameSize=maxWebSocketFrameSize;
      client.connect({},(frame)=>{
        resolve(client);
      },(error)=>{
        console.log("retry:"+this.retry+" error:"+error);
        if(--this.retry>=0){
          setTimeout(()=>{
            this.connnectWebSocket();
          },5000);
        }else{
          console.log("重连失败:"+error);
        }
      });
    });
  }
  /**
  * 连接websocket
  **/
  connnectWebSocket(){
    this.getStompClient(this.websocketConfig).then((client)=>{
      this.initRetry();
      this.client=client;
      
      this.client.subscribe(`/doc/${this.docId}`,(frame)=>{
          let command=JSON.parse(frame.body);
          
          //丢弃自己的消息
          if(this.subscribeId==frame.headers["sourceSubscription"]){
            console.log("丢弃自己的消息:"+frame.body);
          }else{
            // command._styles = null;
            var cm = this.designer.getWorkbook().commandManager();
            cm.removeListener('myListener');
            if(command.fromSheet&&command.fromSheet!=""){
                command.fromSheet=this.designer.getWorkbook().getSheetFromName(command.fromSheet);
            }
            cm.execute(command);
            cm.addListener('myListener',(args)=>{
              this.onCommandExecute(args);
            });

          }
          
      },{id:this.subscribeId});
      

    });
  }

  onCommandExecute(args){
    console.log(args.command);
    var command = args.command;
    var serverCommand = null;
    if(command.fromSheet){
      command.fromSheet=command.fromSheet.name();
    }

    if(command.cmd=="clipboardPaste"){
      command.MA=null;
      command.clipboardHtml=null;
    }

    let frameBody={
      cmd:command.cmd,
      docId:this.docId,
      sheetName:command.sheetName
    }

    if(command.cmd){
      switch(command.cmd){
        case ServerCommands.EditCell:
            serverCommand = {
                sheetName: command.sheetName,
                row: command.row,
                column: command.col,
                newValue: command.newValue
            }
            break;
        case ServerCommands.ResizeRow:
            serverCommand = {
                sheetName: command.sheetName,
                rows: command.rows,
                size: command.size
            };
            break;
        case ServerCommands.ResizeColumn:
            serverCommand = {
                sheetName: command.sheetName,
                columns: command.columns,
                size: command.size
            };
            break;
        case 'Designer.' + ServerCommands.SetFontFamily:
        case 'Designer.' + ServerCommands.SetFontSize:
        case 'Designer.' + ServerCommands.SetBackColor:
        case 'Designer.' + ServerCommands.SetForeColor:
        case 'Designer.' + ServerCommands.SetFontWeight:
        case 'Designer.' + ServerCommands.SetFontStyle:
        case 'Designer.' + ServerCommands.SetUnderline:
        case 'Designer.' + ServerCommands.SetDoubleUnderline:
            if(command.value && command.value.indexOf('undefined') === -1){
                serverCommand = {
                    sheetName: command.sheetName,
                    selections: command.selections,
                    value: command.value
                }
            }
            break;
        case ServerCommands.MoveFloatingObjects:
            serverCommand = {
                sheetName: command.sheetName,
                floatingObjects: command.floatingObjects,
                offsetX: command.offsetX,
                offsetY: command.offsetY
            };
            break;
        case ServerCommands.ResizeFloatingObjects:
            serverCommand = {
                sheetName: command.sheetName,
                floatingObjects: command.floatingObjects,
                offsetX: command.offsetX,
                offsetY: command.offsetY,
                offsetWidth: command.offsetWidth,
                offsetHeight: command.offsetHeight
            };
            break;  
        case ServerCommands.InsertColumns:
        case ServerCommands.InsertRows:
            serverCommand = {
                sheetName: command.sheetName,
                selections: command.selections
            };
            break;
        default:
      }

      if(serverCommand != null){

        var cmd = command.cmd;
        var dotIndex = cmd.lastIndexOf('.');
        if(dotIndex !== -1){
            cmd = cmd.substring(dotIndex + 1);
        }
        serverCommand.cmd = cmd;
        serverCommand.docId = this.docId;
        // command.subscribeId=this.subscribeId;
        // ExecuteCommandAtServer(ServerCommand);

        // this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},JSON.stringify(command));
        console.log("ServerCommand:"+serverCommand);
        frameBody.serverCommand=JSON.stringify(serverCommand);

      }

      // let testCommand=""
      // // 16640  WebSocketSession
      // for(let i=0;i<1024*16+146;i++){
      //   testCommand+="d";
      // }
      frameBody.spreadCommand=JSON.stringify(command);
      this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},JSON.stringify(frameBody));
      // this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},testCommand);

    }else{
      console.log("command.cmd is null");
      console.log(command);
    }
  }

  stringToByte(str) {  
    var bytes = new Array();  
    var len, c;  
    len = str.length;  
    for(var i = 0; i < len; i++) {  
        c = str.charCodeAt(i);  
        if(c >= 0x010000 && c <= 0x10FFFF) {  
            bytes.push(((c >> 18) & 0x07) | 0xF0);  
            bytes.push(((c >> 12) & 0x3F) | 0x80);  
            bytes.push(((c >> 6) & 0x3F) | 0x80);  
            bytes.push((c & 0x3F) | 0x80);  
        } else if(c >= 0x000800 && c <= 0x00FFFF) {  
            bytes.push(((c >> 12) & 0x0F) | 0xE0);  
            bytes.push(((c >> 6) & 0x3F) | 0x80);  
            bytes.push((c & 0x3F) | 0x80);  
        } else if(c >= 0x000080 && c <= 0x0007FF) {  
            bytes.push(((c >> 6) & 0x1F) | 0xC0);  
            bytes.push((c & 0x3F) | 0x80);  
        } else {  
            bytes.push(c & 0xFF);  
        }  
    }  
    return bytes;  
  }  

  resetSheetRowAndCol(workbook){
    for(let i=0;i<workbook.getSheetCount();i++){
      let sheet=workbook.getSheet(i);
      let rowCount = sheet.getRowCount();
      let colCount = sheet.getColumnCount();
      if(rowCount<50){
        sheet.setRowCount(50);
      }else{
        sheet.setRowCount(rowCount+5);
      }
      if(colCount<50){
          sheet.setColumnCount(50);
      }else{
          sheet.setColumnCount(colCount+5);
      }
    }
  }

}
</script>

<style lang="scss">
.m-index {
  width:100%;
  height:100%;
  > .spreadHost{
    width:100%;
    height:100%;
  }
}
.gc-designer-list-control .gc-list-control-container .gc-list-control-item .gc-list-control-item-icon{
  top:auto;
}
</style>
