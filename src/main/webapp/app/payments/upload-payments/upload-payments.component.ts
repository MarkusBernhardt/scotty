import { TranslateService } from '@ngx-translate/core';
import { PaymentsUploadPaymentsService } from './upload-payments.service';
import { HttpEventType } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { PaymentsUploadPaymentsValidateResponse, PaymentsUploadPaymentsExecuteResponse } from './upload-payments.model';
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
    public executeFileName: string | null,
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

  async onFileBrowsed(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files) {
      await this.onFileDropped(target.files);
    }
  }

  async onFileDropped(fileList: FileList) {
    const allowedFileExtensions = new Set(['xls', 'xlsx']);
    for (let i = 0; i < fileList.length; i++) {
      let file = fileList.item(i);
      await new Promise(f => setTimeout(f, 250));
      if (file != null) {
        if (!allowedFileExtensions.has(file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase())) {
          if (!this.discardedFiles.has(file.name)) {
            this.discardedFiles.add(file.name);
            this.deleteDiscardedFile(file.name);
          }
        } else if (!this.fileCaches.has(file.name)) {
          this.fileCaches.set(file.name, new FileCache(file, 0, true, 0, 0, null, 0, 0, null, null));
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
      next: event => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (event.total) {
              fileCache.validateProgress = Math.round((100 * event.loaded) / event.total);
            }
            break;
          case HttpEventType.Response:
            const response = event.body as PaymentsUploadPaymentsValidateResponse;
            fileCache.validateProgress = 100;
            fileCache.validateSuccess = response.success;
            fileCache.validateCount = response.count;
            fileCache.validateAmount = response.amount;
            break;
          default:
            break;
        }
      },
      error: () => {
        fileCache.validateSuccess = false;
        fileCache.validateProgress = 100;
      },
    });
  }

  execute(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    fileCache.executeFileName =
      fileCache.file.name.substring(0, fileCache.file.name.lastIndexOf('.')) +
      '-' +
      formatDate(Date.now(), 'yyyyMMdd-hhmmss', 'en-US') +
      '.xlsx';

    fileCache.executeSubscription = this.paymentsUploadPaymentsService.execute(fileCache.file, fileCache.executeFileName).subscribe({
      next: event => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (event.total) {
              fileCache.executeProgress = Math.round((100 * event.loaded) / event.total);
            }
            break;
          case HttpEventType.Response:
            const response = event.body as PaymentsUploadPaymentsExecuteResponse;
            fileCache.executeProgress = 100;
            fileCache.executeSuccess = response.success;
            break;
          default:
            break;
        }
      },
      error: () => {
        fileCache.executeSuccess = 1;
        fileCache.executeProgress = 100;
      },
    });

    (async () => {
      await new Promise(f => setTimeout(f, 1000));
      fileCache.executeProgress = 10;
      await new Promise(f => setTimeout(f, 1000));
      fileCache.executeProgress = 20;
      await new Promise(f => setTimeout(f, 1000));
      fileCache.executeProgress = 30;
      await new Promise(f => setTimeout(f, 1000));
      fileCache.executeProgress = 40;
      await new Promise(f => setTimeout(f, 1000));
      fileCache.executeProgress = 50;
    })();
  }

  save(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    /*
    if (fileCache.processedFileName && fileCache.processedFile) {
      const blob = new Blob([fileCache.processedFile], { type: 'text/plain;charset=utf-8' });
      FileSaver.saveAs(blob, fileCache.processedFileName);
    }
    */
  }

  example(): void {
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
        FileSaver.saveAs(blob, filename);
      }
    });
  }

  formatBytes(bytes: number, decimals: number = 2) {
    if (bytes === 0) {
      return '0 Bytes';
    }
    const k = 1024;
    const dm = decimals <= 0 ? 0 : decimals || 2;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
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

  deleteFile(name: string) {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    if (fileCache.validateSubscription) {
      fileCache.validateSubscription.unsubscribe();
    }

    this.fileCaches.delete(name);
  }

  deleteDiscardedFile(name: string) {
    setTimeout(() => {
      this.discardedFiles.delete(name);
    }, 5000);
  }

  getFiles() {
    return Array.from(this.fileCaches.values());
  }
}
