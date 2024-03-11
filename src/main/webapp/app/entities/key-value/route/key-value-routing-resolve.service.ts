import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IKeyValue } from '../key-value.model';
import { KeyValueService } from '../service/key-value.service';

export const keyValueResolve = (route: ActivatedRouteSnapshot): Observable<null | IKeyValue> => {
  const id = route.params['id'];
  if (id) {
    return inject(KeyValueService)
      .find(id)
      .pipe(
        mergeMap((keyValue: HttpResponse<IKeyValue>) => {
          if (keyValue.body) {
            return of(keyValue.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default keyValueResolve;
