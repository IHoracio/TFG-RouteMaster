import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FavouriteGasStationDto, User, UserLoginDTO } from '../../Dto/user-dtos';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SavedRoute } from '../../Dto/saved-route';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userUrl = 'http://localhost:8080/api/users';
  private routeUrl = 'http://localhost:8080/api/savedRoute'
  constructor(private http: HttpClient) { }

  receiveUserData(mail: string): Observable<string> {
    const headers = new HttpHeaders().set('key', environment.googleMapsMapId);
    let parameters = new HttpParams()
      .set('mail', mail)

    return this.http.get(this.userUrl + "/get", { headers: headers, params: parameters, responseType: 'text', withCredentials: true });
  }

  receiveFavouriteGasStations(email: string): Observable<string> {
    let parameters = new HttpParams()

    return this.http.get(this.userUrl + "/favouriteStations", { params: parameters, responseType: 'text', withCredentials: true });
  }

  receiveSavedRoutes(email: string): Observable<string> {
    let parameters = new HttpParams()
    return this.http.get(this.routeUrl, { params: parameters, responseType: 'text', withCredentials: true });
  }

}
