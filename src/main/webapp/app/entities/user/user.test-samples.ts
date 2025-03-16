import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 5345,
  login: 'SI7`@J\\\\F\\-T',
};

export const sampleWithPartialData: IUser = {
  id: 5293,
  login: '{|!CE@MB\\}p1b0KF',
};

export const sampleWithFullData: IUser = {
  id: 12358,
  login: '4_@t\\$PjF\\kUih',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
