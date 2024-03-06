import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 26658,
  mandateId: 'anxiously quirkily instead',
  paymentId: 'as',
  gateway: 'EMERCHANTPAY',
  iban: 'AT910427150506006007XX',
  bic: 'OTDCTTW8XXX',
  amount: 25620,
  currencyCode: 'MUR',
  softDescriptor: 'big why',
  firstName: 'Valentine',
  lastName: 'Schneider-Auer',
  addressLine1: 'naive',
  postalCode: 'how',
  city: 'Lake Felipa',
  countryCode: 'HK',
  remoteIp: 'meteor ouch',
  timestamp: dayjs('2024-03-01'),
  status: 'tightly obnoxiously',
  message: 'mechanically mmm',
};

export const sampleWithPartialData: IPayment = {
  id: 23764,
  mandateId: 'healthy wrongly powerless',
  paymentId: 'beautifully personalize but',
  gateway: 'EMERCHANTPAY',
  iban: 'TL147400068030250093927',
  bic: 'NXOXGDBU',
  amount: 16830,
  currencyCode: 'DJF',
  softDescriptor: 'loaf lest',
  firstName: 'Adeline',
  lastName: 'Witting',
  addressLine1: 'curiously unnaturally wherever',
  addressLine2: 'drum past',
  postalCode: 'exotic with',
  city: 'Burbank',
  countryCode: 'PK',
  remoteIp: 'strictly jug',
  timestamp: dayjs('2024-02-29'),
  status: 'lest',
  message: 'indeed woot cummerbund',
  gatewayId: 'lord times than',
  mode: 'who drat',
};

export const sampleWithFullData: IPayment = {
  id: 6254,
  mandateId: 'furthermore eek retouching',
  paymentId: 'beet talk',
  gateway: 'EMERCHANTPAY',
  iban: 'LU130291982114813642XX',
  bic: 'FMIIBSZ3LBT',
  amount: 29812,
  currencyCode: 'ALL',
  softDescriptor: 'fan',
  firstName: 'Chelsey',
  lastName: 'Barrows',
  addressLine1: 'blink geez',
  addressLine2: 'fooey joyously media',
  postalCode: 'apud secondary z',
  city: 'Lake Sageberg',
  countryCode: 'BA',
  remoteIp: 'once doughnut bode',
  timestamp: dayjs('2024-02-29'),
  status: 'times oof aha',
  message: 'till police whereas',
  gatewayId: 'thankfully loftily of',
  gatewayCode: 'predetermine replicate until',
  mode: 'communicate till light',
};

export const sampleWithNewData: NewPayment = {
  mandateId: 'intermix mmm furthermore',
  paymentId: 'whenever',
  gateway: 'EMERCHANTPAY',
  iban: 'FI7600512135503621XXXX',
  bic: 'ACEZAGSS',
  amount: 3224,
  currencyCode: 'SSP',
  softDescriptor: 'berate',
  firstName: 'Elouise',
  lastName: 'Fay',
  addressLine1: 'anti arrogantly yet',
  postalCode: 'tan owlishly',
  city: 'Joyland',
  countryCode: 'SS',
  remoteIp: 'rod',
  timestamp: dayjs('2024-03-01'),
  status: 'wholly mist',
  message: 'almost',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
