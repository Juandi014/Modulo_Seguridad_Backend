import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Profile, UserDetail } from '../models/profile.model';
import { environment } from 'src/environments/environment';

// 'me' = perfil del usuario autenticado; número = perfil de ese usuario (administrador)
export type ProfileTarget = 'me' | number;

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  constructor(private http: HttpClient) { }

  private baseUrl(target: ProfileTarget): string {
    return target === 'me'
      ? `${environment.url_ms_security}/profile/me`
      : `${environment.url_ms_security}/users/${target}/profile`;
  }

  view(target: ProfileTarget): Observable<UserDetail> {
    return this.http.get<UserDetail>(this.baseUrl(target));
  }
  create(target: ProfileTarget, profile: Profile): Observable<Profile> {
    return this.http.post<Profile>(this.baseUrl(target), { phone: profile.phone });
  }
  update(target: ProfileTarget, profile: Profile): Observable<Profile> {
    return this.http.put<Profile>(this.baseUrl(target), { phone: profile.phone });
  }
  uploadPhoto(target: ProfileTarget, file: File): Observable<Profile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.put<Profile>(`${this.baseUrl(target)}/photo`, formData);
  }
  getPhoto(target: ProfileTarget): Observable<Blob> {
    return this.http.get(`${this.baseUrl(target)}/photo`, { responseType: 'blob' });
  }
  removePhoto(target: ProfileTarget): Observable<Profile> {
    return this.http.delete<Profile>(`${this.baseUrl(target)}/photo`);
  }

}
