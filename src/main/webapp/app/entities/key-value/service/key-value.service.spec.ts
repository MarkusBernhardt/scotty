import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IKeyValue } from '../key-value.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../key-value.test-samples';

import { KeyValueService } from './key-value.service';

const requireRestSample: IKeyValue = {
  ...sampleWithRequiredData,
};

describe('KeyValue Service', () => {
  let service: KeyValueService;
  let httpMock: HttpTestingController;
  let expectedResult: IKeyValue | IKeyValue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(KeyValueService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a KeyValue', () => {
      const keyValue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(keyValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a KeyValue', () => {
      const keyValue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(keyValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a KeyValue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of KeyValue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a KeyValue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addKeyValueToCollectionIfMissing', () => {
      it('should add a KeyValue to an empty array', () => {
        const keyValue: IKeyValue = sampleWithRequiredData;
        expectedResult = service.addKeyValueToCollectionIfMissing([], keyValue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(keyValue);
      });

      it('should not add a KeyValue to an array that contains it', () => {
        const keyValue: IKeyValue = sampleWithRequiredData;
        const keyValueCollection: IKeyValue[] = [
          {
            ...keyValue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addKeyValueToCollectionIfMissing(keyValueCollection, keyValue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a KeyValue to an array that doesn't contain it", () => {
        const keyValue: IKeyValue = sampleWithRequiredData;
        const keyValueCollection: IKeyValue[] = [sampleWithPartialData];
        expectedResult = service.addKeyValueToCollectionIfMissing(keyValueCollection, keyValue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(keyValue);
      });

      it('should add only unique KeyValue to an array', () => {
        const keyValueArray: IKeyValue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const keyValueCollection: IKeyValue[] = [sampleWithRequiredData];
        expectedResult = service.addKeyValueToCollectionIfMissing(keyValueCollection, ...keyValueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const keyValue: IKeyValue = sampleWithRequiredData;
        const keyValue2: IKeyValue = sampleWithPartialData;
        expectedResult = service.addKeyValueToCollectionIfMissing([], keyValue, keyValue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(keyValue);
        expect(expectedResult).toContain(keyValue2);
      });

      it('should accept null and undefined values', () => {
        const keyValue: IKeyValue = sampleWithRequiredData;
        expectedResult = service.addKeyValueToCollectionIfMissing([], null, keyValue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(keyValue);
      });

      it('should return initial array if no KeyValue is added', () => {
        const keyValueCollection: IKeyValue[] = [sampleWithRequiredData];
        expectedResult = service.addKeyValueToCollectionIfMissing(keyValueCollection, undefined, null);
        expect(expectedResult).toEqual(keyValueCollection);
      });
    });

    describe('compareKeyValue', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareKeyValue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareKeyValue(entity1, entity2);
        const compareResult2 = service.compareKeyValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareKeyValue(entity1, entity2);
        const compareResult2 = service.compareKeyValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareKeyValue(entity1, entity2);
        const compareResult2 = service.compareKeyValue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
