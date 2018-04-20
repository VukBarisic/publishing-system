import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ModalModule } from 'ngx-bootstrap/modal';
import { TabsModule } from 'ngx-bootstrap/tabs';

import { ConfirmModalComponent } from './confirm-modal/confirm-modal.component';
import { PaperListComponent } from './paper-list/paper-list.component';
import { SafeHtmlPipe } from './pipe/safe-html.pipe';

@NgModule({
  imports: [
    BsDropdownModule.forRoot(),
    CollapseModule.forRoot(),
    TabsModule.forRoot(),
    CommonModule,
    ModalModule.forRoot(),
    FormsModule
  ],
  exports: [
    BsDropdownModule,
    CollapseModule,
    TabsModule,
    FormsModule,
    PaperListComponent,
    SafeHtmlPipe
  ],
  declarations: [PaperListComponent, SafeHtmlPipe, ConfirmModalComponent],
  entryComponents: [ConfirmModalComponent]
})
export class SharedModule { }
