import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FavouriteGasStationDto, User } from '../../Dto/user-dtos';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SavedRoute } from '../../Dto/saved-route';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUrl = 'http://localhost:8080/api/users';
  private routeUrl = 'http://localhost:8080/api/ruta'
  constructor(private http: HttpClient) { }
  saveUser(user: User): Observable<User> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.post<User>(this.userUrl + "/create", user, { headers });
  }

  receiveUserData(mail: string): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);
    let parameters = new HttpParams()
      .set('mail', mail)

    return this.http.get(this.userUrl + "/get", { headers: headers, params: parameters, responseType: 'text' });
  }

  receiveFavouriteGasStations(email: string): Observable<string> {
    let parameters = new HttpParams()
      .set('email', email)

    return this.http.get(this.userUrl + "/favourites", { params: parameters, responseType: 'text' });
  }

  receiveSavedRoutes(email: string): Observable<string> {
    let parameters = new HttpParams()
      .set('email', email)
    return this.http.get(this.routeUrl, { params: parameters, responseType: 'text' });
  }
}
