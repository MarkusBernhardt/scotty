import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReconciliation, NewReconciliation } from '../reconciliation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReconciliation for edit and NewReconciliationFormGroupInput for create.
 */
type ReconciliationFormGroupInput = IReconciliation | PartialWithRequiredKeyOf<NewReconciliation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReconciliation | NewReconciliation> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

type ReconciliationFormRawValue = FormValueOf<IReconciliation>;

type NewReconciliationFormRawValue = FormValueOf<NewReconciliation>;

type ReconciliationFormDefaults = Pick<NewReconciliation, 'id' | 'timestamp'>;

type ReconciliationFormGroupContent = {
  id: FormControl<ReconciliationFormRawValue['id'] | NewReconciliation['id']>;
  mandateId: FormControl<ReconciliationFormRawValue['mandateId']>;
  paymentId: FormControl<ReconciliationFormRawValue['paymentId']>;
  gateway: FormControl<ReconciliationFormRawValue['gateway']>;
  iban: FormControl<ReconciliationFormRawValue['iban']>;
  bic: FormControl<ReconciliationFormRawValue['bic']>;
  amount: FormControl<ReconciliationFormRawValue['amount']>;
  currencyCode: FormControl<ReconciliationFormRawValue['currencyCode']>;
  softDescriptor: FormControl<ReconciliationFormRawValue['softDescriptor']>;
  firstName: FormControl<ReconciliationFormRawValue['firstName']>;
  lastName: FormControl<ReconciliationFormRawValue['lastName']>;
  addressLine1: FormControl<ReconciliationFormRawValue['addressLine1']>;
  addressLine2: FormControl<ReconciliationFormRawValue['addressLine2']>;
  postalCode: FormControl<ReconciliationFormRawValue['postalCode']>;
  city: FormControl<ReconciliationFormRawValue['city']>;
  countryCode: FormControl<ReconciliationFormRawValue['countryCode']>;
  remoteIp: FormControl<ReconciliationFormRawValue['remoteIp']>;
  timestamp: FormControl<ReconciliationFormRawValue['timestamp']>;
  state: FormControl<ReconciliationFormRawValue['state']>;
  message: FormControl<ReconciliationFormRawValue['message']>;
  gatewayId: FormControl<ReconciliationFormRawValue['gatewayId']>;
  mode: FormControl<ReconciliationFormRawValue['mode']>;
  fileName: FormControl<ReconciliationFormRawValue['fileName']>;
  scottyPayment: FormControl<ReconciliationFormRawValue['scottyPayment']>;
};

export type ReconciliationFormGroup = FormGroup<ReconciliationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReconciliationFormService {
  createReconciliationFormGroup(reconciliation: ReconciliationFormGroupInput = { id: null }): ReconciliationFormGroup {
    const reconciliationRawValue = this.convertReconciliationToReconciliationRawValue({
      ...this.getFormDefaults(),
      ...reconciliation,
    });
    return new FormGroup<ReconciliationFormGroupContent>({
      id: new FormControl(
        { value: reconciliationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      mandateId: new FormControl(reconciliationRawValue.mandateId, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      paymentId: new FormControl(reconciliationRawValue.paymentId, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      gateway: new FormControl(reconciliationRawValue.gateway, {
        validators: [Validators.required],
      }),
      iban: new FormControl(reconciliationRawValue.iban, {
        validators: [Validators.required, Validators.minLength(16), Validators.maxLength(34)],
      }),
      bic: new FormControl(reconciliationRawValue.bic, {
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(11)],
      }),
      amount: new FormControl(reconciliationRawValue.amount, {
        validators: [Validators.required],
      }),
      currencyCode: new FormControl(reconciliationRawValue.currencyCode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(3)],
      }),
      softDescriptor: new FormControl(reconciliationRawValue.softDescriptor, {
        validators: [Validators.required, Validators.maxLength(140)],
      }),
      firstName: new FormControl(reconciliationRawValue.firstName, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      lastName: new FormControl(reconciliationRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      addressLine1: new FormControl(reconciliationRawValue.addressLine1, {
        validators: [Validators.required, Validators.maxLength(70)],
      }),
      addressLine2: new FormControl(reconciliationRawValue.addressLine2, {
        validators: [Validators.maxLength(70)],
      }),
      postalCode: new FormControl(reconciliationRawValue.postalCode, {
        validators: [Validators.required, Validators.maxLength(16)],
      }),
      city: new FormControl(reconciliationRawValue.city, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      countryCode: new FormControl(reconciliationRawValue.countryCode, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(2)],
      }),
      remoteIp: new FormControl(reconciliationRawValue.remoteIp, {
        validators: [Validators.required, Validators.maxLength(39)],
      }),
      timestamp: new FormControl(reconciliationRawValue.timestamp, {
        validators: [Validators.required],
      }),
      state: new FormControl(reconciliationRawValue.state, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      message: new FormControl(reconciliationRawValue.message, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      gatewayId: new FormControl(reconciliationRawValue.gatewayId, {
        validators: [Validators.maxLength(35)],
      }),
      mode: new FormControl(reconciliationRawValue.mode, {
        validators: [Validators.maxLength(35)],
      }),
      fileName: new FormControl(reconciliationRawValue.fileName, {
        validators: [Validators.maxLength(255)],
      }),
      scottyPayment: new FormControl(reconciliationRawValue.scottyPayment),
    });
  }

  getReconciliation(form: ReconciliationFormGroup): IReconciliation | NewReconciliation {
    return this.convertReconciliationRawValueToReconciliation(
      form.getRawValue() as ReconciliationFormRawValue | NewReconciliationFormRawValue,
    );
  }

  resetForm(form: ReconciliationFormGroup, reconciliation: ReconciliationFormGroupInput): void {
    const reconciliationRawValue = this.convertReconciliationToReconciliationRawValue({ ...this.getFormDefaults(), ...reconciliation });
    form.reset(
      {
        ...reconciliationRawValue,
        id: { value: reconciliationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReconciliationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      timestamp: currentTime,
    };
  }

  private convertReconciliationRawValueToReconciliation(
    rawReconciliation: ReconciliationFormRawValue | NewReconciliationFormRawValue,
  ): IReconciliation | NewReconciliation {
    return {
      ...rawReconciliation,
      timestamp: dayjs(rawReconciliation.timestamp, DATE_TIME_FORMAT),
    };
  }

  private convertReconciliationToReconciliationRawValue(
    reconciliation: IReconciliation | (Partial<NewReconciliation> & ReconciliationFormDefaults),
  ): ReconciliationFormRawValue | PartialWithRequiredKeyOf<NewReconciliationFormRawValue> {
    return {
      ...reconciliation,
      timestamp: reconciliation.timestamp ? reconciliation.timestamp.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
