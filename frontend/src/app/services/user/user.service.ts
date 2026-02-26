import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUrl = environment.apiUrl + '/api/users';
  private routeUrl = environment.apiUrl +'/api/savedRoute'
  constructor(private http: HttpClient) { }

  receiveUserData(): Observable<string> {
    return this.http.get(this.userUrl + "/get", { responseType: 'text', withCredentials: true });
  }

  receiveFavouriteGasStations(): Observable<string> {
    return this.http.get(this.userUrl + "/favouriteStations", { responseType: 'text', withCredentials: true });
  }

  receiveSavedRoutes(): Observable<string> {
    return this.http.get(this.routeUrl, {responseType: 'text', withCredentials: true });
  }

}
