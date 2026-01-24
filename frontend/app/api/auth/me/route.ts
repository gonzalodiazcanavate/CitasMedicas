import { NextResponse } from "next/server";
import { config } from "@/config/apiConfig";

export async function GET(request: Request) {
  const cookie = request.headers.get("cookie") ?? "";

  const res = await fetch(`${config.apiUrl}/users/me`, {
    headers: { cookie },
  });

  if (!res.ok) {
    return NextResponse.json({ auth: false }, { status: 401 });
  }

  const data = await res.json();
  return NextResponse.json(data);
}