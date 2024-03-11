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

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

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
        KeyValue keyValue = new KeyValue().key(DEFAULT_KEY).value(DEFAULT_VALUE);
        return keyValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KeyValue createUpdatedEntity(EntityManager em) {
        KeyValue keyValue = new KeyValue().key(UPDATED_KEY).value(UPDATED_VALUE);
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
        assertThat(testKeyValue.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testKeyValue.getValue()).isEqualTo(DEFAULT_VALUE);
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
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = keyValueRepository.findAll().size();
        // set the field null
        keyValue.setKey(null);

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
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
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
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
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
    void getAllKeyValuesByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where key equals to DEFAULT_KEY
        defaultKeyValueShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the keyValueList where key equals to UPDATED_KEY
        defaultKeyValueShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where key in DEFAULT_KEY or UPDATED_KEY
        defaultKeyValueShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the keyValueList where key equals to UPDATED_KEY
        defaultKeyValueShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where key is not null
        defaultKeyValueShouldBeFound("key.specified=true");

        // Get all the keyValueList where key is null
        defaultKeyValueShouldNotBeFound("key.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByKeyContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where key contains DEFAULT_KEY
        defaultKeyValueShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the keyValueList where key contains UPDATED_KEY
        defaultKeyValueShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where key does not contain DEFAULT_KEY
        defaultKeyValueShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the keyValueList where key does not contain UPDATED_KEY
        defaultKeyValueShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where value equals to DEFAULT_VALUE
        defaultKeyValueShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the keyValueList where value equals to UPDATED_VALUE
        defaultKeyValueShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByValueIsInShouldWork() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultKeyValueShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the keyValueList where value equals to UPDATED_VALUE
        defaultKeyValueShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where value is not null
        defaultKeyValueShouldBeFound("value.specified=true");

        // Get all the keyValueList where value is null
        defaultKeyValueShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByValueContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where value contains DEFAULT_VALUE
        defaultKeyValueShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the keyValueList where value contains UPDATED_VALUE
        defaultKeyValueShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByValueNotContainsSomething() throws Exception {
        // Initialize the database
        keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where value does not contain DEFAULT_VALUE
        defaultKeyValueShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the keyValueList where value does not contain UPDATED_VALUE
        defaultKeyValueShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
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
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

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
        updatedKeyValue.key(UPDATED_KEY).value(UPDATED_VALUE);
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
        assertThat(testKeyValue.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testKeyValue.getValue()).isEqualTo(UPDATED_VALUE);
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

        partialUpdatedKeyValue.key(UPDATED_KEY);

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
        assertThat(testKeyValue.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testKeyValue.getValue()).isEqualTo(DEFAULT_VALUE);
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

        partialUpdatedKeyValue.key(UPDATED_KEY).value(UPDATED_VALUE);

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
        assertThat(testKeyValue.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testKeyValue.getValue()).isEqualTo(UPDATED_VALUE);
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
