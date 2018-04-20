import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { UserResponse } from '../../shared/model/user-response';
import { RestService } from './rest.service';

@Injectable()
export class ReviewService extends RestService<string> {

  constructor(protected http: HttpClient,
    toastr: ToastrService) {
    super(http, '/api', toastr);
  }

  create(xml: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/reviews`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  preview(xml: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/reviews/preview`, xml, {
      headers: new HttpHeaders({
        'Content-Type': 'application/xml',
        'Accept': '*/*, application/xml, application/json'
      }),
      responseType: 'text'
    }).pipe(
      catchError(this.handleError<string>())
      );
  }

  assignReviewer(id: string, username: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/reviews/${id}/assign/${username}`, null).pipe(
      catchError(this.handleError<void>())
    );
  }

  respondToReviewRequest(id: string, accepted: boolean): Observable<void> {
    const params = new HttpParams().set('accepted', String(accepted));
    return this.http.put<void>(`${this.baseUrl}/reviews/${id}/respond`, null, { params: params }).pipe(
      catchError(this.handleError<void>())
    );
  }

  getSuggestedReviewers(id: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/papers/${id}/suggested-reviewers`).pipe(
      catchError(this.handleError<UserResponse[]>())
    );
  }

  getAssignedReviewers(id: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/papers/${id}/reviewers`).pipe(
      catchError(this.handleError<UserResponse[]>())
    );
  }
}
