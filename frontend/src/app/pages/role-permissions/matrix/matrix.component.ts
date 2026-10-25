import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  moduleLabel, PermissionOperation, PERMISSION_MODULES, PERMISSION_OPERATIONS, resourceIcon
} from 'src/app/models/permission.model';
import { MatrixPermission, RolePermissionMatrix } from 'src/app/models/role-permission.model';
import { Role } from 'src/app/models/role.model';
import { RolePermissionService } from 'src/app/services/role-permission.service';
import { RoleService } from 'src/app/services/role.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Una casilla de la matriz (recurso + operación) puede agrupar varios endpoints.
interface MatrixCell {
  permissions: MatrixPermission[];
}

interface MatrixRow {
  resource: string;
  cells: { [operation: string]: MatrixCell };
}

interface MatrixGroup {
  module: string;
  label: string;
  collapsed: boolean;
  rows: MatrixRow[];
}

@Component({
  selector: 'app-matrix',
  templateUrl: './matrix.component.html',
  styleUrls: ['./matrix.component.scss']
})
export class MatrixComponent implements OnInit {
  roles: Role[] = [];
  selectedRoleId: number = null;
  matrix: RolePermissionMatrix = null;
  groups: MatrixGroup[] = [];
  unclassified: MatrixPermission[] = [];   // permisos sin módulo/recurso/operación
  operations = PERMISSION_OPERATIONS;
  checked = new Set<number>();             // estado en edición
  private original = new Set<number>();    // estado guardado (para Restablecer)
  successMessage = '';
  saving = false;

  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private roleService: RoleService,
    private rolePermissionService: RolePermissionService
  ) { }

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.params.id;
    if (id) {
      this.selectedRoleId = Number(id);
      this.loadMatrix();
    }
    this.roleService.list().subscribe({
      next: (roles) => {
        this.roles = roles;
        // Al entrar desde el menú Roles se muestra de una vez la matriz
        // del primer rol, como en el mockup
        if (!id && roles.length > 0) {
          this.selectedRoleId = roles[0].id;
          this.loadMatrix();
        }
      },
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  // ---- Carga y armado de la matriz ----

  loadPermissions() {
    if (!this.selectedRoleId) {
      Swal.fire('Seleccione un rol', 'Debe seleccionar el rol cuyos permisos desea administrar.', 'info');
      return;
    }
    if (this.hasChanges && this.matrix && this.matrix.role.id !== Number(this.selectedRoleId)) {
      Swal.fire({
        title: 'Cambios sin guardar',
        text: 'Si carga otro rol se perderán los cambios no guardados. ¿Desea continuar?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, continuar',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.isConfirmed) {
          this.loadMatrix();
        }
      });
      return;
    }
    this.loadMatrix();
  }

  private loadMatrix() {
    this.successMessage = '';
    this.rolePermissionService.matrix(this.selectedRoleId).subscribe({
      next: (matrix) => this.applyMatrix(matrix),
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  private applyMatrix(matrix: RolePermissionMatrix) {
    this.matrix = matrix;
    this.selectedRoleId = matrix.role.id;
    this.original = new Set(matrix.permissions.filter(p => p.assigned).map(p => p.id));
    this.checked = new Set(this.original);
    this.buildGroups(matrix.permissions);
  }

  // Agrupa: módulo -> recurso -> operación (como en el mockup)
  private buildGroups(permissions: MatrixPermission[]) {
    const previouslyCollapsed = new Set(this.groups.filter(g => g.collapsed).map(g => g.module));
    this.unclassified = permissions.filter(p => !p.module || !p.resource || !p.operation);
    const classified = permissions.filter(p => p.module && p.resource && p.operation);

    this.groups = PERMISSION_MODULES
      .map(module => {
        const rowsByResource = new Map<string, MatrixRow>();
        classified.filter(p => p.module === module.value).forEach(permission => {
          if (!rowsByResource.has(permission.resource)) {
            rowsByResource.set(permission.resource, { resource: permission.resource, cells: {} });
          }
          const row = rowsByResource.get(permission.resource);
          if (!row.cells[permission.operation]) {
            row.cells[permission.operation] = { permissions: [] };
          }
          row.cells[permission.operation].permissions.push(permission);
        });
        return {
          module: module.value,
          label: module.label,
          collapsed: previouslyCollapsed.has(module.value),
          rows: Array.from(rowsByResource.values())
        };
      })
      .filter(group => group.rows.length > 0);
  }

  // ---- Estado de casillas ----

  cell(row: MatrixRow, operation: PermissionOperation): MatrixCell {
    return row.cells[operation];
  }

  isCellChecked(cell: MatrixCell): boolean {
    return cell.permissions.every(p => this.checked.has(p.id));
  }

  // Parcial: solo algunos endpoints de la casilla están asignados
  isCellPartial(cell: MatrixCell): boolean {
    const assigned = cell.permissions.filter(p => this.checked.has(p.id)).length;
    return assigned > 0 && assigned < cell.permissions.length;
  }

  // Marcar asigna todos los endpoints de la casilla; desmarcar los retira
  toggleCell(cell: MatrixCell) {
    const assign = !this.isCellChecked(cell);
    cell.permissions.forEach(p => assign ? this.checked.add(p.id) : this.checked.delete(p.id));
    this.successMessage = '';
  }

  togglePermission(permission: MatrixPermission) {
    this.checked.has(permission.id) ? this.checked.delete(permission.id) : this.checked.add(permission.id);
    this.successMessage = '';
  }

  cellTitle(cell: MatrixCell): string {
    return cell.permissions.map(p => `${p.method} ${p.url}`).join('\n');
  }

  toggleGroup(group: MatrixGroup) {
    group.collapsed = !group.collapsed;
  }

  // ---- Resumen del rol ----

  private get allCells(): MatrixCell[] {
    const cells: MatrixCell[] = [];
    this.groups.forEach(group => group.rows.forEach(row =>
      Object.values(row.cells).forEach(cell => cells.push(cell))));
    this.unclassified.forEach(p => cells.push({ permissions: [p] }));
    return cells;
  }

  get totalCount(): number {
    return this.allCells.length;
  }

  get assignedCount(): number {
    return this.allCells.filter(cell => this.isCellChecked(cell)).length;
  }

  get notAssignedCount(): number {
    return this.totalCount - this.assignedCount;
  }

  get modulesWithAccess(): number {
    return this.groups.filter(group => group.rows.some(row =>
      Object.values(row.cells).some(cell => cell.permissions.some(p => this.checked.has(p.id))))).length;
  }

  get hasChanges(): boolean {
    if (this.checked.size !== this.original.size) {
      return true;
    }
    return Array.from(this.checked).some(id => !this.original.has(id));
  }

  moduleLabel = moduleLabel;
  resourceIcon = resourceIcon;

  get selectedRole(): Role {
    return this.roles.find(role => role.id === this.selectedRoleId);
  }

  selectRole(role: Role) {
    this.selectedRoleId = role.id;
  }

  // ---- Acciones ----

  save() {
    if (!this.matrix) {
      return;
    }
    this.saving = true;
    this.rolePermissionService.save(this.matrix.role.id, Array.from(this.checked)).subscribe({
      next: (matrix) => {
        this.saving = false;
        this.applyMatrix(matrix);
        this.successMessage = 'Permisos actualizados correctamente.';
      },
      error: (error) => {
        this.saving = false;
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

  reset() {
    this.checked = new Set(this.original);
    this.successMessage = '';
  }

  // Crear, editar, activar/desactivar y eliminar roles (HU-SE-03)
  manageRoles() {
    this.router.navigate(['/roles/list']);
  }

}
