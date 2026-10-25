import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Profile, UserDetail } from 'src/app/models/profile.model';
import { ProfileService, ProfileTarget } from 'src/app/services/profile.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

const PHONE_PATTERN = /^[0-9+()\- ]{7,20}$/;
const MAX_PHOTO_BYTES = 2 * 1024 * 1024;
const ALLOWED_PHOTO_TYPES = ['image/jpeg', 'image/png'];

@Component({
  selector: 'app-manage',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss']
})
export class ManageComponent implements OnInit {
  mode: number; // 1: view, 3: update (el perfil se registra al guardar por primera vez)
  target: ProfileTarget; // 'me' o id del usuario
  userDetail: UserDetail;
  theFormGroup: FormGroup; // Policía de formulario
  trySend: boolean;
  photoUrl: string;        // foto actual (data URL)
  selectedPhoto: File;     // foto nueva elegida, pendiente de subir
  selectedPhotoUrl: string;
  constructor(private activatedRoute: ActivatedRoute,
    private profileService: ProfileService,
    private router: Router,
    private theFormBuilder: FormBuilder //Definir las reglas
  ) {
    this.trySend = false;
    this.userDetail = {};
    this.configFormGroup()
  }

  ngOnInit(): void {
    const currentUrl = this.activatedRoute.snapshot.url.join('/');
    this.mode = currentUrl.includes('update') ? 3 : 1;
    this.target = currentUrl.startsWith('me') ? 'me' : Number(this.activatedRoute.snapshot.params.id);
    this.getProfile();
  }
  configFormGroup() {
    this.theFormGroup = this.theFormBuilder.group({
      phone: ['', [Validators.pattern(PHONE_PATTERN)]]
    })
  }

  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  get isMine(): boolean {
    return this.target === 'me';
  }

  get hasProfile(): boolean {
    return !!this.userDetail.profile;
  }

  getProfile() {
    this.profileService.view(this.target).subscribe({
      next: (response) => {
        this.userDetail = response;
        this.theFormGroup.patchValue({
          phone: response.profile?.phone || ''
        });
        this.photoUrl = null;
        if (response.profile?.hasPhoto) {
          this.loadPhoto();
        }
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
  }

  // La foto se descarga con el token y se convierte a data URL para mostrarla
  loadPhoto() {
    this.profileService.getPhoto(this.target).subscribe({
      next: (blob) => {
        const reader = new FileReader();
        reader.onload = () => this.photoUrl = reader.result as string;
        reader.readAsDataURL(blob);
      }
    });
  }

  onPhotoSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0];
    if (!file) {
      return;
    }
    if (!ALLOWED_PHOTO_TYPES.includes(file.type)) {
      Swal.fire('Error', 'Solo se permiten imágenes JPG o PNG.', 'error');
      input.value = '';
      return;
    }
    if (file.size > MAX_PHOTO_BYTES) {
      Swal.fire('Error', 'La imagen no puede superar 2 MB.', 'error');
      input.value = '';
      return;
    }
    this.selectedPhoto = file;
    const reader = new FileReader();
    reader.onload = () => this.selectedPhotoUrl = reader.result as string;
    reader.readAsDataURL(file);
  }

  edit() {
    this.router.navigate([this.isMine ? '/profiles/me/update' : `/profiles/update/${this.target}`]);
  }

  back() {
    if (this.mode == 3) {
      this.router.navigate([this.isMine ? '/profiles/me/view' : `/profiles/view/${this.target}`]);
    } else {
      this.router.navigate([this.isMine ? '/dashboard' : '/users/list']);
    }
  }

  save() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor, corrija los campos marcados.',
        icon: 'error',
      })
      return;
    }
    const profile: Profile = { phone: this.theFormGroup.value.phone };
    // Si el usuario aún no tiene perfil se registra (POST); si ya tiene, se actualiza (PUT)
    const saveData: Observable<Profile> = this.hasProfile
      ? this.profileService.update(this.target, profile)
      : this.profileService.create(this.target, profile);

    saveData.pipe(
      switchMap(saved => this.selectedPhoto
        ? this.profileService.uploadPhoto(this.target, this.selectedPhoto)
        : of(saved))
    ).subscribe({
      next: () => {
        Swal.fire({
          title: 'Guardado!',
          text: 'Perfil guardado correctamente.',
          icon: 'success',
        })
        this.router.navigate([this.isMine ? '/profiles/me/view' : `/profiles/view/${this.target}`]);
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.getProfile();
      }
    });
  }

  removePhoto() {
    Swal.fire({
      title: 'Quitar fotografía',
      text: '¿Está seguro de quitar la fotografía del perfil?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, quitar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.profileService.removePhoto(this.target).subscribe({
          next: () => {
            Swal.fire('Listo', 'Fotografía eliminada correctamente.', 'success');
            this.getProfile();
          },
          error: (error) => {
            Swal.fire('Error', getErrorMessage(error), 'error');
          }
        });
      }
    })
  }

}
