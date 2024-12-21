package top.kelton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import top.kelton.domain.inputlog.Event;
import top.kelton.domain.inputlog.InputLogParser;
import top.kelton.domain.inputlog.Log;
import top.kelton.domain.inputlog.Part;
import top.kelton.domain.translog.LogFile;
import top.kelton.domain.translog.TranslogParser;

/**
 * @description:
 * @author: zzk
 * @create: 2024-12-21 17:17
 **/
public class Experiment {


    public static void main(String[] args) {

        // 获取inputlog
        int fileIdx = 0;
        for (int i = 0; i < 4; i++) {
            String inputLogUrl = "C:\\Users\\kelton\\Desktop\\T02\\T02_" + i
                    + ".idfx";
            String translogUrl = "C:\\Users\\kelton\\Desktop\\T02\\T" + (i+1)
                    + ".xml";
            statistic(inputLogUrl, translogUrl);
        }

    }

    public static void statistic(String inputLogUrl, String translogUrl) {
        InputLogParser inputLogParser = new InputLogParser(inputLogUrl);
        Log inputLogFile = inputLogParser.getLog();
        Long inputLogStartTimestamp = inputLogParser.getStartTimestamp(); // inputLog的9位数时间记法，如672237281


        // 获取translog 开始结束时间
        TranslogParser translogParser = new TranslogParser(translogUrl);
        LogFile translogFile = translogParser.getLogFile();
        Date startTime = translogParser.getStartTime();
        Date endTime = translogParser.getEndTime();

        long startTimeDistance = startTime.getTime() - inputLogStartTimestamp;
        long endTimeDistance = endTime.getTime() - inputLogStartTimestamp;
        // 转换为inputLog的9位数时间记法
        long transLogRelativeStartTime = startTimeDistance + inputLogParser.getRelativeTime();
        long transLogRelativeEndTime = endTimeDistance + inputLogParser.getRelativeTime();

        // 误差值
        long e = 500;
        // 找最接近的一个时间戳（误差范围+-e内）， 且title带有"Translog-II"的
        List<Event> inputLogEvents = inputLogFile.getEvents();

        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < inputLogEvents.size(); i++) {
            Event inputLogEvent = inputLogEvents.get(i);
            Part part = inputLogEvent.getPart();
            String title = part.getTitle();
            if (StringUtils.isBlank(title) || !title.startsWith("Translog-II")) {
                continue;
            }
            if (part.getStartTime() == null) {
                continue;
            }
            long partStartTime = Long.parseLong(part.getStartTime());
            long partEndTime = Long.parseLong(part.getEndTime());
            // 定位translog开始的event
            if (partStartTime >= transLogRelativeStartTime - e && partStartTime <= transLogRelativeStartTime + e) {
                startIndex = i;
            }
            // 定位translog结束的event
            if (partEndTime >= transLogRelativeEndTime - e && partEndTime <= transLogRelativeEndTime + e) {
                endIndex = i;
            }
        }
        List<Event> targetEvents = new ArrayList<>(endIndex - startIndex);
        // 截取translog录制期间的event
        for (int i = startIndex; i <= endIndex; i++) {
            Event inputLogEvent = inputLogEvents.get(i);
            targetEvents.add(inputLogEvent);
        }
        System.out.println("------------statistic---start-------------------");
        System.out.println("input log: " + inputLogUrl);
        System.out.println("trans log: " + translogUrl);
        System.out.println("截取长度:" + targetEvents.size());
        System.out.println("------------statistic---end-------------------\n");
    }
}
