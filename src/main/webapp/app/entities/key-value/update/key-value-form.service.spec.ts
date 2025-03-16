import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../key-value.test-samples';

import { KeyValueFormService } from './key-value-form.service';

describe('KeyValue Form Service', () => {
  let service: KeyValueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KeyValueFormService);
  });

  describe('Service methods', () => {
    describe('createKeyValueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createKeyValueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            kvKey: expect.any(Object),
            kvValue: expect.any(Object),
          }),
        );
      });

      it('passing IKeyValue should create a new form with FormGroup', () => {
        const formGroup = service.createKeyValueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            kvKey: expect.any(Object),
            kvValue: expect.any(Object),
          }),
        );
      });
    });

    describe('getKeyValue', () => {
      it('should return NewKeyValue for default KeyValue initial value', () => {
        const formGroup = service.createKeyValueFormGroup(sampleWithNewData);

        const keyValue = service.getKeyValue(formGroup) as any;

        expect(keyValue).toMatchObject(sampleWithNewData);
      });

      it('should return NewKeyValue for empty KeyValue initial value', () => {
        const formGroup = service.createKeyValueFormGroup();

        const keyValue = service.getKeyValue(formGroup) as any;

        expect(keyValue).toMatchObject({});
      });

      it('should return IKeyValue', () => {
        const formGroup = service.createKeyValueFormGroup(sampleWithRequiredData);

        const keyValue = service.getKeyValue(formGroup) as any;

        expect(keyValue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IKeyValue should not enable id FormControl', () => {
        const formGroup = service.createKeyValueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewKeyValue should disable id FormControl', () => {
        const formGroup = service.createKeyValueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
