import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReconciliation, NewReconciliation } from '../reconciliation.model';

export type PartialUpdateReconciliation = Partial<IReconciliation> & Pick<IReconciliation, 'id'>;

type RestOf<T extends IReconciliation | NewReconciliation> = Omit<T, 'timestamp' | 'mandateDate' | 'executionDate'> & {
  timestamp?: string | null;
  mandateDate?: string | null;
  executionDate?: string | null;
};

export type RestReconciliation = RestOf<IReconciliation>;

export type NewRestReconciliation = RestOf<NewReconciliation>;

export type PartialUpdateRestReconciliation = RestOf<PartialUpdateReconciliation>;

export type EntityResponseType = HttpResponse<IReconciliation>;
export type EntityArrayResponseType = HttpResponse<IReconciliation[]>;

@Injectable({ providedIn: 'root' })
export class ReconciliationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reconciliations');

  create(reconciliation: NewReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reconciliation);
    return this.http
      .post<RestReconciliation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reconciliation: IReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reconciliation);
    return this.http
      .put<RestReconciliation>(`${this.resourceUrl}/${this.getReconciliationIdentifier(reconciliation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reconciliation: PartialUpdateReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reconciliation);
    return this.http
      .patch<RestReconciliation>(`${this.resourceUrl}/${this.getReconciliationIdentifier(reconciliation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReconciliation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReconciliation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReconciliationIdentifier(reconciliation: Pick<IReconciliation, 'id'>): number {
    return reconciliation.id;
  }

  compareReconciliation(o1: Pick<IReconciliation, 'id'> | null, o2: Pick<IReconciliation, 'id'> | null): boolean {
    return o1 && o2 ? this.getReconciliationIdentifier(o1) === this.getReconciliationIdentifier(o2) : o1 === o2;
  }

  addReconciliationToCollectionIfMissing<Type extends Pick<IReconciliation, 'id'>>(
    reconciliationCollection: Type[],
    ...reconciliationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reconciliations: Type[] = reconciliationsToCheck.filter(isPresent);
    if (reconciliations.length > 0) {
      const reconciliationCollectionIdentifiers = reconciliationCollection.map(reconciliationItem =>
        this.getReconciliationIdentifier(reconciliationItem),
      );
      const reconciliationsToAdd = reconciliations.filter(reconciliationItem => {
        const reconciliationIdentifier = this.getReconciliationIdentifier(reconciliationItem);
        if (reconciliationCollectionIdentifiers.includes(reconciliationIdentifier)) {
          return false;
        }
        reconciliationCollectionIdentifiers.push(reconciliationIdentifier);
        return true;
      });
      return [...reconciliationsToAdd, ...reconciliationCollection];
    }
    return reconciliationCollection;
  }

  protected convertDateFromClient<T extends IReconciliation | NewReconciliation | PartialUpdateReconciliation>(
    reconciliation: T,
  ): RestOf<T> {
    return {
      ...reconciliation,
      timestamp: reconciliation.timestamp?.toJSON() ?? null,
      mandateDate: reconciliation.mandateDate?.toJSON() ?? null,
      executionDate: reconciliation.executionDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReconciliation: RestReconciliation): IReconciliation {
    return {
      ...restReconciliation,
      timestamp: restReconciliation.timestamp ? dayjs(restReconciliation.timestamp) : undefined,
      mandateDate: restReconciliation.mandateDate ? dayjs(restReconciliation.mandateDate) : undefined,
      executionDate: restReconciliation.executionDate ? dayjs(restReconciliation.executionDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReconciliation>): HttpResponse<IReconciliation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReconciliation[]>): HttpResponse<IReconciliation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
