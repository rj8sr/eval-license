package gov.va.services.claimant.controller;


import gov.va.services.claimant.model.Claimant;
import gov.va.services.claimant.model.ContactInfo;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.repository.ClaimantRepository;
import gov.va.services.claimant.repository.ContactInfoRepository;
import gov.va.services.claimant.service.ClaimantService;
import gov.va.services.claimant.service.ClaimantServiceImpl;
import gov.va.services.claimant.service.ResponseClaimant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClaimantIntegrationTest {

    private ClaimantController claimantController;

    private  ClaimantService claimantService ;

    private   Claimant claimantModel = new Claimant();
    private   ClaimantDto claimantDto = new ClaimantDto();
    private ContactInfo contactInfoModel = new ContactInfo();

    @Mock
    private ContactInfoRepository contactInfoRepository;


    @Mock
   private ModelMapper modelMapper ;

    @Mock
    ClaimantRepository claimantRepository;

@BeforeEach
    public void initMocks() {
       MockitoAnnotations.initMocks(this);
       claimantService = new ClaimantServiceImpl(modelMapper,contactInfoRepository,claimantRepository);
    }

    @Test
    void saveClaimant() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(claimantRepository.save(Mockito.any(Claimant.class))).thenReturn(claimantModel);
        assertEquals(claimantController.saveClaimant(claimantDto).getStatusCode(),HttpStatus.OK);
    }

    @Test
    void getClaimant() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        assertEquals(claimantController.getClaimant(1L).getStatusCode(),HttpStatus.OK);
    }

    @Test
    void updateClaimant() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(modelMapper.map(claimantDto, Claimant.class)).thenReturn(claimantModel);
        Mockito.when(claimantRepository.save(Mockito.any(Claimant.class))).thenReturn(claimantModel);
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.of(claimantModel));
      assertEquals(claimantController.updateClaimant(1L,claimantDto).getStatusCode(),HttpStatus.OK);

    }

    @Test
    void deleteClaimant() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));     
        assertEquals(claimantController.deleteClaimant(1L).getStatusCode(),HttpStatus.OK);
}

    @Test
    void getContactInfo() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(contactInfoRepository.findByClaimant_ClaimantKey(Mockito.anyLong())).thenReturn(Optional.of(Arrays.asList(contactInfoModel)));
        assertEquals(claimantController.getContactInfo(1L).getStatusCode(),HttpStatus.OK);
    }
}