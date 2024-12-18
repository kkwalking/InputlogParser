package top.kelton.domain.inputlog;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.util.List;

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

    public static void main(String[] args) {
        // 创建 XStream 实例
        XStream xStream = new XStream(new DomDriver());

        // 启用注解支持
        xStream.processAnnotations(Log.class);
        xStream.processAnnotations(Meta.class);
        xStream.processAnnotations(Session.class);
        xStream.processAnnotations(Event.class);
        xStream.processAnnotations(Part.class);
        xStream.processAnnotations(Entry.class);

        // 添加安全配置：允许解析相关类
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[]{
                "top.kelton.domain.inputlog.**"
        });

        // 读取 XML 文件
        File xmlFile = new File("C:\\Users\\kelton\\Documents\\InputLog\\zzk04\\2024-12-07_0\\zzk04_0.idfx");
        if (!xmlFile.exists()) {
            System.out.println("XML 文件不存在！");
            return;
        }

        // 解析 XML 文件为 Log 对象
        Log log = (Log) xStream.fromXML(xmlFile);

        // 输出解析结果
        System.out.println("Meta Information:");
        System.out.println(log.getMeta());

        System.out.println("Session Information:");
        System.out.println(log.getSession());

        System.out.println("Events:");
        if (log.getEvents() != null) {
            for (Event event : log.getEvents()) {
                System.out.println(event);
            }
        }
    }
}


