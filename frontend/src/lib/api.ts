export type ApiSuccess<T> = { success: true; data: T };
export type ApiFailure = { success: false; error: string };
export type ApiResponse<T> = ApiSuccess<T> | ApiFailure;

export async function fetcher<T>(
  input: RequestInfo,
  init?: RequestInit
): Promise<ApiResponse<T>> {
  const url = `${process.env.REACT_APP_API_URL}${input}`;
  const res = await fetch(url, init);
  if (!res.ok) {
    return {
      success: false,
      error: await res.text(),
    };
  }
  const data = await res.json();
  return {
    success: true,
    data,
  };
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
      ...(options && options.headers ? options.headers : {}),
    },
    body: JSON.stringify(body),
  });
}

// 사용 예시:
// const { data } = useQuery<ApiResponse<MyType>>(['key'], () => get<MyType>('/api/xxx'));
// if (data?.success) { ...data.data... } else { ...data?.error... }
