import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';

export async function POST(request: Request) {
  try {
    const body = await request.json();

    const res = await fetch(`${config.apiUrl}/auth/reset-password`, {
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

export async function GET(request: Request) {
  try {
    const {searchParams} = new URL(request.url);
    const token = searchParams.get('token');

    if (!token) {
      return NextResponse.json({valid: false}, {status: 400});
    }

    const res = await fetch(
      `${config.apiUrl}/auth/validate-reset-token?token=${encodeURIComponent(token)}`,
      {
        method: 'GET',
      }
    );

    const data = await res.json();

    return NextResponse.json(data, {status: res.status});
  } catch (error) {
    return NextResponse.json({valid: false}, {status: 500});
  }
}
