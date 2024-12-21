package top.kelton.domain.inputlog;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias("event")
@Data
public class Event {
    private String type;
    private String id;
    private Part part;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
