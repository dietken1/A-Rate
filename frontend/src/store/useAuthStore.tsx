import { create } from "zustand";
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

const useAuthStore = create<AuthStore>()((set) => ({
  ...initialState,
  authorize: (user) =>
    set(() => ({
      user,
    })),
  deauthorize: () =>
    set(() => ({
      user: null,
    })),
}));

export default useAuthStore;
