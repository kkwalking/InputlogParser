package top.kelton.domain.translog;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: zzk
 * @create: 2024-12-19 23:27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mouse {
    @XStreamAlias("Time")
    @XStreamAsAttribute
    private String time;

    @XStreamAlias("Value")
    @XStreamAsAttribute
    private String value;
}
