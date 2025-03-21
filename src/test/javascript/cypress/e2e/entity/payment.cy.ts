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
    mandateId: 'though',
    paymentId: 'woot fatally times',
    gateway: 'OPENPAYD',
    iban: 'MT19GZWT0061529B9059X91M88KW17K',
    bic: 'JEJEPHI6',
    amount: 11893,
    currencyCode: 'CHF',
    softDescriptor: 'behind fail',
    firstName: 'Diana',
    lastName: 'Wilderman',
    streetName: 'Waverley Road',
    houseNumber: 'fraternise',
    postalCode: 'than phew worthy',
    city: 'New Jeremy',
    countryCode: 'BI',
    remoteIp: 'innovate',
    emailAddress: 'pace',
    timestamp: '2024-03-01T12:57:50.366Z',
    state: 'absent',
    message: 'on shrill',
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
      cy.get(`[data-cy="mandateId"]`).type('headline shameless');
      cy.get(`[data-cy="mandateId"]`).should('have.value', 'headline shameless');

      cy.get(`[data-cy="paymentId"]`).type('cruelly since');
      cy.get(`[data-cy="paymentId"]`).should('have.value', 'cruelly since');

      cy.get(`[data-cy="gateway"]`).select('NOVALNET');

      cy.get(`[data-cy="iban"]`).type('IS079466540092193828685002');
      cy.get(`[data-cy="iban"]`).should('have.value', 'IS079466540092193828685002');

      cy.get(`[data-cy="bic"]`).type('HVPCVAPGXXX');
      cy.get(`[data-cy="bic"]`).should('have.value', 'HVPCVAPGXXX');

      cy.get(`[data-cy="amount"]`).type('4303');
      cy.get(`[data-cy="amount"]`).should('have.value', '4303');

      cy.get(`[data-cy="currencyCode"]`).type('TWD');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'TWD');

      cy.get(`[data-cy="softDescriptor"]`).type('calculus');
      cy.get(`[data-cy="softDescriptor"]`).should('have.value', 'calculus');

      cy.get(`[data-cy="firstName"]`).type('Anya');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Anya');

      cy.get(`[data-cy="lastName"]`).type('Nienow');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Nienow');

      cy.get(`[data-cy="streetName"]`).type('Manor Drive');
      cy.get(`[data-cy="streetName"]`).should('have.value', 'Manor Drive');

      cy.get(`[data-cy="houseNumber"]`).type('above');
      cy.get(`[data-cy="houseNumber"]`).should('have.value', 'above');

      cy.get(`[data-cy="postalCode"]`).type('yellowish');
      cy.get(`[data-cy="postalCode"]`).should('have.value', 'yellowish');

      cy.get(`[data-cy="city"]`).type('Feeneyton');
      cy.get(`[data-cy="city"]`).should('have.value', 'Feeneyton');

      cy.get(`[data-cy="countryCode"]`).type('CC');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'CC');

      cy.get(`[data-cy="remoteIp"]`).type('alligator untried');
      cy.get(`[data-cy="remoteIp"]`).should('have.value', 'alligator untried');

      cy.get(`[data-cy="emailAddress"]`).type('kindly');
      cy.get(`[data-cy="emailAddress"]`).should('have.value', 'kindly');

      cy.get(`[data-cy="timestamp"]`).type('2024-02-29T22:56');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2024-02-29T22:56');

      cy.get(`[data-cy="state"]`).type('shark oily gah');
      cy.get(`[data-cy="state"]`).should('have.value', 'shark oily gah');

      cy.get(`[data-cy="message"]`).type('yum how');
      cy.get(`[data-cy="message"]`).should('have.value', 'yum how');

      cy.get(`[data-cy="gatewayId"]`).type('cuddly like');
      cy.get(`[data-cy="gatewayId"]`).should('have.value', 'cuddly like');

      cy.get(`[data-cy="mode"]`).type('minus');
      cy.get(`[data-cy="mode"]`).should('have.value', 'minus');

      cy.get(`[data-cy="fileName"]`).type('voluminous topsail wring');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'voluminous topsail wring');

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
