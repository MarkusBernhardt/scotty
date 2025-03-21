import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('KeyValue e2e test', () => {
  const keyValuePageUrl = '/key-value';
  const keyValuePageUrlPattern = new RegExp('/key-value(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const keyValueSample = { kvKey: 'bliss' };

  let keyValue;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/key-values+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/key-values').as('postEntityRequest');
    cy.intercept('DELETE', '/api/key-values/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (keyValue) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/key-values/${keyValue.id}`,
      }).then(() => {
        keyValue = undefined;
      });
    }
  });

  it('KeyValues menu should load KeyValues page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('key-value');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('KeyValue').should('exist');
    cy.url().should('match', keyValuePageUrlPattern);
  });

  describe('KeyValue page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(keyValuePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create KeyValue page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/key-value/new$'));
        cy.getEntityCreateUpdateHeading('KeyValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', keyValuePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/key-values',
          body: keyValueSample,
        }).then(({ body }) => {
          keyValue = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/key-values+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/key-values?page=0&size=20>; rel="last",<http://localhost/api/key-values?page=0&size=20>; rel="first"',
              },
              body: [keyValue],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(keyValuePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details KeyValue page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('keyValue');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', keyValuePageUrlPattern);
      });

      it('edit button click should load edit KeyValue page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('KeyValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', keyValuePageUrlPattern);
      });

      it.skip('edit button click should load edit KeyValue page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('KeyValue');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', keyValuePageUrlPattern);
      });

      it('last delete button click should delete instance of KeyValue', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('keyValue').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', keyValuePageUrlPattern);

        keyValue = undefined;
      });
    });
  });

  describe('new KeyValue page', () => {
    beforeEach(() => {
      cy.visit(`${keyValuePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('KeyValue');
    });

    it('should create an instance of KeyValue', () => {
      cy.get(`[data-cy="kvKey"]`).type('apologise spook');
      cy.get(`[data-cy="kvKey"]`).should('have.value', 'apologise spook');

      cy.get(`[data-cy="kvValue"]`).type('phew materialise');
      cy.get(`[data-cy="kvValue"]`).should('have.value', 'phew materialise');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        keyValue = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', keyValuePageUrlPattern);
    });
  });
});
