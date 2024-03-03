import { AfterViewInit, Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PaymentsUploadPaymentsService } from './upload-payments.service';
import { HttpEventType } from '@angular/common/http';
import { Subscription } from 'rxjs';
import {
  PaymentsUploadPaymentsValidateResponse,
  PaymentsUploadPaymentsExecuteResponse,
  PaymentsUploadPaymentsExampleResponse,
} from './upload-payments.model';
import FileSaver from 'file-saver';

class FileCache {
  constructor(
    public file: File,
    public validateProgress: number,
    public validateSuccess: boolean,
    public validateCount: number,
    public validateAmount: number,
    public validateSubscription: Subscription | null,
    public processedFileName: string | null,
    public processedFile: string | null,
  ) {}
}

@Component({
  selector: 'jhi-score-country-match',
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
    const allowedFileExtensions = new Set(['xls', 'xslx']);
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
          this.fileCaches.set(file.name, new FileCache(file, 0, true, 0, 0, null, null, null));
          this.validatePayments(file.name);
        }
      }
    }
  }

  validatePayments(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    fileCache.validateSubscription = this.paymentsUploadPaymentsService.validate(fileCache.file).subscribe(
      (event: any) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            fileCache.validateProgress = Math.round((100 * event.loaded) / event.total);
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
      () => {
        fileCache.validateSuccess = false;
        fileCache.validateProgress = 100;
      },
    );
  }

  save(name: string): void {
    const fileCache = this.fileCaches.get(name);
    if (fileCache == null) {
      return;
    }

    if (fileCache.processedFileName && fileCache.processedFile) {
      const blob = new Blob([fileCache.processedFile], { type: 'text/plain;charset=utf-8' });
      FileSaver.saveAs(blob, fileCache.processedFileName);
    }
  }

  example(): void {
    this.paymentsUploadPaymentsService.example().subscribe(response => {
      const blob = new Blob([response.file], { type: 'text/plain;charset=utf-8' });
      FileSaver.saveAs(blob, response.name);
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
