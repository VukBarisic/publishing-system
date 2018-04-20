import { Component, OnInit } from '@angular/core';

import { PaperService } from '../../core/http/paper.service';
import { PaperResponse } from '../../shared/model/paper-response.model';

@Component({
  selector: 'ps-submitted-papers',
  templateUrl: './submitted-papers.component.html',
  styleUrls: ['./submitted-papers.component.css']
})
export class SubmittedPapersComponent implements OnInit {

  noSubmittedPapers: string;

  papers: PaperResponse[];

  constructor(private paperService: PaperService) { }

  ngOnInit() {
    this.getPapers();
  }

  getPapers() {
    this.paperService.findSubmitted().subscribe(
      res => {
        this.noSubmittedPapers = '';
        this.papers = res;

        if (this.papers.length === 0) {
          this.noSubmittedPapers = 'There are no submitted papers at this time.';
        }
      },
      err => {

      }
    );
  }
}
