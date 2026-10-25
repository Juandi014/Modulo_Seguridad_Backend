import { Component, OnInit } from '@angular/core';
import { LoginAttempt, LOGIN_RESULTS, Page } from 'src/app/models/login-attempt.model';
import { AUTH_PROVIDER_LABELS } from 'src/app/models/user.model';
import { AuditService } from 'src/app/services/audit.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Auditoría: listado de solo lectura de los intentos de inicio de sesión
@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  page: Page<LoginAttempt> = new Page<LoginAttempt>();
  results = LOGIN_RESULTS;
  providerLabels = AUTH_PROVIDER_LABELS;
  emailFilter = '';
  resultFilter = '';
  pageSize = 20;
  loading = false;

  constructor(private auditService: AuditService) { }

  ngOnInit(): void {
    this.list(0);
  }

  list(pageNumber: number) {
    this.loading = true;
    this.auditService.loginAttempts(pageNumber, this.pageSize, this.emailFilter.trim(), this.resultFilter)
      .subscribe({
        next: (page) => {
          this.loading = false;
          this.page = page;
        },
        error: (error) => {
          this.loading = false;
          Swal.fire('Error', getErrorMessage(error), 'error');
        }
      });
  }

  search() {
    this.list(0);
  }

  clearFilters() {
    this.emailFilter = '';
    this.resultFilter = '';
    this.list(0);
  }

  resultLabel(value: string): string {
    return this.results.find(r => r.value === value)?.label || value;
  }

  isSuccess(value: string): boolean {
    return !!this.results.find(r => r.value === value)?.success;
  }

}
