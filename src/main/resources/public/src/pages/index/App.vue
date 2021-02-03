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
    maxWebSocketFrameSize:1024*10
  }

  spread=null;
  designer=null;
  designerConfig=null;
  subscribeId="sub-"+new Date().getTime();


  disabledMenuList=[
    GC.Spread.Sheets.Designer.CommandNames.CellType,
    GC.Spread.Sheets.Designer.CommandNames.CellsInsert,
    GC.Spread.Sheets.Designer.CommandNames.InsertShape
  ];

  docId=window["docId"];

  initRetry(){
    this.retry=5;
  }

  initDesingerConfig(){
    let c=JSON.parse(
      JSON.stringify(DesignerGC.Spread.Sheets.Designer.DefaultConfig)
    );
    c.commandMap={};
    //禁用菜单配置
    if(this.disabledMenuList.length>0){
      for(let i=0;i<this.disabledMenuList.length;i++){
        let command=GC.Spread.Sheets.Designer.getCommand(this.disabledMenuList[i]);
        command.enableContext=command.enableContext+"&&!coopetation";
        c.commandMap[command.commandName]=command;
      }
      
    }
    this.designerConfig=c;
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

  designerInitialized(designer) {
    this.designer = designer;
    this.designer.setData("coopetation",true);
    this.designer.refresh()
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
      client.maxWebSocketFrameSize=maxWebSocketFrameSize;
      client.connect({},(frame)=>{
        resolve(client);
      },(error)=>{
        console.log(error);
      });
    });
  }
  /**
  * 连接websocket
  **/
  connnectWebSocket(){
    this.getStompClient(this.websocketConfig).then((client)=>{
      this.client=client;
      
      this.client.subscribe(`/doc/${this.docId}`,(frame)=>{
          let command=JSON.parse(frame.body);
          
          //丢弃自己的消息
          if(this.subscribeId==frame.headers["sourceSubscription"]){
            console.log("丢弃自己的消息:"+frame.body);
          }else{ 
            
            this.executeCommand(command);
          }
          
      },{id:this.subscribeId});
      console.log("subscribeId:"+this.subscribeId);
      

    });
  }

  executeCommand(command){
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
  
  onCommandExecute(args){
    console.log(args.command);
    var command = args.command;

    if(command.cmd){
      
      // if(command.cmd=="clipboardPaste"){
      //   command.MA=null;
      //   command.clipboardHtml=null;
      //   // command.fromSheet=null;
      // }
      if(command.fromSheet){
        command.fromSheet=command.fromSheet.name();
      }
      
      this.client.send(`/app/save/doc/${this.docId}`,{"sourceSubscription":this.subscribeId},JSON.stringify(command));

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
