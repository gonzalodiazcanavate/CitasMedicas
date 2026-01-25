import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';
import type {LoginRequest} from '@/types/auth';

export async function POST(request: Request) {
  try {
    const body: LoginRequest = await request.json();

    const res = await fetch(`${config.apiUrl}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
      credentials: 'include',
    });

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({error: 'Error en el servidor'}));
      return NextResponse.json(
        {error: errorData.error || 'Error en el servidor'},
        {status: res.status}
      );
    }

    const data = await res.json();

    // Copiar cookies del backend al cliente
    const response = NextResponse.json(data);
    const cookies = res.headers.get('set-cookie');
    console.log(cookies);
    if (cookies) {
      response.headers.set('set-cookie', cookies);
    }

    return response;
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Error en el servidor';
    return NextResponse.json(
      {error: errorMessage},
      {status: 500}
    );
  }
}
