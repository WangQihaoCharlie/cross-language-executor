package com.simtech.sim.cloudcompiler.kernel;

import com.simtech.sim.cloudcompiler.entity.dto.KernelDetail;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SimpleKernelConnector {


    private final RestTemplate restTemplate;

    public SimpleKernelConnector(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }



    public KernelDetail createKernel(){
        ResponseEntity<KernelDetail> response = restTemplate.exchange("http://localhost:8888/api/kernels", HttpMethod.POST, null, KernelDetail.class);
        return response.getBody();
    }


    public String deleteKernel(String id){
        ResponseEntity<KernelDetail> response = restTemplate.exchange("http://localhost:8888/api/kernels/"+id, HttpMethod.DELETE, null, KernelDetail.class, id);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }
        else {
            return "error";
        }
    }

}
