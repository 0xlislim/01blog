import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Angular Material Modules
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { TextFieldModule } from '@angular/cdk/text-field';

// Shared Components
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { PostCardSkeletonComponent } from './components/post-card-skeleton/post-card-skeleton.component';
import { ReportDialogComponent } from './components/report-dialog/report-dialog.component';

const MaterialModules = [
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatInputModule,
  MatFormFieldModule,
  MatToolbarModule,
  MatMenuModule,
  MatSidenavModule,
  MatListModule,
  MatBadgeModule,
  MatSnackBarModule,
  MatDialogModule,
  MatProgressSpinnerModule,
  MatProgressBarModule,
  MatTabsModule,
  MatTableModule,
  MatPaginatorModule,
  MatSortModule,
  MatChipsModule,
  MatTooltipModule,
  MatDividerModule
];

@NgModule({
  declarations: [
    ConfirmDialogComponent,
    FileUploadComponent,
    PostCardSkeletonComponent,
    ReportDialogComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TextFieldModule,
    ...MaterialModules
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TextFieldModule,
    ...MaterialModules,
    ConfirmDialogComponent,
    FileUploadComponent,
    PostCardSkeletonComponent,
    ReportDialogComponent
  ]
})
export class SharedModule { }
