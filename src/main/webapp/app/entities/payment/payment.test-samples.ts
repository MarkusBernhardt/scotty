import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 10213,
  mandateId: 'lard faithfully',
  paymentId: 'decimal how',
  gateway: 'EMERCHANTPAY',
  iban: 'MD2339775EF60651846G3310',
  bic: 'SJWISLHPDI6',
  amount: 23764,
  currencyCode: 'TRY',
  softDescriptor: 'raffle stucco serpentine',
  firstName: 'Kamren',
  lastName: 'Tillman',
  addressLine1: 'once likewise aha',
  postalCode: 'gee',
  city: 'East Loren',
  countryCode: 'BW',
  remoteIp: 'moisturise psst or',
  timestamp: dayjs('2024-03-01T07:47'),
  state: 'loaf lest',
  message: 'spry',
};

export const sampleWithPartialData: IPayment = {
  id: 31752,
  mandateId: 'penalize before',
  paymentId: 'far-flung gah',
  gateway: 'EMERCHANTPAY',
  iban: 'MK32922Q6ZD47708B68',
  bic: 'MYDEBF2U',
  amount: 7786,
  currencyCode: 'NAD',
  softDescriptor: 'ha ford',
  firstName: 'Helmer',
  lastName: 'Hand',
  addressLine1: 'quicker',
  addressLine2: 'brace foam',
  postalCode: 'absentmindedly',
  city: 'New Jessieside',
  countryCode: 'CO',
  remoteIp: 'what furthermore',
  timestamp: dayjs('2024-02-29T20:19'),
  state: 'hence',
  message: 'bondsman impanel demist',
  gatewayId: 'regulation calculation above',
  mode: 'even against',
  fileName: 'astride',
};

export const sampleWithFullData: IPayment = {
  id: 23034,
  mandateId: 'although against border',
  paymentId: 'excitedly',
  gateway: 'EMERCHANTPAY',
  iban: 'TR048900450095714007637004',
  bic: 'POXAPTV6T54',
  amount: 25566,
  currencyCode: 'IDR',
  softDescriptor: 'until',
  firstName: 'Magali',
  lastName: 'Bartell',
  addressLine1: 'duh times oof',
  addressLine2: 'till',
  postalCode: 'eek before hmph',
  city: 'Bernhardville',
  countryCode: 'BG',
  remoteIp: 'where',
  timestamp: dayjs('2024-03-01T06:38'),
  state: 'of qua',
  message: 'replicate until',
  gatewayId: 'communicate till light',
  mode: 'intermix mmm furthermore',
  fileName: 'whenever',
};

export const sampleWithNewData: NewPayment = {
  mandateId: 'continually',
  paymentId: 'switch anodise boo',
  gateway: 'UNKNOWN',
  iban: 'BG75KRYI40032830684600',
  bic: 'CHWZIDWXXXX',
  amount: 21914,
  currencyCode: 'MZN',
  softDescriptor: 'although likely a',
  firstName: 'Katherine',
  lastName: 'Larkin',
  addressLine1: 'and',
  postalCode: 'tracksuit',
  city: 'North Christatown',
  countryCode: 'IM',
  remoteIp: 'swiftly',
  timestamp: dayjs('2024-02-29T20:37'),
  state: 'whopping so snow',
  message: 'distill observation',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
