import dayjs from 'dayjs/esm';

import { IReconciliation, NewReconciliation } from './reconciliation.model';

export const sampleWithRequiredData: IReconciliation = {
  id: 804,
  mandateId: 'worth wholly',
  paymentId: 'forbid indeed',
  gateway: 'EMERCHANTPAY',
  iban: 'NL95NOOX0700243058',
  bic: 'BEOBNUM0',
  amount: 14903,
  currencyCode: 'TOP',
  softDescriptor: 'despite valuable for',
  firstName: 'Eugenia',
  lastName: 'Koepp-Beier',
  addressLine1: 'roasted',
  postalCode: 'briefly or deepl',
  city: 'Eugene',
  countryCode: 'BS',
  remoteIp: 'football psst',
  timestamp: dayjs('2024-02-29T18:42'),
  state: 'absent corps',
  message: 'or everlasting',
};

export const sampleWithPartialData: IReconciliation = {
  id: 18074,
  mandateId: 'below briskly through',
  paymentId: 'confuse deodorant',
  gateway: 'CCBILL',
  iban: 'LU158724C91B35449772',
  bic: 'AUFBLC92',
  amount: 18274,
  currencyCode: 'MMK',
  softDescriptor: 'especially ew',
  firstName: 'Hailie',
  lastName: 'Kub',
  addressLine1: 'standard prang',
  addressLine2: 'after on',
  postalCode: 'ew',
  city: 'Funkfurt',
  countryCode: 'QA',
  remoteIp: 'oh roast provided',
  timestamp: dayjs('2024-03-01T12:16'),
  state: 'fooey',
  message: 'climate pleasing hm',
  gatewayId: 'impolite whoa',
  fileName: 'woefully',
};

export const sampleWithFullData: IReconciliation = {
  id: 26046,
  mandateId: 'phew',
  paymentId: 'typeset',
  gateway: 'UNKNOWN',
  iban: 'AT332462567000965092',
  bic: 'KPRKERUJ',
  amount: 22748,
  currencyCode: 'GEL',
  softDescriptor: 'ah',
  firstName: 'Madelynn',
  lastName: 'Block',
  addressLine1: 'questionable dip',
  addressLine2: 'that bold briefing',
  postalCode: 'oof by',
  city: 'Santa Rosa',
  countryCode: 'MS',
  remoteIp: 'under consequently',
  timestamp: dayjs('2024-02-29T20:05'),
  state: 'when',
  message: 'prod growling supposing',
  gatewayId: 'drat coupon diabetes',
  mode: 'excluding throughout slather',
  fileName: 'furthermore opine but',
};

export const sampleWithNewData: NewReconciliation = {
  mandateId: 'positive wince',
  paymentId: 'compliance rightfully gargle',
  gateway: 'EMERCHANTPAY',
  iban: 'AD2909058599XDT4812Q90W6',
  bic: 'PTJUSV3FXXX',
  amount: 18635,
  currencyCode: 'HUF',
  softDescriptor: 'instead spot',
  firstName: 'Jerel',
  lastName: 'Davis',
  addressLine1: 'which quarantine and',
  postalCode: 'yowza though',
  city: 'Ulisesberg',
  countryCode: 'DE',
  remoteIp: 'huzzah lift',
  timestamp: dayjs('2024-03-01T05:07'),
  state: 'yawning',
  message: 'who hoover',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
