import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reconciliation.test-samples';

import { ReconciliationFormService } from './reconciliation-form.service';

describe('Reconciliation Form Service', () => {
  let service: ReconciliationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReconciliationFormService);
  });

  describe('Service methods', () => {
    describe('createReconciliationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReconciliationFormGroup();

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
            reasonCode: expect.any(Object),
            message: expect.any(Object),
            gatewayId: expect.any(Object),
            mode: expect.any(Object),
            fileName: expect.any(Object),
            scottyPayment: expect.any(Object),
          }),
        );
      });

      it('passing IReconciliation should create a new form with FormGroup', () => {
        const formGroup = service.createReconciliationFormGroup(sampleWithRequiredData);

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
            reasonCode: expect.any(Object),
            message: expect.any(Object),
            gatewayId: expect.any(Object),
            mode: expect.any(Object),
            fileName: expect.any(Object),
            scottyPayment: expect.any(Object),
          }),
        );
      });
    });

    describe('getReconciliation', () => {
      it('should return NewReconciliation for default Reconciliation initial value', () => {
        const formGroup = service.createReconciliationFormGroup(sampleWithNewData);

        const reconciliation = service.getReconciliation(formGroup) as any;

        expect(reconciliation).toMatchObject(sampleWithNewData);
      });

      it('should return NewReconciliation for empty Reconciliation initial value', () => {
        const formGroup = service.createReconciliationFormGroup();

        const reconciliation = service.getReconciliation(formGroup) as any;

        expect(reconciliation).toMatchObject({});
      });

      it('should return IReconciliation', () => {
        const formGroup = service.createReconciliationFormGroup(sampleWithRequiredData);

        const reconciliation = service.getReconciliation(formGroup) as any;

        expect(reconciliation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReconciliation should not enable id FormControl', () => {
        const formGroup = service.createReconciliationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReconciliation should disable id FormControl', () => {
        const formGroup = service.createReconciliationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
