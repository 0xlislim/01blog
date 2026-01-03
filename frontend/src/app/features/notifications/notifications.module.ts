import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';

// Pages
import { NotificationListComponent } from './pages/notification-list/notification-list.component';

// Components
import { NotificationItemComponent } from './components/notification-item/notification-item.component';

const routes: Routes = [
  { path: '', component: NotificationListComponent }
];

@NgModule({
  declarations: [
    NotificationListComponent,
    NotificationItemComponent
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ]
})
export class NotificationsModule { }
