package top.kelton;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;

import javax.xml.parsers.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * 需要以translog的stratlogging开始，stoplogging作为结束，丢弃前后多余的记录
 * 从inputlog的meta可以看出时间戳差值，translog的startTime和endTime可以利用该差值计算得出最接近的一个节点，可以设置误差范围为±10毫秒
 */
public class IdfxParser {


    private static final String EVENT_TYPE_MOUSE = "mouse";
    private static final String EVENT_TYPE_FOCUS = "focus";
    private static final String MOUSE_CLICK = "click";
    private static final String MOUSE_MOVEMENT = "movement";
    private static final String EVENT_KEYBOARD = "keyboard";
    private static final String DURATION = "duration";

    public static void main(String[] args) {
        String filePath = "C:\\Users\\kelton\\Documents\\InputLog\\zzk04\\2024-12-07_0\\zzk04_0.idfx"; // 请替换为实际文件路径
        File xmlFile = new File(filePath);

        // 存储软件统计数据 格式如下：
        // chrome -> map(click-> 11, keyboard->12, duration-> 20000)
        // translog -> map(click-> 11, keyboard->12, duration-> 20000)
        // Map<String, Map<String, Integer>> softwareStats = new HashMap<>();
        Map<String, Integer> chromeMap = new HashMap<>();
        Map<String, Integer> othersMap = new HashMap<>();


        int totalKeyPresses = 0;
        int totalMouseClicks = 0;
        String latestProgramTitle = "unknown";
        Integer latestTimestamp = 0;

        try {
            // 读取文件内容并过滤特殊字符
            String content = readFileAndRemoveInvalidChars(xmlFile);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

            // 创建 DocumentBuilderFactory 和 DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);

            // 获取根元素
            NodeList eventList = doc.getElementsByTagName("event");

            for (int i = 0; i < eventList.getLength(); i++) {
                Node eventNode = eventList.item(i);
                if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eventElement = (Element) eventNode;

                    String eventType = eventElement.getAttribute("type");

                    // 处理 focus 事件，获取活动窗口的标题
                    if (EVENT_TYPE_FOCUS.equals(eventType)) {
                        NodeList titleList = eventElement.getElementsByTagName("title");
                        NodeList endTimeList = eventElement.getElementsByTagName("endTime");
                        String curProgramTitle = titleList.item(0).getTextContent();
                        Integer curTimestamp = Integer.parseInt(endTimeList.item(0).getTextContent());
                        if (StringUtils.isNotBlank(curProgramTitle)) {
                            // 第一次给latestProgramTitle赋值
                            if (latestProgramTitle.equals("unknown")) {
                                latestProgramTitle = isChromeProgram(latestProgramTitle) ? "chrome" : "translog";
                                latestTimestamp = curTimestamp;
                            } else {
                                // 上一次是chrome?
                                boolean latestProgramIsChrome = isChromeProgram(latestProgramTitle);
                                boolean curProgramIsChrome = isChromeProgram(curProgramTitle);
                                if (latestProgramIsChrome == curProgramIsChrome) {
                                    latestTimestamp = curTimestamp;
                                } else {
                                    // 更新累加时间
                                    if (latestProgramIsChrome) {
                                        Integer beforeDuration = chromeMap.getOrDefault(DURATION, 0);
                                        chromeMap.put(DURATION, beforeDuration + (curTimestamp - latestTimestamp));
                                    } else {
                                        Integer beforeDuration = othersMap.getOrDefault(DURATION, 0);
                                        othersMap.put(DURATION, beforeDuration + (curTimestamp - latestTimestamp));
                                    }
                                    // 更新latestProgramIsChrome
                                    latestProgramTitle = curProgramIsChrome ? "chrome" : "translog";
                                }
                            }
                        }
                    }

                    // 处理 mouse 事件，记录鼠标点击
                    if ("mouse".equals(eventType)) {
                        // 鼠标点击
                        totalMouseClicks++;
                        boolean latestProgramIsChrome = isChromeProgram(latestProgramTitle);
                        if (latestProgramIsChrome) {
                            Integer before = chromeMap.getOrDefault(MOUSE_CLICK, 0);
                            chromeMap.put(MOUSE_CLICK, before+1);
                        } else {
                            Integer before = othersMap.getOrDefault(MOUSE_CLICK, 0);
                            othersMap.put(MOUSE_CLICK, before+1);
                        }
                    }
                    // 处理 keyboard 事件，记录键盘敲击
                    if ("keyboard".equals(eventType)) {
                        totalKeyPresses++;
                        boolean latestProgramIsChrome = isChromeProgram(latestProgramTitle);
                        if (latestProgramIsChrome) {
                            Integer before = chromeMap.getOrDefault(EVENT_KEYBOARD, 0);
                            chromeMap.put(EVENT_KEYBOARD, before+1);
                        } else {
                            Integer before = othersMap.getOrDefault(EVENT_KEYBOARD, 0);
                            othersMap.put(EVENT_KEYBOARD, before+1);
                        }
                    }
                }
            }

            // 输出统计结果
            System.out.println("总的鼠标点击次数: " + totalMouseClicks);
            System.out.println("chrome鼠标点击次数：" + chromeMap.getOrDefault(MOUSE_CLICK, 0));
            System.out.println("其他软件鼠标点击次数：" + othersMap.getOrDefault(MOUSE_CLICK, 0));
            System.out.println("总的键盘敲击次数: " + totalKeyPresses);
            System.out.println("chrome键盘敲击次数：" + chromeMap.getOrDefault(EVENT_KEYBOARD, 0));
            System.out.println("其他软件键盘敲击次数：" + othersMap.getOrDefault(EVENT_KEYBOARD, 0));
            System.out.println("chrome使用时长（单位ms）：" + chromeMap.getOrDefault(DURATION, 0));
            System.out.println("其他软件使用时长（单位ms）：" + othersMap.getOrDefault(DURATION, 0));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TASKBAR、任务切换  是系统事件，可以忽略
     */

    private static boolean isChromeProgram(String softwareTitle) {
        if (StringUtils.isBlank(softwareTitle)) {
            return false;
        }
        softwareTitle = softwareTitle.toLowerCase();
        return softwareTitle.toLowerCase().contains("chrome") || softwareTitle.contains("google");
    }

    // 读取文件并移除非法字符
    private static String readFileAndRemoveInvalidChars(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 删除所有控制字符（例如&#x8;）
                line = line.replaceAll("&#x8;", ""); // 移除控制字符
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
