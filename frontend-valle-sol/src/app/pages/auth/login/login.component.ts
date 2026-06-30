import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './login.component.html',
    styles: [`
    .login-screen {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      background-color: #030a06;
      background-image: radial-gradient(circle at 50% 30%, #0a2517 0%, #030a06 70%);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 99999; /* Se superpone a la navbar global */
    }

    .grid-bg {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: linear-gradient(rgba(16, 185, 129, 0.02) 1px, transparent 1px), 
                        linear-gradient(90deg, rgba(16, 185, 129, 0.02) 1px, transparent 1px);
      background-size: 40px 40px;
      pointer-events: none;
    }

    .glass-card {
      background: rgba(15, 23, 42, 0.55);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid rgba(16, 185, 129, 0.25);
      border-radius: 24px;
      box-shadow: 0 20px 50px rgba(0, 0, 0, 0.6), 0 0 40px rgba(16, 185, 129, 0.05);
      padding: 45px 35px;
      width: 100%;
      max-width: 380px;
      text-align: center;
      box-sizing: border-box;
    }

    .input-wrapper {
      text-align: left;
      margin-bottom: 20px;
    }

    .input-wrapper label {
      display: block;
      font-size: 0.75rem;
      text-transform: uppercase;
      color: #64748b;
      font-weight: 700;
      margin-bottom: 8px;
      letter-spacing: 1px;
    }

    .input-field {
      width: 100%;
      background: rgba(2, 6, 23, 0.7);
      border: 1px solid rgba(16, 185, 129, 0.2);
      padding: 14px 16px;
      border-radius: 12px;
      color: #f8fafc;
      font-size: 0.95rem;
      outline: none;
      box-sizing: border-box;
      transition: all 0.3s ease;
    }

    .input-field:focus {
      border-color: #34d399;
      box-shadow: 0 0 15px rgba(52, 211, 153, 0.15);
      background: rgba(2, 6, 23, 0.9);
    }

    .btn-submit {
      width: 100%;
      background: #10b981;
      color: #022c22;
      font-weight: 800;
      font-size: 0.95rem;
      padding: 14px;
      border-radius: 12px;
      border: none;
      cursor: pointer;
      letter-spacing: 0.5px;
      transition: all 0.3s ease;
      margin-top: 10px;
    }

    .btn-submit:hover {
      background: #34d399;
      box-shadow: 0 0 20px rgba(52, 211, 153, 0.4);
      transform: translateY(-1px);
    }

    .btn-submit:active {
      transform: translateY(1px);
    }
  `]
})
export class LoginComponent {
    constructor(private router: Router) { }

    handleLogin(user: string, pass: string) {
        if (user.trim() && pass.trim()) {
            this.router.navigate(['/admin/dashboard']);
        } else {
            alert('⚠️ AWS Security: Por favor ingrese credenciales de operador.');
        }
    }
}