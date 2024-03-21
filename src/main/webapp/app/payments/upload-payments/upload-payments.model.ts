export class PaymentsUploadPaymentsValidateResponse {
  constructor(
    public success: boolean,
    public count: number,
    public amount: number,
  ) {}
}

export class PaymentsUploadPaymentsExecuteResponse {
  constructor(public stillRunning: boolean) {}
}

export class PaymentsUploadPaymentsProgressResponse {
  constructor(
    public success: number,
    public count: number,
    public stillRunning: boolean,
  ) {}
}
