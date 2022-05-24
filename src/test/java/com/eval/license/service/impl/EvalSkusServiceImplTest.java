package com.eval.license.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.eval.license.model.EvalSkus;
import com.eval.license.repository.EvalSkusRepository;



@Transactional
@ExtendWith(MockitoExtension.class)
class EvalSkusServiceImplTest { 

	@Mock
	private EvalSkusRepository evalSkusRepository;

	@InjectMocks
	private EvalSkusServiceImpl evalSkusService;

	@Test
	void testFindBySkuAndProductDescription() {
		EvalSkus evalSkus = new EvalSkus();
		evalSkus.setSku("abcd");
		evalSkus.setProductDescription("efgh");

		EvalSkus evalSkus1 = new EvalSkus();
		evalSkus1.setSku("hijk");
		evalSkus1.setProductDescription("lmop");

		EvalSkus evalSkus2 = new EvalSkus();
		evalSkus2.setSku("qrst");
		evalSkus2.setProductDescription("uvwx");

		Mockito.when(evalSkusRepository.findBySkuAndProductDescription(evalSkus1.getSku(),
				evalSkus1.getProductDescription())).thenReturn(Arrays.asList(evalSkus1));
		List<EvalSkus> evalSkusDetail = evalSkusService.findBySkuAndProductDescription(evalSkus1.getSku(),
				evalSkus1.getProductDescription());
		assertEquals(evalSkus1.getSku(), evalSkusDetail.get(0).getSku());
	}

}
