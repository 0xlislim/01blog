import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { TextFieldModule } from '@angular/cdk/text-field';

import { ProfileComponent } from './pages/profile/profile.component';
import { UserSearchComponent } from './pages/user-search/user-search.component';
import { EditProfileDialogComponent } from './components/edit-profile-dialog/edit-profile-dialog.component';

const routes: Routes = [
  { path: '', redirectTo: 'profile', pathMatch: 'full' },
  { path: 'profile', component: ProfileComponent },
  { path: 'search', component: UserSearchComponent },
  { path: ':id', component: ProfileComponent }
];

@NgModule({
  declarations: [
    ProfileComponent,
    UserSearchComponent,
    EditProfileDialogComponent
  ],
  imports: [
    SharedModule,
    TextFieldModule,
    RouterModule.forChild(routes)
  ]
})
export class UsersModule { }
