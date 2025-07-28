import { HttpErrorResponse, HttpEventType, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";

import { catchError, tap, throwError } from "rxjs";
import { Router } from "@angular/router";
import { LoginService } from "./services/login.service";

export function authInterceptor(req: HttpRequest<any>, next: HttpHandlerFn) {
    const loginService = inject(LoginService);
    const router = inject(Router);

    let authReq = req;
    if (loginService.token) {
        authReq = req.clone({
            setHeaders: {
                "authorization": loginService.token
            }
        });
    }

    return next(authReq).pipe(
        tap(response => {
            if (response.type === HttpEventType.Response) {
                console.log(response.status);
            }
        }),
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                router.navigateByUrl("/login");
            }
            return throwError(() => error);
        })
    );
}
