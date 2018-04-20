import { Component, OnInit } from '@angular/core';

import { PaperService } from '../../core/http/paper.service';
import { PaperResponse } from '../../shared/model/paper-response.model';

@Component({
  selector: 'ps-assigned-papers',
  templateUrl: './assigned-papers.component.html',
  styleUrls: ['./assigned-papers.component.css']
})
export class AssignedPapersComponent implements OnInit {

  noAssignedPapers: string;

  papers: PaperResponse[];

  constructor(private paperService: PaperService) { }

  ngOnInit() {
    this.getPapers();
  }

  getPapers() {
    this.paperService.findAssigned().subscribe(
      res => {
        this.papers = res;
        this.noAssignedPapers = '';

        if (this.papers.length === 0) {
          this.noAssignedPapers = 'There are no assigned papers for review.';
        }
      },
      err => {

      }
    );
  }

}
