import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AUTH_PROVIDER_LABELS, User } from 'src/app/models/user.model';
import { SecurityService } from 'src/app/services/security.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  user: User;
  title = '';
  subtitle = '';
  providerLabels = AUTH_PROVIDER_LABELS;
  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
    private activatedRoute: ActivatedRoute,
    private securityService: SecurityService) { }

  ngOnInit() {
    this.subscriptions.push(
      this.securityService.getUser().subscribe(data => this.user = data),
      this.router.events.pipe(filter(event => event instanceof NavigationEnd))
        .subscribe(() => this.updateTitle())
    );
    this.updateTitle();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  // Cierra la sesión en el backend (el token deja de ser válido) y vuelve al login
  logout() {
    this.securityService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  // El título y subtítulo vienen del "data" de las rutas (admin-layout.routing.ts)
  private updateTitle() {
    let route: ActivatedRouteSnapshot = this.activatedRoute.snapshot;
    let data: any = {};
    while (route) {
      data = { ...data, ...route.data };
      route = route.firstChild;
    }
    this.title = data.title || 'SportManager';
    this.subtitle = data.subtitle || '';
  }
}
