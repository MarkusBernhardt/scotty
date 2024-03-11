package de.scmb.scotty.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.dto.KeyValueDTO;
import de.scmb.scotty.service.mapper.KeyValueMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link KeyValueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class KeyValueResourceIT {

    private static final String DEFAULT_KV_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KV_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_KV_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_KV_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/key-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Autowired
    private KeyValueMapper keyValueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKeyValueMockMvc;

    private KeyValue keyValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KeyValue createEntity(EntityManager em) {
        KeyValue keyValue = new KeyValue().kvKey(DEFAULT_KV_KEY).kvValue(DEFAULT_KV_VALUE);
        return keyValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KeyValue createUpdatedEntity(EntityManager em) {
        KeyValue keyValue = new KeyValue().kvKey(UPDATED_KV_KEY).kvValue(UPDATED_KV_VALUE);
        return keyValue;
    }

    @BeforeEach
    public void initTest() {
        keyValue = createEntity(em);
    }

    @Test
    @Transactional
    void createKeyValue() throws Exception {
        int databaseSizeBeforeCreate = keyValueRepository.findAll().size();
        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);
        restKeyValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyValueDTO)))
            .andExpect(status().isCreated());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeCreate + 1);
        KeyValue testKeyValue = keyValueList.get(keyValueList.size() - 1);
        assertThat(testKeyValue.getKvKey()).isEqualTo(DEFAULT_KV_KEY);
        assertThat(testKeyValue.getKvValue()).isEqualTo(DEFAULT_KV_VALUE);
    }

    @Test
    @Transactional
    void createKeyValueWithExistingId() throws Exception {
        // Create the KeyValue with an existing ID
        keyValue.setId(1L);
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        int databaseSizeBeforeCreate = keyValueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKeyValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKvKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyValueRepository.findAll().size();
        // set the field null
        keyValue.setKvKey(null);

        // Create the KeyValue, which fails.
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        restKeyValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyValueDTO)))
            .andExpect(status().isBadRequest());

        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllKeyValues() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(keyValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].kvKey").value(hasItem(DEFAULT_KV_KEY)))
            .andExpect(jsonPath("$.[*].kvValue").value(hasItem(DEFAULT_KV_VALUE)));
    }

    @Test
    @Transactional
    void getKeyValue() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get the keyValue
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL_ID, keyValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(keyValue.getId().intValue()))
            .andExpect(jsonPath("$.kvKey").value(DEFAULT_KV_KEY))
            .andExpect(jsonPath("$.kvValue").value(DEFAULT_KV_VALUE));
    }

    @Test
    @Transactional
    void getKeyValuesByIdFiltering() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        Long id = keyValue.getId();

        defaultKeyValueShouldBeFound("id.equals=" + id);
        defaultKeyValueShouldNotBeFound("id.notEquals=" + id);

        defaultKeyValueShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultKeyValueShouldNotBeFound("id.greaterThan=" + id);

        defaultKeyValueShouldBeFound("id.lessThanOrEqual=" + id);
        defaultKeyValueShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey equals to DEFAULT_KV_KEY
        defaultKeyValueShouldBeFound("kvKey.equals=" + DEFAULT_KV_KEY);

        // Get all the keyValueList where kvKey equals to UPDATED_KV_KEY
        defaultKeyValueShouldNotBeFound("kvKey.equals=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsInShouldWork() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey in DEFAULT_KV_KEY or UPDATED_KV_KEY
        defaultKeyValueShouldBeFound("kvKey.in=" + DEFAULT_KV_KEY + "," + UPDATED_KV_KEY);

        // Get all the keyValueList where kvKey equals to UPDATED_KV_KEY
        defaultKeyValueShouldNotBeFound("kvKey.in=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey is not null
        defaultKeyValueShouldBeFound("kvKey.specified=true");

        // Get all the keyValueList where kvKey is null
        defaultKeyValueShouldNotBeFound("kvKey.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey contains DEFAULT_KV_KEY
        defaultKeyValueShouldBeFound("kvKey.contains=" + DEFAULT_KV_KEY);

        // Get all the keyValueList where kvKey contains UPDATED_KV_KEY
        defaultKeyValueShouldNotBeFound("kvKey.contains=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyNotContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey does not contain DEFAULT_KV_KEY
        defaultKeyValueShouldNotBeFound("kvKey.doesNotContain=" + DEFAULT_KV_KEY);

        // Get all the keyValueList where kvKey does not contain UPDATED_KV_KEY
        defaultKeyValueShouldBeFound("kvKey.doesNotContain=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsEqualToSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue equals to DEFAULT_KV_VALUE
        defaultKeyValueShouldBeFound("kvValue.equals=" + DEFAULT_KV_VALUE);

        // Get all the keyValueList where kvValue equals to UPDATED_KV_VALUE
        defaultKeyValueShouldNotBeFound("kvValue.equals=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsInShouldWork() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue in DEFAULT_KV_VALUE or UPDATED_KV_VALUE
        defaultKeyValueShouldBeFound("kvValue.in=" + DEFAULT_KV_VALUE + "," + UPDATED_KV_VALUE);

        // Get all the keyValueList where kvValue equals to UPDATED_KV_VALUE
        defaultKeyValueShouldNotBeFound("kvValue.in=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue is not null
        defaultKeyValueShouldBeFound("kvValue.specified=true");

        // Get all the keyValueList where kvValue is null
        defaultKeyValueShouldNotBeFound("kvValue.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue contains DEFAULT_KV_VALUE
        defaultKeyValueShouldBeFound("kvValue.contains=" + DEFAULT_KV_VALUE);

        // Get all the keyValueList where kvValue contains UPDATED_KV_VALUE
        defaultKeyValueShouldNotBeFound("kvValue.contains=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueNotContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue does not contain DEFAULT_KV_VALUE
        defaultKeyValueShouldNotBeFound("kvValue.doesNotContain=" + DEFAULT_KV_VALUE);

        // Get all the keyValueList where kvValue does not contain UPDATED_KV_VALUE
        defaultKeyValueShouldBeFound("kvValue.doesNotContain=" + UPDATED_KV_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKeyValueShouldBeFound(String filter) throws Exception {
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(keyValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].kvKey").value(hasItem(DEFAULT_KV_KEY)))
            .andExpect(jsonPath("$.[*].kvValue").value(hasItem(DEFAULT_KV_VALUE)));

        // Check, that the count call also returns 1
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKeyValueShouldNotBeFound(String filter) throws Exception {
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKeyValueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKeyValue() throws Exception {
        // Get the keyValue
        restKeyValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKeyValue() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();

        // Update the keyValue
        KeyValue updatedKeyValue = keyValueRepository.findById(keyValue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKeyValue are not directly saved in db
        em.detach(updatedKeyValue);
        updatedKeyValue.kvKey(UPDATED_KV_KEY).kvValue(UPDATED_KV_VALUE);
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(updatedKeyValue);

        restKeyValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, keyValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
        KeyValue testKeyValue = keyValueList.get(keyValueList.size() - 1);
        assertThat(testKeyValue.getKvKey()).isEqualTo(UPDATED_KV_KEY);
        assertThat(testKeyValue.getKvValue()).isEqualTo(UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, keyValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKeyValueWithPatch() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();

        // Update the keyValue using partial update
        KeyValue partialUpdatedKeyValue = new KeyValue();
        partialUpdatedKeyValue.setId(keyValue.getId());

        partialUpdatedKeyValue.kvKey(UPDATED_KV_KEY);

        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKeyValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedKeyValue))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
        KeyValue testKeyValue = keyValueList.get(keyValueList.size() - 1);
        assertThat(testKeyValue.getKvKey()).isEqualTo(UPDATED_KV_KEY);
        assertThat(testKeyValue.getKvValue()).isEqualTo(DEFAULT_KV_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateKeyValueWithPatch() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();

        // Update the keyValue using partial update
        KeyValue partialUpdatedKeyValue = new KeyValue();
        partialUpdatedKeyValue.setId(keyValue.getId());

        partialUpdatedKeyValue.kvKey(UPDATED_KV_KEY).kvValue(UPDATED_KV_VALUE);

        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKeyValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedKeyValue))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
        KeyValue testKeyValue = keyValueList.get(keyValueList.size() - 1);
        assertThat(testKeyValue.getKvKey()).isEqualTo(UPDATED_KV_KEY);
        assertThat(testKeyValue.getKvValue()).isEqualTo(UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, keyValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKeyValue() throws Exception {
        int databaseSizeBeforeUpdate = keyValueRepository.findAll().size();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(keyValueDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the KeyValue in the database
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKeyValue() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        int databaseSizeBeforeDelete = keyValueRepository.findAll().size();

        // Delete the keyValue
        restKeyValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, keyValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<KeyValue> keyValueList = keyValueRepository.findAll();
        assertThat(keyValueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
