package com.simtech.sim.cloudcompiler.entity.kernel;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.util.Map;

@Data
@JSONType(orders = {"code", "silent", "store_history", "allow_stdin", "user_expressions"})

public class Content {
    private String status;

    private Integer executionCount;

    private String executionState;

    private String code;

    private String text;

    private String traceback;

    private String ename;

    private String evalue;

    private Map<String, Object> user_expressions;

    private Object[] payload;

    private Boolean storeHistory;

    private Boolean silent;

    private Boolean allow_stdin;


    @Override
    public String toString(){
        return String.format("user executed %s with execution state %s %s", code, executionState, text);
    }

}