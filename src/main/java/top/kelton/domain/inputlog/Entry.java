package top.kelton.domain.inputlog;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@XStreamAlias("entry")
@Data
public class Entry {
    private String key;
    private String value;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
