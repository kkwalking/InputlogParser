package top.kelton.domain.translog;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XStreamAlias("LogFile")
public class LogFile {
    private String startTime;
    private String endTime;
    private Events Events;
}
