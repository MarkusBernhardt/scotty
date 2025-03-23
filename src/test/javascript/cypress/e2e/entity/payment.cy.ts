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

describe('Payment e2e test', () => {
  const paymentPageUrl = '/payment';
  const paymentPageUrlPattern = new RegExp('/payment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const paymentSample = {
    mandateId: 'lest yowza hello',
    paymentId: 'conjecture',
    gateway: 'CCBILL',
    iban: 'FI4607067941009075',
    bic: 'MXFVNACN',
    amount: 13257,
    currencyCode: 'MKD',
    softDescriptor: 'knowingly',
    firstName: 'Dylan',
    lastName: 'Murphy',
    streetName: 'Silver Street',
    houseNumber: 'uh-huh',
    postalCode: 'funny',
    city: 'Huelbury',
    countryCode: 'AU',
    remoteIp: 'sideboard at',
    emailAddress: 'unique',
    timestamp: '2024-03-01T12:14:35.061Z',
    state: 'penalise boo',
    message: 'circa suspension despite',
  };

  let payment;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/payments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/payments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/payments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (payment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payments/${payment.id}`,
      }).then(() => {
        payment = undefined;
      });
    }
  });

  it('Payments menu should load Payments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('payment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Payment').should('exist');
    cy.url().should('match', paymentPageUrlPattern);
  });

  describe('Payment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paymentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Payment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/payment/new$'));
        cy.getEntityCreateUpdateHeading('Payment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/payments',
          body: paymentSample,
        }).then(({ body }) => {
          payment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/payments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/payments?page=0&size=20>; rel="last",<http://localhost/api/payments?page=0&size=20>; rel="first"',
              },
              body: [payment],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(paymentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Payment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('payment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });

      it('edit button click should load edit Payment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Payment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });

      it.skip('edit button click should load edit Payment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Payment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });

      it('last delete button click should delete instance of Payment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('payment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);

        payment = undefined;
      });
    });
  });

  describe('new Payment page', () => {
    beforeEach(() => {
      cy.visit(`${paymentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Payment');
    });

    it('should create an instance of Payment', () => {
      cy.get(`[data-cy="mandateId"]`).type('impish qua');
      cy.get(`[data-cy="mandateId"]`).should('have.value', 'impish qua');

      cy.get(`[data-cy="paymentId"]`).type('onto atop');
      cy.get(`[data-cy="paymentId"]`).should('have.value', 'onto atop');

      cy.get(`[data-cy="gateway"]`).select('UNKNOWN');

      cy.get(`[data-cy="iban"]`).type('FI8928319290050016');
      cy.get(`[data-cy="iban"]`).should('have.value', 'FI8928319290050016');

      cy.get(`[data-cy="bic"]`).type('JPOSVCWW');
      cy.get(`[data-cy="bic"]`).should('have.value', 'JPOSVCWW');

      cy.get(`[data-cy="amount"]`).type('15421');
      cy.get(`[data-cy="amount"]`).should('have.value', '15421');

      cy.get(`[data-cy="currencyCode"]`).type('VES');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'VES');

      cy.get(`[data-cy="softDescriptor"]`).type('yippee esteemed');
      cy.get(`[data-cy="softDescriptor"]`).should('have.value', 'yippee esteemed');

      cy.get(`[data-cy="firstName"]`).type('Zane');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Zane');

      cy.get(`[data-cy="lastName"]`).type('Bode');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Bode');

      cy.get(`[data-cy="streetName"]`).type('Michele Glen');
      cy.get(`[data-cy="streetName"]`).should('have.value', 'Michele Glen');

      cy.get(`[data-cy="houseNumber"]`).type('charm');
      cy.get(`[data-cy="houseNumber"]`).should('have.value', 'charm');

      cy.get(`[data-cy="postalCode"]`).type('soap how stint');
      cy.get(`[data-cy="postalCode"]`).should('have.value', 'soap how stint');

      cy.get(`[data-cy="city"]`).type('Balistreriburgh');
      cy.get(`[data-cy="city"]`).should('have.value', 'Balistreriburgh');

      cy.get(`[data-cy="countryCode"]`).type('MX');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'MX');

      cy.get(`[data-cy="remoteIp"]`).type('phooey');
      cy.get(`[data-cy="remoteIp"]`).should('have.value', 'phooey');

      cy.get(`[data-cy="emailAddress"]`).type('upon');
      cy.get(`[data-cy="emailAddress"]`).should('have.value', 'upon');

      cy.get(`[data-cy="timestamp"]`).type('2024-03-01T13:06');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2024-03-01T13:06');

      cy.get(`[data-cy="state"]`).type('dimly towards of');
      cy.get(`[data-cy="state"]`).should('have.value', 'dimly towards of');

      cy.get(`[data-cy="message"]`).type('hm snow');
      cy.get(`[data-cy="message"]`).should('have.value', 'hm snow');

      cy.get(`[data-cy="gatewayId"]`).type('uh-huh');
      cy.get(`[data-cy="gatewayId"]`).should('have.value', 'uh-huh');

      cy.get(`[data-cy="mode"]`).type('hold');
      cy.get(`[data-cy="mode"]`).should('have.value', 'hold');

      cy.get(`[data-cy="fileName"]`).type('misappropriate regarding');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'misappropriate regarding');

      cy.get(`[data-cy="creditorName"]`).type('into offensively without');
      cy.get(`[data-cy="creditorName"]`).should('have.value', 'into offensively without');

      cy.get(`[data-cy="creditorIban"]`).type('subtract down emergent');
      cy.get(`[data-cy="creditorIban"]`).should('have.value', 'subtract down emergent');

      cy.get(`[data-cy="creditorBic"]`).type('unscramble ');
      cy.get(`[data-cy="creditorBic"]`).should('have.value', 'unscramble ');

      cy.get(`[data-cy="creditorId"]`).type('pace dreamily gosh');
      cy.get(`[data-cy="creditorId"]`).should('have.value', 'pace dreamily gosh');

      cy.get(`[data-cy="mandateDate"]`).type('2024-02-29T18:09');
      cy.get(`[data-cy="mandateDate"]`).blur();
      cy.get(`[data-cy="mandateDate"]`).should('have.value', '2024-02-29T18:09');

      cy.get(`[data-cy="executionDate"]`).type('2024-03-01T06:40');
      cy.get(`[data-cy="executionDate"]`).blur();
      cy.get(`[data-cy="executionDate"]`).should('have.value', '2024-03-01T06:40');

      cy.get(`[data-cy="paymentInformationId"]`).type('duh meanwhile beside');
      cy.get(`[data-cy="paymentInformationId"]`).should('have.value', 'duh meanwhile beside');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        payment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', paymentPageUrlPattern);
    });
  });
});
