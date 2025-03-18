import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { KeyValueService } from '../service/key-value.service';
import { IKeyValue } from '../key-value.model';
import { KeyValueFormService } from './key-value-form.service';

import { KeyValueUpdateComponent } from './key-value-update.component';

describe('KeyValue Management Update Component', () => {
  let comp: KeyValueUpdateComponent;
  let fixture: ComponentFixture<KeyValueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let keyValueFormService: KeyValueFormService;
  let keyValueService: KeyValueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [KeyValueUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(KeyValueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KeyValueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    keyValueFormService = TestBed.inject(KeyValueFormService);
    keyValueService = TestBed.inject(KeyValueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const keyValue: IKeyValue = { id: 456 };

      activatedRoute.data = of({ keyValue });
      comp.ngOnInit();

      expect(comp.keyValue).toEqual(keyValue);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKeyValue>>();
      const keyValue = { id: 123 };
      jest.spyOn(keyValueFormService, 'getKeyValue').mockReturnValue(keyValue);
      jest.spyOn(keyValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ keyValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: keyValue }));
      saveSubject.complete();

      // THEN
      expect(keyValueFormService.getKeyValue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(keyValueService.update).toHaveBeenCalledWith(expect.objectContaining(keyValue));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKeyValue>>();
      const keyValue = { id: 123 };
      jest.spyOn(keyValueFormService, 'getKeyValue').mockReturnValue({ id: null });
      jest.spyOn(keyValueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ keyValue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: keyValue }));
      saveSubject.complete();

      // THEN
      expect(keyValueFormService.getKeyValue).toHaveBeenCalled();
      expect(keyValueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKeyValue>>();
      const keyValue = { id: 123 };
      jest.spyOn(keyValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ keyValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(keyValueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
