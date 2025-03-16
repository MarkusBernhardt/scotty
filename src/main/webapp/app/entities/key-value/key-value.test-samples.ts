import { IKeyValue, NewKeyValue } from './key-value.model';

export const sampleWithRequiredData: IKeyValue = {
  id: 16853,
  kvKey: 'uh-huh',
};

export const sampleWithPartialData: IKeyValue = {
  id: 3534,
  kvKey: 'whoever',
};

export const sampleWithFullData: IKeyValue = {
  id: 16058,
  kvKey: 'from insecure',
  kvValue: 'likewise back',
};

export const sampleWithNewData: NewKeyValue = {
  kvKey: 'roundabout ack',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
