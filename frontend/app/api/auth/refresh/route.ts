import {config} from '@/config/apiConfig';

export async function POST(request: Request) {
  try {
    const res = await fetch(`${config.apiUrl}/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Cookie': request.headers.get('cookie') || '',
      },
    });

    if (!res.ok) {
      return new Response(
        JSON.stringify({error: 'Token inválido o expirado'}),
        {
          status: res.status,
          headers: {'Content-Type': 'application/json'},
        }
      );
    }

    // Crear headers y copiar nuevas cookies del backend
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
    return new Response(
      JSON.stringify({error: errorMessage}),
      {
        status: 500,
        headers: {'Content-Type': 'application/json'},
      }
    );
  }
}
