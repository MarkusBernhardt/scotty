import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../payment.test-samples';

import { PaymentFormService } from './payment-form.service';

describe('Payment Form Service', () => {
  let service: PaymentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentFormService);
  });

  describe('Service methods', () => {
    describe('createPaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaymentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mandateId: expect.any(Object),
            paymentId: expect.any(Object),
            gateway: expect.any(Object),
            iban: expect.any(Object),
            bic: expect.any(Object),
            amount: expect.any(Object),
            currencyCode: expect.any(Object),
            softDescriptor: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            streetName: expect.any(Object),
            houseNumber: expect.any(Object),
            postalCode: expect.any(Object),
            city: expect.any(Object),
            countryCode: expect.any(Object),
            remoteIp: expect.any(Object),
            emailAddress: expect.any(Object),
            timestamp: expect.any(Object),
            state: expect.any(Object),
            message: expect.any(Object),
            gatewayId: expect.any(Object),
            mode: expect.any(Object),
            fileName: expect.any(Object),
            creditorName: expect.any(Object),
            creditorIban: expect.any(Object),
            creditorBic: expect.any(Object),
            creditorId: expect.any(Object),
            mandateDate: expect.any(Object),
            executionDate: expect.any(Object),
            paymentInformationId: expect.any(Object),
          }),
        );
      });

      it('passing IPayment should create a new form with FormGroup', () => {
        const formGroup = service.createPaymentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            mandateId: expect.any(Object),
            paymentId: expect.any(Object),
            gateway: expect.any(Object),
            iban: expect.any(Object),
            bic: expect.any(Object),
            amount: expect.any(Object),
            currencyCode: expect.any(Object),
            softDescriptor: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            streetName: expect.any(Object),
            houseNumber: expect.any(Object),
            postalCode: expect.any(Object),
            city: expect.any(Object),
            countryCode: expect.any(Object),
            remoteIp: expect.any(Object),
            emailAddress: expect.any(Object),
            timestamp: expect.any(Object),
            state: expect.any(Object),
            message: expect.any(Object),
            gatewayId: expect.any(Object),
            mode: expect.any(Object),
            fileName: expect.any(Object),
            creditorName: expect.any(Object),
            creditorIban: expect.any(Object),
            creditorBic: expect.any(Object),
            creditorId: expect.any(Object),
            mandateDate: expect.any(Object),
            executionDate: expect.any(Object),
            paymentInformationId: expect.any(Object),
          }),
        );
      });
    });

    describe('getPayment', () => {
      it('should return NewPayment for default Payment initial value', () => {
        const formGroup = service.createPaymentFormGroup(sampleWithNewData);

        const payment = service.getPayment(formGroup) as any;

        expect(payment).toMatchObject(sampleWithNewData);
      });

      it('should return NewPayment for empty Payment initial value', () => {
        const formGroup = service.createPaymentFormGroup();

        const payment = service.getPayment(formGroup) as any;

        expect(payment).toMatchObject({});
      });

      it('should return IPayment', () => {
        const formGroup = service.createPaymentFormGroup(sampleWithRequiredData);

        const payment = service.getPayment(formGroup) as any;

        expect(payment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPayment should not enable id FormControl', () => {
        const formGroup = service.createPaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPayment should disable id FormControl', () => {
        const formGroup = service.createPaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
