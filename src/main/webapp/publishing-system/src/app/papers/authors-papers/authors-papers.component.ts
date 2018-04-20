import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PaperService } from '../../core/http/paper.service';
import { PaperResponse } from '../../shared/model/paper-response.model';

@Component({
  selector: 'ps-authors-papers',
  templateUrl: './authors-papers.component.html',
  styleUrls: ['./authors-papers.component.css']
})
export class AuthorsPapersComponent implements OnInit {

  username: string;

  papers: PaperResponse[];

  noSubmittedPapers: string;

  constructor(private paperService: PaperService,
    private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.username = params['username'];
      this.getPapers();
    });
  }

  getPapers() {
    this.paperService.findByAuthor(this.username).subscribe(
      res => {
        this.noSubmittedPapers = '';
        this.papers = res;

        if (this.papers.length === 0) {
          this.noSubmittedPapers = 'This author still hasn\'t published any papers.';
        }
      }, err => { });
  }
}
