import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPayment, NewPayment } from '../payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPayment for edit and NewPaymentFormGroupInput for create.
 */
type PaymentFormGroupInput = IPayment | PartialWithRequiredKeyOf<NewPayment>;

type PaymentFormDefaults = Pick<NewPayment, 'id'>;

type PaymentFormGroupContent = {
  id: FormControl<IPayment['id'] | NewPayment['id']>;
  mandateId: FormControl<IPayment['mandateId']>;
  paymentId: FormControl<IPayment['paymentId']>;
  gateway: FormControl<IPayment['gateway']>;
  iban: FormControl<IPayment['iban']>;
  bic: FormControl<IPayment['bic']>;
  amount: FormControl<IPayment['amount']>;
  currencyCode: FormControl<IPayment['currencyCode']>;
  softDescriptor: FormControl<IPayment['softDescriptor']>;
  firstName: FormControl<IPayment['firstName']>;
  lastName: FormControl<IPayment['lastName']>;
  addressLine1: FormControl<IPayment['addressLine1']>;
  addressLine2: FormControl<IPayment['addressLine2']>;
  postalCode: FormControl<IPayment['postalCode']>;
  city: FormControl<IPayment['city']>;
  countryCode: FormControl<IPayment['countryCode']>;
  remoteIp: FormControl<IPayment['remoteIp']>;
  timestamp: FormControl<IPayment['timestamp']>;
  status: FormControl<IPayment['status']>;
  message: FormControl<IPayment['message']>;
  gatewayId: FormControl<IPayment['gatewayId']>;
  gatewayCode: FormControl<IPayment['gatewayCode']>;
  mode: FormControl<IPayment['mode']>;
};

export type PaymentFormGroup = FormGroup<PaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentFormService {
  createPaymentFormGroup(payment: PaymentFormGroupInput = { id: null }): PaymentFormGroup {
    const paymentRawValue = {
      ...this.getFormDefaults(),
      ...payment,
    };
    return new FormGroup<PaymentFormGroupContent>({
      id: new FormControl(
        { value: paymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      mandateId: new FormControl(paymentRawValue.mandateId, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      paymentId: new FormControl(paymentRawValue.paymentId, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      gateway: new FormControl(paymentRawValue.gateway, {
        validators: [Validators.required],
      }),
      iban: new FormControl(paymentRawValue.iban, {
        validators: [Validators.required, Validators.minLength(22), Validators.maxLength(34)],
      }),
      bic: new FormControl(paymentRawValue.bic, {
        validators: [Validators.required, Validators.minLength(8), Validators.maxLength(11)],
      }),
      amount: new FormControl(paymentRawValue.amount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      currencyCode: new FormControl(paymentRawValue.currencyCode, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(3)],
      }),
      softDescriptor: new FormControl(paymentRawValue.softDescriptor, {
        validators: [Validators.required, Validators.maxLength(140)],
      }),
      firstName: new FormControl(paymentRawValue.firstName, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      lastName: new FormControl(paymentRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      addressLine1: new FormControl(paymentRawValue.addressLine1, {
        validators: [Validators.required, Validators.maxLength(70)],
      }),
      addressLine2: new FormControl(paymentRawValue.addressLine2, {
        validators: [Validators.maxLength(70)],
      }),
      postalCode: new FormControl(paymentRawValue.postalCode, {
        validators: [Validators.required, Validators.maxLength(16)],
      }),
      city: new FormControl(paymentRawValue.city, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      countryCode: new FormControl(paymentRawValue.countryCode, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(2)],
      }),
      remoteIp: new FormControl(paymentRawValue.remoteIp, {
        validators: [Validators.required, Validators.maxLength(39)],
      }),
      timestamp: new FormControl(paymentRawValue.timestamp, {
        validators: [Validators.required],
      }),
      status: new FormControl(paymentRawValue.status, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      message: new FormControl(paymentRawValue.message, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      gatewayId: new FormControl(paymentRawValue.gatewayId, {
        validators: [Validators.maxLength(35)],
      }),
      gatewayCode: new FormControl(paymentRawValue.gatewayCode, {
        validators: [Validators.maxLength(35)],
      }),
      mode: new FormControl(paymentRawValue.mode, {
        validators: [Validators.maxLength(35)],
      }),
    });
  }

  getPayment(form: PaymentFormGroup): IPayment | NewPayment {
    return form.getRawValue() as IPayment | NewPayment;
  }

  resetForm(form: PaymentFormGroup, payment: PaymentFormGroupInput): void {
    const paymentRawValue = { ...this.getFormDefaults(), ...payment };
    form.reset(
      {
        ...paymentRawValue,
        id: { value: paymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PaymentFormDefaults {
    return {
      id: null,
    };
  }
}
