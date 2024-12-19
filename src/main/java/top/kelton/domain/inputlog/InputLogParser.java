package top.kelton.domain.inputlog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * @description:
 * @author: zzk
 * @create: 2024-12-18 22:19
 **/
public class InputLogParser {

    private String fileUrl;


    public InputLogParser(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Log getLog() {
        if (fileUrl == null) {
            throw new RuntimeException("idfx文件路径:[" + fileUrl + "]不存在");
        }
        // 创建 XStream 实例
        XStream xStream = new XStream(new DomDriver());

        // 启用注解支持
        xStream.processAnnotations(Log.class);
        xStream.processAnnotations(Meta.class);
        xStream.processAnnotations(Session.class);
        xStream.processAnnotations(Event.class);
        xStream.processAnnotations(Part.class);
        xStream.processAnnotations(Entry.class);
        xStream.ignoreUnknownElements();

        // 添加安全配置：允许解析相关类
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[]{
                "top.kelton.domain.inputlog.**"
        });
        Log log = null;
        try {
            // 文件路径
            Path file = Path.of(fileUrl);
            // 读取文件内容为字符串
            String content = Files.readString(file);
            // 去除文件开头不可见的内容
            content = content.substring(content.indexOf("<?xml version"));
            // 移除十六进制字符引用
            content = content.replaceAll("&#x[0-9A-Fa-f]+;", "");
            // 移除十进制字符引用
            content = content.replaceAll("&#[0-9]+;", "");
            log = (Log) xStream.fromXML(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("idfx文件处理异常, url:" + fileUrl);
        }
        return log;
    }


    public static void main(String[] args) {

        String filePath = "C:\\Users\\kelton\\Desktop\\T02\\T02_0.idfx";
        InputLogParser inputLogParser = new InputLogParser(filePath);
        Log log = inputLogParser.getLog();

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
