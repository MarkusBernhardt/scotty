import { IKeyValue, NewKeyValue } from './key-value.model';

export const sampleWithRequiredData: IKeyValue = {
  id: 10995,
  kvKey: 'verbally wealthy',
};

export const sampleWithPartialData: IKeyValue = {
  id: 5671,
  kvKey: 'ignorant meanwhile on',
  kvValue: 'slather disappointment oof',
};

export const sampleWithFullData: IKeyValue = {
  id: 12528,
  kvKey: 'blissfully likewise',
  kvValue: 'woot suddenly',
};

export const sampleWithNewData: NewKeyValue = {
  kvKey: 'scarper',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
