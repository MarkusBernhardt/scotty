export interface IKeyValue {
  id: number;
  kvKey?: string | null;
  kvValue?: string | null;
}

export type NewKeyValue = Omit<IKeyValue, 'id'> & { id: null };
