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
    mandateId: 'brr',
    paymentId: 'or excluding typeface',
    gateway: 'EMERCHANTPAY',
    iban: 'GT6162B7709114427987628L7694',
    bic: 'DZEZGE7SXXX',
    amount: 11400,
    currencyCode: 'RSD',
    softDescriptor: 'mockingly tough circa',
    firstName: 'Hans',
    lastName: 'Smitham',
    streetName: 'Emmalee Walks',
    houseNumber: 'unblinking',
    postalCode: 'multicolored',
    city: 'Fort Brisaport',
    countryCode: 'ZA',
    remoteIp: 'who incidentally',
    emailAddress: 'yet radiant convection',
    timestamp: '2024-03-01T03:47:57.131Z',
    state: 'ugh',
    reasonCode: 'hence joyously horse',
    message: 'homely instead',
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
      cy.get(`[data-cy="mandateId"]`).type('vivid wretched');
      cy.get(`[data-cy="mandateId"]`).should('have.value', 'vivid wretched');

      cy.get(`[data-cy="paymentId"]`).type('buck');
      cy.get(`[data-cy="paymentId"]`).should('have.value', 'buck');

      cy.get(`[data-cy="gateway"]`).select('UNKNOWN');

      cy.get(`[data-cy="iban"]`).type('MU10GOYP5426165118001160013UAW');
      cy.get(`[data-cy="iban"]`).should('have.value', 'MU10GOYP5426165118001160013UAW');

      cy.get(`[data-cy="bic"]`).type('RXSHPYOK');
      cy.get(`[data-cy="bic"]`).should('have.value', 'RXSHPYOK');

      cy.get(`[data-cy="amount"]`).type('26910');
      cy.get(`[data-cy="amount"]`).should('have.value', '26910');

      cy.get(`[data-cy="currencyCode"]`).type('CHF');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'CHF');

      cy.get(`[data-cy="softDescriptor"]`).type('extent pfft ew');
      cy.get(`[data-cy="softDescriptor"]`).should('have.value', 'extent pfft ew');

      cy.get(`[data-cy="firstName"]`).type('Bernie');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Bernie');

      cy.get(`[data-cy="lastName"]`).type('Davis');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Davis');

      cy.get(`[data-cy="streetName"]`).type('Elmira Forks');
      cy.get(`[data-cy="streetName"]`).should('have.value', 'Elmira Forks');

      cy.get(`[data-cy="houseNumber"]`).type('randomize squigg');
      cy.get(`[data-cy="houseNumber"]`).should('have.value', 'randomize squigg');

      cy.get(`[data-cy="postalCode"]`).type('without');
      cy.get(`[data-cy="postalCode"]`).should('have.value', 'without');

      cy.get(`[data-cy="city"]`).type('Wylie');
      cy.get(`[data-cy="city"]`).should('have.value', 'Wylie');

      cy.get(`[data-cy="countryCode"]`).type('SK');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'SK');

      cy.get(`[data-cy="remoteIp"]`).type('afore insolence hm');
      cy.get(`[data-cy="remoteIp"]`).should('have.value', 'afore insolence hm');

      cy.get(`[data-cy="emailAddress"]`).type('mummify tenant now');
      cy.get(`[data-cy="emailAddress"]`).should('have.value', 'mummify tenant now');

      cy.get(`[data-cy="timestamp"]`).type('2024-03-01T02:35');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2024-03-01T02:35');

      cy.get(`[data-cy="state"]`).type('clamor indeed ouch');
      cy.get(`[data-cy="state"]`).should('have.value', 'clamor indeed ouch');

      cy.get(`[data-cy="reasonCode"]`).type('knickers');
      cy.get(`[data-cy="reasonCode"]`).should('have.value', 'knickers');

      cy.get(`[data-cy="message"]`).type('mundane');
      cy.get(`[data-cy="message"]`).should('have.value', 'mundane');

      cy.get(`[data-cy="gatewayId"]`).type('furiously more');
      cy.get(`[data-cy="gatewayId"]`).should('have.value', 'furiously more');

      cy.get(`[data-cy="mode"]`).type('unless');
      cy.get(`[data-cy="mode"]`).should('have.value', 'unless');

      cy.get(`[data-cy="fileName"]`).type('far ew tangible');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'far ew tangible');

      cy.get(`[data-cy="creditorName"]`).type('a kowtow');
      cy.get(`[data-cy="creditorName"]`).should('have.value', 'a kowtow');

      cy.get(`[data-cy="creditorIban"]`).type('thoroughlyXXXXXX');
      cy.get(`[data-cy="creditorIban"]`).should('have.value', 'thoroughlyXXXXXX');

      cy.get(`[data-cy="creditorBic"]`).type('quixotic bo');
      cy.get(`[data-cy="creditorBic"]`).should('have.value', 'quixotic bo');

      cy.get(`[data-cy="creditorId"]`).type('madly whereas');
      cy.get(`[data-cy="creditorId"]`).should('have.value', 'madly whereas');

      cy.get(`[data-cy="mandateDate"]`).type('2024-02-29');
      cy.get(`[data-cy="mandateDate"]`).blur();
      cy.get(`[data-cy="mandateDate"]`).should('have.value', '2024-02-29');

      cy.get(`[data-cy="executionDate"]`).type('2024-02-29');
      cy.get(`[data-cy="executionDate"]`).blur();
      cy.get(`[data-cy="executionDate"]`).should('have.value', '2024-02-29');

      cy.get(`[data-cy="paymentInformationId"]`).type('so');
      cy.get(`[data-cy="paymentInformationId"]`).should('have.value', 'so');

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
