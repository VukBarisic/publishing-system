import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { CoverLetterService } from '../../core/http/cover-letter.service';
import { PaperService } from '../../core/http/paper.service';

@Component({
  selector: 'ps-new-paper',
  templateUrl: './new-paper.component.html',
  styleUrls: ['./new-paper.component.css']
})
export class NewPaperComponent implements OnInit {

  @ViewChild('paperFrame') paperFrame: ElementRef;

  @ViewChild('coverLetterFrame') coverLetterFrame: ElementRef;

  paperPreview: string;

  sendingReview: boolean;

  paperId: string;

  constructor(private paperService: PaperService,
    private coverLetterService: CoverLetterService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.sendingReview = this.router.url.indexOf('revision') > -1;
  }

  loadPaper() {
    if (!this.sendingReview) {
      this.paperFrame.nativeElement.contentWindow.start();
      return;
    }

    this.route.params.subscribe(params => {
      this.paperId = params['id'];
      this.paperService.findXmlById(this.paperId).subscribe(res => {
        this.paperFrame.nativeElement.contentWindow.start(res);
      }, err => { });
    });
  }

  submit() {
    const paperXml = this.harvestPaper();
    const coverLetterXml = this.harvestCoverLetter();

    if (this.sendingReview) {
      this.paperService.update(this.paperId, paperXml).subscribe(
        paperId => {
          this.coverLetterService.create(coverLetterXml, paperId).subscribe(
            res => {
              this.toastr.success('Paper revision sent!');
              this.router.navigateByUrl('papers');
            }, err => { });
        }, err => { });
    } else {
      this.paperService.create(paperXml).subscribe(
        paperId => {
          this.coverLetterService.create(coverLetterXml, paperId).subscribe(
            res => {
              this.toastr.success('Paper submitted!');
              this.router.navigateByUrl('papers');
            }, err => { });
        }, err => { });
    }
  }

  getPreview() {
    this.paperService.preview(this.harvestPaper()).subscribe(
      res => {
        this.paperPreview = res;
      },
      err => { });
  }

  harvestPaper(): string {
    let paperXml = this.paperFrame.nativeElement.contentWindow.Xonomy.harvest();
    paperXml = paperXml.replace(/xml:space='preserve'/g, '');

    return paperXml;
  }

  harvestCoverLetter(): string {
    let coverLetterXml = this.coverLetterFrame.nativeElement.contentWindow.Xonomy.harvest();
    coverLetterXml = coverLetterXml.replace(/xml:space='preserve'/g, '');

    return coverLetterXml;
  }
}
