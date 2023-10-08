package com.simtech.sim.cloudcompiler.entity.kernel;


import lombok.Data;

@Data
public class Metadata {


    private String started;

    private Boolean dependenciesMet;

    private String engine;

    private String status;


}