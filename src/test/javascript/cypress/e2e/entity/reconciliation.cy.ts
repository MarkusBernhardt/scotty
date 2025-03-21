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

describe('Reconciliation e2e test', () => {
  const reconciliationPageUrl = '/reconciliation';
  const reconciliationPageUrlPattern = new RegExp('/reconciliation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reconciliationSample = {
    mandateId: 'whether secret questioningly',
    paymentId: 'whopping afore insolence',
    gateway: 'CCBILL',
    iban: 'RO40GIIG969244292L55S511',
    bic: 'CSDGTHCVE1D',
    amount: 18253,
    currencyCode: 'SDG',
    softDescriptor: 'knickers',
    firstName: 'Brent',
    lastName: 'Ernser',
    streetName: 'Hansen Wells',
    houseNumber: 'window finding c',
    postalCode: 'transcend cop-ou',
    city: 'Ullrichboro',
    countryCode: 'IR',
    remoteIp: 'finished',
    emailAddress: 'polarisation to',
    timestamp: '2024-03-01T09:17:54.177Z',
    state: 'satirize kaleidoscopic safe',
    reasonCode: 'hence fellow encouragement',
    message: 'hotfoot blindly',
  };

  let reconciliation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reconciliations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reconciliations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reconciliations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reconciliation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reconciliations/${reconciliation.id}`,
      }).then(() => {
        reconciliation = undefined;
      });
    }
  });

  it('Reconciliations menu should load Reconciliations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reconciliation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reconciliation').should('exist');
    cy.url().should('match', reconciliationPageUrlPattern);
  });

  describe('Reconciliation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reconciliationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reconciliation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reconciliation/new$'));
        cy.getEntityCreateUpdateHeading('Reconciliation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reconciliationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reconciliations',
          body: reconciliationSample,
        }).then(({ body }) => {
          reconciliation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reconciliations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/reconciliations?page=0&size=20>; rel="last",<http://localhost/api/reconciliations?page=0&size=20>; rel="first"',
              },
              body: [reconciliation],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(reconciliationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reconciliation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reconciliation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reconciliationPageUrlPattern);
      });

      it('edit button click should load edit Reconciliation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reconciliation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reconciliationPageUrlPattern);
      });

      it.skip('edit button click should load edit Reconciliation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reconciliation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reconciliationPageUrlPattern);
      });

      it('last delete button click should delete instance of Reconciliation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reconciliation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reconciliationPageUrlPattern);

        reconciliation = undefined;
      });
    });
  });

  describe('new Reconciliation page', () => {
    beforeEach(() => {
      cy.visit(`${reconciliationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reconciliation');
    });

    it('should create an instance of Reconciliation', () => {
      cy.get(`[data-cy="mandateId"]`).type('tenderly too');
      cy.get(`[data-cy="mandateId"]`).should('have.value', 'tenderly too');

      cy.get(`[data-cy="paymentId"]`).type('minus mountain');
      cy.get(`[data-cy="paymentId"]`).should('have.value', 'minus mountain');

      cy.get(`[data-cy="gateway"]`).select('EMERCHANTPAY');

      cy.get(`[data-cy="iban"]`).type('DO72RNEH00570392405038924692');
      cy.get(`[data-cy="iban"]`).should('have.value', 'DO72RNEH00570392405038924692');

      cy.get(`[data-cy="bic"]`).type('LRKCMRJT');
      cy.get(`[data-cy="bic"]`).should('have.value', 'LRKCMRJT');

      cy.get(`[data-cy="amount"]`).type('23877');
      cy.get(`[data-cy="amount"]`).should('have.value', '23877');

      cy.get(`[data-cy="currencyCode"]`).type('CDF');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'CDF');

      cy.get(`[data-cy="softDescriptor"]`).type('ha');
      cy.get(`[data-cy="softDescriptor"]`).should('have.value', 'ha');

      cy.get(`[data-cy="firstName"]`).type('Clovis');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Clovis');

      cy.get(`[data-cy="lastName"]`).type('Bernier');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Bernier');

      cy.get(`[data-cy="streetName"]`).type('Omari Junction');
      cy.get(`[data-cy="streetName"]`).should('have.value', 'Omari Junction');

      cy.get(`[data-cy="houseNumber"]`).type('gee impeccable b');
      cy.get(`[data-cy="houseNumber"]`).should('have.value', 'gee impeccable b');

      cy.get(`[data-cy="postalCode"]`).type('triumphantly val');
      cy.get(`[data-cy="postalCode"]`).should('have.value', 'triumphantly val');

      cy.get(`[data-cy="city"]`).type('Patienceview');
      cy.get(`[data-cy="city"]`).should('have.value', 'Patienceview');

      cy.get(`[data-cy="countryCode"]`).type('BS');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'BS');

      cy.get(`[data-cy="remoteIp"]`).type('masquerade broadcast');
      cy.get(`[data-cy="remoteIp"]`).should('have.value', 'masquerade broadcast');

      cy.get(`[data-cy="emailAddress"]`).type('affect webbed');
      cy.get(`[data-cy="emailAddress"]`).should('have.value', 'affect webbed');

      cy.get(`[data-cy="timestamp"]`).type('2024-03-01T04:39');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2024-03-01T04:39');

      cy.get(`[data-cy="state"]`).type('utter nor twine');
      cy.get(`[data-cy="state"]`).should('have.value', 'utter nor twine');

      cy.get(`[data-cy="reasonCode"]`).type('though amongst');
      cy.get(`[data-cy="reasonCode"]`).should('have.value', 'though amongst');

      cy.get(`[data-cy="message"]`).type('times incidentally respray');
      cy.get(`[data-cy="message"]`).should('have.value', 'times incidentally respray');

      cy.get(`[data-cy="gatewayId"]`).type('atomize');
      cy.get(`[data-cy="gatewayId"]`).should('have.value', 'atomize');

      cy.get(`[data-cy="mode"]`).type('meh divine colorfully');
      cy.get(`[data-cy="mode"]`).should('have.value', 'meh divine colorfully');

      cy.get(`[data-cy="fileName"]`).type('before');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'before');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        reconciliation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', reconciliationPageUrlPattern);
    });
  });
});
