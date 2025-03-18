import { Directive, Output, EventEmitter, HostBinding, HostListener } from '@angular/core';

@Directive({
  standalone: true,
  selector: '[jhiSpxDnd]',
})
export default class DndDirective {
  @HostBinding('class.dragover') dragover = false;
  @Output() fileDropped = new EventEmitter<FileList>();

  // DragOver listener
  @HostListener('dragover', ['$event']) onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = true;
  }

  // DragLeave listener
  @HostListener('dragleave', ['$event']) public onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = false;
  }

  // Drop listener
  @HostListener('drop', ['$event']) public ondrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = false;
    if (event.dataTransfer !== null) {
      const files = event.dataTransfer.files;
      if (files.length > 0) {
        this.fileDropped.emit(files);
      }
    }
  }
}
