import dayjs from 'dayjs/esm';

export interface IPaymentsDownloadPaymentsRecord {
  fileName: string;
  minTimestamp: dayjs.Dayjs;
  maxTimestamp: dayjs.Dayjs;
  count: number;
  amount: number;
}
