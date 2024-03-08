import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {
  PaymentsUploadPaymentsValidateResponse,
  PaymentsUploadPaymentsExecuteResponse,
  PaymentsUploadPaymentsProgressResponse,
} from './upload-payments.model';

@Injectable({ providedIn: 'root' })
export class PaymentsUploadPaymentsService {
  protected exampleUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/example');
  protected executeUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/execute');
  protected progressUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/progress');
  protected saveUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/save');
  protected validateUrl = this.applicationConfigService.getEndpointFor('api/payments-upload-payments/validate');

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
    formData.append('fileName', executeFileName);
    return this.http.post<PaymentsUploadPaymentsExecuteResponse>(this.executeUrl, formData, {
      observe: 'events',
      reportProgress: true,
    });
  }

  progress(fileName: string): Observable<PaymentsUploadPaymentsProgressResponse> {
    return this.http.get<PaymentsUploadPaymentsProgressResponse>(this.progressUrl, {
      observe: 'body',
      params: new HttpParams().set('fileName', fileName),
    });
  }

  save(fileName: string): Observable<HttpEvent<any>> {
    return this.http.get(this.saveUrl, {
      observe: 'events',
      reportProgress: true,
      responseType: 'blob',
      params: new HttpParams().set('fileName', fileName),
    });
  }

  example(): Observable<HttpEvent<any>> {
    return this.http.get(this.exampleUrl, {
      observe: 'events',
      reportProgress: true,
      responseType: 'blob',
    });
  }
}
