import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private router: Router, private http: HttpClient) { }

    canActivate(): Observable<boolean> {
        return this.http.get('/api/auth/check', { withCredentials: true }).pipe(
            map(() => true),
            catchError(() => {
                this.router.navigate(['/login']);
                return of(false);
            })
        );

    }

}