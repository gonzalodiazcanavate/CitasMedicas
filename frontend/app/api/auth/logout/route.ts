import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';

export async function POST(request: Request) {
  try {
    const res = await fetch(`${config.apiUrl}/auth/logout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Cookie': request.headers.get('cookie') || '',
      },
    });

    if (!res.ok) {
      return NextResponse.json(
        {error: 'Error al cerrar sesión'},
        {status: res.status}
      );
    }

    // Copiar todas las cookies del backend (cookies de eliminación)
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');
    
    const cookies = res.headers.getSetCookie();
    cookies.forEach((cookie) => {
      headers.append('Set-Cookie', cookie);
    });

    return new Response(JSON.stringify({success: true}), {
      status: 200,
      headers: headers,
    });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Error en el servidor';
    return NextResponse.json(
      {error: errorMessage},
      {status: 500}
    );
  }
}
