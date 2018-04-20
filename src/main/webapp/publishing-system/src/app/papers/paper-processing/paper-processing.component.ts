import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

import {PaperService} from '../../core/http/paper.service';
import {ReviewService} from '../../core/http/review.service';
import {UserResponse} from '../../shared/model/user-response';

@Component({
  selector: 'ps-paper-processing',
  templateUrl: './paper-processing.component.html',
  styleUrls: ['./paper-processing.component.css']
})
export class PaperProcessingComponent implements OnInit {

  constructor(private paperService: PaperService,
    private reviewService: ReviewService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService) { }

  paperId: string;

  suggestedAuthors = new Array<UserResponse>();

  reviewers = new Array<UserResponse>();

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.paperId = params['id'];
      this.getSuggested();
      this.getReviewers();
    });
  }

  getSuggested(): void {
    this.reviewService.getSuggestedReviewers(this.paperId).subscribe(
      res => {
        this.suggestedAuthors = res;
      },
      err => { });
  }

  getReviewers(): void {
    this.reviewService.getAssignedReviewers(this.paperId).subscribe(
      res => {
        this.reviewers = res;
      },
      err => {

      }
    );
  }

  addReviewer(author: UserResponse): void {
    this.reviewService.assignReviewer(this.paperId, author.username).subscribe(
      res => {
        this.getSuggested();
        this.getReviewers();
      },
      err => { }
    );
  }

  respond(response: boolean): void {
    this.paperService.respondToPublishRequest(this.paperId, response).subscribe(
      res => {
        if (response) {
          this.toastr.success('Paper published!');
        } else {
          this.toastr.success('Paper rejected!');
        }
        this.router.navigateByUrl('papers/submitted');
      },
      err => {
      }
    );
  }

  groupReview(): void {
    this.paperService.groupReview(this.paperId).subscribe(
      res => {
        this.toastr.success('Group review submitted!');
      },
      err => {
      }
    );
  }

  checkIfReviewed() {
    let count = 0;
    this.reviewers.forEach(x => {
      if (x.reviewerStatus === 'reviewed') {
        count++;
      }
    });

    return count > 0;
  }

}
