import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, RedirectCommand, Router, RouterStateSnapshot } from "@angular/router";
import { LoginService } from "./services/login.service";

export function authGuard(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    let router = inject(Router);
    let loginService = inject(LoginService);

    if (loginService.isTokenExpired()) {
        localStorage.removeItem("token");
        loginService.token = null;
        return new RedirectCommand(router.parseUrl("/login"));
    }


    if (loginService.validateRoles(route.data['requiredRoles'])) {
        return true;
    }

    return new RedirectCommand(router.parseUrl("/"));
}
