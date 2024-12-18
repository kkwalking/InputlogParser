package top.kelton.domain.inputlog;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import lombok.Data;

@XStreamAlias("part")
@Data
class Part {
    @XStreamAsAttribute
    private String type;
    private String startTime;
    private String endTime;
    private String x;
    private String y;
    private String title;
    private String button;
    private String key;
    private String value;
    private String keyboardstate;
    @XStreamAlias("type")
    private String eventType;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    public static void main(String[] args) {
        // 创建 XStream 实例
        XStream xStream = new XStream(new DomDriver());
        // 添加安全配置：允许解析 Part 类
        XStream.setupDefaultSecurity(xStream); // 设置默认的安全机制
        xStream.allowTypes(new Class[]{Part.class}); // 显式允许 Part 类

        // 启用注解支持
        xStream.processAnnotations(Part.class);

        // 定义 XML 示例
        String xml = "<part type=\"winlog\">" +
                "<startTime>3152437</startTime>" +
                "<endTime>3152546</endTime>" +
                "<x>309</x>" +
                "<y>1317</y>" +
                "<type>click</type>" +
                "<button>LEFT</button>" +
                "</part>";

        // 解析 XML 到 Java 对象
        Part part = (Part) xStream.fromXML(xml);

        // 输出解析结果
        System.out.println("Part Type (Attribute): " + part.getType());
        System.out.println("Event Type (Child Node): " + part.getEventType());
        System.out.println("Start Time: " + part.getStartTime());
        System.out.println("End Time: " + part.getEndTime());
        System.out.println("X: " + part.getX());
        System.out.println("Y: " + part.getY());
        System.out.println("Button: " + part.getButton());
    }
}
