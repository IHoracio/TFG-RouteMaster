import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  private apiUrl = 'http://localhost:8080/routes';
  constructor(private http: HttpClient) {}

  calculateCoordinates(origin: string, destination: string): Observable<string> {
    const headers = new HttpHeaders()
    .set('key', "AIzaSyBygK3kyABotk12DqMLyUjZoK3WLS4-l7E");

    const parameters = new HttpParams()
      .set('origin', origin)
      .set('destination', destination)
      .set('language', 'es')

    return this.http.get(this.apiUrl, {headers: headers, params: parameters, responseType: 'text' });
  }
}
