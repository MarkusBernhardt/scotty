import { IKeyValue, NewKeyValue } from './key-value.model';

export const sampleWithRequiredData: IKeyValue = {
  id: 4798,
  key: 'juvenile',
};

export const sampleWithPartialData: IKeyValue = {
  id: 10949,
  key: 'vivaciously',
  value: 'sea agitate',
};

export const sampleWithFullData: IKeyValue = {
  id: 25461,
  key: 'midst concerning heartwood',
  value: 'wherever',
};

export const sampleWithNewData: NewKeyValue = {
  key: 'podcast',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
