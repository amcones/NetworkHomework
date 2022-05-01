import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServerInfo {
    @JsonProperty("info")
    List<String> info;
    @JsonProperty("msg")
    String msg;
    public ServerInfo(List<String> info,String msg)
    {
        this.info=info;
        this.msg=msg;
    }
}
