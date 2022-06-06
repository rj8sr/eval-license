package gov.va.services.claimant.controller;


import gov.va.services.claimant.model.Claimant;
import gov.va.services.claimant.model.ContactInfo;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.model.claimant.ContactInfoDto;
import gov.va.services.claimant.repository.ClaimantRepository;
import gov.va.services.claimant.repository.ContactInfoRepository;
import gov.va.services.claimant.repository.PersonCommentRepository;
import gov.va.services.claimant.service.ClaimantService;
import gov.va.services.claimant.service.ClaimantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @Mock
    PersonCommentRepository personCommentRepository;

    private ContactInfoDto contactInfoDto=new ContactInfoDto();



    @BeforeEach
    public void initMocks() {
       MockitoAnnotations.initMocks(this);
       claimantService = new ClaimantServiceImpl(modelMapper,contactInfoRepository,claimantRepository,personCommentRepository);
    }

    @Test
    void saveClaimant() {
        claimantController=new ClaimantController(claimantService);
        claimantDto.setContactInfos(Arrays.asList(contactInfoDto));    
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
