import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 14887,
  mandateId: 'localize zany',
  paymentId: 'remind majestically instead',
  gateway: 'EMERCHANTPAY',
  iban: 'GB88IUIT09037606510540',
  bic: 'ZGNIHMDG',
  amount: 6186,
  currencyCode: 'AFN',
  softDescriptor: 'now',
  firstName: 'Mireille',
  lastName: 'Hilpert',
  addressLine1: 'expunge prevention',
  postalCode: 'naive',
  city: 'Olympia',
  countryCode: 'PT',
  remoteIp: 'tremendously cue',
  timestamp: dayjs('2024-02-29T22:08'),
  state: 'reveal',
  message: 'baggage ugh',
};

export const sampleWithPartialData: IPayment = {
  id: 31862,
  mandateId: 'psst or',
  paymentId: 'nauseate',
  gateway: 'CCBILL',
  iban: 'AL66030198860190GV60V36F2O75',
  bic: 'PIMHMQVY',
  amount: 8311,
  currencyCode: 'GEL',
  softDescriptor: 'close questionable whether',
  firstName: 'Rhianna',
  lastName: 'Wilkinson',
  addressLine1: 'lest',
  postalCode: 'indeed woot cumm',
  city: 'Caldwell',
  countryCode: 'MW',
  remoteIp: 'brace foam',
  timestamp: dayjs('2024-03-01T07:25'),
  state: 'cheerfully',
  message: 'denim really',
  gatewayId: 'earth',
  mode: 'canal',
};

export const sampleWithFullData: IPayment = {
  id: 5859,
  mandateId: 'if fooey than',
  paymentId: 'hence',
  gateway: 'EMERCHANTPAY',
  iban: 'JO82DSLR6004065003310039003999',
  bic: 'IETANA92XXX',
  amount: 6213,
  currencyCode: 'BZD',
  softDescriptor: 'border',
  firstName: 'Dario',
  lastName: 'Wuckert',
  addressLine1: 'since',
  addressLine2: 'bruised leading sensitivity',
  postalCode: 'judgementally st',
  city: 'North Reinaville',
  countryCode: 'BW',
  remoteIp: 'out though leadership',
  timestamp: dayjs('2024-02-29T17:43'),
  state: 'perfectly',
  message: 'bah rancher',
  gatewayId: 'gratefully blouse',
  mode: 'blah',
  fileName: 'where',
};

export const sampleWithNewData: NewPayment = {
  mandateId: 'why',
  paymentId: 'qua meh',
  gateway: 'CCBILL',
  iban: 'TR641798167500172725416114',
  bic: 'SAHGCNQXXXX',
  amount: 6940,
  currencyCode: 'BRL',
  softDescriptor: 'once lotion',
  firstName: 'Leila',
  lastName: 'Farrell',
  addressLine1: 'fair',
  postalCode: 'anodise boo know',
  city: 'Annamarieborough',
  countryCode: 'FM',
  remoteIp: 'parallelogram',
  timestamp: dayjs('2024-03-01T09:13'),
  state: 'dearly guideline tan',
  message: 'since',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
