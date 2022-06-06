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
        Mockito.when(modelMapper.map(contactInfoDto, ContactInfo.class)).thenReturn(contactInfoModel);
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
        Mockito.when(personCommentRepository.findPersonCommentByPersonCommentKeyAndClaimant_ClaimantKey(1l,1l)).thenReturn(Optional.of(personComment));
        Mockito.when(claimantRepository.findById(1L)).thenReturn(Optional.of(claimantModel));
        PersonCommentDto result =  claimantService.updateComment(1L,1L,personCommentDto);
        Mockito.verify(personCommentRepository).save(personComment);

    }

}