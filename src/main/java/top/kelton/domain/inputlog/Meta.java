package top.kelton.domain.inputlog;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Data;

@XStreamAlias("meta")
@Data
class Meta {
    @XStreamImplicit
    private List<Entry> entries;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}