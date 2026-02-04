import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';

export async function POST(request: Request) {
  try {
    const body = await request.json();

    const res = await fetch(`${config.apiUrl}/auth/forgot-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const data = await res.json();

    return NextResponse.json(data, {status: res.status});
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : 'Error en el servidor';
    return NextResponse.json({error: errorMessage}, {status: 500});
  }
}
