import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { PaymentsUploadPaymentsValidateResponse, PaymentsUploadPaymentsExecuteResponse } from './upload-payments.model';

@Injectable({ providedIn: 'root' })
export class PaymentsUploadPaymentsService {
  protected validateUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/validate');
  protected executeUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/execute');
  protected exampleUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/example');

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

  execute(file: File, executeFileName: string): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('executeFileName', executeFileName);
    return this.http.post<PaymentsUploadPaymentsExecuteResponse>(this.executeUrl, formData, {
      observe: 'events',
      reportProgress: true,
    });
  }

  //progress(executeFileName: string):

  example(): Observable<HttpEvent<any>> {
    return this.http.get(this.exampleUrl, {
      observe: 'events',
      reportProgress: true,
      responseType: 'blob',
    });
  }
}
