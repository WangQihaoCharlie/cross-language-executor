package com.simtech.sim.cloudcompiler.entity.kernel;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JSONType(orders = {"header", "msg_id", "msg_type", "parent_header",  "channel","content", "metadata", "buffers"})
public class WsResponse {
    private Header header;

    @JSONField(name = "msg_id")
    private String msg_id;

    @JSONField(name = "msg_type")
    private String msg_type;
    private ParentHeader parent_header;
    private Metadata metadata;
    private Content content;
    private Buffer buffers;
    private String channel;

    public WsResponse getDefaultWsRequest(String code){

        WsResponse wsResponse = new WsResponse();
        wsResponse.setChannel("shell");

        Header header = new Header();
        header.setUsername("");
        header.setSession("");
        header.setMsgId(UUID.randomUUID().toString().replace("-", ""));
        header.setMsgType("execute_request");
        header.setVersion("5.0");
        wsResponse.setHeader(header);

        wsResponse.setParent_header(new ParentHeader());

        wsResponse.setMetadata(new Metadata());

        wsResponse.setBuffers(new Buffer());

        Content content = new Content();
        content.setCode(code);
        content.setSilent(false);
        content.setStoreHistory(false);
        content.setUser_expressions(new HashMap<>());
        content.setAllow_stdin(false);
        wsResponse.setContent(content);
        return wsResponse;
    }
}
