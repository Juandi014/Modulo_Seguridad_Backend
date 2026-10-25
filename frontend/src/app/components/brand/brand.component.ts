import { Component, Input } from '@angular/core';

// Logo y nombre de la plataforma: se usa en el menú lateral y en las
// pantallas de inicio de sesión para que la marca sea la misma en todo el sitio.
@Component({
  selector: 'app-brand',
  templateUrl: './brand.component.html'
})
export class BrandComponent {
  @Input() large = false;
}
