import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SecurityService } from 'src/app/services/security.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Destino del backend tras un login OAuth exitoso: cambia el código de
// un solo uso por la sesión interna y entra a la plataforma.
@Component({
  selector: 'app-oauth-callback',
  templateUrl: './oauth-callback.component.html',
  styleUrls: ['./oauth-callback.component.scss']
})
export class OAuthCallbackComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private securityService: SecurityService) { }

  ngOnInit(): void {
    const code = this.activatedRoute.snapshot.queryParams.code;
    if (!code) {
      this.router.navigate(['/login']);
      return;
    }
    this.securityService.exchangeOAuthCode(code).subscribe({
      next: (data) => {
        this.securityService.saveSession(data);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        Swal.fire('No fue posible iniciar sesión', getErrorMessage(error), 'error');
        this.router.navigate(['/login']);
      }
    });
  }

}
