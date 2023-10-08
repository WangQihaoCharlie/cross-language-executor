package com.simtech.sim.cloudcompiler.entity.dto;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
public class KernelDetail {

    private String id;

    private String name;

    private String last_activity;

    private String execution_state;

    private Integer connections;

}
