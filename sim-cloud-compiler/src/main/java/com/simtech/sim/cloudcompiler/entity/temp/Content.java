package com.simtech.sim.cloudcompiler.entity.temp;

import lombok.Data;

@Data
public class Content {
    private String content;


    @Override
    public String toString(){
        return content;
    }
}
