import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'a6c2440c-8367-4f5b-8ec9-d3bbe850067e',
};

export const sampleWithPartialData: IAuthority = {
  name: '13d0096e-013e-484f-b517-f786d0835e10',
};

export const sampleWithFullData: IAuthority = {
  name: '0cce66bb-e5f5-4261-acda-aa39ac2116ae',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
