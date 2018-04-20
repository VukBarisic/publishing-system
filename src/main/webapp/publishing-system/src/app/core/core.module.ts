import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from '../routes/app-routing.module';
import { SharedModule } from '../shared/shared.module';
import { AuthService } from './http/auth.service';
import { CoverLetterService } from './http/cover-letter.service';
import { EvaluationFormService } from './http/evaluation-form.service';
import { PaperService } from './http/paper.service';
import { ReviewService } from './http/review.service';
import { NavComponent } from './nav/nav.component';

@NgModule({
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    CommonModule,
    HttpClientModule,
    SharedModule,
    ToastrModule.forRoot({
      preventDuplicates: true,
      positionClass: 'toast-position'
    })
  ],
  declarations: [NavComponent],
  exports: [
    AppRoutingModule,
    NavComponent
  ],
  providers: [
    AuthService,
    CoverLetterService,
    EvaluationFormService,
    PaperService,
    ReviewService
  ]
})
export class CoreModule { }
