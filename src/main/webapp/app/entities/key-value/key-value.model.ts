export interface IKeyValue {
  id: number;
  key?: string | null;
  value?: string | null;
}

export type NewKeyValue = Omit<IKeyValue, 'id'> & { id: null };
