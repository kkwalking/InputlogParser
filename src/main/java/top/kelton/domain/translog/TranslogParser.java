package top.kelton.domain.translog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cn.hutool.core.date.DateUtil;

/**
 * @description: translog
 * @author: zzk
 * @create: 2024-12-21 17:01
 **/
public class TranslogParser {

    private String fileUrl;
    private LogFile logFile;


    public TranslogParser(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Date getStartTime() {
        Date startTime = DateUtil.parse(logFile.getStartTime());
        return startTime;
    }
    public Date getEndTime() {
        Date endTime = DateUtil.parse(logFile.getEndTime());
        return endTime;
    }

    public LogFile getLogFile() {
        if (logFile != null) {
            return logFile;
        }
        if (fileUrl == null) {
            throw new RuntimeException("translog 日志文件路径:[" + fileUrl + "]不存在");
        }
        // 创建 XStream 实例
        XStream xStream = new XStream(new DomDriver());

        // 启用注解支持
        xStream.processAnnotations(LogFile.class);
        xStream.processAnnotations(Events.class);
        xStream.processAnnotations(Event.class);
        xStream.processAnnotations(System.class);
        xStream.ignoreUnknownElements();

        // 添加安全配置：允许解析相关类
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[]{
                "top.kelton.domain.translog.**"
        });
        LogFile log = null;
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
            log = (LogFile) xStream.fromXML(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("translog 日志文件处理异常, url:" + fileUrl);
        }
        logFile = log;
        return logFile;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\kelton\\Desktop\\T02\\T1.xml";
        TranslogParser translogParser = new TranslogParser(filePath);
        LogFile log = translogParser.getLogFile();

        // 输出解析结果
        java.lang.System.out.println("start time:");
        Date startTime = DateUtil.parse(log.getStartTime());
        java.lang.System.out.println(startTime.getTime());
        java.lang.System.out.println(startTime);

        java.lang.System.out.println("end time:");
        Date endTime = DateUtil.parse(log.getEndTime());
        java.lang.System.out.println(endTime.getTime());
        java.lang.System.out.println(endTime);

    }
}
