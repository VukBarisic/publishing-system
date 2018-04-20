import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SigninComponent } from './signin/signin.component';
import { SignupComponent } from './signup/signup.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [SignupComponent, SigninComponent],
  exports: [SignupComponent, SigninComponent]
})
export class AuthModule { }
