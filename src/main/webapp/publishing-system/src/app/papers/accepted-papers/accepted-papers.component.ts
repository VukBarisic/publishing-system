import { Component, OnInit } from '@angular/core';

import { PaperService } from '../../core/http/paper.service';
import { PaperResponse } from '../../shared/model/paper-response.model';

@Component({
  selector: 'ps-accepted-papers',
  templateUrl: './accepted-papers.component.html',
  styleUrls: ['./accepted-papers.component.css']
})
export class AcceptedPapersComponent implements OnInit {

  noAcceptedPapers: string;

  papers: PaperResponse[];

  constructor(private paperService: PaperService) { }

  ngOnInit() {
    this.getPapers();
  }

  getPapers() {
    this.paperService.findAccepted().subscribe(
      res => {
        this.papers = res;
        this.noAcceptedPapers = '';

        if (this.papers.length === 0) {
          this.noAcceptedPapers = 'There are no papers you accepted for review.';
        }
      },
      err => {

      }
    );
  }

}
