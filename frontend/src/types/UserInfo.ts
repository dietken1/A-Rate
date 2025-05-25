export default interface AuthResponse {
  token: string;
  user: User;
}

export interface User {
  id: number;
  name: string;
  profileImage: string;
  role: "STUDENT" | "PROFESSOR" | "ADMIN";
}
