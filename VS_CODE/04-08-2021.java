package gov.va.services.claimant.controller;


import gov.va.services.claimant.model.Claimant;
import gov.va.services.claimant.model.ContactInfo;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.model.claimant.ContactInfoDto;
import gov.va.services.claimant.repository.*;
import gov.va.services.claimant.service.ClaimantService;
import gov.va.services.claimant.service.ClaimantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClaimantIntegrationTest {

    private ClaimantController claimantController;

    private  ClaimantService claimantService ;

    private   Claimant claimantModel = new Claimant();
    private   ClaimantDto claimantDto = new ClaimantDto();
    private ContactInfo contactInfoModel = new ContactInfo();
    private ContactInfoDto contactInfoDto=new ContactInfoDto();

    @Mock
    private ContactInfoRepository contactInfoRepository;


    @Mock
   private ModelMapper modelMapper ;

    @Mock
    ClaimantRepository claimantRepository;
    @Mock
    PersonCommentRepository personCommentRepository;
    @Mock
    PhoneRepository phoneRepository;


    @BeforeEach
    public void initMocks() {
       MockitoAnnotations.initMocks(this);
       claimantService = new ClaimantServiceImpl(modelMapper,contactInfoRepository,claimantRepository,personCommentRepository,phoneRepository);
    }

    @Test
    void saveClaimant() {
        claimantController=new ClaimantController(claimantService);
        Mockito.when(modelMapper.map(claimantDto, Claimant.class)).thenReturn(claimantModel);
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
        claimantModel.setDeleted(new BigDecimal(0));
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


package gov.va.services.claimant.service;

import gov.va.services.claimant.model.Claimant;
import gov.va.services.claimant.model.ContactInfo;
import gov.va.services.claimant.model.PersonComment;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.model.claimant.ContactInfoDto;
import gov.va.services.claimant.model.claimant.PersonCommentDto;
import gov.va.services.claimant.repository.ClaimantRepository;
import gov.va.services.claimant.repository.ContactInfoRepository;

import gov.va.services.claimant.repository.PersonCommentRepository;
import io.swagger.v3.oas.models.info.Contact;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.configuration.injection.MockInjection;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClaimantServiceImplTest {


    @InjectMocks
    ClaimantServiceImpl claimantService;

    @Mock
    ClaimantRepository claimantRepository;

    @Mock
    ContactInfoRepository contactInfoRepository;

    @Mock
    PersonCommentRepository personCommentRepository;

    @Mock
    private ModelMapper modelMapper = new ModelMapper();

    private ClaimantDto claimantDto = new ClaimantDto();
    private ContactInfoDto contactInfoDto = new ContactInfoDto();
    private ContactInfo contactInfoModel = new ContactInfo();
    private Claimant claimantModel = new Claimant();
    private PersonCommentDto personCommentDto = new PersonCommentDto();
    private PersonComment personComment = new PersonComment();

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void saveClaimant() {
        claimantDto.setPersonId(new BigDecimal(1));
        claimantDto.setFirstName("New Test First Name");
        claimantDto.setContactInfos(Arrays.asList(contactInfoDto));
        Mockito.when(modelMapper.map(claimantDto, Claimant.class)).thenReturn(claimantModel);
        Mockito.when(claimantRepository.save(Mockito.any())).thenReturn(claimantModel);
        ClaimantDto expectedDto = claimantService.saveClaimant(claimantDto);
        Mockito.verify(claimantRepository).save(claimantModel);
    }

    @Test
    void getClaimantByclaimantId() {

        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(claimantDto);
        claimantDto.setPersonId(new BigDecimal(1));
        claimantDto.setFirstName("Testing First Name");

        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        ClaimantDto result = claimantService.getClaimantByclaimantId(1L);
        assertEquals("Testing First Name", result.getFirstName(), "First Name Are Not Equal");
    }

    @Test
    void updateClaimant() {

        claimantModel.setPersonId(new BigDecimal(1));
        claimantModel.setFirstName("Test First Name");

        claimantDto.setPersonId(new BigDecimal(1));
        claimantDto.setFirstName("New Test First Name");

        Mockito.when(modelMapper.map(claimantDto, Claimant.class)).thenReturn(claimantModel);
        Mockito.when(claimantRepository.save(Mockito.any())).thenReturn(claimantModel);

        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        ClaimantDto expectedDto = claimantService.updateClaimant(1L, claimantDto);
        Mockito.verify(claimantRepository).save(claimantModel);
        Mockito.verify(claimantRepository).findById(1L);
    }

    @Test
    void deleteClaimant() {
        Claimant claimant = new Claimant();
        claimant.setPersonId(new BigDecimal(1));
        claimant.setFirstName("Testing Delete Claimant");
        claimant.setDeleted(new BigDecimal(0));
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimant));
        Long claimantId = claimant.getPersonId().longValue();
        claimantService.deleteClaimant(claimantId);
        Mockito.verify(claimantRepository).save(claimant);
    }

    @Test
    void getContactInfo() {
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(contactInfoDto);

        contactInfoDto.setAddressLine1("Test Address");
        contactInfoDto.setCity("MD");

        Mockito.when(contactInfoRepository.findByClaimant_ClaimantKey(Mockito.anyLong()))
                .thenReturn(Optional.of(Arrays.asList(contactInfoModel)));
        List<ContactInfoDto> result = claimantService.getContactInfo(1L);
        assertEquals(1, result.size(), "size Are Not Equal");
    }

    @Test
    void saveComments() {
        Mockito.when(modelMapper.map(personCommentDto, PersonComment.class)).thenReturn(personComment);
        Mockito.when(personCommentRepository.saveAll(Arrays.asList(personComment))).thenReturn(Arrays.asList(personComment));
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        claimantService.saveComments(Mockito.anyLong(), Arrays.asList(personCommentDto));
        Mockito.verify(personCommentRepository).saveAll(Arrays.asList(personComment));
    }

    @Test
    void updateComments() {

        personComment.setComments("Test First Comment");
        personCommentDto.setComments("Test Comment");

        Mockito.when(modelMapper.map(personCommentDto, PersonComment.class)).thenReturn(personComment);
        Mockito.when(personCommentRepository.save(personComment)).thenReturn(personComment);
        Mockito.when(personCommentRepository.findPersonCommentByPersonCommentKeyAndClaimant_ClaimantKey(1l, 1l)).thenReturn(Optional.of(personComment));
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.of(claimantModel));
        PersonCommentDto result = claimantService.updateComment(1L, 1L, personCommentDto);
        Mockito.verify(personCommentRepository).save(personComment);

    }

}

package gov.va.services.claimant.controller;

import gov.va.services.claimant.exception.ClaimantNotFoundException;
import gov.va.services.claimant.exception.NoSuchElementFoundException;
import gov.va.services.claimant.model.claimant.ClaimantDto;
import gov.va.services.claimant.model.claimant.ContactInfoDto;
import gov.va.services.claimant.repository.ClaimantRepository;
import gov.va.services.claimant.service.ClaimantService;
import gov.va.services.claimant.service.ClaimantServiceImpl;
import gov.va.services.claimant.service.ResponseClaimant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class ClaimantControllerTest {

    long claimantId=1L;
    private ClaimantController claimantController;
    @Mock
    private ClaimantService claimantService;


    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
        claimantController=new ClaimantController(claimantService);
    }

    @Test
    void whenSaveClaimantStatusIsOK() {
        Mockito.when(claimantService.saveClaimant(any(ClaimantDto.class))).thenReturn(ResponseClaimant.getClaimantDto());
        ClaimantDto claimantDto=new ClaimantDto();
        assertEquals(claimantController.saveClaimant(claimantDto).getStatusCode(), HttpStatus.OK,"Response Status is not equal");
    }

    @Test
    void getClaimantWithOKStatus() {
        Mockito.when(claimantService.getClaimantByclaimantId(anyLong())).thenReturn(ResponseClaimant.getClaimantDto());
         assertEquals(claimantController.getClaimant(claimantId).getStatusCode(),HttpStatus.OK,"Response Status is not equal");
    }
    @Test
    void getClaimantWithNoStatusException() {
        Mockito.when(claimantService.getClaimantByclaimantId(anyLong())).thenThrow(NoSuchElementFoundException.class);
        Assertions.assertThrows(NoSuchElementFoundException.class,()->claimantController.getClaimant(claimantId),"ClaimantNotFoundException was not thrown");
    }

    @Test
    void updateClaimantwithStatusOk() {
        Mockito.when(claimantService.updateClaimant(anyLong(),any(ClaimantDto.class))).thenReturn(ResponseClaimant.getClaimantDto());
        ClaimantDto claimantDto=new ClaimantDto();
        assertEquals(claimantController.updateClaimant(claimantId,claimantDto).getStatusCode(),HttpStatus.OK,"Response Status is not equal");
    }
    @Test
    void updateClaimantWhereIdNotFound() {
        Mockito.when(claimantService.updateClaimant(anyLong(),any(ClaimantDto.class))).thenThrow(NoSuchElementFoundException.class);
        Assertions.assertThrows(NoSuchElementFoundException.class,()->claimantController.updateClaimant(claimantId,new ClaimantDto()),"ClaimantNotFoundException was not thrown");
    }
    @Test
    void deleteClaimantWhereStatusIsOk(){
        Mockito.when(claimantService.deleteClaimant(anyLong())).thenReturn(claimantId);
        assertEquals(claimantController.deleteClaimant(claimantId).getBody(),claimantId,"The Claimant is Not Equal");
    }
    @Test
    void deleteClaimantWhereClaimantNotFound(){
        Mockito.when(claimantService.deleteClaimant(anyLong())).thenThrow(ClaimantNotFoundException.class);
        Assertions.assertThrows(ClaimantNotFoundException.class,()->claimantController.deleteClaimant(claimantId),"ClaimantNotFoundException was not thrown");

    }

    @Test
    void getContactInfoWhenStatusOk() {
        Mockito.when(claimantService.getContactInfo(anyLong())).thenReturn(ResponseClaimant.getContactInfos());
        assertEquals(claimantController.getContactInfo(claimantId).getStatusCode(),HttpStatus.OK,"Response Status is not equal");

    }
    @Test
    void getContactInfoWhenStatusSendException() {
        Mockito.when(claimantService.getContactInfo(anyLong())).thenThrow(NoSuchElementFoundException.class);
        Assertions.assertThrows(NoSuchElementFoundException.class,()->claimantController.getContactInfo(claimantId),"NoSuchElementFoundException was not thrown");

    }

}

     @Test
    public void saveCommentsWhereClaimantIdNotFound(){
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.empty());
      ClaimantNotFoundException expected =   assertThrows(ClaimantNotFoundException.class,()->{  claimantService.saveComments(1L, Arrays.asList(personCommentDto));});
        assertEquals(expected.getMessage(), CommonConstant.NO_CLAIMANT_FOUND.getKey()+":"+1L);
    }

    @Test
    public void updateCommentsWhereClaimantIdNotFound(){
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.empty());
        ClaimantNotFoundException expected =  assertThrows(ClaimantNotFoundException.class,()->{  claimantService.updateComment(1L,1L, personCommentDto);});
        assertEquals(expected.getMessage(), CommonConstant.NO_CLAIMANT_FOUND.getKey()+":"+1L);
    }

    @Test
    public void updateCommentsWhereNoPersonWithCommentId(){
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.of(claimantModel));
        Mockito.when(personCommentRepository.findPersonCommentByPersonCommentKeyAndClaimant_ClaimantKey(1l, 1l)).thenReturn(Optional.empty());
       PersonCommentNotFoundException expected =  assertThrows(PersonCommentNotFoundException.class,()->{  claimantService.updateComment(1L,1L, personCommentDto);});
        assertEquals(expected.getMessage(), CommonConstant.NO_PERSON_WITH_COMMENTID.getKey()+":"+1L);
    }

    @Test
    public void saveClaimantWherePersonCommentsIsNull(){
        claimantModel.setPersonComments(null);
        Mockito.when(modelMapper.map(claimantDto, Claimant.class)).thenReturn(claimantModel);
        Mockito.when(claimantRepository.save(Mockito.any())).thenReturn(claimantModel);
        ClaimantDto expectedDto = claimantService.saveClaimant(claimantDto);
        Mockito.verify(claimantRepository).save(claimantModel);
    }

    @Test
    public void getClaimantWhereClaimantIdIsNull()
    {
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementFoundException.class,()->{  claimantService.getClaimantByclaimantId(1L);});
    }

       @Test
    public void updateClaimantWhereClaimantIdIsNull()
    {
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        NoSuchElementFoundException expected =   assertThrows(NoSuchElementFoundException.class,()->{  claimantService.updateClaimant(1L,claimantDto);});
        assertEquals(expected.getMessage(), CommonConstant.NO_CLAIMANT_FOUND.getKey()+":"+1L);

    }

       @Test
    public void deleteClaimantWithClaimantDeleteException()
    {
        Claimant claimant = new Claimant();
        claimant.setPersonId(new BigDecimal(1));
        claimant.setFirstName("Testing Delete Claimant");
        claimant.setDeleted(new BigDecimal(1));
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimant));
        ClaimantDeleteException expected =   assertThrows(ClaimantDeleteException.class,()->{  claimantService.deleteClaimant(1L);});
        assertEquals(expected.getMessage(), CommonConstant.CLAIMANT_ALREADY_DELETED.getKey()+":"+1L);
    }

      @Test
    public void getContactInfoWhenContactInfoNotFoundByClaimantId()
    {
        Mockito.when(contactInfoRepository.findByClaimant_ClaimantKey(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        NoSuchElementFoundException expected =   assertThrows(NoSuchElementFoundException.class,()->{  claimantService.getContactInfo(1L);});
        assertEquals(expected.getMessage(), CommonConstant.NO_CONTACT_INFO.getKey()+":"+1L);
    }

       @Test
    public void saveContactInfoWhereClaimantNotFoundById()
    {
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.empty());
        ClaimantNotFoundException expected =  assertThrows(ClaimantNotFoundException.class,()->{  claimantService.saveContactInfo(1L,contactInfoDto);});
        assertEquals(expected.getMessage(), CommonConstant.NO_CLAIMANT_FOUND.getKey()+":"+1L);
    }

       @Test
    void saveContactInfo(){
        Mockito.when(modelMapper.map(contactInfoDto, ContactInfo.class)).thenReturn(contactInfoModel);
        Mockito.when(claimantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(claimantModel));
        Mockito.when(contactInfoRepository.save(contactInfoModel)).thenReturn(contactInfoModel);
        claimantService.saveContactInfo(1L,contactInfoDto);
        Mockito.verify(contactInfoRepository).save(contactInfoModel);
    }