export class PaymentsUploadPaymentsValidateResponse {
  constructor(
    public success: boolean,
    public count: number,
    public amount: number,
  ) {}
}

export class PaymentsUploadPaymentsExecuteResponse {
  constructor(
    public success: number,
    public filename: string,
  ) {}
}

export class PaymentsUploadPaymentsProgressResponse {
  constructor(
    public success: number,
    public count: number,
  ) {}
}
