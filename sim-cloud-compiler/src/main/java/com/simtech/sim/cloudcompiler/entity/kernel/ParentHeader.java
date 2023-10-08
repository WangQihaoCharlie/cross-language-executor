package com.simtech.sim.cloudcompiler.entity.kernel;

import lombok.Data;

@Data
public class ParentHeader {

    private String username;

    private String version;

    private String session;

    private String msgId;

    private String date;
}
