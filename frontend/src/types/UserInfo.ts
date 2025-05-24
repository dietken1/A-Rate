export default interface AuthResponse {
  token: string;
  user: {
    id: number;
    name: string;
    picture: string;
    role: "STUDENT" | "PROFESSOR" | "ADMIN";
    isProfileComplete: boolean;
  };
}
