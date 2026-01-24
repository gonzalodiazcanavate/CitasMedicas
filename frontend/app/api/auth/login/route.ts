import {NextResponse} from 'next/server';
import {config} from '@/config/apiConfig';
import type {LoginRequest} from '@/types/auth';

export async function POST(request: Request) {
  const body: LoginRequest = await request.json();

  const res = await fetch(`${config.apiUrl}/login`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    credentials: 'include',
    body: JSON.stringify(body),
  });

  const data = await res.json();

  return NextResponse.json(data, {status: res.status});
}
