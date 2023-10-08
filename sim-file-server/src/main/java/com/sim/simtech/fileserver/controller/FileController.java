package com.sim.simtech.fileserver.controller;


import com.sim.simtech.fileserver.util.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/fileServer")
public class FileController {

    @Autowired
    private MinioUtils minioUtils;


    @RequestMapping("/uploadFile/{fileCategory}")
    public String uploadFile(@RequestBody MultipartFile file, @PathVariable String fileCategory) throws Exception {
        minioUtils.uploadFile(file, fileCategory);

        return "success";
    }

    @RequestMapping("/getFile")
    public InputStream getFile(@RequestParam String instanceId, @RequestParam String algorithmType){
        return minioUtils.getObject(algorithmType, instanceId);
    }

}
