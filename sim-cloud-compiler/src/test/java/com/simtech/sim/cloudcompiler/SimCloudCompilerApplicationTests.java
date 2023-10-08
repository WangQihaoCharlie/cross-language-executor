package com.simtech.sim.cloudcompiler;

import com.simtech.sim.cloudcompiler.kernel.SimpleKernelConnector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimCloudCompilerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private SimpleKernelConnector simpleKernelConnector;

	@Test
	void littleTest(){
		simpleKernelConnector.createKernel();
	}

}
