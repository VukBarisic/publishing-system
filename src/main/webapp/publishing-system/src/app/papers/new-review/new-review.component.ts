import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

import {EvaluationFormService} from '../../core/http/evaluation-form.service';
import {PaperService} from '../../core/http/paper.service';
import {ReviewService} from '../../core/http/review.service';

@Component({
  selector: 'ps-new-review',
  templateUrl: './new-review.component.html',
  styleUrls: ['./new-review.component.css']
})
export class NewReviewComponent implements OnInit {

  @ViewChild('reviewFrame') reviewFrame: ElementRef;

  @ViewChild('evaluationFrame') evaluationFrame: ElementRef;

  paperPreview: string;

  paperId: string;

  constructor(private paperService: PaperService,
    private reviewService: ReviewService,
    private evaluationFormService: EvaluationFormService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
  }

  loadPaper() {
    this.route.params.subscribe(params => {
      this.paperId = params['id'];
      this.paperService.findXmlById(this.paperId).subscribe(res => {
        this.reviewFrame.nativeElement.contentWindow.start(res);
      }, err => { });
    });
  }

  submit() {
    const reviewXml = this.harvestReview();
    const evaluationXml = this.harvestEvaluation();

    this.reviewService.create(reviewXml).subscribe(
      reviewId => {
        this.evaluationFormService.create(evaluationXml, this.paperId).subscribe(
          res => {
            this.toastr.success('Review submitted!');
            this.router.navigateByUrl('papers');
          }, err => { });
      }, err => { });
  }

  getPreview() {
    this.reviewService.preview(this.harvestReview()).subscribe(
      res => {
        this.paperPreview = res;
      },
      err => { });
  }

  harvestReview(): string {
    let reviewXml = this.reviewFrame.nativeElement.contentWindow.Xonomy.harvest();
    reviewXml = reviewXml.replace(/xml:space='preserve'/g, '');

    return reviewXml;
  }

  harvestEvaluation(): string {
    let evaluationXml = this.evaluationFrame.nativeElement.contentWindow.Xonomy.harvest();
    evaluationXml = evaluationXml.replace(/xml:space='preserve'/g, '');

    return evaluationXml;
  }
}
