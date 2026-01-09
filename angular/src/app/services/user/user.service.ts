import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FavouriteGasStationDto, User } from '../../Dto/user-dtos';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/api/users';
  constructor(private http: HttpClient) {}
  saveUser(user: User): Observable<User> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<User>(this.apiUrl + "/create", user, {headers});
  }

  receiveUserData(mail: string): Observable<string>{
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);
    let parameters = new HttpParams()
      .set('mail', mail)

    return this.http.get(this.apiUrl + "/get", {headers: headers, params: parameters, responseType: 'text' });
  }

  receiveFavouriteGasStations(mail: string): Observable<string>{
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);
    let parameters = new HttpParams()
      .set('email', mail)

    return this.http.get(this.apiUrl + "/favourites", {headers: headers, params: parameters, responseType: 'text' });
  }
}
