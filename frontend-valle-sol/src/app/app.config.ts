import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { routes } from './app.routes.server';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // HttpClient con fetch: compatible con SSR y con el navegador.
    provideHttpClient(withFetch())
  ]
};
