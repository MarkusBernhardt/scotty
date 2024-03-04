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

export class PaymentsUploadPaymentsExampleResponse {
  constructor(
    public name: string,
    public data: Uint8Array,
  ) {}
}
