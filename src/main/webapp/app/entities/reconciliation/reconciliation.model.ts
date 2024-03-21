import dayjs from 'dayjs/esm';
import { IPayment } from 'app/entities/payment/payment.model';
import { Gateway } from 'app/entities/enumerations/gateway.model';

export interface IReconciliation {
  id: number;
  mandateId?: string | null;
  paymentId?: string | null;
  gateway?: keyof typeof Gateway | null;
  iban?: string | null;
  bic?: string | null;
  amount?: number | null;
  currencyCode?: string | null;
  softDescriptor?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  postalCode?: string | null;
  city?: string | null;
  countryCode?: string | null;
  remoteIp?: string | null;
  timestamp?: dayjs.Dayjs | null;
  state?: string | null;
  reasonCode?: string | null;
  message?: string | null;
  gatewayId?: string | null;
  mode?: string | null;
  fileName?: string | null;
  scottyPayment?: Pick<IPayment, 'id'> | null;
}

export type NewReconciliation = Omit<IReconciliation, 'id'> & { id: null };
