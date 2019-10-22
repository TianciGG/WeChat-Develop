package chauncy.entity;

/**   
 * @classDesc: 功能描述(给微信回消息实体类)  
 * @author: ChauncyWang
 * @createTime: 2019年10月22日 下午3:46:26   
 * @version: 1.0  
 */ 
public class TextMessage {
	
	private String ToUserName;
	
	private String FromUserName;
	
	private Long CreateTime;
	
	private String MsgType;
	
	private String Content;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
