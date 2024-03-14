import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPaymentsDownloadReconciliationsRecord } from './download-reconciliations.model';

type RestOf<T extends IPaymentsDownloadReconciliationsRecord> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

export type RestPayment = RestOf<IPaymentsDownloadReconciliationsRecord>;

export type EntityArrayResponseType = HttpResponse<IPaymentsDownloadReconciliationsRecord[]>;

@Injectable({ providedIn: 'root' })
export class PaymentsDownloadReconciliationsService {
  protected listUrl = this.applicationConfigService.getEndpointFor('api/payments-download-reconciliations/list');
  protected saveUrl = this.applicationConfigService.getEndpointFor('api/payments-download-reconciliations/save');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPayment[]>(this.listUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getDownloadReconciliationRecordIdentifier(payment: Pick<IPaymentsDownloadReconciliationsRecord, 'fileName'>): string {
    return payment.fileName;
  }

  protected convertDateFromServer(restPayment: RestPayment): IPaymentsDownloadReconciliationsRecord {
    return {
      ...restPayment,
    };
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPayment[]>): HttpResponse<IPaymentsDownloadReconciliationsRecord[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
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
}
