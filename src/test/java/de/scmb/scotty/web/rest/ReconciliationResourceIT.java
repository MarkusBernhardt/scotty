package de.scmb.scotty.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.Reconciliation;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.service.dto.ReconciliationDTO;
import de.scmb.scotty.service.mapper.ReconciliationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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

    private static final Gateway DEFAULT_GATEWAY = Gateway.CCBILL;
    private static final Gateway UPDATED_GATEWAY = Gateway.EMERCHANTPAY;

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

    private static final String ENTITY_API_URL = "/api/reconciliations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReconciliationRepository reconciliationRepository;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReconciliationMockMvc;

    private Reconciliation reconciliation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reconciliation createEntity(EntityManager em) {
        Reconciliation reconciliation = new Reconciliation()
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
            .fileName(DEFAULT_FILE_NAME);
        return reconciliation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reconciliation createUpdatedEntity(EntityManager em) {
        Reconciliation reconciliation = new Reconciliation()
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
            .fileName(UPDATED_FILE_NAME);
        return reconciliation;
    }

    @BeforeEach
    public void initTest() {
        reconciliation = createEntity(em);
    }

    @Test
    @Transactional
    void createReconciliation() throws Exception {
        int databaseSizeBeforeCreate = reconciliationRepository.findAll().size();
        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);
        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeCreate + 1);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getMandateId()).isEqualTo(DEFAULT_MANDATE_ID);
        assertThat(testReconciliation.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testReconciliation.getGateway()).isEqualTo(DEFAULT_GATEWAY);
        assertThat(testReconciliation.getIban()).isEqualTo(DEFAULT_IBAN);
        assertThat(testReconciliation.getBic()).isEqualTo(DEFAULT_BIC);
        assertThat(testReconciliation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testReconciliation.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testReconciliation.getSoftDescriptor()).isEqualTo(DEFAULT_SOFT_DESCRIPTOR);
        assertThat(testReconciliation.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testReconciliation.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testReconciliation.getStreetName()).isEqualTo(DEFAULT_STREET_NAME);
        assertThat(testReconciliation.getHouseNumber()).isEqualTo(DEFAULT_HOUSE_NUMBER);
        assertThat(testReconciliation.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testReconciliation.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testReconciliation.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testReconciliation.getRemoteIp()).isEqualTo(DEFAULT_REMOTE_IP);
        assertThat(testReconciliation.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testReconciliation.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testReconciliation.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testReconciliation.getReasonCode()).isEqualTo(DEFAULT_REASON_CODE);
        assertThat(testReconciliation.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testReconciliation.getGatewayId()).isEqualTo(DEFAULT_GATEWAY_ID);
        assertThat(testReconciliation.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testReconciliation.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void createReconciliationWithExistingId() throws Exception {
        // Create the Reconciliation with an existing ID
        reconciliation.setId(1L);
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        int databaseSizeBeforeCreate = reconciliationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMandateIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setMandateId(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setPaymentId(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setGateway(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIbanIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setIban(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBicIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setBic(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setAmount(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setCurrencyCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSoftDescriptorIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setSoftDescriptor(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setFirstName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setLastName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStreetNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setStreetName(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHouseNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setHouseNumber(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setPostalCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setCity(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setCountryCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemoteIpIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setRemoteIp(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setEmailAddress(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setTimestamp(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setState(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setReasonCode(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = reconciliationRepository.findAll().size();
        // set the field null
        reconciliation.setMessage(null);

        // Create the Reconciliation, which fails.
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        restReconciliationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReconciliations() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

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
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));
    }

    @Test
    @Transactional
    void getReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

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
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME));
    }

    @Test
    @Transactional
    void getReconciliationsByIdFiltering() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        Long id = reconciliation.getId();

        defaultReconciliationShouldBeFound("id.equals=" + id);
        defaultReconciliationShouldNotBeFound("id.notEquals=" + id);

        defaultReconciliationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReconciliationShouldNotBeFound("id.greaterThan=" + id);

        defaultReconciliationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReconciliationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId equals to DEFAULT_MANDATE_ID
        defaultReconciliationShouldBeFound("mandateId.equals=" + DEFAULT_MANDATE_ID);

        // Get all the reconciliationList where mandateId equals to UPDATED_MANDATE_ID
        defaultReconciliationShouldNotBeFound("mandateId.equals=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId in DEFAULT_MANDATE_ID or UPDATED_MANDATE_ID
        defaultReconciliationShouldBeFound("mandateId.in=" + DEFAULT_MANDATE_ID + "," + UPDATED_MANDATE_ID);

        // Get all the reconciliationList where mandateId equals to UPDATED_MANDATE_ID
        defaultReconciliationShouldNotBeFound("mandateId.in=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId is not null
        defaultReconciliationShouldBeFound("mandateId.specified=true");

        // Get all the reconciliationList where mandateId is null
        defaultReconciliationShouldNotBeFound("mandateId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId contains DEFAULT_MANDATE_ID
        defaultReconciliationShouldBeFound("mandateId.contains=" + DEFAULT_MANDATE_ID);

        // Get all the reconciliationList where mandateId contains UPDATED_MANDATE_ID
        defaultReconciliationShouldNotBeFound("mandateId.contains=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMandateIdNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mandateId does not contain DEFAULT_MANDATE_ID
        defaultReconciliationShouldNotBeFound("mandateId.doesNotContain=" + DEFAULT_MANDATE_ID);

        // Get all the reconciliationList where mandateId does not contain UPDATED_MANDATE_ID
        defaultReconciliationShouldBeFound("mandateId.doesNotContain=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId equals to DEFAULT_PAYMENT_ID
        defaultReconciliationShouldBeFound("paymentId.equals=" + DEFAULT_PAYMENT_ID);

        // Get all the reconciliationList where paymentId equals to UPDATED_PAYMENT_ID
        defaultReconciliationShouldNotBeFound("paymentId.equals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId in DEFAULT_PAYMENT_ID or UPDATED_PAYMENT_ID
        defaultReconciliationShouldBeFound("paymentId.in=" + DEFAULT_PAYMENT_ID + "," + UPDATED_PAYMENT_ID);

        // Get all the reconciliationList where paymentId equals to UPDATED_PAYMENT_ID
        defaultReconciliationShouldNotBeFound("paymentId.in=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId is not null
        defaultReconciliationShouldBeFound("paymentId.specified=true");

        // Get all the reconciliationList where paymentId is null
        defaultReconciliationShouldNotBeFound("paymentId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId contains DEFAULT_PAYMENT_ID
        defaultReconciliationShouldBeFound("paymentId.contains=" + DEFAULT_PAYMENT_ID);

        // Get all the reconciliationList where paymentId contains UPDATED_PAYMENT_ID
        defaultReconciliationShouldNotBeFound("paymentId.contains=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPaymentIdNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where paymentId does not contain DEFAULT_PAYMENT_ID
        defaultReconciliationShouldNotBeFound("paymentId.doesNotContain=" + DEFAULT_PAYMENT_ID);

        // Get all the reconciliationList where paymentId does not contain UPDATED_PAYMENT_ID
        defaultReconciliationShouldBeFound("paymentId.doesNotContain=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway equals to DEFAULT_GATEWAY
        defaultReconciliationShouldBeFound("gateway.equals=" + DEFAULT_GATEWAY);

        // Get all the reconciliationList where gateway equals to UPDATED_GATEWAY
        defaultReconciliationShouldNotBeFound("gateway.equals=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway in DEFAULT_GATEWAY or UPDATED_GATEWAY
        defaultReconciliationShouldBeFound("gateway.in=" + DEFAULT_GATEWAY + "," + UPDATED_GATEWAY);

        // Get all the reconciliationList where gateway equals to UPDATED_GATEWAY
        defaultReconciliationShouldNotBeFound("gateway.in=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gateway is not null
        defaultReconciliationShouldBeFound("gateway.specified=true");

        // Get all the reconciliationList where gateway is null
        defaultReconciliationShouldNotBeFound("gateway.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban equals to DEFAULT_IBAN
        defaultReconciliationShouldBeFound("iban.equals=" + DEFAULT_IBAN);

        // Get all the reconciliationList where iban equals to UPDATED_IBAN
        defaultReconciliationShouldNotBeFound("iban.equals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban in DEFAULT_IBAN or UPDATED_IBAN
        defaultReconciliationShouldBeFound("iban.in=" + DEFAULT_IBAN + "," + UPDATED_IBAN);

        // Get all the reconciliationList where iban equals to UPDATED_IBAN
        defaultReconciliationShouldNotBeFound("iban.in=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban is not null
        defaultReconciliationShouldBeFound("iban.specified=true");

        // Get all the reconciliationList where iban is null
        defaultReconciliationShouldNotBeFound("iban.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban contains DEFAULT_IBAN
        defaultReconciliationShouldBeFound("iban.contains=" + DEFAULT_IBAN);

        // Get all the reconciliationList where iban contains UPDATED_IBAN
        defaultReconciliationShouldNotBeFound("iban.contains=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByIbanNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where iban does not contain DEFAULT_IBAN
        defaultReconciliationShouldNotBeFound("iban.doesNotContain=" + DEFAULT_IBAN);

        // Get all the reconciliationList where iban does not contain UPDATED_IBAN
        defaultReconciliationShouldBeFound("iban.doesNotContain=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic equals to DEFAULT_BIC
        defaultReconciliationShouldBeFound("bic.equals=" + DEFAULT_BIC);

        // Get all the reconciliationList where bic equals to UPDATED_BIC
        defaultReconciliationShouldNotBeFound("bic.equals=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic in DEFAULT_BIC or UPDATED_BIC
        defaultReconciliationShouldBeFound("bic.in=" + DEFAULT_BIC + "," + UPDATED_BIC);

        // Get all the reconciliationList where bic equals to UPDATED_BIC
        defaultReconciliationShouldNotBeFound("bic.in=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic is not null
        defaultReconciliationShouldBeFound("bic.specified=true");

        // Get all the reconciliationList where bic is null
        defaultReconciliationShouldNotBeFound("bic.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic contains DEFAULT_BIC
        defaultReconciliationShouldBeFound("bic.contains=" + DEFAULT_BIC);

        // Get all the reconciliationList where bic contains UPDATED_BIC
        defaultReconciliationShouldNotBeFound("bic.contains=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByBicNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where bic does not contain DEFAULT_BIC
        defaultReconciliationShouldNotBeFound("bic.doesNotContain=" + DEFAULT_BIC);

        // Get all the reconciliationList where bic does not contain UPDATED_BIC
        defaultReconciliationShouldBeFound("bic.doesNotContain=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount equals to DEFAULT_AMOUNT
        defaultReconciliationShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the reconciliationList where amount equals to UPDATED_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultReconciliationShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the reconciliationList where amount equals to UPDATED_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is not null
        defaultReconciliationShouldBeFound("amount.specified=true");

        // Get all the reconciliationList where amount is null
        defaultReconciliationShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultReconciliationShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the reconciliationList where amount is greater than or equal to UPDATED_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is less than or equal to DEFAULT_AMOUNT
        defaultReconciliationShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the reconciliationList where amount is less than or equal to SMALLER_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is less than DEFAULT_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the reconciliationList where amount is less than UPDATED_AMOUNT
        defaultReconciliationShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where amount is greater than DEFAULT_AMOUNT
        defaultReconciliationShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the reconciliationList where amount is greater than SMALLER_AMOUNT
        defaultReconciliationShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode equals to DEFAULT_CURRENCY_CODE
        defaultReconciliationShouldBeFound("currencyCode.equals=" + DEFAULT_CURRENCY_CODE);

        // Get all the reconciliationList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultReconciliationShouldNotBeFound("currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode in DEFAULT_CURRENCY_CODE or UPDATED_CURRENCY_CODE
        defaultReconciliationShouldBeFound("currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE);

        // Get all the reconciliationList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultReconciliationShouldNotBeFound("currencyCode.in=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode is not null
        defaultReconciliationShouldBeFound("currencyCode.specified=true");

        // Get all the reconciliationList where currencyCode is null
        defaultReconciliationShouldNotBeFound("currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode contains DEFAULT_CURRENCY_CODE
        defaultReconciliationShouldBeFound("currencyCode.contains=" + DEFAULT_CURRENCY_CODE);

        // Get all the reconciliationList where currencyCode contains UPDATED_CURRENCY_CODE
        defaultReconciliationShouldNotBeFound("currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where currencyCode does not contain DEFAULT_CURRENCY_CODE
        defaultReconciliationShouldNotBeFound("currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE);

        // Get all the reconciliationList where currencyCode does not contain UPDATED_CURRENCY_CODE
        defaultReconciliationShouldBeFound("currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor equals to DEFAULT_SOFT_DESCRIPTOR
        defaultReconciliationShouldBeFound("softDescriptor.equals=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the reconciliationList where softDescriptor equals to UPDATED_SOFT_DESCRIPTOR
        defaultReconciliationShouldNotBeFound("softDescriptor.equals=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor in DEFAULT_SOFT_DESCRIPTOR or UPDATED_SOFT_DESCRIPTOR
        defaultReconciliationShouldBeFound("softDescriptor.in=" + DEFAULT_SOFT_DESCRIPTOR + "," + UPDATED_SOFT_DESCRIPTOR);

        // Get all the reconciliationList where softDescriptor equals to UPDATED_SOFT_DESCRIPTOR
        defaultReconciliationShouldNotBeFound("softDescriptor.in=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor is not null
        defaultReconciliationShouldBeFound("softDescriptor.specified=true");

        // Get all the reconciliationList where softDescriptor is null
        defaultReconciliationShouldNotBeFound("softDescriptor.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor contains DEFAULT_SOFT_DESCRIPTOR
        defaultReconciliationShouldBeFound("softDescriptor.contains=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the reconciliationList where softDescriptor contains UPDATED_SOFT_DESCRIPTOR
        defaultReconciliationShouldNotBeFound("softDescriptor.contains=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllReconciliationsBySoftDescriptorNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where softDescriptor does not contain DEFAULT_SOFT_DESCRIPTOR
        defaultReconciliationShouldNotBeFound("softDescriptor.doesNotContain=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the reconciliationList where softDescriptor does not contain UPDATED_SOFT_DESCRIPTOR
        defaultReconciliationShouldBeFound("softDescriptor.doesNotContain=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName equals to DEFAULT_FIRST_NAME
        defaultReconciliationShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the reconciliationList where firstName equals to UPDATED_FIRST_NAME
        defaultReconciliationShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultReconciliationShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the reconciliationList where firstName equals to UPDATED_FIRST_NAME
        defaultReconciliationShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName is not null
        defaultReconciliationShouldBeFound("firstName.specified=true");

        // Get all the reconciliationList where firstName is null
        defaultReconciliationShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName contains DEFAULT_FIRST_NAME
        defaultReconciliationShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the reconciliationList where firstName contains UPDATED_FIRST_NAME
        defaultReconciliationShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where firstName does not contain DEFAULT_FIRST_NAME
        defaultReconciliationShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the reconciliationList where firstName does not contain UPDATED_FIRST_NAME
        defaultReconciliationShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName equals to DEFAULT_LAST_NAME
        defaultReconciliationShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the reconciliationList where lastName equals to UPDATED_LAST_NAME
        defaultReconciliationShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultReconciliationShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the reconciliationList where lastName equals to UPDATED_LAST_NAME
        defaultReconciliationShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName is not null
        defaultReconciliationShouldBeFound("lastName.specified=true");

        // Get all the reconciliationList where lastName is null
        defaultReconciliationShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName contains DEFAULT_LAST_NAME
        defaultReconciliationShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the reconciliationList where lastName contains UPDATED_LAST_NAME
        defaultReconciliationShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where lastName does not contain DEFAULT_LAST_NAME
        defaultReconciliationShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the reconciliationList where lastName does not contain UPDATED_LAST_NAME
        defaultReconciliationShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName equals to DEFAULT_STREET_NAME
        defaultReconciliationShouldBeFound("streetName.equals=" + DEFAULT_STREET_NAME);

        // Get all the reconciliationList where streetName equals to UPDATED_STREET_NAME
        defaultReconciliationShouldNotBeFound("streetName.equals=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName in DEFAULT_STREET_NAME or UPDATED_STREET_NAME
        defaultReconciliationShouldBeFound("streetName.in=" + DEFAULT_STREET_NAME + "," + UPDATED_STREET_NAME);

        // Get all the reconciliationList where streetName equals to UPDATED_STREET_NAME
        defaultReconciliationShouldNotBeFound("streetName.in=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName is not null
        defaultReconciliationShouldBeFound("streetName.specified=true");

        // Get all the reconciliationList where streetName is null
        defaultReconciliationShouldNotBeFound("streetName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName contains DEFAULT_STREET_NAME
        defaultReconciliationShouldBeFound("streetName.contains=" + DEFAULT_STREET_NAME);

        // Get all the reconciliationList where streetName contains UPDATED_STREET_NAME
        defaultReconciliationShouldNotBeFound("streetName.contains=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStreetNameNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where streetName does not contain DEFAULT_STREET_NAME
        defaultReconciliationShouldNotBeFound("streetName.doesNotContain=" + DEFAULT_STREET_NAME);

        // Get all the reconciliationList where streetName does not contain UPDATED_STREET_NAME
        defaultReconciliationShouldBeFound("streetName.doesNotContain=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber equals to DEFAULT_HOUSE_NUMBER
        defaultReconciliationShouldBeFound("houseNumber.equals=" + DEFAULT_HOUSE_NUMBER);

        // Get all the reconciliationList where houseNumber equals to UPDATED_HOUSE_NUMBER
        defaultReconciliationShouldNotBeFound("houseNumber.equals=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber in DEFAULT_HOUSE_NUMBER or UPDATED_HOUSE_NUMBER
        defaultReconciliationShouldBeFound("houseNumber.in=" + DEFAULT_HOUSE_NUMBER + "," + UPDATED_HOUSE_NUMBER);

        // Get all the reconciliationList where houseNumber equals to UPDATED_HOUSE_NUMBER
        defaultReconciliationShouldNotBeFound("houseNumber.in=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber is not null
        defaultReconciliationShouldBeFound("houseNumber.specified=true");

        // Get all the reconciliationList where houseNumber is null
        defaultReconciliationShouldNotBeFound("houseNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber contains DEFAULT_HOUSE_NUMBER
        defaultReconciliationShouldBeFound("houseNumber.contains=" + DEFAULT_HOUSE_NUMBER);

        // Get all the reconciliationList where houseNumber contains UPDATED_HOUSE_NUMBER
        defaultReconciliationShouldNotBeFound("houseNumber.contains=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByHouseNumberNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where houseNumber does not contain DEFAULT_HOUSE_NUMBER
        defaultReconciliationShouldNotBeFound("houseNumber.doesNotContain=" + DEFAULT_HOUSE_NUMBER);

        // Get all the reconciliationList where houseNumber does not contain UPDATED_HOUSE_NUMBER
        defaultReconciliationShouldBeFound("houseNumber.doesNotContain=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode equals to DEFAULT_POSTAL_CODE
        defaultReconciliationShouldBeFound("postalCode.equals=" + DEFAULT_POSTAL_CODE);

        // Get all the reconciliationList where postalCode equals to UPDATED_POSTAL_CODE
        defaultReconciliationShouldNotBeFound("postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode in DEFAULT_POSTAL_CODE or UPDATED_POSTAL_CODE
        defaultReconciliationShouldBeFound("postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE);

        // Get all the reconciliationList where postalCode equals to UPDATED_POSTAL_CODE
        defaultReconciliationShouldNotBeFound("postalCode.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode is not null
        defaultReconciliationShouldBeFound("postalCode.specified=true");

        // Get all the reconciliationList where postalCode is null
        defaultReconciliationShouldNotBeFound("postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode contains DEFAULT_POSTAL_CODE
        defaultReconciliationShouldBeFound("postalCode.contains=" + DEFAULT_POSTAL_CODE);

        // Get all the reconciliationList where postalCode contains UPDATED_POSTAL_CODE
        defaultReconciliationShouldNotBeFound("postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where postalCode does not contain DEFAULT_POSTAL_CODE
        defaultReconciliationShouldNotBeFound("postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE);

        // Get all the reconciliationList where postalCode does not contain UPDATED_POSTAL_CODE
        defaultReconciliationShouldBeFound("postalCode.doesNotContain=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city equals to DEFAULT_CITY
        defaultReconciliationShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the reconciliationList where city equals to UPDATED_CITY
        defaultReconciliationShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city in DEFAULT_CITY or UPDATED_CITY
        defaultReconciliationShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the reconciliationList where city equals to UPDATED_CITY
        defaultReconciliationShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city is not null
        defaultReconciliationShouldBeFound("city.specified=true");

        // Get all the reconciliationList where city is null
        defaultReconciliationShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city contains DEFAULT_CITY
        defaultReconciliationShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the reconciliationList where city contains UPDATED_CITY
        defaultReconciliationShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where city does not contain DEFAULT_CITY
        defaultReconciliationShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the reconciliationList where city does not contain UPDATED_CITY
        defaultReconciliationShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultReconciliationShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the reconciliationList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultReconciliationShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultReconciliationShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the reconciliationList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultReconciliationShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode is not null
        defaultReconciliationShouldBeFound("countryCode.specified=true");

        // Get all the reconciliationList where countryCode is null
        defaultReconciliationShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultReconciliationShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the reconciliationList where countryCode contains UPDATED_COUNTRY_CODE
        defaultReconciliationShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultReconciliationShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the reconciliationList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultReconciliationShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp equals to DEFAULT_REMOTE_IP
        defaultReconciliationShouldBeFound("remoteIp.equals=" + DEFAULT_REMOTE_IP);

        // Get all the reconciliationList where remoteIp equals to UPDATED_REMOTE_IP
        defaultReconciliationShouldNotBeFound("remoteIp.equals=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp in DEFAULT_REMOTE_IP or UPDATED_REMOTE_IP
        defaultReconciliationShouldBeFound("remoteIp.in=" + DEFAULT_REMOTE_IP + "," + UPDATED_REMOTE_IP);

        // Get all the reconciliationList where remoteIp equals to UPDATED_REMOTE_IP
        defaultReconciliationShouldNotBeFound("remoteIp.in=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp is not null
        defaultReconciliationShouldBeFound("remoteIp.specified=true");

        // Get all the reconciliationList where remoteIp is null
        defaultReconciliationShouldNotBeFound("remoteIp.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp contains DEFAULT_REMOTE_IP
        defaultReconciliationShouldBeFound("remoteIp.contains=" + DEFAULT_REMOTE_IP);

        // Get all the reconciliationList where remoteIp contains UPDATED_REMOTE_IP
        defaultReconciliationShouldNotBeFound("remoteIp.contains=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByRemoteIpNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where remoteIp does not contain DEFAULT_REMOTE_IP
        defaultReconciliationShouldNotBeFound("remoteIp.doesNotContain=" + DEFAULT_REMOTE_IP);

        // Get all the reconciliationList where remoteIp does not contain UPDATED_REMOTE_IP
        defaultReconciliationShouldBeFound("remoteIp.doesNotContain=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress equals to DEFAULT_EMAIL_ADDRESS
        defaultReconciliationShouldBeFound("emailAddress.equals=" + DEFAULT_EMAIL_ADDRESS);

        // Get all the reconciliationList where emailAddress equals to UPDATED_EMAIL_ADDRESS
        defaultReconciliationShouldNotBeFound("emailAddress.equals=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress in DEFAULT_EMAIL_ADDRESS or UPDATED_EMAIL_ADDRESS
        defaultReconciliationShouldBeFound("emailAddress.in=" + DEFAULT_EMAIL_ADDRESS + "," + UPDATED_EMAIL_ADDRESS);

        // Get all the reconciliationList where emailAddress equals to UPDATED_EMAIL_ADDRESS
        defaultReconciliationShouldNotBeFound("emailAddress.in=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress is not null
        defaultReconciliationShouldBeFound("emailAddress.specified=true");

        // Get all the reconciliationList where emailAddress is null
        defaultReconciliationShouldNotBeFound("emailAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress contains DEFAULT_EMAIL_ADDRESS
        defaultReconciliationShouldBeFound("emailAddress.contains=" + DEFAULT_EMAIL_ADDRESS);

        // Get all the reconciliationList where emailAddress contains UPDATED_EMAIL_ADDRESS
        defaultReconciliationShouldNotBeFound("emailAddress.contains=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByEmailAddressNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where emailAddress does not contain DEFAULT_EMAIL_ADDRESS
        defaultReconciliationShouldNotBeFound("emailAddress.doesNotContain=" + DEFAULT_EMAIL_ADDRESS);

        // Get all the reconciliationList where emailAddress does not contain UPDATED_EMAIL_ADDRESS
        defaultReconciliationShouldBeFound("emailAddress.doesNotContain=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp equals to DEFAULT_TIMESTAMP
        defaultReconciliationShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the reconciliationList where timestamp equals to UPDATED_TIMESTAMP
        defaultReconciliationShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultReconciliationShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the reconciliationList where timestamp equals to UPDATED_TIMESTAMP
        defaultReconciliationShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllReconciliationsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where timestamp is not null
        defaultReconciliationShouldBeFound("timestamp.specified=true");

        // Get all the reconciliationList where timestamp is null
        defaultReconciliationShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state equals to DEFAULT_STATE
        defaultReconciliationShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the reconciliationList where state equals to UPDATED_STATE
        defaultReconciliationShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state in DEFAULT_STATE or UPDATED_STATE
        defaultReconciliationShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the reconciliationList where state equals to UPDATED_STATE
        defaultReconciliationShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state is not null
        defaultReconciliationShouldBeFound("state.specified=true");

        // Get all the reconciliationList where state is null
        defaultReconciliationShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state contains DEFAULT_STATE
        defaultReconciliationShouldBeFound("state.contains=" + DEFAULT_STATE);

        // Get all the reconciliationList where state contains UPDATED_STATE
        defaultReconciliationShouldNotBeFound("state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByStateNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where state does not contain DEFAULT_STATE
        defaultReconciliationShouldNotBeFound("state.doesNotContain=" + DEFAULT_STATE);

        // Get all the reconciliationList where state does not contain UPDATED_STATE
        defaultReconciliationShouldBeFound("state.doesNotContain=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode equals to DEFAULT_REASON_CODE
        defaultReconciliationShouldBeFound("reasonCode.equals=" + DEFAULT_REASON_CODE);

        // Get all the reconciliationList where reasonCode equals to UPDATED_REASON_CODE
        defaultReconciliationShouldNotBeFound("reasonCode.equals=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode in DEFAULT_REASON_CODE or UPDATED_REASON_CODE
        defaultReconciliationShouldBeFound("reasonCode.in=" + DEFAULT_REASON_CODE + "," + UPDATED_REASON_CODE);

        // Get all the reconciliationList where reasonCode equals to UPDATED_REASON_CODE
        defaultReconciliationShouldNotBeFound("reasonCode.in=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode is not null
        defaultReconciliationShouldBeFound("reasonCode.specified=true");

        // Get all the reconciliationList where reasonCode is null
        defaultReconciliationShouldNotBeFound("reasonCode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode contains DEFAULT_REASON_CODE
        defaultReconciliationShouldBeFound("reasonCode.contains=" + DEFAULT_REASON_CODE);

        // Get all the reconciliationList where reasonCode contains UPDATED_REASON_CODE
        defaultReconciliationShouldNotBeFound("reasonCode.contains=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByReasonCodeNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where reasonCode does not contain DEFAULT_REASON_CODE
        defaultReconciliationShouldNotBeFound("reasonCode.doesNotContain=" + DEFAULT_REASON_CODE);

        // Get all the reconciliationList where reasonCode does not contain UPDATED_REASON_CODE
        defaultReconciliationShouldBeFound("reasonCode.doesNotContain=" + UPDATED_REASON_CODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message equals to DEFAULT_MESSAGE
        defaultReconciliationShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the reconciliationList where message equals to UPDATED_MESSAGE
        defaultReconciliationShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultReconciliationShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the reconciliationList where message equals to UPDATED_MESSAGE
        defaultReconciliationShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message is not null
        defaultReconciliationShouldBeFound("message.specified=true");

        // Get all the reconciliationList where message is null
        defaultReconciliationShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message contains DEFAULT_MESSAGE
        defaultReconciliationShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the reconciliationList where message contains UPDATED_MESSAGE
        defaultReconciliationShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where message does not contain DEFAULT_MESSAGE
        defaultReconciliationShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the reconciliationList where message does not contain UPDATED_MESSAGE
        defaultReconciliationShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId equals to DEFAULT_GATEWAY_ID
        defaultReconciliationShouldBeFound("gatewayId.equals=" + DEFAULT_GATEWAY_ID);

        // Get all the reconciliationList where gatewayId equals to UPDATED_GATEWAY_ID
        defaultReconciliationShouldNotBeFound("gatewayId.equals=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId in DEFAULT_GATEWAY_ID or UPDATED_GATEWAY_ID
        defaultReconciliationShouldBeFound("gatewayId.in=" + DEFAULT_GATEWAY_ID + "," + UPDATED_GATEWAY_ID);

        // Get all the reconciliationList where gatewayId equals to UPDATED_GATEWAY_ID
        defaultReconciliationShouldNotBeFound("gatewayId.in=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId is not null
        defaultReconciliationShouldBeFound("gatewayId.specified=true");

        // Get all the reconciliationList where gatewayId is null
        defaultReconciliationShouldNotBeFound("gatewayId.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId contains DEFAULT_GATEWAY_ID
        defaultReconciliationShouldBeFound("gatewayId.contains=" + DEFAULT_GATEWAY_ID);

        // Get all the reconciliationList where gatewayId contains UPDATED_GATEWAY_ID
        defaultReconciliationShouldNotBeFound("gatewayId.contains=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByGatewayIdNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where gatewayId does not contain DEFAULT_GATEWAY_ID
        defaultReconciliationShouldNotBeFound("gatewayId.doesNotContain=" + DEFAULT_GATEWAY_ID);

        // Get all the reconciliationList where gatewayId does not contain UPDATED_GATEWAY_ID
        defaultReconciliationShouldBeFound("gatewayId.doesNotContain=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode equals to DEFAULT_MODE
        defaultReconciliationShouldBeFound("mode.equals=" + DEFAULT_MODE);

        // Get all the reconciliationList where mode equals to UPDATED_MODE
        defaultReconciliationShouldNotBeFound("mode.equals=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode in DEFAULT_MODE or UPDATED_MODE
        defaultReconciliationShouldBeFound("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE);

        // Get all the reconciliationList where mode equals to UPDATED_MODE
        defaultReconciliationShouldNotBeFound("mode.in=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode is not null
        defaultReconciliationShouldBeFound("mode.specified=true");

        // Get all the reconciliationList where mode is null
        defaultReconciliationShouldNotBeFound("mode.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode contains DEFAULT_MODE
        defaultReconciliationShouldBeFound("mode.contains=" + DEFAULT_MODE);

        // Get all the reconciliationList where mode contains UPDATED_MODE
        defaultReconciliationShouldNotBeFound("mode.contains=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByModeNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where mode does not contain DEFAULT_MODE
        defaultReconciliationShouldNotBeFound("mode.doesNotContain=" + DEFAULT_MODE);

        // Get all the reconciliationList where mode does not contain UPDATED_MODE
        defaultReconciliationShouldBeFound("mode.doesNotContain=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName equals to DEFAULT_FILE_NAME
        defaultReconciliationShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the reconciliationList where fileName equals to UPDATED_FILE_NAME
        defaultReconciliationShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultReconciliationShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the reconciliationList where fileName equals to UPDATED_FILE_NAME
        defaultReconciliationShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName is not null
        defaultReconciliationShouldBeFound("fileName.specified=true");

        // Get all the reconciliationList where fileName is null
        defaultReconciliationShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName contains DEFAULT_FILE_NAME
        defaultReconciliationShouldBeFound("fileName.contains=" + DEFAULT_FILE_NAME);

        // Get all the reconciliationList where fileName contains UPDATED_FILE_NAME
        defaultReconciliationShouldNotBeFound("fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        // Get all the reconciliationList where fileName does not contain DEFAULT_FILE_NAME
        defaultReconciliationShouldNotBeFound("fileName.doesNotContain=" + DEFAULT_FILE_NAME);

        // Get all the reconciliationList where fileName does not contain UPDATED_FILE_NAME
        defaultReconciliationShouldBeFound("fileName.doesNotContain=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllReconciliationsByScottyPaymentIsEqualToSomething() throws Exception {
        Payment scottyPayment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            reconciliationRepository.saveAndFlush(reconciliation);
            scottyPayment = PaymentResourceIT.createEntity(em);
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
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));

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
        reconciliationRepository.saveAndFlush(reconciliation);

        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();

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
            .fileName(UPDATED_FILE_NAME);
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(updatedReconciliation);

        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getMandateId()).isEqualTo(UPDATED_MANDATE_ID);
        assertThat(testReconciliation.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testReconciliation.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testReconciliation.getIban()).isEqualTo(UPDATED_IBAN);
        assertThat(testReconciliation.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testReconciliation.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testReconciliation.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testReconciliation.getSoftDescriptor()).isEqualTo(UPDATED_SOFT_DESCRIPTOR);
        assertThat(testReconciliation.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testReconciliation.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testReconciliation.getStreetName()).isEqualTo(UPDATED_STREET_NAME);
        assertThat(testReconciliation.getHouseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER);
        assertThat(testReconciliation.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testReconciliation.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testReconciliation.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testReconciliation.getRemoteIp()).isEqualTo(UPDATED_REMOTE_IP);
        assertThat(testReconciliation.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testReconciliation.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testReconciliation.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testReconciliation.getReasonCode()).isEqualTo(UPDATED_REASON_CODE);
        assertThat(testReconciliation.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReconciliation.getGatewayId()).isEqualTo(UPDATED_GATEWAY_ID);
        assertThat(testReconciliation.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testReconciliation.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void putNonExistingReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReconciliationWithPatch() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();

        // Update the reconciliation using partial update
        Reconciliation partialUpdatedReconciliation = new Reconciliation();
        partialUpdatedReconciliation.setId(reconciliation.getId());

        partialUpdatedReconciliation
            .mandateId(UPDATED_MANDATE_ID)
            .gateway(UPDATED_GATEWAY)
            .bic(UPDATED_BIC)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .houseNumber(UPDATED_HOUSE_NUMBER)
            .postalCode(UPDATED_POSTAL_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .reasonCode(UPDATED_REASON_CODE)
            .message(UPDATED_MESSAGE)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME);

        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getMandateId()).isEqualTo(UPDATED_MANDATE_ID);
        assertThat(testReconciliation.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testReconciliation.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testReconciliation.getIban()).isEqualTo(DEFAULT_IBAN);
        assertThat(testReconciliation.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testReconciliation.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testReconciliation.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testReconciliation.getSoftDescriptor()).isEqualTo(DEFAULT_SOFT_DESCRIPTOR);
        assertThat(testReconciliation.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testReconciliation.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testReconciliation.getStreetName()).isEqualTo(DEFAULT_STREET_NAME);
        assertThat(testReconciliation.getHouseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER);
        assertThat(testReconciliation.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testReconciliation.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testReconciliation.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testReconciliation.getRemoteIp()).isEqualTo(DEFAULT_REMOTE_IP);
        assertThat(testReconciliation.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testReconciliation.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testReconciliation.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testReconciliation.getReasonCode()).isEqualTo(UPDATED_REASON_CODE);
        assertThat(testReconciliation.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReconciliation.getGatewayId()).isEqualTo(DEFAULT_GATEWAY_ID);
        assertThat(testReconciliation.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testReconciliation.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void fullUpdateReconciliationWithPatch() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();

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
            .fileName(UPDATED_FILE_NAME);

        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReconciliation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReconciliation))
            )
            .andExpect(status().isOk());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
        Reconciliation testReconciliation = reconciliationList.get(reconciliationList.size() - 1);
        assertThat(testReconciliation.getMandateId()).isEqualTo(UPDATED_MANDATE_ID);
        assertThat(testReconciliation.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testReconciliation.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testReconciliation.getIban()).isEqualTo(UPDATED_IBAN);
        assertThat(testReconciliation.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testReconciliation.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testReconciliation.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testReconciliation.getSoftDescriptor()).isEqualTo(UPDATED_SOFT_DESCRIPTOR);
        assertThat(testReconciliation.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testReconciliation.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testReconciliation.getStreetName()).isEqualTo(UPDATED_STREET_NAME);
        assertThat(testReconciliation.getHouseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER);
        assertThat(testReconciliation.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testReconciliation.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testReconciliation.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testReconciliation.getRemoteIp()).isEqualTo(UPDATED_REMOTE_IP);
        assertThat(testReconciliation.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testReconciliation.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testReconciliation.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testReconciliation.getReasonCode()).isEqualTo(UPDATED_REASON_CODE);
        assertThat(testReconciliation.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReconciliation.getGatewayId()).isEqualTo(UPDATED_GATEWAY_ID);
        assertThat(testReconciliation.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testReconciliation.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reconciliationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReconciliation() throws Exception {
        int databaseSizeBeforeUpdate = reconciliationRepository.findAll().size();
        reconciliation.setId(longCount.incrementAndGet());

        // Create the Reconciliation
        ReconciliationDTO reconciliationDTO = reconciliationMapper.toDto(reconciliation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReconciliationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reconciliationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reconciliation in the database
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReconciliation() throws Exception {
        // Initialize the database
        reconciliationRepository.saveAndFlush(reconciliation);

        int databaseSizeBeforeDelete = reconciliationRepository.findAll().size();

        // Delete the reconciliation
        restReconciliationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reconciliation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reconciliation> reconciliationList = reconciliationRepository.findAll();
        assertThat(reconciliationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
