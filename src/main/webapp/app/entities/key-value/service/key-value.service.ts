import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IKeyValue, NewKeyValue } from '../key-value.model';

export type PartialUpdateKeyValue = Partial<IKeyValue> & Pick<IKeyValue, 'id'>;

export type EntityResponseType = HttpResponse<IKeyValue>;
export type EntityArrayResponseType = HttpResponse<IKeyValue[]>;

@Injectable({ providedIn: 'root' })
export class KeyValueService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/key-values');

  create(keyValue: NewKeyValue): Observable<EntityResponseType> {
    return this.http.post<IKeyValue>(this.resourceUrl, keyValue, { observe: 'response' });
  }

  update(keyValue: IKeyValue): Observable<EntityResponseType> {
    return this.http.put<IKeyValue>(`${this.resourceUrl}/${this.getKeyValueIdentifier(keyValue)}`, keyValue, { observe: 'response' });
  }

  partialUpdate(keyValue: PartialUpdateKeyValue): Observable<EntityResponseType> {
    return this.http.patch<IKeyValue>(`${this.resourceUrl}/${this.getKeyValueIdentifier(keyValue)}`, keyValue, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IKeyValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IKeyValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getKeyValueIdentifier(keyValue: Pick<IKeyValue, 'id'>): number {
    return keyValue.id;
  }

  compareKeyValue(o1: Pick<IKeyValue, 'id'> | null, o2: Pick<IKeyValue, 'id'> | null): boolean {
    return o1 && o2 ? this.getKeyValueIdentifier(o1) === this.getKeyValueIdentifier(o2) : o1 === o2;
  }

  addKeyValueToCollectionIfMissing<Type extends Pick<IKeyValue, 'id'>>(
    keyValueCollection: Type[],
    ...keyValuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const keyValues: Type[] = keyValuesToCheck.filter(isPresent);
    if (keyValues.length > 0) {
      const keyValueCollectionIdentifiers = keyValueCollection.map(keyValueItem => this.getKeyValueIdentifier(keyValueItem));
      const keyValuesToAdd = keyValues.filter(keyValueItem => {
        const keyValueIdentifier = this.getKeyValueIdentifier(keyValueItem);
        if (keyValueCollectionIdentifiers.includes(keyValueIdentifier)) {
          return false;
        }
        keyValueCollectionIdentifiers.push(keyValueIdentifier);
        return true;
      });
      return [...keyValuesToAdd, ...keyValueCollection];
    }
    return keyValueCollection;
  }
}
