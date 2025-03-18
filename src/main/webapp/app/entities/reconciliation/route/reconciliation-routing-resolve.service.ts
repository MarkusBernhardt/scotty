import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReconciliation } from '../reconciliation.model';
import { ReconciliationService } from '../service/reconciliation.service';

const reconciliationResolve = (route: ActivatedRouteSnapshot): Observable<null | IReconciliation> => {
  const id = route.params.id;
  if (id) {
    return inject(ReconciliationService)
      .find(id)
      .pipe(
        mergeMap((reconciliation: HttpResponse<IReconciliation>) => {
          if (reconciliation.body) {
            return of(reconciliation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default reconciliationResolve;
