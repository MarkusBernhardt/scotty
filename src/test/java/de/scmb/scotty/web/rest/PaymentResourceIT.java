package de.scmb.scotty.web.rest;

import static de.scmb.scotty.domain.PaymentAsserts.*;
import static de.scmb.scotty.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.mapper.PaymentMapper;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentResourceIT {

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

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    private Payment insertedPayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity() {
        return new Payment()
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
    public static Payment createUpdatedEntity() {
        return new Payment()
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
        payment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPayment != null) {
            paymentRepository.delete(insertedPayment);
            insertedPayment = null;
        }
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        var returnedPaymentDTO = om.readValue(
            restPaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentDTO.class
        );

        // Validate the Payment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPayment = paymentMapper.toEntity(returnedPaymentDTO);
        assertPaymentUpdatableFieldsEquals(returnedPayment, getPersistedPayment(returnedPayment));

        insertedPayment = returnedPayment;
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMandateIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setMandateId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setPaymentId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setGateway(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIbanIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setIban(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBicIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setBic(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setCurrencyCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSoftDescriptorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setSoftDescriptor(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setFirstName(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setLastName(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStreetNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setStreetName(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHouseNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setHouseNumber(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setPostalCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setCity(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setCountryCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemoteIpIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setRemoteIp(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setEmailAddress(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setTimestamp(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setState(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setMessage(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
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
    void getPayment() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
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
    void getPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        Long id = payment.getId();

        defaultPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId equals to
        defaultPaymentFiltering("mandateId.equals=" + DEFAULT_MANDATE_ID, "mandateId.equals=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId in
        defaultPaymentFiltering("mandateId.in=" + DEFAULT_MANDATE_ID + "," + UPDATED_MANDATE_ID, "mandateId.in=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId is not null
        defaultPaymentFiltering("mandateId.specified=true", "mandateId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId contains
        defaultPaymentFiltering("mandateId.contains=" + DEFAULT_MANDATE_ID, "mandateId.contains=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId does not contain
        defaultPaymentFiltering("mandateId.doesNotContain=" + UPDATED_MANDATE_ID, "mandateId.doesNotContain=" + DEFAULT_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId equals to
        defaultPaymentFiltering("paymentId.equals=" + DEFAULT_PAYMENT_ID, "paymentId.equals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId in
        defaultPaymentFiltering("paymentId.in=" + DEFAULT_PAYMENT_ID + "," + UPDATED_PAYMENT_ID, "paymentId.in=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId is not null
        defaultPaymentFiltering("paymentId.specified=true", "paymentId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId contains
        defaultPaymentFiltering("paymentId.contains=" + DEFAULT_PAYMENT_ID, "paymentId.contains=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId does not contain
        defaultPaymentFiltering("paymentId.doesNotContain=" + UPDATED_PAYMENT_ID, "paymentId.doesNotContain=" + DEFAULT_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway equals to
        defaultPaymentFiltering("gateway.equals=" + DEFAULT_GATEWAY, "gateway.equals=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway in
        defaultPaymentFiltering("gateway.in=" + DEFAULT_GATEWAY + "," + UPDATED_GATEWAY, "gateway.in=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway is not null
        defaultPaymentFiltering("gateway.specified=true", "gateway.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban equals to
        defaultPaymentFiltering("iban.equals=" + DEFAULT_IBAN, "iban.equals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban in
        defaultPaymentFiltering("iban.in=" + DEFAULT_IBAN + "," + UPDATED_IBAN, "iban.in=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban is not null
        defaultPaymentFiltering("iban.specified=true", "iban.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban contains
        defaultPaymentFiltering("iban.contains=" + DEFAULT_IBAN, "iban.contains=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban does not contain
        defaultPaymentFiltering("iban.doesNotContain=" + UPDATED_IBAN, "iban.doesNotContain=" + DEFAULT_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic equals to
        defaultPaymentFiltering("bic.equals=" + DEFAULT_BIC, "bic.equals=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic in
        defaultPaymentFiltering("bic.in=" + DEFAULT_BIC + "," + UPDATED_BIC, "bic.in=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic is not null
        defaultPaymentFiltering("bic.specified=true", "bic.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByBicContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic contains
        defaultPaymentFiltering("bic.contains=" + DEFAULT_BIC, "bic.contains=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic does not contain
        defaultPaymentFiltering("bic.doesNotContain=" + UPDATED_BIC, "bic.doesNotContain=" + DEFAULT_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount equals to
        defaultPaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount in
        defaultPaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is not null
        defaultPaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is greater than or equal to
        defaultPaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is less than or equal to
        defaultPaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is less than
        defaultPaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is greater than
        defaultPaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode equals to
        defaultPaymentFiltering("currencyCode.equals=" + DEFAULT_CURRENCY_CODE, "currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode in
        defaultPaymentFiltering(
            "currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE,
            "currencyCode.in=" + UPDATED_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode is not null
        defaultPaymentFiltering("currencyCode.specified=true", "currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode contains
        defaultPaymentFiltering("currencyCode.contains=" + DEFAULT_CURRENCY_CODE, "currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode does not contain
        defaultPaymentFiltering(
            "currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE,
            "currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor equals to
        defaultPaymentFiltering("softDescriptor.equals=" + DEFAULT_SOFT_DESCRIPTOR, "softDescriptor.equals=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor in
        defaultPaymentFiltering(
            "softDescriptor.in=" + DEFAULT_SOFT_DESCRIPTOR + "," + UPDATED_SOFT_DESCRIPTOR,
            "softDescriptor.in=" + UPDATED_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor is not null
        defaultPaymentFiltering("softDescriptor.specified=true", "softDescriptor.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor contains
        defaultPaymentFiltering("softDescriptor.contains=" + DEFAULT_SOFT_DESCRIPTOR, "softDescriptor.contains=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor does not contain
        defaultPaymentFiltering(
            "softDescriptor.doesNotContain=" + UPDATED_SOFT_DESCRIPTOR,
            "softDescriptor.doesNotContain=" + DEFAULT_SOFT_DESCRIPTOR
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName equals to
        defaultPaymentFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName in
        defaultPaymentFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName is not null
        defaultPaymentFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName contains
        defaultPaymentFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName does not contain
        defaultPaymentFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName equals to
        defaultPaymentFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName in
        defaultPaymentFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName is not null
        defaultPaymentFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName contains
        defaultPaymentFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName does not contain
        defaultPaymentFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByStreetNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where streetName equals to
        defaultPaymentFiltering("streetName.equals=" + DEFAULT_STREET_NAME, "streetName.equals=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByStreetNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where streetName in
        defaultPaymentFiltering("streetName.in=" + DEFAULT_STREET_NAME + "," + UPDATED_STREET_NAME, "streetName.in=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByStreetNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where streetName is not null
        defaultPaymentFiltering("streetName.specified=true", "streetName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByStreetNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where streetName contains
        defaultPaymentFiltering("streetName.contains=" + DEFAULT_STREET_NAME, "streetName.contains=" + UPDATED_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByStreetNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where streetName does not contain
        defaultPaymentFiltering("streetName.doesNotContain=" + UPDATED_STREET_NAME, "streetName.doesNotContain=" + DEFAULT_STREET_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByHouseNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where houseNumber equals to
        defaultPaymentFiltering("houseNumber.equals=" + DEFAULT_HOUSE_NUMBER, "houseNumber.equals=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentsByHouseNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where houseNumber in
        defaultPaymentFiltering(
            "houseNumber.in=" + DEFAULT_HOUSE_NUMBER + "," + UPDATED_HOUSE_NUMBER,
            "houseNumber.in=" + UPDATED_HOUSE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByHouseNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where houseNumber is not null
        defaultPaymentFiltering("houseNumber.specified=true", "houseNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByHouseNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where houseNumber contains
        defaultPaymentFiltering("houseNumber.contains=" + DEFAULT_HOUSE_NUMBER, "houseNumber.contains=" + UPDATED_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentsByHouseNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where houseNumber does not contain
        defaultPaymentFiltering("houseNumber.doesNotContain=" + UPDATED_HOUSE_NUMBER, "houseNumber.doesNotContain=" + DEFAULT_HOUSE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode equals to
        defaultPaymentFiltering("postalCode.equals=" + DEFAULT_POSTAL_CODE, "postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode in
        defaultPaymentFiltering("postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE, "postalCode.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode is not null
        defaultPaymentFiltering("postalCode.specified=true", "postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode contains
        defaultPaymentFiltering("postalCode.contains=" + DEFAULT_POSTAL_CODE, "postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode does not contain
        defaultPaymentFiltering("postalCode.doesNotContain=" + UPDATED_POSTAL_CODE, "postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city equals to
        defaultPaymentFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city in
        defaultPaymentFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city is not null
        defaultPaymentFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city contains
        defaultPaymentFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city does not contain
        defaultPaymentFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode equals to
        defaultPaymentFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode in
        defaultPaymentFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode is not null
        defaultPaymentFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode contains
        defaultPaymentFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode does not contain
        defaultPaymentFiltering("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE, "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp equals to
        defaultPaymentFiltering("remoteIp.equals=" + DEFAULT_REMOTE_IP, "remoteIp.equals=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp in
        defaultPaymentFiltering("remoteIp.in=" + DEFAULT_REMOTE_IP + "," + UPDATED_REMOTE_IP, "remoteIp.in=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp is not null
        defaultPaymentFiltering("remoteIp.specified=true", "remoteIp.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp contains
        defaultPaymentFiltering("remoteIp.contains=" + DEFAULT_REMOTE_IP, "remoteIp.contains=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp does not contain
        defaultPaymentFiltering("remoteIp.doesNotContain=" + UPDATED_REMOTE_IP, "remoteIp.doesNotContain=" + DEFAULT_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByEmailAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where emailAddress equals to
        defaultPaymentFiltering("emailAddress.equals=" + DEFAULT_EMAIL_ADDRESS, "emailAddress.equals=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPaymentsByEmailAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where emailAddress in
        defaultPaymentFiltering(
            "emailAddress.in=" + DEFAULT_EMAIL_ADDRESS + "," + UPDATED_EMAIL_ADDRESS,
            "emailAddress.in=" + UPDATED_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByEmailAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where emailAddress is not null
        defaultPaymentFiltering("emailAddress.specified=true", "emailAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByEmailAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where emailAddress contains
        defaultPaymentFiltering("emailAddress.contains=" + DEFAULT_EMAIL_ADDRESS, "emailAddress.contains=" + UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPaymentsByEmailAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where emailAddress does not contain
        defaultPaymentFiltering(
            "emailAddress.doesNotContain=" + UPDATED_EMAIL_ADDRESS,
            "emailAddress.doesNotContain=" + DEFAULT_EMAIL_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp equals to
        defaultPaymentFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp in
        defaultPaymentFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp is not null
        defaultPaymentFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state equals to
        defaultPaymentFiltering("state.equals=" + DEFAULT_STATE, "state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state in
        defaultPaymentFiltering("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE, "state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state is not null
        defaultPaymentFiltering("state.specified=true", "state.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByStateContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state contains
        defaultPaymentFiltering("state.contains=" + DEFAULT_STATE, "state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state does not contain
        defaultPaymentFiltering("state.doesNotContain=" + UPDATED_STATE, "state.doesNotContain=" + DEFAULT_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message equals to
        defaultPaymentFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message in
        defaultPaymentFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message is not null
        defaultPaymentFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message contains
        defaultPaymentFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message does not contain
        defaultPaymentFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId equals to
        defaultPaymentFiltering("gatewayId.equals=" + DEFAULT_GATEWAY_ID, "gatewayId.equals=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId in
        defaultPaymentFiltering("gatewayId.in=" + DEFAULT_GATEWAY_ID + "," + UPDATED_GATEWAY_ID, "gatewayId.in=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId is not null
        defaultPaymentFiltering("gatewayId.specified=true", "gatewayId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId contains
        defaultPaymentFiltering("gatewayId.contains=" + DEFAULT_GATEWAY_ID, "gatewayId.contains=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId does not contain
        defaultPaymentFiltering("gatewayId.doesNotContain=" + UPDATED_GATEWAY_ID, "gatewayId.doesNotContain=" + DEFAULT_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode equals to
        defaultPaymentFiltering("mode.equals=" + DEFAULT_MODE, "mode.equals=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode in
        defaultPaymentFiltering("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE, "mode.in=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode is not null
        defaultPaymentFiltering("mode.specified=true", "mode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByModeContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode contains
        defaultPaymentFiltering("mode.contains=" + DEFAULT_MODE, "mode.contains=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode does not contain
        defaultPaymentFiltering("mode.doesNotContain=" + UPDATED_MODE, "mode.doesNotContain=" + DEFAULT_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName equals to
        defaultPaymentFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName in
        defaultPaymentFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName is not null
        defaultPaymentFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName contains
        defaultPaymentFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName does not contain
        defaultPaymentFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorName equals to
        defaultPaymentFiltering("creditorName.equals=" + DEFAULT_CREDITOR_NAME, "creditorName.equals=" + UPDATED_CREDITOR_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorName in
        defaultPaymentFiltering(
            "creditorName.in=" + DEFAULT_CREDITOR_NAME + "," + UPDATED_CREDITOR_NAME,
            "creditorName.in=" + UPDATED_CREDITOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorName is not null
        defaultPaymentFiltering("creditorName.specified=true", "creditorName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorName contains
        defaultPaymentFiltering("creditorName.contains=" + DEFAULT_CREDITOR_NAME, "creditorName.contains=" + UPDATED_CREDITOR_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorName does not contain
        defaultPaymentFiltering(
            "creditorName.doesNotContain=" + UPDATED_CREDITOR_NAME,
            "creditorName.doesNotContain=" + DEFAULT_CREDITOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorIban equals to
        defaultPaymentFiltering("creditorIban.equals=" + DEFAULT_CREDITOR_IBAN, "creditorIban.equals=" + UPDATED_CREDITOR_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIbanIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorIban in
        defaultPaymentFiltering(
            "creditorIban.in=" + DEFAULT_CREDITOR_IBAN + "," + UPDATED_CREDITOR_IBAN,
            "creditorIban.in=" + UPDATED_CREDITOR_IBAN
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorIban is not null
        defaultPaymentFiltering("creditorIban.specified=true", "creditorIban.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIbanContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorIban contains
        defaultPaymentFiltering("creditorIban.contains=" + DEFAULT_CREDITOR_IBAN, "creditorIban.contains=" + UPDATED_CREDITOR_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIbanNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorIban does not contain
        defaultPaymentFiltering(
            "creditorIban.doesNotContain=" + UPDATED_CREDITOR_IBAN,
            "creditorIban.doesNotContain=" + DEFAULT_CREDITOR_IBAN
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorBicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorBic equals to
        defaultPaymentFiltering("creditorBic.equals=" + DEFAULT_CREDITOR_BIC, "creditorBic.equals=" + UPDATED_CREDITOR_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorBicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorBic in
        defaultPaymentFiltering(
            "creditorBic.in=" + DEFAULT_CREDITOR_BIC + "," + UPDATED_CREDITOR_BIC,
            "creditorBic.in=" + UPDATED_CREDITOR_BIC
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorBic is not null
        defaultPaymentFiltering("creditorBic.specified=true", "creditorBic.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorBicContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorBic contains
        defaultPaymentFiltering("creditorBic.contains=" + DEFAULT_CREDITOR_BIC, "creditorBic.contains=" + UPDATED_CREDITOR_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorBicNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorBic does not contain
        defaultPaymentFiltering("creditorBic.doesNotContain=" + UPDATED_CREDITOR_BIC, "creditorBic.doesNotContain=" + DEFAULT_CREDITOR_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorId equals to
        defaultPaymentFiltering("creditorId.equals=" + DEFAULT_CREDITOR_ID, "creditorId.equals=" + UPDATED_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorId in
        defaultPaymentFiltering("creditorId.in=" + DEFAULT_CREDITOR_ID + "," + UPDATED_CREDITOR_ID, "creditorId.in=" + UPDATED_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorId is not null
        defaultPaymentFiltering("creditorId.specified=true", "creditorId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorId contains
        defaultPaymentFiltering("creditorId.contains=" + DEFAULT_CREDITOR_ID, "creditorId.contains=" + UPDATED_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByCreditorIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where creditorId does not contain
        defaultPaymentFiltering("creditorId.doesNotContain=" + UPDATED_CREDITOR_ID, "creditorId.doesNotContain=" + DEFAULT_CREDITOR_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate equals to
        defaultPaymentFiltering("mandateDate.equals=" + DEFAULT_MANDATE_DATE, "mandateDate.equals=" + UPDATED_MANDATE_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate in
        defaultPaymentFiltering(
            "mandateDate.in=" + DEFAULT_MANDATE_DATE + "," + UPDATED_MANDATE_DATE,
            "mandateDate.in=" + UPDATED_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate is not null
        defaultPaymentFiltering("mandateDate.specified=true", "mandateDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate is greater than or equal to
        defaultPaymentFiltering(
            "mandateDate.greaterThanOrEqual=" + DEFAULT_MANDATE_DATE,
            "mandateDate.greaterThanOrEqual=" + UPDATED_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate is less than or equal to
        defaultPaymentFiltering(
            "mandateDate.lessThanOrEqual=" + DEFAULT_MANDATE_DATE,
            "mandateDate.lessThanOrEqual=" + SMALLER_MANDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate is less than
        defaultPaymentFiltering("mandateDate.lessThan=" + UPDATED_MANDATE_DATE, "mandateDate.lessThan=" + DEFAULT_MANDATE_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateDate is greater than
        defaultPaymentFiltering("mandateDate.greaterThan=" + SMALLER_MANDATE_DATE, "mandateDate.greaterThan=" + DEFAULT_MANDATE_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate equals to
        defaultPaymentFiltering("executionDate.equals=" + DEFAULT_EXECUTION_DATE, "executionDate.equals=" + UPDATED_EXECUTION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate in
        defaultPaymentFiltering(
            "executionDate.in=" + DEFAULT_EXECUTION_DATE + "," + UPDATED_EXECUTION_DATE,
            "executionDate.in=" + UPDATED_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate is not null
        defaultPaymentFiltering("executionDate.specified=true", "executionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate is greater than or equal to
        defaultPaymentFiltering(
            "executionDate.greaterThanOrEqual=" + DEFAULT_EXECUTION_DATE,
            "executionDate.greaterThanOrEqual=" + UPDATED_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate is less than or equal to
        defaultPaymentFiltering(
            "executionDate.lessThanOrEqual=" + DEFAULT_EXECUTION_DATE,
            "executionDate.lessThanOrEqual=" + SMALLER_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate is less than
        defaultPaymentFiltering("executionDate.lessThan=" + UPDATED_EXECUTION_DATE, "executionDate.lessThan=" + DEFAULT_EXECUTION_DATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByExecutionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where executionDate is greater than
        defaultPaymentFiltering(
            "executionDate.greaterThan=" + SMALLER_EXECUTION_DATE,
            "executionDate.greaterThan=" + DEFAULT_EXECUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentInformationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentInformationId equals to
        defaultPaymentFiltering(
            "paymentInformationId.equals=" + DEFAULT_PAYMENT_INFORMATION_ID,
            "paymentInformationId.equals=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentInformationIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentInformationId in
        defaultPaymentFiltering(
            "paymentInformationId.in=" + DEFAULT_PAYMENT_INFORMATION_ID + "," + UPDATED_PAYMENT_INFORMATION_ID,
            "paymentInformationId.in=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentInformationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentInformationId is not null
        defaultPaymentFiltering("paymentInformationId.specified=true", "paymentInformationId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentInformationIdContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentInformationId contains
        defaultPaymentFiltering(
            "paymentInformationId.contains=" + DEFAULT_PAYMENT_INFORMATION_ID,
            "paymentInformationId.contains=" + UPDATED_PAYMENT_INFORMATION_ID
        );
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentInformationIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentInformationId does not contain
        defaultPaymentFiltering(
            "paymentInformationId.doesNotContain=" + UPDATED_PAYMENT_INFORMATION_ID,
            "paymentInformationId.doesNotContain=" + DEFAULT_PAYMENT_INFORMATION_ID
        );
    }

    private void defaultPaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaymentShouldBeFound(shouldBeFound);
        defaultPaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentShouldBeFound(String filter) throws Exception {
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
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
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentShouldNotBeFound(String filter) throws Exception {
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayment() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
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
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentToMatchAllProperties(updatedPayment);
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .gateway(UPDATED_GATEWAY)
            .amount(UPDATED_AMOUNT)
            .streetName(UPDATED_STREET_NAME)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .state(UPDATED_STATE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .creditorIban(UPDATED_CREDITOR_IBAN)
            .mandateDate(UPDATED_MANDATE_DATE)
            .paymentInformationId(UPDATED_PAYMENT_INFORMATION_ID);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPayment, payment), getPersistedPayment(payment));
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
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

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(partialUpdatedPayment, getPersistedPayment(partialUpdatedPayment));
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.saveAndFlush(payment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentRepository.count();
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

    protected Payment getPersistedPayment(Payment payment) {
        return paymentRepository.findById(payment.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentToMatchAllProperties(Payment expectedPayment) {
        assertPaymentAllPropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }

    protected void assertPersistedPaymentToMatchUpdatableProperties(Payment expectedPayment) {
        assertPaymentAllUpdatablePropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }
}
