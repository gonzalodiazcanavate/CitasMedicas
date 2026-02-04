export async function fetcher<T>(
  url: string,
  options?: RequestInit
): Promise<T> {
  const res = await fetch(url, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
    ...options,
  });

  if (!res.ok) {
    let errorMessage = 'Error en la petición';
    try {
      const error = await res.json();
      errorMessage = error.error ?? errorMessage;
    } catch {}
    throw new Error(errorMessage);
  }

  return res.json() as Promise<T>;
}