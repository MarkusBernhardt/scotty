import { Component, OnInit } from '@angular/core';
import { HttpEventType, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ASC, DEFAULT_SORT_DATA, DESC, SORT } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { IPaymentsDownloadPaymentsRecord } from './download-payments.model';

import { PaymentsDownloadPaymentsService, EntityArrayResponseType } from './download-payments.service';
import FileSaver from 'file-saver';

@Component({
  standalone: true,
  selector: 'jhi-payments-download-downloadPaymentRecords',
  templateUrl: './download-payments.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export default class PaymentsDownloadPaymentsComponent implements OnInit {
  downloadPaymentRecords?: IPaymentsDownloadPaymentsRecord[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  constructor(
    protected downloadPaymentRecordService: PaymentsDownloadPaymentsService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
  ) {}

  trackId = (_index: string, item: IPaymentsDownloadPaymentsRecord): string =>
    this.downloadPaymentRecordService.getDownloadPaymentRecordIdentifier(item);

  ngOnInit(): void {
    this.load();

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.predicate, this.ascending, filterOptions));
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending, this.filters.filterOptions)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    this.downloadPaymentRecords = this.fillComponentAttributesFromResponseBody(response.body);
  }

  protected fillComponentAttributesFromResponseBody(data: IPaymentsDownloadPaymentsRecord[] | null): IPaymentsDownloadPaymentsRecord[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(
    page?: number,
    predicate?: string,
    ascending?: boolean,
    filterOptions?: IFilterOption[],
  ): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    filterOptions?.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.downloadPaymentRecordService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  formatCurrency(amount: number, decimals: number = 2) {
    const formatter = new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    });
    return formatter.format(amount);
  }

  formatCount(count: number, decimals: number = 0) {
    const formatter = new Intl.NumberFormat('de-DE', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    });
    return formatter.format(count);
  }

  save(fileName: string): void {
    this.downloadPaymentRecordService.save(fileName).subscribe(event => {
      if (event.type === HttpEventType.Response) {
        const contentDisposition = event.headers.get('content-disposition') ?? 'attachment; ' + fileName;
        const contentDispositionArray = contentDisposition.split(';');
        const contentDispositionFilename = contentDispositionArray.find(n => n.includes('filename='));
        let filename = fileName;
        if (contentDispositionFilename) {
          filename = contentDispositionFilename.replace('filename=', '').trim();
        }
        const blob = new Blob([event.body], { type: 'application/octet-stream' });
        FileSaver.saveAs(blob, filename);
      }
    });
  }
}
