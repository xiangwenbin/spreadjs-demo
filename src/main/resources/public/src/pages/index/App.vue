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
    maxWebSocketFrameSize:1024*1024*10  
  }

  spread=null;
  designer=null;
  designerConfig=null;
  subscribeId="sub-"+new Date().getTime();
  // subscribeId=null;

  docId=window["docId"];

  initRetry(){
    this.retry=5;
  }

  initDesingerConfig(){
    let c=JSON.parse(
      JSON.stringify(DesignerGC.Spread.Sheets.Designer.DefaultConfig)
    );
    this.designerConfig=c;
  }

  create(){
    this.initRetry();
    this.initDesingerConfig();
    
  }

  mounted() {
     
  }
  

  initSpread(spread) {
    this.spread = spread;
  }

  designerInitialized(designer) {
    this.designer = designer;

    axios({
      method: "GET",
      url: `http://localhost:8080/doc/${this.docId}/${window["year"]}`,
      headers: { "X-Requested-With": "XMLHttpRequest" }
      }).then(result => {
        let workbook=this.designer.getWorkbook();
        // workbook.suspendPaint();
        workbook.fromJSON(result.data);
        this.resetSheetRowAndCol(workbook);
        // workbook.suspendPaint();
      }).then( ()=>{
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
      // client.maxWebSocketFrameSize=maxWebSocketFrameSize;
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
          let command=JSON.parse(Base64.decode(frame.body));
          
          //丢弃自己的消息
          if(this.subscribeId==frame.headers["sourceSubscription"]){
            console.log("丢弃自己的消息:"+frame.body);
          }else{
            // command._styles = null;
            var cm = this.designer.getWorkbook().commandManager();
            cm.removeListener('myListener');
            cm.execute(command);
            cm.addListener('myListener',(args)=>{
              this.onCommandExecute(args);
            });

          }
          
      },{id:this.subscribeId});
      console.log("subscribeId:"+this.subscribeId);
      

    });
  }

  onCommandExecute(args){
    console.log(args.command);
    var command = args.command;
    var ServerCommand = null;

    if(command.cmd){
      switch(command.cmd){
        case ServerCommands.EditCell:
            ServerCommand = {
                sheetName: command.sheetName,
                row: command.row,
                column: command.col,
                newValue: command.newValue
            }
            break;
        case ServerCommands.ResizeRow:
            ServerCommand = {
                sheetName: command.sheetName,
                rows: command.rows,
                size: command.size
            };
            break;
        case ServerCommands.ResizeColumn:
            ServerCommand = {
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
                ServerCommand = {
                    sheetName: command.sheetName,
                    selections: command.selections,
                    value: command.value
                }
            }
            break;
        case ServerCommands.MoveFloatingObjects:
            ServerCommand = {
                sheetName: command.sheetName,
                floatingObjects: command.floatingObjects,
                offsetX: command.offsetX,
                offsetY: command.offsetY
            };
            break;
        case ServerCommands.ResizeFloatingObjects:
            ServerCommand = {
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
            ServerCommand = {
                sheetName: command.sheetName,
                selections: command.selections
            };
            break;
        default:
      }

      if(ServerCommand != null){

        var cmd = command.cmd;
        var dotIndex = cmd.lastIndexOf('.');
        if(dotIndex !== -1){
            cmd = cmd.substring(dotIndex + 1);
        }
        ServerCommand.cmd = cmd;
        ServerCommand.docId = this.docId;
        // command.subscribeId=this.subscribeId;
        // ExecuteCommandAtServer(ServerCommand);

        // this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},JSON.stringify(command));
        console.log("ServerCommand:"+ServerCommand);

      }
      
      if(command.cmd=="clipboardPaste"){
        command.MA=null;
        command.clipboardHtml=null;
        // command.fromSheet=null;
      }
      // console.log("command size:"+JSON.stringify(command).length);
      // // let testCommand=""
      // // for(let i=0;i<10*1024*1024;i++){
      // //   testCommand+="d";
      // // }
      // command={
      //   "fromSheet":null,"fromRanges":null,"isCutting":false,"pasteOption":0,"pastedRanges":[{"row":1,"rowCount":1,"col":2,"colCount":1}],"clipboardText":"aaa ","cmd":"clipboardPaste","sheetName":"其他业务收入与成本","clipboardHtml":null,
      //   "MA":[
      //     {
      //       "3":{"value":"#IF(D12-利润表!E10<>0,\"不符,\"&D12-利润表!E10,\"与报表相符\")"},
      //       "background1":{"a":255,"r":255,"g":255,"b":255},
      //       "formatter":"_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ "
      //     }
      //   ],
      //   "sheetId":4
      // }
      this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},Base64.encode(JSON.stringify(command)));
      // this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},testCommand);

    }else{
      console.log("command.cmd is null");
      console.log(command);
    }
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
</style>
