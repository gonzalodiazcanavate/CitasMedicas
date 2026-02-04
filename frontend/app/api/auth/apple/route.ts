import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';

export async function POST(request: Request) {
  try {
    const body = await request.json();

    const res = await fetch(`${config.apiUrl}/auth/apple`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
      credentials: 'include',
    });

    const data = await res.json().catch(() => ({}));

    // Crear respuesta con el status apropiado
    const response = NextResponse.json(data, {status: res.status});

    // Copiar cookies del backend al cliente (si el login fue exitoso)
    const cookies = res.headers.get('set-cookie');
    if (cookies) {
      response.headers.set('set-cookie', cookies);
    }

    return response;
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : 'Error en el servidor';
    return NextResponse.json({error: errorMessage}, {status: 500});
  }
}
