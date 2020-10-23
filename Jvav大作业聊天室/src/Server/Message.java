package Server;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;
    //类型
    private Object content;
    //消息内容
    private String to;
    //接收方名字
    private String from;

    //发送方名字
    public String getType() { return (this.type); }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return (this.content);
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getTo() { return (this.to); }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return (this.from);
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
