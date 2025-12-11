import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { MainLayoutComponent } from './components/main-layout/main-layout.component';
import { LayoutModule as CdkLayoutModule } from '@angular/cdk/layout';

@NgModule({
  declarations: [
    MainLayoutComponent
  ],
  imports: [
    SharedModule,
    CdkLayoutModule
  ],
  exports: [
    MainLayoutComponent
  ]
})
export class LayoutModule { }
