package Models;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String url;
    private String message;
    private String date;
    private  long time;
    public  static ArrayList<Message> messages=new ArrayList<>();
    public  Message(String url,String message,String date,long time){
        this.url=url;
        this.message=message;
        this.date=date;
        this.time=time;
    }
    public String getMessage() {
        return message;
    }
    public String getUrl() {
        return url;
    }
    public String getDate() {
        return date;
    }
    public long getTime() {
        return time;
    }
}
