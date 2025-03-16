import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Reconciliation',
    route: '/reconciliation',
    translationKey: 'global.menu.entities.reconciliation',
  },
  {
    name: 'KeyValue',
    route: '/key-value',
    translationKey: 'global.menu.entities.keyValue',
  },
  {
    name: 'Payment',
    route: '/payment',
    translationKey: 'global.menu.entities.payment',
  },
];
