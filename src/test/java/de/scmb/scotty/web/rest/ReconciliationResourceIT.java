package de.scmb.scotty.web.rest;

import static de.scmb.scotty.domain.ReconciliationAsserts.*;
import static de.scmb.scotty.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReconciliationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReconciliationResourceIT {

    private static final String DEFAULT_MANDATE_ID = "AAAAAAAAAA";
    private static final String UPDATED_MANDATE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_ID = "BBBBBBBBBB";

    private static final Gateway DEFAULT_GATEWAY = Gateway.BANKINGCIRCLE;
    private static final Gateway UPDATED_GATEWAY = Gateway.CCBILL;

    private static final String DEFAULT_IBAN = "AAAAAAAAAAAAAAAA";
    private static final String UPDATED_IBAN = "BBBBBBBBBBBBBBBB";

    private static final String DEFAULT_BIC = "AAAAAAAAAA";
    private static final String UPDATED_BIC = "BBBBBBBBBB";

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;
    private static final Integer SMALLER_AMOUNT = 1 - 1;

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final String DEFAULT_SOFT_DESCRIPTOR = "AAAAAAAAAA";
    private static final String UPDATED_SOFT_DESCRIPTOR = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STREET_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STREET_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HOUSE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_HOUSE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AA";
    private static final String UPDATED_COUNTRY_CODE = "BB";

    private static final String DEFAULT_REMOTE_IP = "AAAAAAAAAA";
    private static final String UPDATED_REMOTE_IP = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_REASON_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REASON_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_GATEWAY_ID = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_MODE = "AAAAAAAAAA";
    private static final String UPDATED_MODE = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CREDITOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CREDITOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CREDITOR_IBAN = "AAAAAAAAAAAAAAAA";
    private static final String UPDATED_CREDITOR_IBAN = "BBBBBBBBBBBBBBBB";

    private static final String DEFAULT_CREDITOR_BIC = "AAAAAAAAAA";
    private static final String UPDATED_CREDITOR_BIC = "BBBBBBBBBB";

    private static final String DEFAULT_CREDITOR_ID = "AAAAAAAAAA";
    private static final String UPDATED_CREDITOR_ID = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_MANDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MANDATE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_MANDATE_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_EXECUTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXECUTION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EXECUTION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_PAYMENT_INFORMATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_INFORMATION_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reconciliations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReconciliationRepository reconciliationRepository;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReconciliationMockMvc;

    private Reconciliation reconciliation;

    private Reconciliation insertedReconciliation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reconciliation createEntity() {
        return new Reconciliation()
            .mandateId(DEFAULT_MANDATE_ID)
            .paymentId(DEFAULT_PAYMENT_ID)
            .gateway(DEFAULT_GATEWAY)
            .iban(DEFAULT_IBAN)
            .bic(DEFAULT_BIC)
            .amount(DEFAULT_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .softDescriptor(DEFAULT_SOFT_DESCRIPTOR)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .streetName(DEFAULT_STREET_NAME)
            .houseNumber(DEFAULT_HOUSE_NUMBER)
            .postalCode(DEFAULT_POSTAL_CODE)
            .city(DEFAULT_CITY)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .remoteIp(DEFAULT_REMOTE_IP)
            .emailAddress(DEFAULT_EMAIL_ADDRESS)
            .timestamp(DEFAULT_TIMESTAMP)
            .state(DEFAULT_STATE)
            .reasonCode(DEFAULT_REASON_CODE)
            .message(DEFAULT_MESSAGE)
            .gatewayId(DEFAULT_GATEWAY_ID)
            .mode(DEFAULT_MODE)
            .fileName(DEFAULT_FILE_NAME)
            .creditorName(DEFAULT_CREDITOR_NAME)
            .creditorIban(DEFAULT_CREDITOR_IBAN)
            .creditorBic(DEFAULT_CREDITOR_BIC)
            .creditorId(DEFAULT_CREDITOR_ID)
            .mandateDate(DEFAULT_MANDATE_DATE)
            .executionDate(DEFAULT_EXECUTION_DATE)
            .paymentInformationId(DEFAULT_PAYMENT_INFORMATION_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reconciliation createUpdatedEntity() {
        return new Reconciliation()
            .mandateId(UPDATED_MANDATE_ID)
            .paymentId(UPDATED_PAYMENT_ID)
            .gateway(UPDATED_GATEWAY)
            .iban(UPDATED_IBAN)
            .bic(UPDATED_BIC)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .softDescriptor(UPDATED_SOFT_DESCRIPTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .streetName(UPDATED_STREET_NAME)
            .houseNumber(UPDATED_HOUSE_NUMBER)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .reasonCode(UPDATED_REASON_CODE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME)
            .creditorName(UPDATED_CREDITOR_NAME)
            .creditorIban(UPDATED_CREDITOR_IBAN)
            .creditorBic(UPDATED_CREDITOR_BIC)
            .creditorId(UPDATED_CREDITOR_ID)
            .mandateDate(UPDATED_MANDATE_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .paymentInformationId(UPDATED_PAYMENT_INFORMATION_ID);
    }

    @BeforeEach
    public void initTest() {
        reconciliation = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReconciliation != null) {
            reconciliationRepository.delete(insertedReconciliation);
            insertedReconciliation = null;
        }
    }

    @Test
    @Transactional
    void createReconciliation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);
        var returnedReconciliationDTO = om.readValue(
            restReconciliationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReconciliationDTO.class
        );

        // Validate the Reconciliation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReconciliation = reconciliationMapper.toEntity(returnedReconciliationDTO);
        assertReconciliationUpdatableFieldsEquals(returnedReconciliation, getPersistedReconciliation(returnedReconciliation));

        insertedReconciliation = returnedReconciliation;
    }

    @Test
    @Transactional
    void createReconciliationWithExistingId() throws Exception {
        // Create the Reconciliation with an existing ID
        reconciliation.setId(1L);
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMandateIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setMandateId(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setPaymentId(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setGateway(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIbanIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setIban(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBicIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setBic(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setAmount(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setCurrencyCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSoftDescriptorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setSoftDescriptor(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setFirstName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setLastName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStreetNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setStreetName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHouseNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setHouseNumber(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setPostalCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setCity(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setCountryCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemoteIpIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setRemoteIp(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setEmailAddress(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setTimestamp(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setState(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setReasonCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reconciliation.setMessage(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReconciliations() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].mandateId").value(hasItem(DEFAULT_MANDATE_ID)))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].gateway").value(hasItem(DEFAULT_GATEWAY.toString())))
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)))
            .andExpect(jsonPath("$.[*].bic").value(hasItem(DEFAULT_BIC)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].softDescriptor").value(hasItem(DEFAULT_SOFT_DESCRIPTOR)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].streetName").value(hasItem(DEFAULT_STREET_NAME)))
            .andExpect(jsonPath("$.[*].houseNumber").value(hasItem(DEFAULT_HOUSE_NUMBER)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].remoteIp").value(hasItem(DEFAULT_REMOTE_IP)))
            .andExpect(jsonPath("$.[*].emailAddress").value(hasItem(DEFAULT_EMAIL_ADDRESS)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].reasonCode").value(hasItem(DEFAULT_REASON_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].gatewayId").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].creditorName").value(hasItem(DEFAULT_CREDITOR_NAME)))
            .andExpect(jsonPath("$.[*].creditorIban").value(hasItem(DEFAULT_CREDITOR_IBAN)))
            .andExpect(jsonPath("$.[*].creditorBic").value(hasItem(DEFAULT_CREDITOR_BIC)))
            .andExpect(jsonPath("$.[*].creditorId").value(hasItem(DEFAULT_CREDITOR_ID)))
            .andExpect(jsonPath("$.[*].mandateDate").value(hasItem(DEFAULT_MANDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].executionDate").value(hasItem(DEFAULT_EXECUTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentInformationId").value(hasItem(DEFAULT_PAYMENT_INFORMATION_ID)));
    }

    @Test
    @Transactional
    void getReconciliation() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get the reconciliation
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL_ID, reconciliation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reconciliation.getId().intValue()))
            .andExpect(jsonPath("$.mandateId").value(DEFAULT_MANDATE_ID))
            .andExpect(jsonPath("$.paymentId").value(DEFAULT_PAYMENT_ID))
            .andExpect(jsonPath("$.gateway").value(DEFAULT_GATEWAY.toString()))
            .andExpect(jsonPath("$.iban").value(DEFAULT_IBAN))
            .andExpect(jsonPath("$.bic").value(DEFAULT_BIC))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE))
            .andExpect(jsonPath("$.softDescriptor").value(DEFAULT_SOFT_DESCRIPTOR))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.streetName").value(DEFAULT_STREET_NAME))
            .andExpect(jsonPath("$.houseNumber").value(DEFAULT_HOUSE_NUMBER))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.remoteIp").value(DEFAULT_REMOTE_IP))
            .andExpect(jsonPath("$.emailAddress").value(DEFAULT_EMAIL_ADDRESS))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.reasonCode").value(DEFAULT_REASON_CODE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.gatewayId").value(DEFAULT_GATEWAY_ID))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.creditorName").value(DEFAULT_CREDITOR_NAME))
            .andExpect(jsonPath("$.creditorIban").value(DEFAULT_CREDITOR_IBAN))
            .andExpect(jsonPath("$.creditorBic").value(DEFAULT_CREDITOR_BIC))
            .andExpect(jsonPath("$.creditorId").value(DEFAULT_CREDITOR_ID))
            .andExpect(jsonPath("$.mandateDate").value(DEFAULT_MANDATE_DATE.toString()))
            .andExpect(jsonPath("$.executionDate").value(DEFAULT_EXECUTION_DATE.toString()))
            .andExpect(jsonPath("$.paymentInformationId").value(DEFAULT_PAYMENT_INFORMATION_ID));
    }

    @Test
    @Transactional
    void getReconciliationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        Long id = reconciliation.getId();

        defaultReconciliationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReconciliationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReconciliationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId equals to
        defaultReconciliationFiltering("mandateId.equals=" + DEFAULT_MANDATE_ID, "mandateId.equals=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId in
        defaultReconciliationFiltering(
            "mandateId.in=" + DEFAULT_MANDATE_ID + "," + UPDATED_MANDATE_ID,
            "mandateId.in=" + UPDATED_MANDATE_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId is not null
        defaultReconciliationFiltering("mandateId.specified=true", "mandateId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId contains
        defaultReconciliationFiltering("mandateId.contains=" + DEFAULT_MANDATE_ID, "mandateId.contains=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId does not contain
        defaultReconciliationFiltering("mandateId.doesNotContain=" + UPDATED_MANDATE_ID, "mandateId.doesNotContain=" + DEFAULT_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId equals to
        defaultReconciliationFiltering("paymentId.equals=" + DEFAULT_PAYMENT_ID, "paymentId.equals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId in
        defaultReconciliationFiltering(
            "paymentId.in=" + DEFAULT_PAYMENT_ID + "," + UPDATED_PAYMENT_ID,
            "paymentId.in=" + UPDATED_PAYMENT_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId is not null
        defaultReconciliationFiltering("paymentId.specified=true", "paymentId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId contains
        defaultReconciliationFiltering("paymentId.contains=" + DEFAULT_PAYMENT_ID, "paymentId.contains=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId does not contain
        defaultReconciliationFiltering("paymentId.doesNotContain=" + UPDATED_PAYMENT_ID, "paymentId.doesNotContain=" + DEFAULT_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway equals to
        defaultReconciliationFiltering("gateway.equals=" + DEFAULT_GATEWAY, "gateway.equals=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway in
        defaultReconciliationFiltering("gateway.in=" + DEFAULT_GATEWAY + "," + UPDATED_GATEWAY, "gateway.in=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway is not null
        defaultReconciliationFiltering("gateway.specified=true", "gateway.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban equals to
        defaultReconciliationFiltering("iban.equals=" + DEFAULT_IBAN, "iban.equals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban in
        defaultReconciliationFiltering("iban.in=" + DEFAULT_IBAN + "," + UPDATED_IBAN, "iban.in=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban is not null
        defaultReconciliationFiltering("iban.specified=true", "iban.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban contains
        defaultReconciliationFiltering("iban.contains=" + DEFAULT_IBAN, "iban.contains=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban does not contain
        defaultReconciliationFiltering("iban.doesNotContain=" + UPDATED_IBAN, "iban.doesNotContain=" + DEFAULT_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic equals to
        defaultReconciliationFiltering("bic.equals=" + DEFAULT_BIC, "bic.equals=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic in
        defaultReconciliationFiltering("bic.in=" + DEFAULT_BIC + "," + UPDATED_BIC, "bic.in=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic is not null
        defaultReconciliationFiltering("bic.specified=true", "bic.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic contains
        defaultReconciliationFiltering("bic.contains=" + DEFAULT_BIC, "bic.contains=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic does not contain
        defaultReconciliationFiltering("bic.doesNotContain=" + UPDATED_BIC, "bic.doesNotContain=" + DEFAULT_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount equals to
        defaultReconciliationFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount in
        defaultReconciliationFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is not null
        defaultReconciliationFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is greater than or equal to
        defaultReconciliationFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is less than or equal to
        defaultReconciliationFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is less than
        defaultReconciliationFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is greater than
        defaultReconciliationFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode equals to
        defaultReconciliationFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode in
        defaultReconciliationFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode is not null
        defaultReconciliationFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode contains
        defaultReconciliationFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode does not contain
        defaultReconciliationFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor equals to
        defaultReconciliationFiltering(
            "softDescriptor.equals=" + DEFAULT_SOFT_DESCRIPTOR,
            "softDescriptor.equals=" + UPDATED_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor in
        defaultReconciliationFiltering(
            "softDescriptor.in=" + DEFAULT_SOFT_DESCRIPTOR + "," + UPDATED_SOFT_DESCRIPTOR,
            "softDescriptor.in=" + UPDATED_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor is not null
        defaultReconciliationFiltering("softDescriptor.specified=true", "softDescriptor.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor contains
        defaultReconciliationFiltering(
            "softDescriptor.contains=" + DEFAULT_SOFT_DESCRIPTOR,
            "softDescriptor.contains=" + UPDATED_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor does not contain
        defaultReconciliationFiltering(
            "softDescriptor.doesNotContain=" + UPDATED_SOFT_DESCRIPTOR,
            "softDescriptor.doesNotContain=" + DEFAULT_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName equals to
        defaultReconciliationFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName in
        defaultReconciliationFiltering(
            "firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME,
            "firstName.in=" + UPDATED_FIRST_NAME
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName is not null
        defaultReconciliationFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName contains
        defaultReconciliationFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName does not contain
        defaultReconciliationFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName equals to
        defaultReconciliationFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName in
        defaultReconciliationFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName is not null
        defaultReconciliationFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName contains
        defaultReconciliationFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName does not contain
        defaultReconciliationFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName equals to
        defaultReconciliationFiltering("streetName.equals=" + DEFAULT_STREET_NAME, "streetName.equals=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName in
        defaultReconciliationFiltering(
            "streetName.in=" + DEFAULT_STREET_NAME + "," + UPDATED_STREET_NAME,
            "streetName.in=" + UPDATED_STREET_NAME
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName is not null
        defaultReconciliationFiltering("streetName.specified=true", "streetName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName contains
        defaultReconciliationFiltering("streetName.contains=" + DEFAULT_STREET_NAME, "streetName.contains=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName does not contain
        defaultReconciliationFiltering(
            "streetName.doesNotContain=" + UPDATED_STREET_NAME,
            "streetName.doesNotContain=" + DEFAULT_STREET_NAME
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber equals to
        defaultReconciliationFiltering("houseNumber.equals=" + DEFAULT_HOUSE_NUMBER, "houseNumber.equals=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber in
        defaultReconciliationFiltering(
            "houseNumber.in=" + DEFAULT_HOUSE_NUMBER + "," + UPDATED_HOUSE_NUMBER,
            "houseNumber.in=" + UPDATED_HOUSE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber is not null
        defaultReconciliationFiltering("houseNumber.specified=true", "houseNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber contains
        defaultReconciliationFiltering("houseNumber.contains=" + DEFAULT_HOUSE_NUMBER, "houseNumber.contains=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber does not contain
        defaultReconciliationFiltering(
            "houseNumber.doesNotContain=" + UPDATED_HOUSE_NUMBER,
            "houseNumber.doesNotContain=" + DEFAULT_HOUSE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode equals to
        defaultReconciliationFiltering("postalCode.equals=" + DEFAULT_POSTAL_CODE, "postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode in
        defaultReconciliationFiltering(
            "postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE,
            "postalCode.in=" + UPDATED_POSTAL_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode is not null
        defaultReconciliationFiltering("postalCode.specified=true", "postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode contains
        defaultReconciliationFiltering("postalCode.contains=" + DEFAULT_POSTAL_CODE, "postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode does not contain
        defaultReconciliationFiltering(
            "postalCode.doesNotContain=" + UPDATED_POSTAL_CODE,
            "postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city equals to
        defaultReconciliationFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city in
        defaultReconciliationFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city is not null
        defaultReconciliationFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city contains
        defaultReconciliationFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city does not contain
        defaultReconciliationFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode equals to
        defaultReconciliationFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode in
        defaultReconciliationFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode is not null
        defaultReconciliationFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode contains
        defaultReconciliationFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode does not contain
        defaultReconciliationFiltering(
            "countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE,
            "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp equals to
        defaultReconciliationFiltering("remoteIp.equals=" + DEFAULT_REMOTE_IP, "remoteIp.equals=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp in
        defaultReconciliationFiltering("remoteIp.in=" + DEFAULT_REMOTE_IP + "," + UPDATED_REMOTE_IP, "remoteIp.in=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp is not null
        defaultReconciliationFiltering("remoteIp.specified=true", "remoteIp.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp contains
        defaultReconciliationFiltering("remoteIp.contains=" + DEFAULT_REMOTE_IP, "remoteIp.contains=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp does not contain
        defaultReconciliationFiltering("remoteIp.doesNotContain=" + UPDATED_REMOTE_IP, "remoteIp.doesNotContain=" + DEFAULT_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress equals to
        defaultReconciliationFiltering("emailAddress.equals=" + DEFAULT_EMAIL_ADDRESS, "emailAddress.equals=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress in
        defaultReconciliationFiltering(
            "emailAddress.in=" + DEFAULT_EMAIL_ADDRESS + "," + UPDATED_EMAIL_ADDRESS,
            "emailAddress.in=" + UPDATED_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress is not null
        defaultReconciliationFiltering("emailAddress.specified=true", "emailAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress contains
        defaultReconciliationFiltering("emailAddress.contains=" + DEFAULT_EMAIL_ADDRESS, "emailAddress.contains=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress does not contain
        defaultReconciliationFiltering(
            "emailAddress.doesNotContain=" + UPDATED_EMAIL_ADDRESS,
            "emailAddress.doesNotContain=" + DEFAULT_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp equals to
        defaultReconciliationFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp in
        defaultReconciliationFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp is not null
        defaultReconciliationFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state equals to
        defaultReconciliationFiltering("state.equals=" + DEFAULT_STATE, "state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state in
        defaultReconciliationFiltering("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE, "state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state is not null
        defaultReconciliationFiltering("state.specified=true", "state.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state contains
        defaultReconciliationFiltering("state.contains=" + DEFAULT_STATE, "state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state does not contain
        defaultReconciliationFiltering("state.doesNotContain=" + UPDATED_STATE, "state.doesNotContain=" + DEFAULT_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode equals to
        defaultReconciliationFiltering("reasonCode.equals=" + DEFAULT_REASON_CODE, "reasonCode.equals=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode in
        defaultReconciliationFiltering(
            "reasonCode.in=" + DEFAULT_REASON_CODE + "," + UPDATED_REASON_CODE,
            "reasonCode.in=" + UPDATED_REASON_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode is not null
        defaultReconciliationFiltering("reasonCode.specified=true", "reasonCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode contains
        defaultReconciliationFiltering("reasonCode.contains=" + DEFAULT_REASON_CODE, "reasonCode.contains=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode does not contain
        defaultReconciliationFiltering(
            "reasonCode.doesNotContain=" + UPDATED_REASON_CODE,
            "reasonCode.doesNotContain=" + DEFAULT_REASON_CODE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message equals to
        defaultReconciliationFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message in
        defaultReconciliationFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message is not null
        defaultReconciliationFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message contains
        defaultReconciliationFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message does not contain
        defaultReconciliationFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId equals to
        defaultReconciliationFiltering("gatewayId.equals=" + DEFAULT_GATEWAY_ID, "gatewayId.equals=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId in
        defaultReconciliationFiltering(
            "gatewayId.in=" + DEFAULT_GATEWAY_ID + "," + UPDATED_GATEWAY_ID,
            "gatewayId.in=" + UPDATED_GATEWAY_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId is not null
        defaultReconciliationFiltering("gatewayId.specified=true", "gatewayId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId contains
        defaultReconciliationFiltering("gatewayId.contains=" + DEFAULT_GATEWAY_ID, "gatewayId.contains=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId does not contain
        defaultReconciliationFiltering("gatewayId.doesNotContain=" + UPDATED_GATEWAY_ID, "gatewayId.doesNotContain=" + DEFAULT_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode equals to
        defaultReconciliationFiltering("mode.equals=" + DEFAULT_MODE, "mode.equals=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode in
        defaultReconciliationFiltering("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE, "mode.in=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode is not null
        defaultReconciliationFiltering("mode.specified=true", "mode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode contains
        defaultReconciliationFiltering("mode.contains=" + DEFAULT_MODE, "mode.contains=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode does not contain
        defaultReconciliationFiltering("mode.doesNotContain=" + UPDATED_MODE, "mode.doesNotContain=" + DEFAULT_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName equals to
        defaultReconciliationFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName in
        defaultReconciliationFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName is not null
        defaultReconciliationFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName contains
        defaultReconciliationFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName does not contain
        defaultReconciliationFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorName equals to
        defaultReconciliationFiltering("creditorName.equals=" + DEFAULT_CREDITOR_NAME, "creditorName.equals=" + UPDATED_CREDITOR_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorName in
        defaultReconciliationFiltering(
            "creditorName.in=" + DEFAULT_CREDITOR_NAME + "," + UPDATED_CREDITOR_NAME,
            "creditorName.in=" + UPDATED_CREDITOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorName is not null
        defaultReconciliationFiltering("creditorName.specified=true", "creditorName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorName contains
        defaultReconciliationFiltering("creditorName.contains=" + DEFAULT_CREDITOR_NAME, "creditorName.contains=" + UPDATED_CREDITOR_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorName does not contain
        defaultReconciliationFiltering(
            "creditorName.doesNotContain=" + UPDATED_CREDITOR_NAME,
            "creditorName.doesNotContain=" + DEFAULT_CREDITOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorIban equals to
        defaultReconciliationFiltering("creditorIban.equals=" + DEFAULT_CREDITOR_IBAN, "creditorIban.equals=" + UPDATED_CREDITOR_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIbanIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorIban in
        defaultReconciliationFiltering(
            "creditorIban.in=" + DEFAULT_CREDITOR_IBAN + "," + UPDATED_CREDITOR_IBAN,
            "creditorIban.in=" + UPDATED_CREDITOR_IBAN
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorIban is not null
        defaultReconciliationFiltering("creditorIban.specified=true", "creditorIban.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIbanContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorIban contains
        defaultReconciliationFiltering("creditorIban.contains=" + DEFAULT_CREDITOR_IBAN, "creditorIban.contains=" + UPDATED_CREDITOR_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIbanNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorIban does not contain
        defaultReconciliationFiltering(
            "creditorIban.doesNotContain=" + UPDATED_CREDITOR_IBAN,
            "creditorIban.doesNotContain=" + DEFAULT_CREDITOR_IBAN
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorBicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorBic equals to
        defaultReconciliationFiltering("creditorBic.equals=" + DEFAULT_CREDITOR_BIC, "creditorBic.equals=" + UPDATED_CREDITOR_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorBicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorBic in
        defaultReconciliationFiltering(
            "creditorBic.in=" + DEFAULT_CREDITOR_BIC + "," + UPDATED_CREDITOR_BIC,
            "creditorBic.in=" + UPDATED_CREDITOR_BIC
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorBic is not null
        defaultReconciliationFiltering("creditorBic.specified=true", "creditorBic.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorBicContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorBic contains
        defaultReconciliationFiltering("creditorBic.contains=" + DEFAULT_CREDITOR_BIC, "creditorBic.contains=" + UPDATED_CREDITOR_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorBicNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorBic does not contain
        defaultReconciliationFiltering(
            "creditorBic.doesNotContain=" + UPDATED_CREDITOR_BIC,
            "creditorBic.doesNotContain=" + DEFAULT_CREDITOR_BIC
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorId equals to
        defaultReconciliationFiltering("creditorId.equals=" + DEFAULT_CREDITOR_ID, "creditorId.equals=" + UPDATED_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorId in
        defaultReconciliationFiltering(
            "creditorId.in=" + DEFAULT_CREDITOR_ID + "," + UPDATED_CREDITOR_ID,
            "creditorId.in=" + UPDATED_CREDITOR_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorId is not null
        defaultReconciliationFiltering("creditorId.specified=true", "creditorId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIdContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorId contains
        defaultReconciliationFiltering("creditorId.contains=" + DEFAULT_CREDITOR_ID, "creditorId.contains=" + UPDATED_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCreditorIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where creditorId does not contain
        defaultReconciliationFiltering(
            "creditorId.doesNotContain=" + UPDATED_CREDITOR_ID,
            "creditorId.doesNotContain=" + DEFAULT_CREDITOR_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate equals to
        defaultReconciliationFiltering("mandateDate.equals=" + DEFAULT_MANDATE_DATE, "mandateDate.equals=" + UPDATED_MANDATE_DATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate in
        defaultReconciliationFiltering(
            "mandateDate.in=" + DEFAULT_MANDATE_DATE + "," + UPDATED_MANDATE_DATE,
            "mandateDate.in=" + UPDATED_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate is not null
        defaultReconciliationFiltering("mandateDate.specified=true", "mandateDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate is greater than or equal to
        defaultReconciliationFiltering(
            "mandateDate.greaterThanOrEqual=" + DEFAULT_MANDATE_DATE,
            "mandateDate.greaterThanOrEqual=" + UPDATED_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate is less than or equal to
        defaultReconciliationFiltering(
            "mandateDate.lessThanOrEqual=" + DEFAULT_MANDATE_DATE,
            "mandateDate.lessThanOrEqual=" + SMALLER_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate is less than
        defaultReconciliationFiltering("mandateDate.lessThan=" + UPDATED_MANDATE_DATE, "mandateDate.lessThan=" + DEFAULT_MANDATE_DATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateDate is greater than
        defaultReconciliationFiltering(
            "mandateDate.greaterThan=" + SMALLER_MANDATE_DATE,
            "mandateDate.greaterThan=" + DEFAULT_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate equals to
        defaultReconciliationFiltering("executionDate.equals=" + DEFAULT_EXECUTION_DATE, "executionDate.equals=" + UPDATED_EXECUTION_DATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate in
        defaultReconciliationFiltering(
            "executionDate.in=" + DEFAULT_EXECUTION_DATE + "," + UPDATED_EXECUTION_DATE,
            "executionDate.in=" + UPDATED_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate is not null
        defaultReconciliationFiltering("executionDate.specified=true", "executionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate is greater than or equal to
        defaultReconciliationFiltering(
            "executionDate.greaterThanOrEqual=" + DEFAULT_EXECUTION_DATE,
            "executionDate.greaterThanOrEqual=" + UPDATED_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate is less than or equal to
        defaultReconciliationFiltering(
            "executionDate.lessThanOrEqual=" + DEFAULT_EXECUTION_DATE,
            "executionDate.lessThanOrEqual=" + SMALLER_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate is less than
        defaultReconciliationFiltering(
            "executionDate.lessThan=" + UPDATED_EXECUTION_DATE,
            "executionDate.lessThan=" + DEFAULT_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByExecutionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where executionDate is greater than
        defaultReconciliationFiltering(
            "executionDate.greaterThan=" + SMALLER_EXECUTION_DATE,
            "executionDate.greaterThan=" + DEFAULT_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentInformationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentInformationId equals to
        defaultReconciliationFiltering(
            "paymentInformationId.equals=" + DEFAULT_PAYMENT_INFORMATION_ID,
            "paymentInformationId.equals=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentInformationIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentInformationId in
        defaultReconciliationFiltering(
            "paymentInformationId.in=" + DEFAULT_PAYMENT_INFORMATION_ID + "," + UPDATED_PAYMENT_INFORMATION_ID,
            "paymentInformationId.in=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentInformationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentInformationId is not null
        defaultReconciliationFiltering("paymentInformationId.specified=true", "paymentInformationId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentInformationIdContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentInformationId contains
        defaultReconciliationFiltering(
            "paymentInformationId.contains=" + DEFAULT_PAYMENT_INFORMATION_ID,
            "paymentInformationId.contains=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentInformationIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentInformationId does not contain
        defaultReconciliationFiltering(
            "paymentInformationId.doesNotContain=" + UPDATED_PAYMENT_INFORMATION_ID,
            "paymentInformationId.doesNotContain=" + DEFAULT_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllReconciliationsByScottyPaymentIsEqualToSomething() throws Exception {
        Payment scottyPayment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            reconciliationRepository.saveAndFlush(reconciliation);
            scottyPayment = PaymentResourceIT.createEntity();
        } else {
            scottyPayment = TestUtil.findAll(em, Payment.class).get(0);
        }
        em.persist(scottyPayment);
        em.flush();
        reconciliation.setScottyPayment(scottyPayment);
        reconciliationRepository.saveAndFlush(reconciliation);
        Long scottyPaymentId = scottyPayment.getId();
        // Get all the reconciliationList where scottyPayment equals to scottyPaymentId
        defaultReconciliationShouldBeFound("scottyPaymentId.equals=" + scottyPaymentId);

        // Get all the reconciliationList where scottyPayment equals to (scottyPaymentId + 1)
        defaultReconciliationShouldNotBeFound("scottyPaymentId.equals=" + (scottyPaymentId + 1));
    }

    private void defaultReconciliationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReconciliationShouldBeFound(shouldBeFound);
        defaultReconciliationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReconciliationShouldBeFound(String filter) throws Exception {
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reconciliation.getId().intValue())))
            .andExpect(jsonPath("$.[*].mandateId").value(hasItem(DEFAULT_MANDATE_ID)))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].gateway").value(hasItem(DEFAULT_GATEWAY.toString())))
            .andExpect(jsonPath("$.[*].iban").value(hasItem(DEFAULT_IBAN)))
            .andExpect(jsonPath("$.[*].bic").value(hasItem(DEFAULT_BIC)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE)))
            .andExpect(jsonPath("$.[*].softDescriptor").value(hasItem(DEFAULT_SOFT_DESCRIPTOR)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].streetName").value(hasItem(DEFAULT_STREET_NAME)))
            .andExpect(jsonPath("$.[*].houseNumber").value(hasItem(DEFAULT_HOUSE_NUMBER)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].remoteIp").value(hasItem(DEFAULT_REMOTE_IP)))
            .andExpect(jsonPath("$.[*].emailAddress").value(hasItem(DEFAULT_EMAIL_ADDRESS)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].reasonCode").value(hasItem(DEFAULT_REASON_CODE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].gatewayId").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].creditorName").value(hasItem(DEFAULT_CREDITOR_NAME)))
            .andExpect(jsonPath("$.[*].creditorIban").value(hasItem(DEFAULT_CREDITOR_IBAN)))
            .andExpect(jsonPath("$.[*].creditorBic").value(hasItem(DEFAULT_CREDITOR_BIC)))
            .andExpect(jsonPath("$.[*].creditorId").value(hasItem(DEFAULT_CREDITOR_ID)))
            .andExpect(jsonPath("$.[*].mandateDate").value(hasItem(DEFAULT_MANDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].executionDate").value(hasItem(DEFAULT_EXECUTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentInformationId").value(hasItem(DEFAULT_PAYMENT_INFORMATION_ID)));

        // Check, that the count call also returns 1
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReconciliationShouldNotBeFound(String filter) throws Exception {
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReconciliationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReconciliation() throws Exception {
        // Get the reconciliation
        restReconciliationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReconciliation() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reconciliation
        Reconciliation updatedReconciliation = reconciliationRepository.findById(reconciliation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReconciliation are not directly saved in db
        em.detach(updatedReconciliation);
        updatedReconciliation
            .mandateId(UPDATED_MANDATE_ID)
            .paymentId(UPDATED_PAYMENT_ID)
            .gateway(UPDATED_GATEWAY)
            .iban(UPDATED_IBAN)
            .bic(UPDATED_BIC)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .softDescriptor(UPDATED_SOFT_DESCRIPTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .streetName(UPDATED_STREET_NAME)
            .houseNumber(UPDATED_HOUSE_NUMBER)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .reasonCode(UPDATED_REASON_CODE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME)
            .creditorName(UPDATED_CREDITOR_NAME)
            .creditorIban(UPDATED_CREDITOR_IBAN)
            .creditorBic(UPDATED_CREDITOR_BIC)
            .creditorId(UPDATED_CREDITOR_ID)
            .mandateDate(UPDATED_MANDATE_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .paymentInformationId(UPDATED_PAYMENT_INFORMATION_ID);
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(updatedReconciliation);

        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reconciliationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReconciliationToMatchAllProperties(updatedReconciliation);
    }

    @Test
    @Transactional
    void putNonExistingReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReconciliationWithPatch() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reconciliation using partial update
        Reconciliation partialUpdatedReconciliation = new Reconciliation();
        partialUpdatedReconciliation.setId(reconciliation.getId());

        partialUpdatedReconciliation
            .mandateId(UPDATED_MANDATE_ID)
            .gateway(UPDATED_GATEWAY)
            .softDescriptor(UPDATED_SOFT_DESCRIPTOR)
            .lastName(UPDATED_LAST_NAME)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME)
            .creditorIban(UPDATED_CREDITOR_IBAN)
            .creditorBic(UPDATED_CREDITOR_BIC)
            .executionDate(UPDATED_EXECUTION_DATE)
            .paymentInformationId(UPDATED_PAYMENT_INFORMATION_ID);

        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReconciliationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReconciliation, reconciliation),
            getPersistedReconciliation(reconciliation)
        );
    }

    @Test
    @Transactional
    void fullUpdateReconciliationWithPatch() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reconciliation using partial update
        Reconciliation partialUpdatedReconciliation = new Reconciliation();
        partialUpdatedReconciliation.setId(reconciliation.getId());

        partialUpdatedReconciliation
            .mandateId(UPDATED_MANDATE_ID)
            .paymentId(UPDATED_PAYMENT_ID)
            .gateway(UPDATED_GATEWAY)
            .iban(UPDATED_IBAN)
            .bic(UPDATED_BIC)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .softDescriptor(UPDATED_SOFT_DESCRIPTOR)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .streetName(UPDATED_STREET_NAME)
            .houseNumber(UPDATED_HOUSE_NUMBER)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .reasonCode(UPDATED_REASON_CODE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME)
            .creditorName(UPDATED_CREDITOR_NAME)
            .creditorIban(UPDATED_CREDITOR_IBAN)
            .creditorBic(UPDATED_CREDITOR_BIC)
            .creditorId(UPDATED_CREDITOR_ID)
            .mandateDate(UPDATED_MANDATE_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .paymentInformationId(UPDATED_PAYMENT_INFORMATION_ID);

        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReconciliationUpdatableFieldsEquals(partialUpdatedReconciliation, getPersistedReconciliation(partialUpdatedReconciliation));
    }

    @Test
    @Transactional
    void patchNonExistingReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReconciliation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reconciliationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reconciliation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReconciliation() throws Exception {
        // Initialize the database
        insertedReconciliation = reconciliationRepository.saveAndFlush(reconciliation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reconciliation
        restReconciliationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reconciliation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reconciliationRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Reconciliation getPersistedReconciliation(Reconciliation reconciliation) {
        return reconciliationRepository.findById(reconciliation.getId()).orElseThrow();
    }

    protected void assertPersistedReconciliationToMatchAllProperties(Reconciliation expectedReconciliation) {
        assertReconciliationAllPropertiesEquals(expectedReconciliation, getPersistedReconciliation(expectedReconciliation));
    }

    protected void assertPersistedReconciliationToMatchUpdatableProperties(Reconciliation expectedReconciliation) {
        assertReconciliationAllUpdatablePropertiesEquals(expectedReconciliation, getPersistedReconciliation(expectedReconciliation));
    }
}
