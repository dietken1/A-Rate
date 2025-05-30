import { create } from "zustand";
import { persist } from "zustand/middleware";
import UserInfo from "../types/UserInfo";

interface AuthState {
  user: UserInfo | null;
}

export interface AuthStore extends AuthState {
  authorize: (user: UserInfo) => void;
  deauthorize: () => void;
}

const initialState: AuthState = {
  user: null,
};

/*
const initialState: AuthState = {
  user: {
    token: "",
    user: {
      id: 0,
      name: "",
      picture: "",
      role: "STUDENT",
      isProfileComplete: false,
    },
  },
};
*/

const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      ...initialState,
      authorize: (user) =>
        set(() => ({
          user,
        })),
      deauthorize: () =>
        set(() => ({
          user: null,
        })),
    }),
    {
      name: "auth-store", // localStorage key
    }
  )
);

export default useAuthStore;
