package com.eval.license;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eval.license.controller.EvalController;
import com.eval.license.service.impl.CustomerServiceImpl;
import com.eval.license.service.impl.EvalLicensesServiceImpl;
import com.eval.license.service.impl.EvalSkusServiceImpl;



@SpringBootTest
class EvalLicenseApplicationTests {

	@Autowired
	private EvalController evalController;

	@Autowired
	CustomerServiceImpl customerService;
	@Autowired
	EvalSkusServiceImpl evalSkusService;

	@Autowired
	EvalLicensesServiceImpl evalLicensesService;

	@Test
	public void contextLoads() throws Exception {
		assertThat(evalController).isNotNull();
		assertThat(customerService).isNotNull();
		assertThat(evalLicensesService).isNotNull();
		assertThat(evalSkusService).isNotNull();
	}
	 
	 @Test
	   public void main() {
	      EvalLicenseApplication.main(new String[] {});
	      assertThat(evalSkusService).isNotNull();
	   }
	
	
}
