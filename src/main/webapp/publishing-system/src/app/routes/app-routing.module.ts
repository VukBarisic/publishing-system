import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SigninComponent } from '../auth/signin/signin.component';
import { SignupComponent } from '../auth/signup/signup.component';
import { AcceptedPapersComponent } from '../papers/accepted-papers/accepted-papers.component';
import { AssignedPapersComponent } from '../papers/assigned-papers/assigned-papers.component';
import { AuthorsListComponent } from '../papers/authors-list/authors-list.component';
import { AuthorsPapersComponent } from '../papers/authors-papers/authors-papers.component';
import { NewPaperComponent } from '../papers/new-paper/new-paper.component';
import { NewReviewComponent } from '../papers/new-review/new-review.component';
import { PaperDetailsComponent } from '../papers/paper-details/paper-details.component';
import { PaperProcessingComponent } from '../papers/paper-processing/paper-processing.component';
import { PublishedPapersComponent } from '../papers/published-papers/published-papers.component';
import { SubmittedPapersComponent } from '../papers/submitted-papers/submitted-papers.component';
import { IsAuthenticatedGuard } from './is-authenticated.guard';
import { IsAuthorGuard } from './is-author.guard';
import { IsEditorGuard } from './is-editor.guard';
import { IsUnuthenticatedGuard } from './is-unauthenticated.guard';

const routes: Routes = [
  { path: '', redirectTo: '/signin', pathMatch: 'full' },
  // Auth
  { path: 'signup', component: SignupComponent, canActivate: [IsUnuthenticatedGuard] },
  { path: 'signin', component: SigninComponent, canActivate: [IsUnuthenticatedGuard] },

  // Papers
  { path: 'papers', component: PublishedPapersComponent },
  { path: 'papers/new', component: NewPaperComponent, canActivate: [IsAuthorGuard] },
  { path: 'papers/submitted', component: SubmittedPapersComponent, canActivate: [IsEditorGuard] },
  { path: 'papers/assigned', component: AssignedPapersComponent, canActivate: [IsAuthorGuard] },
  { path: 'papers/accepted', component: AcceptedPapersComponent, canActivate: [IsAuthorGuard] },
  { path: 'papers/:id', component: PaperDetailsComponent },
  { path: 'papers/:id/processing', component: PaperProcessingComponent },
  { path: 'papers/:id/review', component: NewReviewComponent, canActivate: [IsAuthenticatedGuard] },
  { path: 'papers/:id/revision', component: NewPaperComponent, canActivate: [] },

  // Authors
  { path: 'authors', component: AuthorsListComponent },
  { path: 'authors/:username/papers', component: AuthorsPapersComponent }
];


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule],
  providers: [
    IsAuthenticatedGuard,
    IsUnuthenticatedGuard,
    IsEditorGuard,
    IsAuthorGuard
  ]
})
export class AppRoutingModule { }
