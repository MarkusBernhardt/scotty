import { Directive, Output, EventEmitter, HostBinding, HostListener } from '@angular/core';

@Directive({
  standalone: true,
  selector: '[spxDnd]',
})
export default class DndDirective {
  @HostBinding('class.dragover') dragover: boolean = false;
  @Output() fileDropped = new EventEmitter<FileList>();

  // DragOver listener
  @HostListener('dragover', ['$event']) onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = true;
  }

  // DragLeave listener
  @HostListener('dragleave', ['$event']) public onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = false;
  }

  // Drop listener
  @HostListener('drop', ['$event']) public ondrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragover = false;
    if (event.dataTransfer !== null) {
      let files = event.dataTransfer.files;
      if (files.length > 0) {
        this.fileDropped.emit(files);
      }
    }
  }
}
