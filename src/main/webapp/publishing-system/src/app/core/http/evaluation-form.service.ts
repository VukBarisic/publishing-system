import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { RestService } from './rest.service';

@Injectable()
export class EvaluationFormService extends RestService<string> {

  constructor(protected http: HttpClient,
    toastr: ToastrService) {
    super(http, '/api', toastr);
  }

  create(xml: string, id: string): Observable<string> {
    const params = new HttpParams().set('documentId', id);

    return this.http.post(`${this.baseUrl}/evaluationForms`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml'
      }),
      responseType: 'text',
      params: params
    }).pipe(
      catchError(this.handleError<string>())
      );
  }
}
