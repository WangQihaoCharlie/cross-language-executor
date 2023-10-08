package com.simtech.sim.cloudcompiler.entity.kernel;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
@JSONType(orders = {"username", "version", "session", "msg_id", "msg_type"})
public class Header {

    private String msgId;

    private String msgType;

    private String username;

    private String session;

    private String date;

    private String version;

}
