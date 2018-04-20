import { Component, OnInit } from '@angular/core';
import { text } from '@angular/core/src/render3/instructions';

import { AuthService } from '../../core/http/auth.service';
import { PaperService } from '../../core/http/paper.service';
import { PaperResponse } from '../../shared/model/paper-response.model';

@Component({
  selector: 'ps-published-papers',
  templateUrl: './published-papers.component.html',
  styleUrls: ['./published-papers.component.css']
})
export class PublishedPapersComponent implements OnInit {

  papers: PaperResponse[];

  text: string;

  metadata: string;

  mine = false;

  constructor(private paperService: PaperService,
    private authService: AuthService) { }

  ngOnInit() {
    this.getPapers();
  }

  textSearch(): void {
    this.paperService.findByText(this.text, this.mine).subscribe(
      res => {
        this.papers = res;
      },
      err => {

      }
    );
  }

  metadataSearch(): void {
    this.paperService.findByMetadata(this.metadata, this.mine).subscribe(
      res => {
        this.papers = res;
      },
      err => {

      }
    );
  }

  showAll(): void {
    this.text = '';
    this.metadata = '';
    this.getPapers();
  }

  authenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  private getPapers(): void {
    this.paperService.findAll(this.mine).subscribe(
      res => {
        this.papers = res;
      },
      err => {

      }
    );
  }

}
