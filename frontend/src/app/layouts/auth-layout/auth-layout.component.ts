import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-auth-layout',
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.scss']
})
export class AuthLayoutComponent implements OnInit, OnDestroy {

  // La clase "auth-layout" quita el margen reservado para el menú lateral
  ngOnInit() {
    document.documentElement.classList.add('auth-layout');
  }

  ngOnDestroy() {
    document.documentElement.classList.remove('auth-layout');
  }
}
