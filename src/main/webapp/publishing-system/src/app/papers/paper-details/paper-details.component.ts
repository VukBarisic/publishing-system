import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { saveAs as importedSaveAs } from 'file-saver';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { PaperService } from '../../core/http/paper.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';

@Component({
  selector: 'ps-paper-details',
  templateUrl: './paper-details.component.html',
  styleUrls: ['./paper-details.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class PaperDetailsComponent implements OnInit {

  modalRef: BsModalRef;

  paperHtml: string;

  paperId: string;

  showDeleteButton: boolean;

  constructor(private paperService: PaperService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private modalService: BsModalService,
    private toastr: ToastrService,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.paperId = params['id'];
      this.getPaper();
      this.isPaperAuthor();
    });
  }

  getPaper() {
    this.paperService.findHtmlById(this.paperId).subscribe(
      res => {
        this.paperHtml = res;
      },
      err => {
        this.router.navigateByUrl('papers');
      });
  }

  downloadPdf() {
    this.paperService.downloadPdf(this.paperId).subscribe(
      blob => {
        importedSaveAs(blob, 'paper.pdf');
      }, err => { });
  }

  downloadXml() {
    this.paperService.downloadXml(this.paperId).subscribe(
      blob => {
        importedSaveAs(blob, 'paper.xml');
      }, err => { });
  }

  openModal() {
    this.modalRef = this.modalService.show(ConfirmModalComponent);
    this.modalRef.content.onClose.subscribe(result => {
      if (result) {
        this.deletePaper();
      }
    });
  }

  deletePaper() {
    this.paperService.delete(this.paperId).subscribe(
      res => {
        this.router.navigateByUrl('papers');
      }, err => { });
  }

  isPaperAuthor() {
    this.paperService.findAuthorUsername(this.paperId).subscribe(
      res => {
        this.showDeleteButton = res === this.authService.getAuthenticatedUsername();
      }, err => { });
  }
}
