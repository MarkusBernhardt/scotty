export class PaymentsUploadPaymentsValidateResponse {
  constructor(
    public success: boolean,
    public count: number,
    public amount: number,
  ) {}
}

export class PaymentsUploadPaymentsExecuteResponse {
  constructor(
    public name: string,
    public file: string,
  ) {}
}

export class PaymentsUploadPaymentsExampleResponse {
  constructor(
    public name: string,
    public file: string,
  ) {}
}
