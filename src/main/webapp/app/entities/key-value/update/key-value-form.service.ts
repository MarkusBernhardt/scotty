import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IKeyValue, NewKeyValue } from '../key-value.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IKeyValue for edit and NewKeyValueFormGroupInput for create.
 */
type KeyValueFormGroupInput = IKeyValue | PartialWithRequiredKeyOf<NewKeyValue>;

type KeyValueFormDefaults = Pick<NewKeyValue, 'id'>;

type KeyValueFormGroupContent = {
  id: FormControl<IKeyValue['id'] | NewKeyValue['id']>;
  key: FormControl<IKeyValue['key']>;
  value: FormControl<IKeyValue['value']>;
};

export type KeyValueFormGroup = FormGroup<KeyValueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class KeyValueFormService {
  createKeyValueFormGroup(keyValue: KeyValueFormGroupInput = { id: null }): KeyValueFormGroup {
    const keyValueRawValue = {
      ...this.getFormDefaults(),
      ...keyValue,
    };
    return new FormGroup<KeyValueFormGroupContent>({
      id: new FormControl(
        { value: keyValueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      key: new FormControl(keyValueRawValue.key, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      value: new FormControl(keyValueRawValue.value, {
        validators: [Validators.maxLength(255)],
      }),
    });
  }

  getKeyValue(form: KeyValueFormGroup): IKeyValue | NewKeyValue {
    return form.getRawValue() as IKeyValue | NewKeyValue;
  }

  resetForm(form: KeyValueFormGroup, keyValue: KeyValueFormGroupInput): void {
    const keyValueRawValue = { ...this.getFormDefaults(), ...keyValue };
    form.reset(
      {
        ...keyValueRawValue,
        id: { value: keyValueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): KeyValueFormDefaults {
    return {
      id: null,
    };
  }
}
