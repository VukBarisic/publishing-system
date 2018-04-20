import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { AcceptedPapersComponent } from './accepted-papers/accepted-papers.component';
import { AssignedPapersComponent } from './assigned-papers/assigned-papers.component';
import { AuthorsListComponent } from './authors-list/authors-list.component';
import { AuthorsPapersComponent } from './authors-papers/authors-papers.component';
import { NewPaperComponent } from './new-paper/new-paper.component';
import { NewReviewComponent } from './new-review/new-review.component';
import { PaperDetailsComponent } from './paper-details/paper-details.component';
import { PaperProcessingComponent } from './paper-processing/paper-processing.component';
import { PublishedPapersComponent } from './published-papers/published-papers.component';
import { SubmittedPapersComponent } from './submitted-papers/submitted-papers.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [
    PublishedPapersComponent,
    PaperDetailsComponent,
    NewPaperComponent,
    SubmittedPapersComponent,
    AssignedPapersComponent,
    AcceptedPapersComponent,
    AuthorsListComponent,
    AuthorsPapersComponent,
    PaperProcessingComponent,
    NewReviewComponent],
  exports: [PublishedPapersComponent,
    PaperDetailsComponent,
    NewPaperComponent,
    SubmittedPapersComponent,
    AssignedPapersComponent,
    AcceptedPapersComponent,
    AuthorsListComponent,
    AuthorsPapersComponent,
    NewReviewComponent]
})
export class PapersModule { }
