package chauncy.controller;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import chauncy.entity.TextMessage;
import chauncy.util.CheckUtil;
import chauncy.util.HttpClientUtil;
import chauncy.util.XmlUtils;

/**
 * 微信对接步骤：
 * 1.填写服务器地址
 * 2.验证服务器地址是否正确（验签）
 * 3.验证通过后，任何微信操作都会以post请求通知到该服务地址
 * @classDesc: 功能描述(微信消息处理类)  
 * @author: ChauncyWang
 * @createTime: 2019年10月21日 下午10:58:28   
 * @version: 1.0
 */
@Controller
public class DispatCherServlet {
	
	private static final Logger logger=LoggerFactory.getLogger(DispatCherServlet.class);
	
	/**
	 * @methodDesc: 功能描述(微信验签接口)  
	 * @author: ChauncyWang
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return   
	 * @createTime: 2019年10月22日 下午5:02:35   
	 * @returnType: String
	 */
	@RequestMapping(value = "/dispatCherServlet", method = RequestMethod.GET)
	@ResponseBody
	public String getDispatCherServlet(String signature, String timestamp, String nonce, String echostr) {
		boolean checkSignature = CheckUtil.checkSignature(signature, timestamp, nonce);
		if (!checkSignature) {
			return null;
		}
		return echostr;
	}
	
	/**
	 * @methodDesc: 功能描述(微信接收消息后处理返回消息接口)  
	 * @author: ChauncyWang
	 * @param request
	 * @param response   
	 * @createTime: 2019年10月22日 下午5:02:23   
	 * @returnType: void
	 */
	@RequestMapping(value = "/dispatCherServlet", method = RequestMethod.POST)
	@ResponseBody
	public void getDispatCherServlet(HttpServletRequest request,HttpServletResponse response){
		response.setContentType("text/html;charset=utf-8");//防止乱码
		try(PrintWriter out=response.getWriter()){
			Map<String, String> result = XmlUtils.parseXml(request);
			String toUserName = result.get("ToUserName");
			String fromUserName = result.get("FromUserName");
			String msgType = result.get("MsgType");
			String content = result.get("Content");
			String resultXml = null;
			switch (msgType == null ? "":msgType){
			case "text":
				if(content.equals("chauncy")){
					resultXml = SendTextMessage(toUserName, fromUserName,"chauncy is handsome boy","text");
				}else{
					resultXml = CallQingYunKeRobotApi(toUserName, fromUserName, content);
				}
				break;
			default:
				resultXml = CallQingYunKeRobotApi(toUserName, fromUserName, content);
				break;
			}
			out.print(resultXml);
		}catch (Exception e) {
			logger.error("给微信回消息出错！",e.getMessage());
		}
		
	}
	
	/**
	 * @methodDesc: 功能描述(调用青云客智能聊天机器人API)  
	 * @author: ChauncyWang
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @return   
	 * @createTime: 2019年10月22日 下午6:35:14   
	 * @returnType: String
	 */
	private String CallQingYunKeRobotApi(String toUserName, String fromUserName, String content) {
		String resultXml;
		String apiResultStr = HttpClientUtil.doGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+content);
		JSONObject jsonObj = new JSONObject().parseObject(apiResultStr);
		Integer apiResult = jsonObj.getInteger("result");
		if(apiResult != null && apiResult==0){
			String apiContent = jsonObj.getString("content");
			resultXml = SendTextMessage(toUserName, fromUserName,apiContent,"text");
		}else{
			resultXml = SendTextMessage(toUserName, fromUserName,"I dont know you message","text");
		}
		return resultXml;
	}
	
	/**
	 * @methodDesc: 功能描述(组装返回微信的实体生成对应的XML报文)  
	 * @author: ChauncyWang
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @param text
	 * @return   
	 * @createTime: 2019年10月22日 下午5:01:21   
	 * @returnType: String
	 */
	private String SendTextMessage(String toUserName, String fromUserName,String content,String text) {
		String resultXml=null;
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(System.currentTimeMillis());
		textMessage.setContent(content);
		textMessage.setMsgType(text);
		resultXml=XmlUtils.messageToXml(textMessage);
		return resultXml;
	}
}
