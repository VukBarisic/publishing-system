import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ReviewService } from '../../core/http/review.service';
import { PaperResponse } from '../model/paper-response.model';

@Component({
  selector: 'ps-paper-list',
  templateUrl: './paper-list.component.html',
  styleUrls: ['./paper-list.component.css']
})
export class PaperListComponent implements OnInit {

  @Input() state: string;

  @Input() papers = new Array<PaperResponse>();

  constructor(private router: Router,
    private reviewService: ReviewService) { }

  ngOnInit() {
  }

  showDetails(paper: PaperResponse): void {
    this.router.navigate(['papers', paper.id]);
  }

  acceptPaper(paper: PaperResponse) {
    this.respondToRequest(paper, true);
  }

  rejectPaper(paper: PaperResponse) {
    this.respondToRequest(paper, false);
  }

  respondToRequest(paper: PaperResponse, response: boolean) {
    this.reviewService.respondToReviewRequest(paper.id, response).subscribe(
      res => {
        this.papers.splice(this.papers.indexOf(paper), 1);
      }, err => { });
  }

  reviewPaper(paper: PaperResponse) {
    this.router.navigate(['papers', paper.id, 'review']);
  }

  processPaper(paper: PaperResponse) {
    this.router.navigate(['papers', paper.id, 'processing']);
  }

  sendRevision(paper: PaperResponse) {
    this.router.navigate(['papers', paper.id, 'revision']);
  }

  isAssigned() {
    return this.state === 'assigned';
  }

  isAccepted() {
    return this.state === 'accepted';
  }

  isSubmitted() {
    return this.state === 'submitted';
  }

  isAuthorPapers() {
    return this.state === 'authors';
  }
}
