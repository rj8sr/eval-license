package com.eval.license.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.eval.license.model.EvalSkus;

@DataJpaTest
@ActiveProfiles("dev")
class EvalSkusRepositoryTest {

	@Autowired
	EvalSkusRepository evalSkusRepository;

	private List<EvalSkus> evalSkusList;

	@BeforeEach
	void setUp() throws ParseException {
		EvalSkus evalSkus = new EvalSkus();
		evalSkus.setSku("abcd");
		evalSkus.setProductDescription("efgh");

		EvalSkus evalSkus1 = new EvalSkus();
		evalSkus1.setSku("hijk");
		evalSkus1.setProductDescription("lmop");

		EvalSkus evalSkus2 = new EvalSkus();
		evalSkus2.setSku("qrst");
		evalSkus2.setProductDescription("uvwx");
		evalSkusList = new ArrayList<EvalSkus>();
		evalSkusList.add(evalSkus);
		evalSkusList.add(evalSkus1);
		evalSkusList.add(evalSkus2);

		evalSkusRepository.saveAll(evalSkusList);
	}

	@AfterEach
	public void destroyAll() {
		evalSkusRepository.deleteAll();
	}

	@Test
	public void testFindBySkuAndProductDescription() {
		List<EvalSkus> evalSkus = evalSkusRepository.findBySkuAndProductDescription(evalSkusList.get(0).getSku(),
				evalSkusList.get(0).getProductDescription());
		assertEquals(evalSkusList.get(0).getSku(), evalSkus.get(0).getSku());
	}

}
