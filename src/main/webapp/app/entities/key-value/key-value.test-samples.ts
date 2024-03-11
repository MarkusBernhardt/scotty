import { IKeyValue, NewKeyValue } from './key-value.model';

export const sampleWithRequiredData: IKeyValue = {
  id: 4798,
  kvKey: 'juvenile',
};

export const sampleWithPartialData: IKeyValue = {
  id: 10949,
  kvKey: 'vivaciously',
  kvValue: 'sea agitate',
};

export const sampleWithFullData: IKeyValue = {
  id: 25461,
  kvKey: 'midst concerning heartwood',
  kvValue: 'wherever',
};

export const sampleWithNewData: NewKeyValue = {
  kvKey: 'podcast',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
