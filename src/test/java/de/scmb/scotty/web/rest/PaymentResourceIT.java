package de.scmb.scotty.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.dto.PaymentDTO;
import de.scmb.scotty.service.mapper.PaymentMapper;
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

    private static final Gateway DEFAULT_GATEWAY = Gateway.EMERCHANTPAY;
    private static final Gateway UPDATED_GATEWAY = Gateway.CCBILL;

    private static final String DEFAULT_IBAN = "AAAAAAAAAAAAAAAA";
    private static final String UPDATED_IBAN = "BBBBBBBBBBBBBBBB";

    private static final String DEFAULT_BIC = "AAAAAAAAAA";
    private static final String UPDATED_BIC = "BBBBBBBBBB";

    private static final Integer DEFAULT_AMOUNT = 0;
    private static final Integer UPDATED_AMOUNT = 1;
    private static final Integer SMALLER_AMOUNT = 0 - 1;

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final String DEFAULT_SOFT_DESCRIPTOR = "AAAAAAAAAA";
    private static final String UPDATED_SOFT_DESCRIPTOR = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AA";
    private static final String UPDATED_COUNTRY_CODE = "BB";

    private static final String DEFAULT_REMOTE_IP = "AAAAAAAAAA";
    private static final String UPDATED_REMOTE_IP = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
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
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .postalCode(DEFAULT_POSTAL_CODE)
            .city(DEFAULT_CITY)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .remoteIp(DEFAULT_REMOTE_IP)
            .timestamp(DEFAULT_TIMESTAMP)
            .state(DEFAULT_STATE)
            .message(DEFAULT_MESSAGE)
            .gatewayId(DEFAULT_GATEWAY_ID)
            .mode(DEFAULT_MODE)
            .fileName(DEFAULT_FILE_NAME);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
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
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME);
        return payment;
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getMandateId()).isEqualTo(DEFAULT_MANDATE_ID);
        assertThat(testPayment.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testPayment.getGateway()).isEqualTo(DEFAULT_GATEWAY);
        assertThat(testPayment.getIban()).isEqualTo(DEFAULT_IBAN);
        assertThat(testPayment.getBic()).isEqualTo(DEFAULT_BIC);
        assertThat(testPayment.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testPayment.getSoftDescriptor()).isEqualTo(DEFAULT_SOFT_DESCRIPTOR);
        assertThat(testPayment.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPayment.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testPayment.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testPayment.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testPayment.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testPayment.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testPayment.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testPayment.getRemoteIp()).isEqualTo(DEFAULT_REMOTE_IP);
        assertThat(testPayment.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testPayment.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testPayment.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testPayment.getGatewayId()).isEqualTo(DEFAULT_GATEWAY_ID);
        assertThat(testPayment.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testPayment.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMandateIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setMandateId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setPaymentId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setGateway(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIbanIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setIban(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBicIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setBic(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setCurrencyCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSoftDescriptorIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setSoftDescriptor(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setFirstName(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setLastName(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setAddressLine1(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostalCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setPostalCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setCity(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setCountryCode(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemoteIpIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setRemoteIp(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setTimestamp(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setState(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setMessage(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

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
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].remoteIp").value(hasItem(DEFAULT_REMOTE_IP)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].gatewayId").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));
    }

    @Test
    @Transactional
    void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

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
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.remoteIp").value(DEFAULT_REMOTE_IP))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.gatewayId").value(DEFAULT_GATEWAY_ID))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME));
    }

    @Test
    @Transactional
    void getPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        Long id = payment.getId();

        defaultPaymentShouldBeFound("id.equals=" + id);
        defaultPaymentShouldNotBeFound("id.notEquals=" + id);

        defaultPaymentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPaymentShouldNotBeFound("id.greaterThan=" + id);

        defaultPaymentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPaymentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId equals to DEFAULT_MANDATE_ID
        defaultPaymentShouldBeFound("mandateId.equals=" + DEFAULT_MANDATE_ID);

        // Get all the paymentList where mandateId equals to UPDATED_MANDATE_ID
        defaultPaymentShouldNotBeFound("mandateId.equals=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId in DEFAULT_MANDATE_ID or UPDATED_MANDATE_ID
        defaultPaymentShouldBeFound("mandateId.in=" + DEFAULT_MANDATE_ID + "," + UPDATED_MANDATE_ID);

        // Get all the paymentList where mandateId equals to UPDATED_MANDATE_ID
        defaultPaymentShouldNotBeFound("mandateId.in=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId is not null
        defaultPaymentShouldBeFound("mandateId.specified=true");

        // Get all the paymentList where mandateId is null
        defaultPaymentShouldNotBeFound("mandateId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId contains DEFAULT_MANDATE_ID
        defaultPaymentShouldBeFound("mandateId.contains=" + DEFAULT_MANDATE_ID);

        // Get all the paymentList where mandateId contains UPDATED_MANDATE_ID
        defaultPaymentShouldNotBeFound("mandateId.contains=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByMandateIdNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mandateId does not contain DEFAULT_MANDATE_ID
        defaultPaymentShouldNotBeFound("mandateId.doesNotContain=" + DEFAULT_MANDATE_ID);

        // Get all the paymentList where mandateId does not contain UPDATED_MANDATE_ID
        defaultPaymentShouldBeFound("mandateId.doesNotContain=" + UPDATED_MANDATE_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId equals to DEFAULT_PAYMENT_ID
        defaultPaymentShouldBeFound("paymentId.equals=" + DEFAULT_PAYMENT_ID);

        // Get all the paymentList where paymentId equals to UPDATED_PAYMENT_ID
        defaultPaymentShouldNotBeFound("paymentId.equals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId in DEFAULT_PAYMENT_ID or UPDATED_PAYMENT_ID
        defaultPaymentShouldBeFound("paymentId.in=" + DEFAULT_PAYMENT_ID + "," + UPDATED_PAYMENT_ID);

        // Get all the paymentList where paymentId equals to UPDATED_PAYMENT_ID
        defaultPaymentShouldNotBeFound("paymentId.in=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId is not null
        defaultPaymentShouldBeFound("paymentId.specified=true");

        // Get all the paymentList where paymentId is null
        defaultPaymentShouldNotBeFound("paymentId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId contains DEFAULT_PAYMENT_ID
        defaultPaymentShouldBeFound("paymentId.contains=" + DEFAULT_PAYMENT_ID);

        // Get all the paymentList where paymentId contains UPDATED_PAYMENT_ID
        defaultPaymentShouldNotBeFound("paymentId.contains=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByPaymentIdNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where paymentId does not contain DEFAULT_PAYMENT_ID
        defaultPaymentShouldNotBeFound("paymentId.doesNotContain=" + DEFAULT_PAYMENT_ID);

        // Get all the paymentList where paymentId does not contain UPDATED_PAYMENT_ID
        defaultPaymentShouldBeFound("paymentId.doesNotContain=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway equals to DEFAULT_GATEWAY
        defaultPaymentShouldBeFound("gateway.equals=" + DEFAULT_GATEWAY);

        // Get all the paymentList where gateway equals to UPDATED_GATEWAY
        defaultPaymentShouldNotBeFound("gateway.equals=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway in DEFAULT_GATEWAY or UPDATED_GATEWAY
        defaultPaymentShouldBeFound("gateway.in=" + DEFAULT_GATEWAY + "," + UPDATED_GATEWAY);

        // Get all the paymentList where gateway equals to UPDATED_GATEWAY
        defaultPaymentShouldNotBeFound("gateway.in=" + UPDATED_GATEWAY);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gateway is not null
        defaultPaymentShouldBeFound("gateway.specified=true");

        // Get all the paymentList where gateway is null
        defaultPaymentShouldNotBeFound("gateway.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban equals to DEFAULT_IBAN
        defaultPaymentShouldBeFound("iban.equals=" + DEFAULT_IBAN);

        // Get all the paymentList where iban equals to UPDATED_IBAN
        defaultPaymentShouldNotBeFound("iban.equals=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban in DEFAULT_IBAN or UPDATED_IBAN
        defaultPaymentShouldBeFound("iban.in=" + DEFAULT_IBAN + "," + UPDATED_IBAN);

        // Get all the paymentList where iban equals to UPDATED_IBAN
        defaultPaymentShouldNotBeFound("iban.in=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban is not null
        defaultPaymentShouldBeFound("iban.specified=true");

        // Get all the paymentList where iban is null
        defaultPaymentShouldNotBeFound("iban.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban contains DEFAULT_IBAN
        defaultPaymentShouldBeFound("iban.contains=" + DEFAULT_IBAN);

        // Get all the paymentList where iban contains UPDATED_IBAN
        defaultPaymentShouldNotBeFound("iban.contains=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByIbanNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where iban does not contain DEFAULT_IBAN
        defaultPaymentShouldNotBeFound("iban.doesNotContain=" + DEFAULT_IBAN);

        // Get all the paymentList where iban does not contain UPDATED_IBAN
        defaultPaymentShouldBeFound("iban.doesNotContain=" + UPDATED_IBAN);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic equals to DEFAULT_BIC
        defaultPaymentShouldBeFound("bic.equals=" + DEFAULT_BIC);

        // Get all the paymentList where bic equals to UPDATED_BIC
        defaultPaymentShouldNotBeFound("bic.equals=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic in DEFAULT_BIC or UPDATED_BIC
        defaultPaymentShouldBeFound("bic.in=" + DEFAULT_BIC + "," + UPDATED_BIC);

        // Get all the paymentList where bic equals to UPDATED_BIC
        defaultPaymentShouldNotBeFound("bic.in=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic is not null
        defaultPaymentShouldBeFound("bic.specified=true");

        // Get all the paymentList where bic is null
        defaultPaymentShouldNotBeFound("bic.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByBicContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic contains DEFAULT_BIC
        defaultPaymentShouldBeFound("bic.contains=" + DEFAULT_BIC);

        // Get all the paymentList where bic contains UPDATED_BIC
        defaultPaymentShouldNotBeFound("bic.contains=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByBicNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where bic does not contain DEFAULT_BIC
        defaultPaymentShouldNotBeFound("bic.doesNotContain=" + DEFAULT_BIC);

        // Get all the paymentList where bic does not contain UPDATED_BIC
        defaultPaymentShouldBeFound("bic.doesNotContain=" + UPDATED_BIC);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount equals to DEFAULT_AMOUNT
        defaultPaymentShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the paymentList where amount equals to UPDATED_AMOUNT
        defaultPaymentShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultPaymentShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the paymentList where amount equals to UPDATED_AMOUNT
        defaultPaymentShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is not null
        defaultPaymentShouldBeFound("amount.specified=true");

        // Get all the paymentList where amount is null
        defaultPaymentShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultPaymentShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the paymentList where amount is greater than or equal to UPDATED_AMOUNT
        defaultPaymentShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is less than or equal to DEFAULT_AMOUNT
        defaultPaymentShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the paymentList where amount is less than or equal to SMALLER_AMOUNT
        defaultPaymentShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is less than DEFAULT_AMOUNT
        defaultPaymentShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the paymentList where amount is less than UPDATED_AMOUNT
        defaultPaymentShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where amount is greater than DEFAULT_AMOUNT
        defaultPaymentShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the paymentList where amount is greater than SMALLER_AMOUNT
        defaultPaymentShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode equals to DEFAULT_CURRENCY_CODE
        defaultPaymentShouldBeFound("currencyCode.equals=" + DEFAULT_CURRENCY_CODE);

        // Get all the paymentList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultPaymentShouldNotBeFound("currencyCode.equals=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode in DEFAULT_CURRENCY_CODE or UPDATED_CURRENCY_CODE
        defaultPaymentShouldBeFound("currencyCode.in=" + DEFAULT_CURRENCY_CODE + "," + UPDATED_CURRENCY_CODE);

        // Get all the paymentList where currencyCode equals to UPDATED_CURRENCY_CODE
        defaultPaymentShouldNotBeFound("currencyCode.in=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode is not null
        defaultPaymentShouldBeFound("currencyCode.specified=true");

        // Get all the paymentList where currencyCode is null
        defaultPaymentShouldNotBeFound("currencyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode contains DEFAULT_CURRENCY_CODE
        defaultPaymentShouldBeFound("currencyCode.contains=" + DEFAULT_CURRENCY_CODE);

        // Get all the paymentList where currencyCode contains UPDATED_CURRENCY_CODE
        defaultPaymentShouldNotBeFound("currencyCode.contains=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCurrencyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where currencyCode does not contain DEFAULT_CURRENCY_CODE
        defaultPaymentShouldNotBeFound("currencyCode.doesNotContain=" + DEFAULT_CURRENCY_CODE);

        // Get all the paymentList where currencyCode does not contain UPDATED_CURRENCY_CODE
        defaultPaymentShouldBeFound("currencyCode.doesNotContain=" + UPDATED_CURRENCY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor equals to DEFAULT_SOFT_DESCRIPTOR
        defaultPaymentShouldBeFound("softDescriptor.equals=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the paymentList where softDescriptor equals to UPDATED_SOFT_DESCRIPTOR
        defaultPaymentShouldNotBeFound("softDescriptor.equals=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor in DEFAULT_SOFT_DESCRIPTOR or UPDATED_SOFT_DESCRIPTOR
        defaultPaymentShouldBeFound("softDescriptor.in=" + DEFAULT_SOFT_DESCRIPTOR + "," + UPDATED_SOFT_DESCRIPTOR);

        // Get all the paymentList where softDescriptor equals to UPDATED_SOFT_DESCRIPTOR
        defaultPaymentShouldNotBeFound("softDescriptor.in=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor is not null
        defaultPaymentShouldBeFound("softDescriptor.specified=true");

        // Get all the paymentList where softDescriptor is null
        defaultPaymentShouldNotBeFound("softDescriptor.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor contains DEFAULT_SOFT_DESCRIPTOR
        defaultPaymentShouldBeFound("softDescriptor.contains=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the paymentList where softDescriptor contains UPDATED_SOFT_DESCRIPTOR
        defaultPaymentShouldNotBeFound("softDescriptor.contains=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsBySoftDescriptorNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where softDescriptor does not contain DEFAULT_SOFT_DESCRIPTOR
        defaultPaymentShouldNotBeFound("softDescriptor.doesNotContain=" + DEFAULT_SOFT_DESCRIPTOR);

        // Get all the paymentList where softDescriptor does not contain UPDATED_SOFT_DESCRIPTOR
        defaultPaymentShouldBeFound("softDescriptor.doesNotContain=" + UPDATED_SOFT_DESCRIPTOR);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName equals to DEFAULT_FIRST_NAME
        defaultPaymentShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the paymentList where firstName equals to UPDATED_FIRST_NAME
        defaultPaymentShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultPaymentShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the paymentList where firstName equals to UPDATED_FIRST_NAME
        defaultPaymentShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName is not null
        defaultPaymentShouldBeFound("firstName.specified=true");

        // Get all the paymentList where firstName is null
        defaultPaymentShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName contains DEFAULT_FIRST_NAME
        defaultPaymentShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the paymentList where firstName contains UPDATED_FIRST_NAME
        defaultPaymentShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where firstName does not contain DEFAULT_FIRST_NAME
        defaultPaymentShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the paymentList where firstName does not contain UPDATED_FIRST_NAME
        defaultPaymentShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName equals to DEFAULT_LAST_NAME
        defaultPaymentShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the paymentList where lastName equals to UPDATED_LAST_NAME
        defaultPaymentShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultPaymentShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the paymentList where lastName equals to UPDATED_LAST_NAME
        defaultPaymentShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName is not null
        defaultPaymentShouldBeFound("lastName.specified=true");

        // Get all the paymentList where lastName is null
        defaultPaymentShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName contains DEFAULT_LAST_NAME
        defaultPaymentShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the paymentList where lastName contains UPDATED_LAST_NAME
        defaultPaymentShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where lastName does not contain DEFAULT_LAST_NAME
        defaultPaymentShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the paymentList where lastName does not contain UPDATED_LAST_NAME
        defaultPaymentShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultPaymentShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the paymentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultPaymentShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultPaymentShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the paymentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultPaymentShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine1 is not null
        defaultPaymentShouldBeFound("addressLine1.specified=true");

        // Get all the paymentList where addressLine1 is null
        defaultPaymentShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultPaymentShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the paymentList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultPaymentShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultPaymentShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the paymentList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultPaymentShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultPaymentShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the paymentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultPaymentShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultPaymentShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the paymentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultPaymentShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine2 is not null
        defaultPaymentShouldBeFound("addressLine2.specified=true");

        // Get all the paymentList where addressLine2 is null
        defaultPaymentShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultPaymentShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the paymentList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultPaymentShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllPaymentsByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultPaymentShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the paymentList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultPaymentShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode equals to DEFAULT_POSTAL_CODE
        defaultPaymentShouldBeFound("postalCode.equals=" + DEFAULT_POSTAL_CODE);

        // Get all the paymentList where postalCode equals to UPDATED_POSTAL_CODE
        defaultPaymentShouldNotBeFound("postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode in DEFAULT_POSTAL_CODE or UPDATED_POSTAL_CODE
        defaultPaymentShouldBeFound("postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE);

        // Get all the paymentList where postalCode equals to UPDATED_POSTAL_CODE
        defaultPaymentShouldNotBeFound("postalCode.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode is not null
        defaultPaymentShouldBeFound("postalCode.specified=true");

        // Get all the paymentList where postalCode is null
        defaultPaymentShouldNotBeFound("postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode contains DEFAULT_POSTAL_CODE
        defaultPaymentShouldBeFound("postalCode.contains=" + DEFAULT_POSTAL_CODE);

        // Get all the paymentList where postalCode contains UPDATED_POSTAL_CODE
        defaultPaymentShouldNotBeFound("postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where postalCode does not contain DEFAULT_POSTAL_CODE
        defaultPaymentShouldNotBeFound("postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE);

        // Get all the paymentList where postalCode does not contain UPDATED_POSTAL_CODE
        defaultPaymentShouldBeFound("postalCode.doesNotContain=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city equals to DEFAULT_CITY
        defaultPaymentShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the paymentList where city equals to UPDATED_CITY
        defaultPaymentShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city in DEFAULT_CITY or UPDATED_CITY
        defaultPaymentShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the paymentList where city equals to UPDATED_CITY
        defaultPaymentShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city is not null
        defaultPaymentShouldBeFound("city.specified=true");

        // Get all the paymentList where city is null
        defaultPaymentShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCityContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city contains DEFAULT_CITY
        defaultPaymentShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the paymentList where city contains UPDATED_CITY
        defaultPaymentShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where city does not contain DEFAULT_CITY
        defaultPaymentShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the paymentList where city does not contain UPDATED_CITY
        defaultPaymentShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultPaymentShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the paymentList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultPaymentShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultPaymentShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the paymentList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultPaymentShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode is not null
        defaultPaymentShouldBeFound("countryCode.specified=true");

        // Get all the paymentList where countryCode is null
        defaultPaymentShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultPaymentShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the paymentList where countryCode contains UPDATED_COUNTRY_CODE
        defaultPaymentShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultPaymentShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the paymentList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultPaymentShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp equals to DEFAULT_REMOTE_IP
        defaultPaymentShouldBeFound("remoteIp.equals=" + DEFAULT_REMOTE_IP);

        // Get all the paymentList where remoteIp equals to UPDATED_REMOTE_IP
        defaultPaymentShouldNotBeFound("remoteIp.equals=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp in DEFAULT_REMOTE_IP or UPDATED_REMOTE_IP
        defaultPaymentShouldBeFound("remoteIp.in=" + DEFAULT_REMOTE_IP + "," + UPDATED_REMOTE_IP);

        // Get all the paymentList where remoteIp equals to UPDATED_REMOTE_IP
        defaultPaymentShouldNotBeFound("remoteIp.in=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp is not null
        defaultPaymentShouldBeFound("remoteIp.specified=true");

        // Get all the paymentList where remoteIp is null
        defaultPaymentShouldNotBeFound("remoteIp.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp contains DEFAULT_REMOTE_IP
        defaultPaymentShouldBeFound("remoteIp.contains=" + DEFAULT_REMOTE_IP);

        // Get all the paymentList where remoteIp contains UPDATED_REMOTE_IP
        defaultPaymentShouldNotBeFound("remoteIp.contains=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByRemoteIpNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where remoteIp does not contain DEFAULT_REMOTE_IP
        defaultPaymentShouldNotBeFound("remoteIp.doesNotContain=" + DEFAULT_REMOTE_IP);

        // Get all the paymentList where remoteIp does not contain UPDATED_REMOTE_IP
        defaultPaymentShouldBeFound("remoteIp.doesNotContain=" + UPDATED_REMOTE_IP);
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp equals to DEFAULT_TIMESTAMP
        defaultPaymentShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the paymentList where timestamp equals to UPDATED_TIMESTAMP
        defaultPaymentShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultPaymentShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the paymentList where timestamp equals to UPDATED_TIMESTAMP
        defaultPaymentShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllPaymentsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where timestamp is not null
        defaultPaymentShouldBeFound("timestamp.specified=true");

        // Get all the paymentList where timestamp is null
        defaultPaymentShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state equals to DEFAULT_STATE
        defaultPaymentShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the paymentList where state equals to UPDATED_STATE
        defaultPaymentShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state in DEFAULT_STATE or UPDATED_STATE
        defaultPaymentShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the paymentList where state equals to UPDATED_STATE
        defaultPaymentShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state is not null
        defaultPaymentShouldBeFound("state.specified=true");

        // Get all the paymentList where state is null
        defaultPaymentShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByStateContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state contains DEFAULT_STATE
        defaultPaymentShouldBeFound("state.contains=" + DEFAULT_STATE);

        // Get all the paymentList where state contains UPDATED_STATE
        defaultPaymentShouldNotBeFound("state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByStateNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where state does not contain DEFAULT_STATE
        defaultPaymentShouldNotBeFound("state.doesNotContain=" + DEFAULT_STATE);

        // Get all the paymentList where state does not contain UPDATED_STATE
        defaultPaymentShouldBeFound("state.doesNotContain=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message equals to DEFAULT_MESSAGE
        defaultPaymentShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the paymentList where message equals to UPDATED_MESSAGE
        defaultPaymentShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultPaymentShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the paymentList where message equals to UPDATED_MESSAGE
        defaultPaymentShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message is not null
        defaultPaymentShouldBeFound("message.specified=true");

        // Get all the paymentList where message is null
        defaultPaymentShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message contains DEFAULT_MESSAGE
        defaultPaymentShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the paymentList where message contains UPDATED_MESSAGE
        defaultPaymentShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where message does not contain DEFAULT_MESSAGE
        defaultPaymentShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the paymentList where message does not contain UPDATED_MESSAGE
        defaultPaymentShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId equals to DEFAULT_GATEWAY_ID
        defaultPaymentShouldBeFound("gatewayId.equals=" + DEFAULT_GATEWAY_ID);

        // Get all the paymentList where gatewayId equals to UPDATED_GATEWAY_ID
        defaultPaymentShouldNotBeFound("gatewayId.equals=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId in DEFAULT_GATEWAY_ID or UPDATED_GATEWAY_ID
        defaultPaymentShouldBeFound("gatewayId.in=" + DEFAULT_GATEWAY_ID + "," + UPDATED_GATEWAY_ID);

        // Get all the paymentList where gatewayId equals to UPDATED_GATEWAY_ID
        defaultPaymentShouldNotBeFound("gatewayId.in=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId is not null
        defaultPaymentShouldBeFound("gatewayId.specified=true");

        // Get all the paymentList where gatewayId is null
        defaultPaymentShouldNotBeFound("gatewayId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId contains DEFAULT_GATEWAY_ID
        defaultPaymentShouldBeFound("gatewayId.contains=" + DEFAULT_GATEWAY_ID);

        // Get all the paymentList where gatewayId contains UPDATED_GATEWAY_ID
        defaultPaymentShouldNotBeFound("gatewayId.contains=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByGatewayIdNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where gatewayId does not contain DEFAULT_GATEWAY_ID
        defaultPaymentShouldNotBeFound("gatewayId.doesNotContain=" + DEFAULT_GATEWAY_ID);

        // Get all the paymentList where gatewayId does not contain UPDATED_GATEWAY_ID
        defaultPaymentShouldBeFound("gatewayId.doesNotContain=" + UPDATED_GATEWAY_ID);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode equals to DEFAULT_MODE
        defaultPaymentShouldBeFound("mode.equals=" + DEFAULT_MODE);

        // Get all the paymentList where mode equals to UPDATED_MODE
        defaultPaymentShouldNotBeFound("mode.equals=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode in DEFAULT_MODE or UPDATED_MODE
        defaultPaymentShouldBeFound("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE);

        // Get all the paymentList where mode equals to UPDATED_MODE
        defaultPaymentShouldNotBeFound("mode.in=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode is not null
        defaultPaymentShouldBeFound("mode.specified=true");

        // Get all the paymentList where mode is null
        defaultPaymentShouldNotBeFound("mode.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByModeContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode contains DEFAULT_MODE
        defaultPaymentShouldBeFound("mode.contains=" + DEFAULT_MODE);

        // Get all the paymentList where mode contains UPDATED_MODE
        defaultPaymentShouldNotBeFound("mode.contains=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByModeNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where mode does not contain DEFAULT_MODE
        defaultPaymentShouldNotBeFound("mode.doesNotContain=" + DEFAULT_MODE);

        // Get all the paymentList where mode does not contain UPDATED_MODE
        defaultPaymentShouldBeFound("mode.doesNotContain=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName equals to DEFAULT_FILE_NAME
        defaultPaymentShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the paymentList where fileName equals to UPDATED_FILE_NAME
        defaultPaymentShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultPaymentShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the paymentList where fileName equals to UPDATED_FILE_NAME
        defaultPaymentShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName is not null
        defaultPaymentShouldBeFound("fileName.specified=true");

        // Get all the paymentList where fileName is null
        defaultPaymentShouldNotBeFound("fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName contains DEFAULT_FILE_NAME
        defaultPaymentShouldBeFound("fileName.contains=" + DEFAULT_FILE_NAME);

        // Get all the paymentList where fileName contains UPDATED_FILE_NAME
        defaultPaymentShouldNotBeFound("fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllPaymentsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList where fileName does not contain DEFAULT_FILE_NAME
        defaultPaymentShouldNotBeFound("fileName.doesNotContain=" + DEFAULT_FILE_NAME);

        // Get all the paymentList where fileName does not contain UPDATED_FILE_NAME
        defaultPaymentShouldBeFound("fileName.doesNotContain=" + UPDATED_FILE_NAME);
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
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].remoteIp").value(hasItem(DEFAULT_REMOTE_IP)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].gatewayId").value(hasItem(DEFAULT_GATEWAY_ID)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)));

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
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

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
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getMandateId()).isEqualTo(UPDATED_MANDATE_ID);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testPayment.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testPayment.getIban()).isEqualTo(UPDATED_IBAN);
        assertThat(testPayment.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testPayment.getSoftDescriptor()).isEqualTo(UPDATED_SOFT_DESCRIPTOR);
        assertThat(testPayment.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPayment.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testPayment.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testPayment.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testPayment.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testPayment.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testPayment.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testPayment.getRemoteIp()).isEqualTo(UPDATED_REMOTE_IP);
        assertThat(testPayment.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testPayment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testPayment.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testPayment.getGatewayId()).isEqualTo(UPDATED_GATEWAY_ID);
        assertThat(testPayment.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testPayment.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .paymentId(UPDATED_PAYMENT_ID)
            .gateway(UPDATED_GATEWAY)
            .bic(UPDATED_BIC)
            .amount(UPDATED_AMOUNT)
            .softDescriptor(UPDATED_SOFT_DESCRIPTOR)
            .firstName(UPDATED_FIRST_NAME)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .state(UPDATED_STATE)
            .fileName(UPDATED_FILE_NAME);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getMandateId()).isEqualTo(DEFAULT_MANDATE_ID);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testPayment.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testPayment.getIban()).isEqualTo(DEFAULT_IBAN);
        assertThat(testPayment.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testPayment.getSoftDescriptor()).isEqualTo(UPDATED_SOFT_DESCRIPTOR);
        assertThat(testPayment.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPayment.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testPayment.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testPayment.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testPayment.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testPayment.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testPayment.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testPayment.getRemoteIp()).isEqualTo(DEFAULT_REMOTE_IP);
        assertThat(testPayment.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testPayment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testPayment.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testPayment.getGatewayId()).isEqualTo(DEFAULT_GATEWAY_ID);
        assertThat(testPayment.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testPayment.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

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
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .countryCode(UPDATED_COUNTRY_CODE)
            .remoteIp(UPDATED_REMOTE_IP)
            .timestamp(UPDATED_TIMESTAMP)
            .state(UPDATED_STATE)
            .message(UPDATED_MESSAGE)
            .gatewayId(UPDATED_GATEWAY_ID)
            .mode(UPDATED_MODE)
            .fileName(UPDATED_FILE_NAME);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getMandateId()).isEqualTo(UPDATED_MANDATE_ID);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testPayment.getGateway()).isEqualTo(UPDATED_GATEWAY);
        assertThat(testPayment.getIban()).isEqualTo(UPDATED_IBAN);
        assertThat(testPayment.getBic()).isEqualTo(UPDATED_BIC);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testPayment.getSoftDescriptor()).isEqualTo(UPDATED_SOFT_DESCRIPTOR);
        assertThat(testPayment.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPayment.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testPayment.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testPayment.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testPayment.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testPayment.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testPayment.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testPayment.getRemoteIp()).isEqualTo(UPDATED_REMOTE_IP);
        assertThat(testPayment.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testPayment.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testPayment.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testPayment.getGatewayId()).isEqualTo(UPDATED_GATEWAY_ID);
        assertThat(testPayment.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testPayment.getFileName()).isEqualTo(UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
