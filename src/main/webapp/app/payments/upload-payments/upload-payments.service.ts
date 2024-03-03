import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {
  PaymentsUploadPaymentsValidateResponse,
  PaymentsUploadPaymentsExecuteResponse,
  PaymentsUploadPaymentsExampleResponse,
} from './upload-payments.model';

@Injectable({ providedIn: 'root' })
export class PaymentsUploadPaymentsService {
  protected validateUrl = this.applicationConfigService.getEndpointFor('api/upload-payments-validate');
  protected executeUrl = this.applicationConfigService.getEndpointFor('api/upload-payments-execute');
  protected exampleUrl = this.applicationConfigService.getEndpointFor('api/upload-payments-example');

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  validate(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post<PaymentsUploadPaymentsValidateResponse>(this.validateUrl, formData, {
      observe: 'events',
      reportProgress: true,
    });
  }

  execute(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post<PaymentsUploadPaymentsExecuteResponse>(this.executeUrl, formData, {
      observe: 'events',
      reportProgress: true,
    });
  }

  example(): Observable<PaymentsUploadPaymentsExampleResponse> {
    return this.http.get<PaymentsUploadPaymentsExampleResponse>(this.exampleUrl, {
      observe: 'body',
    });
  }
}
