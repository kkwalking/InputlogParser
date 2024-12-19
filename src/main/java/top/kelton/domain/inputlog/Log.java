package top.kelton.domain.inputlog;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;

// 主类 log
@XStreamAlias("log")
@Data
public class Log {
    private Meta meta;
    private Session session;
    @XStreamImplicit
    private List<Event> events;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}


