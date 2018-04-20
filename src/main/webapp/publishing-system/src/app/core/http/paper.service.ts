import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { PaperResponse } from '../../shared/model/paper-response.model';
import { UserResponse } from '../../shared/model/user-response';
import { RestService } from './rest.service';

@Injectable()
export class PaperService extends RestService<PaperResponse> {

  constructor(protected http: HttpClient,
    toastr: ToastrService) {
    super(http, '/api', toastr);
  }

  create(xml: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/papers`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  update(id: string, xml: string): Observable<string> {
    return this.http.put(`${this.baseUrl}/papers/${id}`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  findAll(mine: boolean): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers`, { params: { 'mine': String(mine) } }).pipe(
      catchError(this.handleError<PaperResponse[]>())
    );
  }

  preview(xml: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/papers/preview`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  findHtmlById(id: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/papers/${id}`, {
      headers: new HttpHeaders({
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  findXmlById(id: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/papers/${id}/xml`, {
      headers: new HttpHeaders({
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  downloadPdf(id: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/papers/${id}/pdf/download`, {
      headers: new HttpHeaders({
        'Accept': '*/*, application/pdf, application/json'
      }),
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError<Blob>())
      );
  }

  downloadXml(id: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/papers/${id}/xml/download`, {
      headers: new HttpHeaders({
        'Accept': '*/*, application/xml'
      }),
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError<Blob>())
      );
  }

  findByText(text: string, mine: boolean): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers/search-text`,
      { params: { 'text': text, 'mine': String(mine) } }).pipe(
      catchError(this.handleError<PaperResponse[]>())
      );
  }

  findByMetadata(text: string, mine: boolean): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers/search-metadata`,
      { params: { 'query': text, 'mine': String(mine) } }).pipe(
      catchError(this.handleError<PaperResponse[]>())
      );
  }

  findByAuthor(username: string): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/authors/${username}/papers`).pipe(
      catchError(this.handleError<PaperResponse[]>())
    );
  }

  findAuthorUsername(paperId: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/papers/${paperId}/author`, {
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  findAssigned(): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers/assigned`).pipe(
      catchError(this.handleError<PaperResponse[]>())
    );
  }

  findAccepted(): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers/accepted`).pipe(
      catchError(this.handleError<PaperResponse[]>())
    );
  }

  findSubmitted(): Observable<PaperResponse[]> {
    return this.http.get<PaperResponse[]>(`${this.baseUrl}/papers/submitted`).pipe(
      catchError(this.handleError<PaperResponse[]>())
    );
  }

  groupReview(id: string): Observable<string> {
    return this.http.put(`${this.baseUrl}/papers/${id}/group-review`, null, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  respondToPublishRequest(id: string, response: boolean): Observable<void> {
    const params = new HttpParams().set('accepted', String(response));
    return this.http.put<void>(`${this.baseUrl}/papers/${id}/respond`, null, { params: params }).pipe(
      catchError(this.handleError<void>())
    );
  }

  findAllAuthors(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/authors`).pipe(
      catchError(this.handleError<UserResponse[]>())
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/papers/${id}`).pipe(
      catchError(this.handleError<void>())
    );
  }
}
