import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPayment | NewPayment> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

type PaymentFormRawValue = FormValueOf<IPayment>;

type NewPaymentFormRawValue = FormValueOf<NewPayment>;

type PaymentFormDefaults = Pick<NewPayment, 'id' | 'timestamp'>;

type PaymentFormGroupContent = {
  id: FormControl<PaymentFormRawValue['id'] | NewPayment['id']>;
  mandateId: FormControl<PaymentFormRawValue['mandateId']>;
  paymentId: FormControl<PaymentFormRawValue['paymentId']>;
  gateway: FormControl<PaymentFormRawValue['gateway']>;
  iban: FormControl<PaymentFormRawValue['iban']>;
  bic: FormControl<PaymentFormRawValue['bic']>;
  amount: FormControl<PaymentFormRawValue['amount']>;
  currencyCode: FormControl<PaymentFormRawValue['currencyCode']>;
  softDescriptor: FormControl<PaymentFormRawValue['softDescriptor']>;
  firstName: FormControl<PaymentFormRawValue['firstName']>;
  lastName: FormControl<PaymentFormRawValue['lastName']>;
  addressLine1: FormControl<PaymentFormRawValue['addressLine1']>;
  addressLine2: FormControl<PaymentFormRawValue['addressLine2']>;
  postalCode: FormControl<PaymentFormRawValue['postalCode']>;
  city: FormControl<PaymentFormRawValue['city']>;
  countryCode: FormControl<PaymentFormRawValue['countryCode']>;
  remoteIp: FormControl<PaymentFormRawValue['remoteIp']>;
  timestamp: FormControl<PaymentFormRawValue['timestamp']>;
  state: FormControl<PaymentFormRawValue['state']>;
  message: FormControl<PaymentFormRawValue['message']>;
  gatewayId: FormControl<PaymentFormRawValue['gatewayId']>;
  mode: FormControl<PaymentFormRawValue['mode']>;
  fileName: FormControl<PaymentFormRawValue['fileName']>;
};

export type PaymentFormGroup = FormGroup<PaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentFormService {
  createPaymentFormGroup(payment: PaymentFormGroupInput = { id: null }): PaymentFormGroup {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({
      ...this.getFormDefaults(),
      ...payment,
    });
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
        validators: [Validators.required, Validators.minLength(16), Validators.maxLength(34)],
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
      state: new FormControl(paymentRawValue.state, {
        validators: [Validators.required, Validators.maxLength(35)],
      }),
      message: new FormControl(paymentRawValue.message, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      gatewayId: new FormControl(paymentRawValue.gatewayId, {
        validators: [Validators.maxLength(35)],
      }),
      mode: new FormControl(paymentRawValue.mode, {
        validators: [Validators.maxLength(35)],
      }),
      fileName: new FormControl(paymentRawValue.fileName, {
        validators: [Validators.maxLength(255)],
      }),
    });
  }

  getPayment(form: PaymentFormGroup): IPayment | NewPayment {
    return this.convertPaymentRawValueToPayment(form.getRawValue() as PaymentFormRawValue | NewPaymentFormRawValue);
  }

  resetForm(form: PaymentFormGroup, payment: PaymentFormGroupInput): void {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({ ...this.getFormDefaults(), ...payment });
    form.reset(
      {
        ...paymentRawValue,
        id: { value: paymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      timestamp: currentTime,
    };
  }

  private convertPaymentRawValueToPayment(rawPayment: PaymentFormRawValue | NewPaymentFormRawValue): IPayment | NewPayment {
    return {
      ...rawPayment,
      timestamp: dayjs(rawPayment.timestamp, DATE_TIME_FORMAT),
    };
  }

  private convertPaymentToPaymentRawValue(
    payment: IPayment | (Partial<NewPayment> & PaymentFormDefaults),
  ): PaymentFormRawValue | PartialWithRequiredKeyOf<NewPaymentFormRawValue> {
    return {
      ...payment,
      timestamp: payment.timestamp ? payment.timestamp.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
