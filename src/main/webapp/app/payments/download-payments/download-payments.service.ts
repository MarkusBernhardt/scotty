import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPaymentsDownloadPaymentsRecord } from './download-payments.model';

type RestOf<T extends IPaymentsDownloadPaymentsRecord> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

export type RestPayment = RestOf<IPaymentsDownloadPaymentsRecord>;

export type EntityArrayResponseType = HttpResponse<IPaymentsDownloadPaymentsRecord[]>;

@Injectable({ providedIn: 'root' })
export class PaymentsDownloadPaymentsService {
  protected listUrl = this.applicationConfigService.getEndpointFor('api/payments-download-payments/list');
  protected saveUrl = this.applicationConfigService.getEndpointFor('api/payments-download-payments/save');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  public query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPayment[]>(this.listUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  public getDownloadPaymentRecordIdentifier(payment: Pick<IPaymentsDownloadPaymentsRecord, 'fileName'>): string {
    return payment.fileName;
  }

  public save(fileName: string): Observable<HttpEvent<any>> {
    return this.http.get(this.saveUrl, {
      observe: 'events',
      reportProgress: true,
      responseType: 'blob',
      params: new HttpParams().set('fileName', fileName),
    });
  }

  protected convertDateFromServer(restPayment: RestPayment): IPaymentsDownloadPaymentsRecord {
    return {
      ...restPayment,
    };
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPayment[]>): HttpResponse<IPaymentsDownloadPaymentsRecord[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
