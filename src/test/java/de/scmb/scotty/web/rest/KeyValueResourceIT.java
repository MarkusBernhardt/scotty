package de.scmb.scotty.web.rest;

import static de.scmb.scotty.domain.KeyValueAsserts.*;
import static de.scmb.scotty.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.IntegrationTest;
import de.scmb.scotty.domain.KeyValue;
import de.scmb.scotty.repository.KeyValueRepository;
import de.scmb.scotty.service.dto.KeyValueDTO;
import de.scmb.scotty.service.mapper.KeyValueMapper;
import jakarta.persistence.EntityManager;
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
    private ObjectMapper om;

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Autowired
    private KeyValueMapper keyValueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKeyValueMockMvc;

    private KeyValue keyValue;

    private KeyValue insertedKeyValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KeyValue createEntity() {
        return new KeyValue().kvKey(DEFAULT_KV_KEY).kvValue(DEFAULT_KV_VALUE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KeyValue createUpdatedEntity() {
        return new KeyValue().kvKey(UPDATED_KV_KEY).kvValue(UPDATED_KV_VALUE);
    }

    @BeforeEach
    public void initTest() {
        keyValue = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedKeyValue != null) {
            keyValueRepository.delete(insertedKeyValue);
            insertedKeyValue = null;
        }
    }

    @Test
    @Transactional
    void createKeyValue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);
        var returnedKeyValueDTO = om.readValue(
            restKeyValueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(keyValueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KeyValueDTO.class
        );

        // Validate the KeyValue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKeyValue = keyValueMapper.toEntity(returnedKeyValueDTO);
        assertKeyValueUpdatableFieldsEquals(returnedKeyValue, getPersistedKeyValue(returnedKeyValue));

        insertedKeyValue = returnedKeyValue;
    }

    @Test
    @Transactional
    void createKeyValueWithExistingId() throws Exception {
        // Create the KeyValue with an existing ID
        keyValue.setId(1L);
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restKeyValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(keyValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKvKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        keyValue.setKvKey(null);

        // Create the KeyValue, which fails.
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        restKeyValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(keyValueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllKeyValues() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

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
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

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
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        Long id = keyValue.getId();

        defaultKeyValueFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKeyValueFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKeyValueFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey equals to
        defaultKeyValueFiltering("kvKey.equals=" + DEFAULT_KV_KEY, "kvKey.equals=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey in
        defaultKeyValueFiltering("kvKey.in=" + DEFAULT_KV_KEY + "," + UPDATED_KV_KEY, "kvKey.in=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey is not null
        defaultKeyValueFiltering("kvKey.specified=true", "kvKey.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey contains
        defaultKeyValueFiltering("kvKey.contains=" + DEFAULT_KV_KEY, "kvKey.contains=" + UPDATED_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvKey does not contain
        defaultKeyValueFiltering("kvKey.doesNotContain=" + UPDATED_KV_KEY, "kvKey.doesNotContain=" + DEFAULT_KV_KEY);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue equals to
        defaultKeyValueFiltering("kvValue.equals=" + DEFAULT_KV_VALUE, "kvValue.equals=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue in
        defaultKeyValueFiltering("kvValue.in=" + DEFAULT_KV_VALUE + "," + UPDATED_KV_VALUE, "kvValue.in=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue is not null
        defaultKeyValueFiltering("kvValue.specified=true", "kvValue.specified=false");
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueContainsSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue contains
        defaultKeyValueFiltering("kvValue.contains=" + DEFAULT_KV_VALUE, "kvValue.contains=" + UPDATED_KV_VALUE);
    }

    @Test
    @Transactional
    void getAllKeyValuesByKvValueNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        // Get all the keyValueList where kvValue does not contain
        defaultKeyValueFiltering("kvValue.doesNotContain=" + UPDATED_KV_VALUE, "kvValue.doesNotContain=" + DEFAULT_KV_VALUE);
    }

    private void defaultKeyValueFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKeyValueShouldBeFound(shouldBeFound);
        defaultKeyValueShouldNotBeFound(shouldNotBeFound);
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
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

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
                    .content(om.writeValueAsBytes(keyValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKeyValueToMatchAllProperties(updatedKeyValue);
    }

    @Test
    @Transactional
    void putNonExistingKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, keyValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(keyValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateKeyValueWithPatch() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the keyValue using partial update
        KeyValue partialUpdatedKeyValue = new KeyValue();
        partialUpdatedKeyValue.setId(keyValue.getId());

        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKeyValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKeyValue))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKeyValueUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedKeyValue, keyValue), getPersistedKeyValue(keyValue));
    }

    @Test
    @Transactional
    void fullUpdateKeyValueWithPatch() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the keyValue using partial update
        KeyValue partialUpdatedKeyValue = new KeyValue();
        partialUpdatedKeyValue.setId(keyValue.getId());

        partialUpdatedKeyValue.kvKey(UPDATED_KV_KEY).kvValue(UPDATED_KV_VALUE);

        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKeyValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKeyValue))
            )
            .andExpect(status().isOk());

        // Validate the KeyValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKeyValueUpdatableFieldsEquals(partialUpdatedKeyValue, getPersistedKeyValue(partialUpdatedKeyValue));
    }

    @Test
    @Transactional
    void patchNonExistingKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, keyValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(keyValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKeyValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        keyValue.setId(longCount.incrementAndGet());

        // Create the KeyValue
        KeyValueDTO keyValueDTO = keyValueMapper.toDto(keyValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKeyValueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(keyValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the KeyValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteKeyValue() throws Exception {
        // Initialize the database
        insertedKeyValue = keyValueRepository.saveAndFlush(keyValue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the keyValue
        restKeyValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, keyValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return keyValueRepository.count();
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

    protected KeyValue getPersistedKeyValue(KeyValue keyValue) {
        return keyValueRepository.findById(keyValue.getId()).orElseThrow();
    }

    protected void assertPersistedKeyValueToMatchAllProperties(KeyValue expectedKeyValue) {
        assertKeyValueAllPropertiesEquals(expectedKeyValue, getPersistedKeyValue(expectedKeyValue));
    }

    protected void assertPersistedKeyValueToMatchUpdatableProperties(KeyValue expectedKeyValue) {
        assertKeyValueAllUpdatablePropertiesEquals(expectedKeyValue, getPersistedKeyValue(expectedKeyValue));
    }
}
