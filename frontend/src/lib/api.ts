export async function fetcher<T>(
  input: RequestInfo,
  init?: RequestInit
): Promise<T> {
  const res = await fetch(input, init);
  if (!res.ok) {
    throw new Error(await res.text());
  }
  return res.json();
}

export function get<T>(url: string, options?: RequestInit) {
  return fetcher<T>(url, { ...options, method: "GET" });
}

export function post<T>(url: string, body: any, options?: RequestInit) {
  return fetcher<T>(url, {
    ...options,
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(options?.headers || {}),
    },
    body: JSON.stringify(body),
  });
}

// const { data, error, isLoading } = useQuery(['key', param], () => get<DataType>('/api/data'));
// const mutation = useMutation((payload) => post<DataType>('/api/data', payload));
