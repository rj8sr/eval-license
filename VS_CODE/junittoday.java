package gov.va.services.claimant.controller;


import gov.va.services.claimant.model.Claimant;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.repository.ClaimantRepository;
import gov.va.services.claimant.service.ClaimantService;
import gov.va.services.claimant.service.ClaimantServiceImpl;
import gov.va.services.claimant.service.ResponseClaimant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ClaimantIntegrationTest {

    private ClaimantController claimantController;


  private  ClaimantService claimantService ;

   private ClaimantDto claimantDto = new ClaimantDto();
   private Claimant claimantModel = new Claimant();

   private ModelMapper modelMapper = new ModelMapper();

    @Mock
    ClaimantRepository claimantRepository;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        claimantController=new ClaimantController(claimantService);
    }

    @Test
    void saveClaimant() {

        claimantDto.setPersonId(new BigDecimal(1));
        claimantDto.setFirstName("New Test First Name");
//        Mockito.when(modelMapper.map(claimantDto,Claimant.class)).thenReturn(claimantModel);
     //   Mockito.when(claimantService.saveClaimant(claimantDto)).thenReturn(claimantDto);

        claimantRepository.save(claimantModel);
    }

    @Test
    void getClaimant() {
        Mockito.when(modelMapper.map(Mockito.any(),Mockito.any())).thenReturn(claimantDto);

        claimantDto.setPersonId(new BigDecimal(1));
        claimantDto.setFirstName("Testing First Name");

        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        ClaimantDto result = claimantService.getClaimantByclaimantId(1L);


    }

    @Test
    void updateClaimant() {
    }

    @Test
    void deleteClaimant() {
    }

    @Test
    void getContactInfo() {
    }
}
