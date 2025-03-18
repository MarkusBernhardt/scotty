import { TranslateService } from '@ngx-translate/core';
import { PaymentsUploadPaymentsService } from './upload-payments.service';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { PaymentsUploadPaymentsValidateResponse, PaymentsUploadPaymentsProgressResponse } from './upload-payments.model';
import FileSaver from 'file-saver';
import SharedModule from '../../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { formatDate } from '@angular/common';

class FileCache {
  constructor(
    public file: File,
    public validateProgress: number,
    public validateSuccess: boolean,
    public validateCount: number,
    public validateAmount: number,
    public validateSubscription: Subscription | null,
    public executeProgress: number,
    public executeSuccess: number,
    public executeSubscription: Subscription | null,
    public executeFileName: string,
  ) {}
}

@Component({
  selector: 'jhi-payments-upload-payments',
  standalone: true,
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
  templateUrl: './upload-payments.component.html',
  styleUrls: ['./upload-payments.component.scss'],
})
export default class PaymentsUploadPaymentsComponent {
  fileCaches: Map<string, FileCache> = new Map<string, FileCache>();

  discardedFiles: Set<string> = new Set<string>();

  constructor(
    private translateService: TranslateService,
    private paymentsUploadPaymentsService: PaymentsUploadPaymentsService,
  ) {}

  async onFileBrowsed(event: Event): Promise<void> {
    const target = event.target as HTMLInputElement;
    if (target.files) {
      await this.onFileDropped(target.files);
    }
  }

  async onFileDropped(fileList: FileList): Promise<void> {
    const allowedFileExtensions = new Set(['xls', 'xlsx']);
    for (let i = 0; i < fileList.length; i++) {
      const file = fileList.item(i);
      await new Promise(f => setTimeout(f, 250));
      if (file != null) {
        if (!allowedFileExtensions.has(file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase())) {
          if (!this.discardedFiles.has(file.name)) {
            this.discardedFiles.add(file.name);
            this.deleteDiscardedFile(file.name);
          }
        } else if (!this.fileCaches.has(file.name)) {
          this.fileCaches.set(file.name, new FileCache(file, 0, true, 0, 0, null, 0, 0, null, ''));
          this.validate(file.name);
        }
      }
    }
  }

  validate(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    fileCache.validateSubscription = this.paymentsUploadPaymentsService.validate(fileCache.file).subscribe({
      next: event => this.validateOnNext(fileCache, event),
      error: () => this.validateOnError(fileCache),
    });
  }

  execute(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    fileCache.executeFileName = 'payments-' + formatDate(Date.now(), 'yyyyMMdd-hhmmss', 'en-US') + '.xlsx';

    fileCache.executeSubscription = this.paymentsUploadPaymentsService.execute(fileCache.file, fileCache.executeFileName).subscribe({
      next: event => this.executeOnNext(fileCache, event),
      error: () => this.executeOnError(fileCache),
    });

    (async () => {
      await new Promise(f => setTimeout(f, 3000));
      let paymentsUploadPaymentsProgressResponse = new PaymentsUploadPaymentsProgressResponse(0, 0, true);
      while (paymentsUploadPaymentsProgressResponse.stillRunning) {
        this.paymentsUploadPaymentsService.progress(fileCache.executeFileName).subscribe(response => {
          fileCache.executeSuccess = response.success;
          fileCache.executeProgress = Math.round(10 + (90 * response.count) / fileCache.validateCount);
          paymentsUploadPaymentsProgressResponse = response;
        });
        await new Promise(f => setTimeout(f, 1000));
      }
      fileCache.executeProgress = 100;
    })();
  }

  protected validateOnNext(fileCache: FileCache, event: HttpEvent<any>): void {
    let response: PaymentsUploadPaymentsValidateResponse;
    switch (event.type) {
      case HttpEventType.UploadProgress:
        if (event.total) {
          fileCache.validateProgress = Math.round((100 * event.loaded) / event.total);
        }
        break;
      case HttpEventType.Response:
        response = event.body as PaymentsUploadPaymentsValidateResponse;
        fileCache.validateProgress = 100;
        fileCache.validateSuccess = response.success;
        fileCache.validateCount = response.count;
        fileCache.validateAmount = response.amount;
        break;
      default:
        break;
    }
  }

  protected validateOnError(fileCache: FileCache): void {
    fileCache.validateSuccess = false;
    fileCache.validateProgress = 100;
  }

  protected executeOnNext(fileCache: FileCache, event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        if (event.total) {
          fileCache.executeProgress = Math.round((50 * event.loaded) / event.total);
        }
        break;
      case HttpEventType.Response:
        fileCache.executeProgress = 10;
        break;
      default:
        break;
    }
  }

  protected executeOnError(fileCache: FileCache): void {
    fileCache.executeSuccess = 1;
    fileCache.executeProgress = 100;
  }

  protected save(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    if (fileCache.executeProgress !== 100) {
      return;
    }

    this.paymentsUploadPaymentsService.save(fileCache.executeFileName).subscribe(event => {
      if (event.type === HttpEventType.Response) {
        const contentDisposition = event.headers.get('content-disposition') ?? 'attachment; ' + fileCache.executeFileName;
        const contentDispositionArray = contentDisposition.split(';');
        const contentDispositionFilename = contentDispositionArray.find(n => n.includes('filename='));
        let filename = fileCache.executeFileName;
        if (contentDispositionFilename) {
          filename = contentDispositionFilename.replace('filename=', '').trim();
        }
        const blob = new Blob([event.body], { type: 'application/octet-stream' });
        // eslint-disable-next-line @typescript-eslint/no-deprecated
        FileSaver.saveAs(blob, filename, { autoBom: false });
      }
    });
  }

  protected example(): void {
    this.paymentsUploadPaymentsService.example().subscribe(event => {
      if (event.type === HttpEventType.Response) {
        const contentDisposition = event.headers.get('content-disposition') ?? 'attachment; filename=payments.xlsx';
        const contentDispositionArray = contentDisposition.split(';');
        const contentDispositionFilename = contentDispositionArray.find(n => n.includes('filename='));
        let filename = 'payments.xlsx';
        if (contentDispositionFilename) {
          filename = contentDispositionFilename.replace('filename=', '').trim();
        }
        const blob = new Blob([event.body], { type: 'application/octet-stream' });
        // eslint-disable-next-line @typescript-eslint/no-deprecated
        FileSaver.saveAs(blob, filename, { autoBom: false });
      }
    });
  }

  protected formatBytes(bytes: number, decimals = 2): string {
    if (bytes === 0) {
      return '0 Bytes';
    }
    const k = 1024;
    const dm = decimals <= 0 ? 0 : decimals || 2;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)).toString() + ' ' + sizes[i];
  }

  protected formatCurrency(amount: number, decimals = 2): string {
    const formatter = new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    });
    return formatter.format(amount);
  }

  protected formatCount(count: number, decimals = 0): string {
    const formatter = new Intl.NumberFormat('de-DE', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    });
    return formatter.format(count);
  }

  protected deleteFile(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    if (fileCache.validateSubscription) {
      fileCache.validateSubscription.unsubscribe();
    }

    this.fileCaches.delete(name);
  }

  protected deleteDiscardedFile(name: string): void {
    setTimeout(() => {
      this.discardedFiles.delete(name);
    }, 5000);
  }

  protected getFiles(): FileCache[] {
    return Array.from(this.fileCaches.values());
  }
}
